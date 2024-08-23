#!/bin/bash

# Check if gradlew exists in the project
if [ ! -f "./gradlew" ]; then
    echo "Error: gradlew not found in the project."
    exit 1
fi

echo "Starting all checks and tests..."

run_gradle_task() {
    echo "Running: $1"
    "./gradlew" $1
    if [ $? -ne 0 ]; then
        echo "Error: Task $1 failed"
        exit 1
    fi
}

run_gradle_task "check -p build-logic"
run_gradle_task "spotlessApply --no-configuration-cache"
run_gradle_task "dependencyGuardBaseline"
run_gradle_task "detekt"
run_gradle_task "testDemoDebug"
run_gradle_task "lintProdRelease"
run_gradle_task "build"
run_gradle_task "updateProdReleaseBadging"

echo "All checks and tests completed successfully."
exit 0