#!/bin/bash

# Build the WASM browser distribution
./gradlew wasmJsBrowserDistribution

# Execute the copy-to-docs.sh script
./copy-to-docs.sh

# Stage all changes
git add .

# Commit changes with a message
git commit -m "Trying to trigger handler after OAuth success."

# Push changes to the repository
git push origin main
