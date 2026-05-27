# Workshop: Bygg et API for en Kaffebar "Contract-First"

Velkommen til workshop i API-design! I dag skal vi ikke starte med å skrive Spring Boot-kontrollere eller databasetabeller. Vi skal starte i den andre enden: **Kontrakten**.

Ved å jobbe *Contract-First* definerer vi API-ets grensesnitt og datamodeller i en OpenAPI-spesifikasjon (YAML) først. Dette fungerer som fasiten vår. Deretter lar vi byggeverktøyet (Maven) automatisk generere Java/Kotlin-kodemodeller og interface for oss.

Domenet for dagen er et **Kaffebar-system**. 

NB! Når dere åpner opp prosjektet i Intellij eller lignende så er det lurt om dere åpner det fra denne mappen. Enten ved å velge mappen eller pom.xml inne i mappen.

## Introduksjon og Utgangspunkt ("Hello Coffee")

For å komme i gang har vi allerede satt opp skjelettet for API-et vårt. Dette er den første kontrakten. Den definerer et endepunkt for å hente kaffemenyen, og en gjenbrukbar datamodell for en kaffedrikk.

Åpne filen `src/main/resources/api/kaffebar-api.yaml` i Starter Kit-et ditt for å se utgangspunktet.
```yaml
openapi: 3.0.3
info:
  title: Kaffebar API
  version: 1.0.0
  description: Et enkelt contract-first API for å administrere bestillinger i en kaffebar.

paths:
  /menu:
    get:
      summary: Hent kaffemeny
      description: Returnerer en liste over alle tilgjengelige kaffedrikker i kaffebaren.
      operationId: getMenu
      responses:
        '200':
          description: En liste med kaffedrikker ble vellykket hentet.
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Coffee'

components:
  schemas:
    Coffee:
      type: object
      description: Representerer en kaffedrikk på menyen.
      required:
        - id
        - name
        - price
      properties:
        id:
          type: string
          format: uuid
          example: "550e8400-e29b-41d4-a716-446655440000"
        name:
          type: string
          example: "Kaffe Latte"
        price:
          type: number
          format: double
          example: 48.50
```

*Når du bygger prosjektet (`mvn clean compile`), vil generatoren lese denne filen og opprette Java/Kotlin-modellen `Coffee` og API-grensesnittet `MenuApi` automatisk.*

Nå er det deres tur til å bygge videre på denne kontrakten!

---

## Del 1: Fundament og Mutasjoner

### Oppgave 1: Den første bestillingen (POST)
Vi må kunne bestille kaffe, ikke bare se på menyen!
* **Mål:** Introdusere mutasjoner og request-bodies.
* **Oppgave:** Lag et `POST /orders`-endepunkt i `paths`-seksjonen for å opprette en ny bestilling. Requesten skal ta inn et nytt objekt som inneholder en `coffeeId`. Responsen skal returnere den opprettede ordren (inkludert en autogenerert `orderId`).
* **Fokus:** Skille mellom request-objekt (det kunden sender inn) og response-objekt (det serveren svarer med).

### Oppgave 2: Bygg og implementer
Sjekk at kontrakten faktisk oversettes til fungerende kode.
* **Mål:** Knytte kontrakten til koden.
* **Oppgave:** Kjør byggeprosessen (`mvn clean compile`). Se på de genererte filene under `target/generated-sources/`. Lag deretter en kontrollerklasse i `src/main/java` som implementerer det nye ordre-grensesnittet.
* **Fokus:** Fikk metodene logiske navn? Hvordan ser modellene ut i koden?

---

## Del 2: Datatyper, Regler og Validering

### Oppgave 3: Typer, Enums og Valgfrihet
En bestilling trenger mer informasjon for at baristaen skal vite hva som skal lages.
* **Mål:** Gjøre kontrakten strengere og mer domenespesifikk.
* **Oppgave:** Utvid bestillingsobjektet. Bestillingen skal ha en størrelse (`SMALL`, `MEDIUM`, `LARGE`), en melketype, og et valgfritt (ikke-required) felt for `extraShot` (boolean).
* **Fokus:** Bruk av `enum` under egenskapene i OpenAPI. Definere nøye hvilke felt som ligger under `required`. Bygg prosjektet og sjekk hvordan dette oversettes til koden.

### Oppgave 4: Validering i kontrakten
La kontrakten ta seg av input-validering, slik at forretningslogikken slipper!
* **Mål:** Beskytte API-et mot dårlig data.
* **Oppgave:** Sett regler for bestillingen: Kunden må oppgi sitt `customerName`, og det kan ikke være blankt. Man kan bestille minimum 1 og maksimum 10 kaffe i samme ordre.
* **Fokus:** Bruk av `minimum`, `maximum`, `minLength` og `pattern` i YAML. Verifiser at Maven-pluginen genererer tilsvarende valideringsannotasjoner i koden (f.eks. `@Min`, `@Size`).

---

## Del 3: Tilstand og Feilhåndtering

### Oppgave 5: Hent en spesifikk bestilling (Path Parameters)
Kunden må kunne sjekke kvitteringen sin.
* **Mål:** Hente ut en eksisterende bestilling.
* **Oppgave:** Definer `GET /orders/{orderId}`. Endepunktet må ta inn `orderId` som et path-parameter.
* **Fokus:** Riktig bruk av `parameters` med `in: path` i YAML-spesifikasjonen.

### Oppgave 6: Standardisert Feilhåndtering
Hva skjer når noen prøver å hente en `orderId` som ikke finnes?
* **Mål:** Skape et forutsigbart API når ting går galt.
* **Oppgave:** Definer en `404 Not Found`-respons for `GET /orders/{orderId}`. For å gjøre API-et profesjonelt, lag et gjenbrukbart feil-objekt under `components/schemas` basert på standarden *RFC 7807 Problem Details* (felter som `type`, `title`, `status`, `detail`).
* **Fokus:** Bruk av `$ref` for å gjenbruke det samme feil-objektet på tvers av ulike endepunkter og HTTP-statuser (som 400 og 404).

### Oppgave 7: Baristaens oppdatering (PATCH vs PUT)
Ordren må endre tilstand mens baristaen jobber.
* **Mål:** Håndtere delvise oppdateringer.
* **Oppgave:** Baristaen må kunne markere en bestilling som under arbeid eller ferdig. Definer et endepunkt for å oppdatere status på ordren (f.eks. `PENDING` -> `BREWING` -> `READY`).
* **Fokus:** Diskusjon! Bør dette være en `PUT` (erstatt hele ordren) eller en `PATCH` (oppdater kun statusen)? Hvordan modellerer man et dedikert request-objekt som kun inneholder det som skal endres?

---

## Del 4: Avansert Bruk (Bonus/Ekspert)

### Oppgave 8: Filtrering og Paginering (Query Parameters)
Når kaffebaren blir populær, vokser listen med bestillinger.
* **Mål:** Håndtere store datamengder.
* **Oppgave:** Utvid deres eksisterende liste-endepunkt (`GET /orders`) slik at baristaen kan hente ut kun aktive bestillinger. Legg til query-parametere for `status`, `limit` og `offset`.
* **Fokus:** Hvordan definere default-verdier for query-parametere i OpenAPI (f.eks. default `limit` er 20).

### Oppgave 9: Polymorfisme (Ekspertnivå)
Kaffebaren begynner å selge bakst!
* **Mål:** Utvide domenet med sammensatte typer.
* **Oppgave:** En ordre kan nå bestå av *enten* en "Kaffedrikk" *eller* "Bakst" (som har helt andre attributter, f.eks. `isVegan` i stedet for `milkType`).
* **Fokus:** Bruk av `oneOf` eller `anyOf` i kontrakten. Sjekk koden: Hvordan løser kodegeneratoren dette? Blir det et felles interface med to implementasjoner? *(Advarsel: Dette krever ofte litt fikling med generatoren!)*
