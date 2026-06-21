#!/usr/bin/env bash
set -euo pipefail

echo "Running backend build and unit tests with coverage..."

ROOT_DIR="$(git rev-parse --show-toplevel)"
cd "$ROOT_DIR/backend"

# Run mvn verify which includes tests and jacoco check
./mvnw clean verify

echo "Backend build, tests and coverage validation passed."

