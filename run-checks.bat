@echo off
setlocal enabledelayedexpansion

rem Enable ANSI escape codes for colored output
for /f "tokens=4-5 delims=. " %%i in ('ver') do set VERSION=%%i.%%j
if "%version%" == "10.0" (
    reg add HKCU\Console /v VirtualTerminalLevel /t REG_DWORD /d 1 /f >nul 2>&1
)

rem Color codes
set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "MAGENTA=[95m"
set "CYAN=[96m"
set "RESET=[0m"

rem Check if gradlew exists in the parent directory
if not exist "%~dp0gradlew" (
    echo %RED%Error: gradlew not found in the parent directory.%RESET%
    exit /b 1
)

rem Title banner
echo %CYAN%=======================================
echo        Gradle Tasks Runner
echo =======================================%RESET%

echo %YELLOW%Starting all checks and tests...%RESET%
echo.

set "tasks=spotlessApply dependencyGuardBaseline detekt testDemoDebug build updateProdReleaseBadging"
set "count=0"
for %%t in (%tasks%) do set /a count+=1

set "current=0"
for %%t in (%tasks%) do (
    set /a current+=1
    call :run_gradle_task "%%t"
    if !ERRORLEVEL! neq 0 goto :error
)

echo.
echo %GREEN%All checks and tests completed successfully.%RESET%
exit /b 0

:run_gradle_task
set "task=%~1"
set /a "percent=current*100/count"
echo %MAGENTA%[!percent!%%] Running:%RESET% %BLUE%%task%%RESET%
call "%~dp0gradlew" %task% --console=plain
if %ERRORLEVEL% neq 0 (
    echo %RED%Error: Task %task% failed%RESET%
    exit /b 1
)
echo %GREEN%Task completed successfully.%RESET%
echo.
exit /b 0

:error
echo.
echo %RED%An error occurred. Check the output above for details.%RESET%
exit /b 1