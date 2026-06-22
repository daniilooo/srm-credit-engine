#!/bin/sh
set -e

DB_USER=$(cat /run/secrets/db_user)
DB_PASSWORD=$(cat /run/secrets/db_password)

exec java \
  -XX:MaxRAMPercentage=75.0 \
  -jar /app/app.jar \
  "--spring.datasource.username=${DB_USER}" \
  "--spring.datasource.password=${DB_PASSWORD}" \
  "--spring.datasource.url=${DB_URL:-jdbc:postgresql://postgres:5432/srm_credit_engine}"
