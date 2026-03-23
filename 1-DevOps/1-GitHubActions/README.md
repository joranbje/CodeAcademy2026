# Workshop 1: GitHub Actions CI

I denne workshopen skal du **selv opprette** en CI-pipeline med GitHub Actions for Idempotweet-applikasjonen.

## Læringsmål
asdasd
- Skrive en GitHub Actions workflow fra bunnen av
- Trigge bygg automatisk på pull requests
- Bruke path filters for å begrense når workflows kjører
- Sette opp Node.js med corepack og Yarn v4 i CI
- Kjøre tester automatisk i en pipeline
- Integrere statisk kodeanalyse med SonarQube

## Oversikt

Du skal lage en CI-workflow som gjør følgende hver gang noen lager en pull request som endrer applikasjonskoden:

1. **Sjekker ut koden** fra repoet
2. **Setter opp Node.js** med riktig versjon og pakkehåndterer
3. **Installerer avhengigheter** med caching for raskere bygg
4. **Kjører tester** for å verifisere at ingenting er ødelagt
5. **Analyserer koden** med SonarQube for å finne bugs og sårbarheter

Filen du skal opprette: **`.github/workflows/ci.yml`**

Applikasjonskoden som bygges ligger i: **`1-DevOps/idempotweet/`**

## Forutsetninger

### just — snarveier for utviklingsmiljøet

Vi bruker [**just**](https://just.systems) for å samle nyttige kommandoer i prosjektet. I stedet for å huske lange kommandoer med miljøvariabler og stier, skriver du bare `just dev`, `just test`, osv. Alle snarveiene er definert i `justfile` i roten av repoet.

Installer med:

```bash
brew install just          # macOS
winget install Casey.Just  # Windows
```

### Kom i gang

```bash
just setup    # Kopierer .env.example → .env og installerer dependencies
```

> **Podman-brukere:** Kjør `just use-podman` én gang — da bruker alle kommandoer Podman fremover. Bytt tilbake med `just use-docker`.

### Tilgjengelige kommandoer

Kjør `just` uten argumenter for å se alle kommandoer. Her er oversikten:

| Kommando | Beskrivelse |
|----------|-------------|
| `just setup` | Oppretter `.env` fra `.env.example` og installerer dependencies. Kjør denne først. |
| `just dev` | Starter PostgreSQL og utviklingsserveren. Åpne http://localhost:3000 |
| `just test` | Kjører alle tester (vitest) |
| `just test-watch` | Kjører tester i watch-modus (re-kjører ved endringer) |
| `just build` | Bygger applikasjonen for produksjon |
| `just seed` | Fyller databasen med 200 demo-idems |
| `just truncate` | Tømmer alle idems fra databasen |
| `just stop` | Stopper PostgreSQL og andre tjenester |
| `just clean` | Stopper tjenester og sletter all data (inkl. database-volum) |
| `just use-podman` | Bytter til Podman (huskes i `.env`) |
| `just use-docker` | Bytter tilbake til Docker |

Konfigurasjon styres via `.env`-filen. Se `.env.example` for alle tilgjengelige variabler.

## Steg-for-steg

### 1. Utforsk applikasjonen

Se på `1-DevOps/idempotweet/package.json` for å forstå hvilke scripts som finnes:

- `yarn test` — kjører testene (vitest)
- `yarn build` — bygger applikasjonen (Next.js)
- `yarn dev` — starter utviklingsserver

### 2. Kjør testene lokalt

Før du setter opp CI, kjør testene lokalt for å se at alt fungerer:

```bash
just test
```

Du skal se at alle testene passerer. Det er disse testene CI-pipelinen din skal kjøre automatisk.

> **Tips:** Prøv også `just dev` for å starte appen lokalt med en database. Åpne http://localhost:3000 for å se den i aksjon.

### 3. Opprett workflow-filen

Lag filen `.github/workflows/ci.yml` i roten av repoet.

> **Tips:** GitHub Actions-workflows er YAML-filer som alltid ligger under `.github/workflows/`. Opprett mappene om de ikke finnes.

### 4. Definer navn og triggere

Start med å gi workflowen et navn, og definer **når** den skal kjøre.

Din workflow skal trigges på to måter:

- **`pull_request`** — når noen lager eller oppdaterer en PR
- **`workflow_call`** — slik at andre workflows kan gjenbruke denne (vi bruker dette i Workshop 2)

For `pull_request` skal du legge til et **path filter** slik at workflowen bare kjører når filer under `1-DevOps/idempotweet/**` endres.

> **Hint:** Strukturen ser omtrent slik ut:
> ```yaml
> name: ...
> on:
>   pull_request:
>     paths:
>       - ...
>   workflow_call:
> ```

### 5. Sett opp jobb og runner

Definer en `jobs`-seksjon med én jobb (f.eks. kalt `ci` eller `test`).

Runneren skal settes til: `${{ vars.RUNNER || 'self-hosted' }}`

> **Hint:** `vars.RUNNER` er en repository-variabel i GitHub. Uttrykket betyr: "bruk variabelen RUNNER hvis den finnes, ellers bruk `self-hosted`".

Siden applikasjonskoden ligger i en undermappe, trenger du å sette **working directory** for alle `run`-steg. Bruk `defaults.run.working-directory` på jobb-nivå for å slippe å gjenta stien i hvert steg.

> **Hint:**
> ```yaml
> defaults:
>   run:
>     working-directory: 1-DevOps/idempotweet
> ```

### 6. Legg til stegene

Nå skal du fylle inn `steps` i jobben din. Du trenger følgende steg, i rekkefølge:

#### a) Checkout

Bruk den offisielle `actions/checkout`-actionen for å sjekke ut koden.

#### b) Setup Node.js

Bruk `actions/setup-node` for å sette opp Node.js versjon **20**.

> **Tips:** Denne actionen har et `cache`-parameter som kan settes til `yarn` for automatisk caching av avhengigheter.

#### c) Aktiver corepack

Yarn v4 bruker corepack (en del av Node.js) for å håndtere pakkehåndtereren. Du må aktivere corepack med en `run`-kommando.

> **Hint:** Kommandoen er `corepack enable`.

> **OBS:** `corepack enable` må kjøre fra **repository-roten**, ikke fra working directory-en du satte i `defaults`. Overstyr med `working-directory: .` på dette steget.

#### d) Installer avhengigheter

Kjør installasjonskommandoen for å laste ned alle pakker.

> **Tips:** Med Yarn bruker vi `yarn install --immutable` i CI. Flagget `--immutable` sikrer at `yarn.lock` ikke endres — hvis noen har glemt å oppdatere lockfilen, feiler bygget.

#### e) Kjør tester

Kjør testene med riktig yarn-kommando.

#### f) Generer test coverage

For at SonarQube skal kunne vise dekningsgrad, må vi generere en coverage-rapport. Legg til et steg som kjører `yarn test:coverage`.

> **Tips:** Dette genererer en `coverage/lcov.info`-fil som SonarQube leser automatisk (konfigurert i `sonar-project.properties`).

#### g) SonarQube-analyse

Legg til et steg som kjører SonarQube-skanning med `SonarSource/sonarqube-scan-action@v6`.

Denne actionen trenger to miljøvariabler:
- `SONAR_TOKEN` — autentiseringstoken (lagret som GitHub Secret)
- `SONAR_HOST_URL` — URL til SonarQube-serveren (lagret som GitHub Variable)

```yaml
- name: SonarQube Scan
  uses: SonarSource/sonarqube-scan-action@v6
  with:
    projectBaseDir: 1-DevOps/idempotweet
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
    SONAR_HOST_URL: ${{ vars.SONAR_HOST_URL }}
```

> **OBS:** Før du kjører workflowen, må du sette opp SonarQube-tilgang:
>
> **1. Logg inn på SonarQube:**
> Gå til **https://sonarqube.thawi.io** og logg inn med e-postadressen din. Passord får du av kursleder.
>
> **2. Opprett en token:**
> Klikk på profilbildet ditt øverst til høyre → **My Account** → **Security** → **Generate Tokens**.
> Gi tokenet et navn (f.eks. `github-actions`), velg type **Project Analysis Token**, og klikk **Generate**. Kopier tokenet — du får bare se det én gang.
>
> **3. Legg til i GitHub:**
> Gå til GitHub-repoet ditt → **Settings** → **Secrets and variables** → **Actions**:
> - Under **Secrets**: Opprett `SONAR_TOKEN` med tokenet du kopierte
> - Under **Variables**: Opprett `SONAR_HOST_URL` med verdien `https://sonarqube.thawi.io`

Etter at workflowen har kjørt, kan du logge inn på SonarQube og finne prosjektet ditt der. SonarQube viser:
- Bugs og sårbarheter i koden
- Code smells (vedlikeholdsproblemer)
- Testdekningsgrad (coverage)
- Duplisert kode

### 7. Commit og push

```bash
git checkout -b ci-workshop
git add .github/workflows/ci.yml
git commit -m "Legg til CI-workflow"
git push -u origin ci-workshop
```

### 8. Lag en Pull Request

Gå til GitHub og lag en Pull Request fra `ci-workshop` til `main`.

Siden du endrer filer som matcher path-filteret ditt, skal workflowen starte automatisk.

### 9. Verifiser at CI kjører

Gå til **Actions**-fanen i GitHub-repoet. Du skal se at CI-workflowen starter. Klikk inn på den for å se:

- Hvilke steg som kjører
- Loggene for hvert steg
- Om testene passerer (grønn hake) eller feiler (rødt kryss)

> **Feilsøking:** Hvis workflowen ikke starter, sjekk at path-filteret matcher filene i PRen din. Hvis steg feiler, les feilmeldingen nøye — ofte er det en skrivefeil i YAML-filen.

### 10. (Valgfritt) Introduser en feil

Prøv å ødelegge en test med vilje, push endringen, og se at CI feiler. Dette viser verdien av automatisert testing — feil fanges opp før koden merges.

## Nøkkelbegreper

| Begrep | Forklaring |
|--------|-----------|
| **CI (Continuous Integration)** | Automatisk bygging og testing av kode ved hver endring |
| **Workflow** | En automatisert prosess definert i en YAML-fil under `.github/workflows/` |
| **Job** | En samling steg som kjører på samme maskin (runner) |
| **Step** | En enkelt oppgave i en job (f.eks. "kjør tester") |
| **Runner** | Maskinen som utfører jobben — kan være GitHub-hostet eller self-hosted |
| **Path filter** | Begrenser når en workflow kjører basert på hvilke filer som endres |
| **workflow_call** | Lar en workflow gjenbrukes av andre workflows |
| **corepack** | Node.js-verktøy for å håndtere pakkehåndterere som Yarn |
| **`--immutable`** | Yarn-flagg som sikrer at lockfilen ikke endres under installasjon |
| **defaults.run** | Setter standardverdier (f.eks. working directory) for alle `run`-steg i en jobb |
| **SonarQube** | Verktøy for statisk kodeanalyse — finner bugs, sårbarheter og code smells |
| **Test coverage** | Hvor stor andel av koden som dekkes av tester (målt i prosent) |
| **GitHub Secrets** | Krypterte verdier (f.eks. tokens) som er tilgjengelige i workflows via `secrets.*` |
