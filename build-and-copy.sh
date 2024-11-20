#!/bin/bash

# Build the WASM browser distribution
./gradlew wasmJsBrowserDistribution

# Execute the copy-to-docs.sh script
./copy-to-docs.sh

# Stage all changes
git add .

# Commit changes with a message
git commit -m "Changed login post body."

# Push changes to the repository
git push origin main
