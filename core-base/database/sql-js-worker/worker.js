/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Web Worker — Room 3 SQLite driver backed by sql.js (in-memory only).
 *
 * Bundled via webpack. The sql.js JS is bundled locally from npm; the WASM
 * binary is fetched from CDN at runtime (sql.js does not export it as a
 * webpack asset without additional configuration).
 *
 * Storage: always in-memory. Data does NOT persist across page reloads.
 * Use this worker as a fallback on browsers/hosts without OPFS support.
 *
 * To switch the app to this driver, change `AppDatabaseFactory` to call
 * `createSqlJsWorker()` instead of `createSQLiteWasmWorker()`.
 *
 * Protocol: Room's WebWorkerSQLiteDriver message protocol (Room 3.0.0-alpha05+).
 */

import initSqlJs from 'sql.js';

let SQL = null;

const databases = new Map();
const statements = new Map();
let nextDatabaseId = 0;
let nextStatementId = 0;

function openRequest(id, requestData) {
    try {
        const newDatabaseId = nextDatabaseId++;
        // sql.js is always in-memory; OPFS is not supported.
        const newDatabase = new SQL.Database();
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
        const resultData = {
            'statementId': newStatementId,
            'parameterCount': 256, // sql.js doesn't expose bind_parameter_count
            'columnNames': statement.getColumnNames(),
        };
        postMessage({'id': id, data: resultData});
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
        const resultData = {'rows': [], 'columnTypes': []};
        statement.reset();
        if (requestData.bindings && requestData.bindings.length > 0) {
            statement.bind(requestData.bindings);
        }
        while (statement.step()) {
            const row = statement.get();
            if (!resultData.columnTypes.length) {
                for (const val of row) {
                    if (val === null) resultData.columnTypes.push(5);
                    else if (typeof val === 'number') resultData.columnTypes.push(Number.isInteger(val) ? 1 : 2);
                    else if (typeof val === 'string') resultData.columnTypes.push(3);
                    else if (val instanceof Uint8Array) resultData.columnTypes.push(4);
                    else resultData.columnTypes.push(5);
                }
            }
            resultData.rows.push(row);
        }
        postMessage({'id': id, data: resultData});
    } catch (error) {
        postMessage({'id': id, error: String(error.message || error)});
    }
}

function closeRequest(requestData) {
    // Room's closeStatement / closeDatabase are fire-and-forget — no pending deferred.
    // Do NOT postMessage back — any response triggers "was not expected" in CoroutineWebWorker.
    if (requestData.statementId != null) {
        const statement = statements.get(requestData.statementId);
        if (statement) {
            try { statement.free(); } catch (_) {}
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
            closeRequest(requestData);
            break;
        default:
            postMessage({'id': id, error: "Unknown command: '" + cmd + "'."});
    }
}

const messageQueue = [];
onmessage = (e) => {
    if (!SQL) {
        messageQueue.push(e);
    } else {
        handleMessage(e);
    }
};

initSqlJs({
    locateFile: file => `https://cdnjs.cloudflare.com/ajax/libs/sql.js/1.14.0/${file}`,
}).then(instance => {
    SQL = instance;
    while (messageQueue.length > 0) {
        handleMessage(messageQueue.shift());
    }
}).catch(err => {
    console.error('Room sql.js worker: Failed to initialize sql.js:', err);
});
