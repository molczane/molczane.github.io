#!/bin/bash

# Set the source and destination directories
SOURCE_DIR="composeApp/build/dist/wasmJs/productionExecutable"
DEST_DIR="docs"

# Check if docs directory exists
if [ -d "$DEST_DIR" ]; then
    # Delete all files in the docs directory
    rm -rf "$DEST_DIR"/*
else
    # Create the docs directory if it doesn't exist
    mkdir -p "$DEST_DIR"
fi

# Copy all files from source to destination
cp -r "$SOURCE_DIR"/* "$DEST_DIR"

echo "Files copied from $SOURCE_DIR to $DEST_DIR successfully."
