@echo off
rem build-secrets.cmd — Windows launcher for the ONE secrets resolver (build_secrets.rb).
rem
rem Mirrors the bash `deployment/scripts/build-secrets` so the resolver is callable from cmd /
rem PowerShell / Windows Actions steps, where a `#!/usr/bin/env bash` shebang cannot be exec'd.
rem Ruby is on the GitHub windows-latest image PATH (and the stdlib-only lib needs nothing else).
ruby "%~dp0..\_shared\lib\build_secrets.rb" %*
exit /b %errorlevel%
