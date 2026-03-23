# Idempotweet development commands
# Usage: just <command>
# Install just: brew install just (macOS) / https://just.systems
# Kom i gang: just setup && just install && just dev

set dotenv-load
set windows-shell := ["powershell.exe", "-NoLogo", "-Command"]

app_dir := "1-DevOps/idempotweet"
compose := env("COMPOSE", "docker compose")
db_url := env("DATABASE_URL", "postgresql://codeacademy:codeacademy@localhost:5432/codeacademy")

# List available commands
default:
    @just --list

# Set up project: create .env, configure GitHub repo, install dependencies
[unix]
setup:
    #!/usr/bin/env bash
    set -euo pipefail
    if [ ! -f .env ]; then
        cp .env.example .env
        echo "Opprettet .env fra .env.example"
    else
        echo ".env finnes allerede."
    fi
    # Spør om GitHub-repo hvis det ikke er satt
    current=$(grep '^GITHUB_REPOSITORY=' .env | cut -d= -f2)
    if [ -z "$current" ]; then
        read -rp "Hva er ditt GitHub-repo? (f.eks. mittbrukernavn/CodeAcademy2026): " repo
        sed -i'' -e "s|^GITHUB_REPOSITORY=.*|GITHUB_REPOSITORY=$repo|" .env
        echo "GITHUB_REPOSITORY satt til $repo"
    fi
    just install

[windows]
setup:
    #!powershell
    $ErrorActionPreference = "Stop"
    if (!(Test-Path .env)) { Copy-Item .env.example .env; Write-Host "Opprettet .env fra .env.example" } else { Write-Host ".env finnes allerede." }
    $current = (Select-String -Path .env -Pattern '^GITHUB_REPOSITORY=(.+)$' | ForEach-Object { $_.Matches.Groups[1].Value })
    if (!$current) { $repo = Read-Host "Hva er ditt GitHub-repo? (f.eks. mittbrukernavn/CodeAcademy2026)"; (Get-Content .env) -replace '^GITHUB_REPOSITORY=.*', "GITHUB_REPOSITORY=$repo" | Set-Content .env; Write-Host "GITHUB_REPOSITORY satt til $repo" }
    just install

# Switch to podman
[unix]
use-podman:
    @sed -i'' -e 's|^COMPOSE=.*|COMPOSE="podman compose"|' .env
    @echo "Byttet til podman. Alle kommandoer bruker nå 'podman compose'."

[windows]
use-podman:
    @(Get-Content .env) -replace '^COMPOSE=.*', 'COMPOSE="podman compose"' | Set-Content .env
    @Write-Host "Byttet til podman. Alle kommandoer bruker nå 'podman compose'."

# Switch to docker
[unix]
use-docker:
    @sed -i'' -e 's|^COMPOSE=.*|COMPOSE="docker compose"|' .env
    @echo "Byttet til docker. Alle kommandoer bruker nå 'docker compose'."

[windows]
use-docker:
    @(Get-Content .env) -replace '^COMPOSE=.*', 'COMPOSE="docker compose"' | Set-Content .env
    @Write-Host "Byttet til docker. Alle kommandoer bruker nå 'docker compose'."

# Start postgres and the dev server
[unix]
dev: postgres
    cd {{app_dir}} && DATABASE_URL={{db_url}} NEXT_PUBLIC_ENABLE_IDEM_FORM={{env("NEXT_PUBLIC_ENABLE_IDEM_FORM", "true")}} yarn dev

[windows]
dev: postgres
    $env:DATABASE_URL="{{db_url}}"; $env:NEXT_PUBLIC_ENABLE_IDEM_FORM="{{env("NEXT_PUBLIC_ENABLE_IDEM_FORM", "true")}}"; cd {{app_dir}}; yarn dev

# Start only postgres in the background
postgres:
    {{compose}} -f docker-compose.dev.yml up -d postgres
    @just _wait-postgres

# Poll until postgres is ready (max 30s)
[unix]
_wait-postgres:
    #!/usr/bin/env bash
    for i in $(seq 1 30); do
        {{compose}} -f docker-compose.dev.yml exec -T postgres pg_isready -U codeacademy -q 2>/dev/null && exit 0
        sleep 1
    done
    echo "Postgres did not become ready in time" && exit 1

[windows]
_wait-postgres:
    for ($i = 0; $i -lt 30; $i++) { \
        $out = ({{compose}} -f docker-compose.dev.yml exec -T postgres pg_isready -U codeacademy 2>&1); \
        if ($LASTEXITCODE -eq 0) { Write-Host "Postgres is ready."; exit 0 } \
        Start-Sleep -Seconds 1 \
    }; Write-Host "Postgres did not become ready in time"; exit 1

# Install dependencies
[unix]
install:
    cd {{app_dir}} && corepack enable && yarn install

[windows]
install:
    cd {{app_dir}}; corepack enable; yarn install

# Run tests
[unix]
test:
    cd {{app_dir}} && yarn test

[windows]
test:
    cd {{app_dir}}; yarn test

# Run tests in watch mode
[unix]
test-watch:
    cd {{app_dir}} && yarn test:watch

[windows]
test-watch:
    cd {{app_dir}}; yarn test:watch

# Build the application
[unix]
build:
    cd {{app_dir}} && yarn build

[windows]
build:
    cd {{app_dir}}; yarn build

# Seed the database with demo data
[unix]
seed: postgres
    cd {{app_dir}} && DATABASE_URL={{db_url}} yarn seed

[windows]
seed: postgres
    $env:DATABASE_URL="{{db_url}}"; cd {{app_dir}}; yarn seed

# Truncate all data in the database
truncate: postgres
    {{compose}} -f docker-compose.dev.yml exec -T postgres psql -U codeacademy -c "TRUNCATE TABLE idems;"
    @echo "Databasen er tømt."

# Stop all running services
stop:
    {{compose}} -f docker-compose.dev.yml down

# Stop and remove all data
clean:
    {{compose}} -f docker-compose.dev.yml down -v
