#!/bin/bash

npx openapi-merge-cli --config ./openapi-merge.json
#&& openapi-generator generate -i ./src/main/resources/api/api.yaml -g spring -o ./tmp
