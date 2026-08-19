// COOP + COEP headers for the JS webpack-dev-server.
// These enable self.crossOriginIsolated = true on localhost, which allows
// AppDatabaseFactory to use WebWorkerSQLiteDriver + OPFS-backed persistent storage.
// GitHub Pages does not send these headers → crossOriginIsolated = false → falls back to
// Room.inMemoryDatabaseBuilder automatically (see AppDatabaseFactory).
config.devServer = config.devServer || {};
config.devServer.headers = Object.assign({}, config.devServer.headers || {}, {
    'Cross-Origin-Opener-Policy': 'same-origin',
    'Cross-Origin-Embedder-Policy': 'require-corp',
});
