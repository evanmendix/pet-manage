@echo off
REM This script starts the database container for development.
REM It navigates to the project root to ensure it can find the docker-compose.yml file.

echo "Navigating to project root and starting database container..."

REM Change directory to the parent of the current script's directory.
cd /d "%~dp0..\"

REM Start the database service in detached mode.
REM This command is idempotent: it will create the container if it doesn't exist,
REM start it if it's stopped, and do nothing if it's already running.
docker compose up -d db

echo "Database container is starting or already running. You can check its status with 'docker compose ps'."
pause
