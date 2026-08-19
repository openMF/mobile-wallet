/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Web Worker — Room 3 SQLite driver backed by @sqlite.org/sqlite-wasm.
 *
 * Bundled via webpack (no CDN dependency). The @sqlite.org/sqlite-wasm npm
 * package ships the WASM binary; webpack handles it as an asset automatically.
 *
 * Storage strategy:
 *  - crossOriginIsolated = true  (COOP/COEP headers set) →
 *    sqlite3.oo1.OpfsDb(fileName) — persistent OPFS-backed database.
 *  - crossOriginIsolated = false (GitHub Pages / no headers) →
 *    sqlite3.oo1.DB(':memory:') — in-memory for the page session.
 *
 * Protocol: Room 3 WebWorkerSQLiteDriver message protocol (alpha05+).
 *
 * Request shape  (from Room's CoroutineWebWorker via sendRequest):
 *   { id: N, data: { cmd: 'open'|'prepare'|'step', fileName?, databaseId?, sql?, statementId?, bindings? } }
 *
 * Close shape (Room fires these WITHOUT awaiting a response — fire-and-forget):
 *   { id: N, data: { cmd: 'close', statementId? or databaseId? } }
 *   ⚠ Do NOT postMessage back for 'close' — Room's CoroutineWebWorker has no
 *     pending deferred for those IDs, so any response triggers "was not expected".
 *
 * Response shape (for open/prepare/step only):
 *   { id: N, data: { ... } }  on success
 *   { id: N, error: "..." }   on failure
 */

import sqlite3InitModule from '@sqlite.org/sqlite-wasm';

let sqlite3 = null;

const databases = new Map();
const statements = new Map();
let nextDatabaseId = 0;
let nextStatementId = 0;

function openRequest(id, requestData) {
    try {
        const newDatabaseId = nextDatabaseId++;
        let newDatabase;
        // Room sends 'fileName' (not 'path') per DatabaseWebWorkerImpl.wasmJs.kt
        const dbFileName = requestData.fileName;
        if (dbFileName && sqlite3.oo1.OpfsDb) {
            try {
                newDatabase = new sqlite3.oo1.OpfsDb(dbFileName);
            } catch (_) {
                newDatabase = new sqlite3.oo1.DB(':memory:');
            }
        } else {
            newDatabase = new sqlite3.oo1.DB(':memory:');
        }
        databases.set(newDatabaseId, newDatabase);
        postMessage({'id': id, data: {'databaseId': newDatabaseId}});
    } catch (error) {
        postMessage({'id': id, error: String(error.message || error)});
    }
}

function prepareRequest(id, requestData) {
    try {
        const database = databases.get(requestData.databaseId);
        if (!database) {
            postMessage({'id': id, error: 'Invalid database ID: ' + requestData.databaseId});
            return;
        }
        const newStatementId = nextStatementId++;
        const statement = database.prepare(requestData.sql);
        statements.set(newStatementId, statement);
        const columnNames = [];
        for (let i = 0; i < statement.columnCount; i++) {
            columnNames.push(sqlite3.capi.sqlite3_column_name(statement, i));
        }
        postMessage({'id': id, data: {
            'statementId': newStatementId,
            'parameterCount': sqlite3.capi.sqlite3_bind_parameter_count(statement),
            'columnNames': columnNames,
        }});
    } catch (error) {
        postMessage({'id': id, error: String(error.message || error)});
    }
}

function stepRequest(id, requestData) {
    const statement = statements.get(requestData.statementId);
    if (!statement) {
        postMessage({'id': id, error: 'Invalid statement ID: ' + requestData.statementId});
        return;
    }
    try {
        const rows = [];
        let columnTypes = [];
        statement.reset();
        statement.clearBindings();
        const bindings = requestData.bindings;
        if (bindings) {
            for (let i = 0; i < bindings.length; i++) {
                statement.bind(i + 1, bindings[i]);
            }
        }
        while (statement.step()) {
            if (columnTypes.length === 0) {
                for (let i = 0; i < statement.columnCount; i++) {
                    columnTypes.push(sqlite3.capi.sqlite3_column_type(statement, i));
                }
            }
            rows.push(statement.get([]));
        }
        postMessage({'id': id, data: {'rows': rows, 'columnTypes': columnTypes}});
    } catch (error) {
        postMessage({'id': id, error: String(error.message || error)});
    }
}

function closeRequest(requestData) {
    // Room's closeStatement / closeDatabase are fire-and-forget — no sendRequest,
    // no pending deferred. Sending a response would trigger "was not expected" in
    // CoroutineWebWorker.onMessage. Just clean up resources silently.
    if (requestData.statementId != null) {
        const statement = statements.get(requestData.statementId);
        if (statement) {
            try { statement.finalize(); } catch (_) {}
            statements.delete(requestData.statementId);
        }
    }
    if (requestData.databaseId != null) {
        const database = databases.get(requestData.databaseId);
        if (database) {
            try { database.close(); } catch (_) {}
            databases.delete(requestData.databaseId);
        }
    }
    // Intentionally no postMessage — Room never awaits close responses.
}

function handleMessage(e) {
    const requestMsg = e.data;
    if (requestMsg == null || requestMsg.data == null) {
        return;
    }
    const cmd = requestMsg.data.cmd;
    const id = requestMsg.id;
    const requestData = requestMsg.data;
    switch (cmd) {
        case 'open':
            openRequest(id, requestData);
            break;
        case 'prepare':
            prepareRequest(id, requestData);
            break;
        case 'step':
            stepRequest(id, requestData);
            break;
        case 'close':
            // No id needed — fire-and-forget, no response sent.
            closeRequest(requestData);
            break;
        default:
            postMessage({'id': id, error: "Unknown command: '" + cmd + "'."});
    }
}

const messageQueue = [];
onmessage = (e) => {
    if (!sqlite3) {
        messageQueue.push(e);
    } else {
        handleMessage(e);
    }
};

sqlite3InitModule().then(instance => {
    sqlite3 = instance;
    while (messageQueue.length > 0) {
        handleMessage(messageQueue.shift());
    }
}).catch(err => {
    console.error('Room SQLite worker: Failed to initialize SQLite WASM:', err);
});
