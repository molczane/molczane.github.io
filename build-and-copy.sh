#!/bin/bash

./gradlew wasmJsBrowserDistribution
./copy-to-docs.sh