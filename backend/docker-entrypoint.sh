#!/bin/bash
set -e

if [ -n "$USERS_DB_URL" ]; then
  JDBC_URL=$(echo "$USERS_DB_URL" | sed 's|^postgresql://|jdbc:postgresql://|' | sed 's|^postgres://|jdbc:postgresql://|')
  export SPRING_DATASOURCE_URL="$JDBC_URL"
fi

exec java -jar /app/app.jar