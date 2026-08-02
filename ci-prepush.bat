@echo off
setlocal enabledelayedexpansion

rem Check if gradlew exists in the project
if not exist "%~dp0gradlew" (
    echo Error: gradlew not found in the project.
    exit /b 1
)

echo Starting all checks and tests...

call :run_gradle_task "check -p build-logic"
call :run_gradle_task "spotlessApply --no-configuration-cache"
call :run_gradle_task "detekt"
call :run_gradle_task ":cmp-android:build"
call :run_gradle_task ":cmp-android:checkProdReleaseBadging"
call :rebaseline_dependencies

echo All checks and tests completed successfully.
exit /b 0

rem Re-baseline using CI-compatible resolution (all .local=false).
:rebaseline_dependencies
set "props=lib-integrate.properties"
if not exist "%props%" (
    call :run_gradle_task "dependencyGuardBaseline"
    exit /b 0
)
findstr /m "\.local=true" "%props%" >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo Detected .local=true in %props% -- flipping to false for CI-compatible baseline
    copy /y "%props%" "%props%.prepush.bak" >nul
    powershell -Command "(Get-Content '%props%') -replace '\.local=true', '.local=false' | Set-Content '%props%'"
    call :run_gradle_task "dependencyGuardBaseline"
    copy /y "%props%.prepush.bak" "%props%" >nul
    del "%props%.prepush.bak"
) else (
    call :run_gradle_task "dependencyGuardBaseline"
)
exit /b 0

:run_gradle_task
echo ########################################################
echo Running: %~1
call "%~dp0gradlew" %~1
if %ERRORLEVEL% neq 0 (
    echo Error: Task %~1 failed
    exit /b 1
)
echo ########################################################
exit /b 0