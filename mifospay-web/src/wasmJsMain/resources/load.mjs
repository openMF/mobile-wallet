import { instantiate } from './mifoswasmapp.uninstantiated.mjs';

await wasmSetup;

instantiate({ skia: Module['asm'] });