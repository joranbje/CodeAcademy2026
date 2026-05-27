# Workshop: Bygg et REST API for en Kaffebar i .NET

Velkommen til workshop i API-design med **.NET 10**! I dag skal vi bygge et REST API for en kaffebar – og samtidig utforske hva som er "best practice" i .NET-verdenen.

I motsetning til Java-økosystemet, hvor *Contract-First* (OpenAPI YAML → generert kode) er svært utbredt, er **Code-First** standarden i .NET. Vi skriver C#-kode, og rammeverket genererer OpenAPI-spesifikasjonen for oss. Vi skal også se på to ulike måter å definere endepunkter på i .NET:

- **Minimal APIs** – lettvekt, funksjonell stil, perfekt for mikrotjenester og små API-er.
- **Controllers (MVC)** – klassisk OO-stil med attributter, godt egnet for større API-er med mye gjenbruk.

Vi bruker også **Scalar** for å få et flott interaktivt UI for å utforske og teste API-et.

Domenet for dagen er et **Kaffebar-system**.

---

## Verktøy og oppsett

Vi kommer til å bruke:

- **.NET 10 SDK** (`dotnet --version` bør gi 10.x)
- **Microsoft.AspNetCore.OpenApi** – innebygd OpenAPI-generering i .NET 10
- **Scalar.AspNetCore** – moderne UI for å utforske og teste API-et
- **.http-filer** i VS Code – enkel måte å teste endepunkter direkte fra editoren (alternativ til Postman)


### Forarbeid: Sett opp prosjektet

Starter-koden ligger allerede klar i `Kaffebar/`-mappen. Den inneholder et minimal API med ett endepunkt (`GET /menu`), OpenAPI-generering og Scalar – alt aktivert ut av boksen.

```bash
cd Kaffebar
dotnet run
```

API-et starter på `http://localhost:5035`.

Åpne `Kaffebar/Program.cs` og se hva som er der.

Når prosjektet kjører:

1. Åpne `http://localhost:5035/openapi/v1.json` i nettleseren. Dette er den **maskinlesbare kontrakten** som .NET har generert automatisk basert på koden din. Bli kjent med strukturen – kjenner dere igjen `paths`, `components/schemas` osv. fra OpenAPI-standarden?
2. Åpne deretter `http://localhost:5035/scalar/v1`. Her får dere et interaktivt UI hvor dere kan utforske og teste alle endepunkter underveis i workshopen.

> 💡 **Tips:** Filen `Kaffebar/Kaffebar.http` ligger klar i prosjektet. VS Code (med C# Dev Kit) lar deg sende HTTP-requests direkte fra editoren – kjapt og versjonskontrollerbart. Legg gjerne til nye requests her etter hvert som dere bygger flere endepunkter.

For å bruke .http-filer må du sørge for at følgende extensions er installert i VS Code:

- **C# Dev Kit** (`ms-dotnettools.csdevkit`) – C#-språkstøtte, debugging og testing.
- **REST Client** (`humao.rest-client`) – gir `Send Request`-knapp i `.http`-filer.

  > 💡 C# Dev Kit gir *ikke* `.http`-støtte i VS Code (kun i Visual Studio på Windows). Du må installere REST Client separat:
  >
  > ```bash
  > code --install-extension humao.rest-client
  > ```

Nå er det deres tur til å bygge videre!

---

## Del 1: Fundament og Mutasjoner

### Oppgave 1: Den første bestillingen (POST)
Vi må kunne bestille kaffe, ikke bare se på menyen!
* **Mål:** Introdusere mutasjoner og request-bodies.
* **Oppgave:** Lag et `POST /orders`-endepunkt som tar imot et request-objekt med en `CoffeeId`. Responsen skal returnere den opprettede ordren, inkludert en autogenerert `OrderId`.
* **Fokus:** Skille klart mellom **request-DTO** (det klienten sender inn) og **response-DTO** (det serveren svarer med). Bruk `record`-typer – de er idiomatiske for DTO-er i moderne .NET.
* **Tips:** Returner `TypedResults.Created(...)` for å få korrekt `201 Created`-status og `Location`-header.

### Oppgave 2: Fra Minimal API til Controller
Nå skal vi se hvordan det samme API-et ser ut med klassiske controllers.
* **Mål:** Forstå forskjellen på de to stilene og når man velger hva.
* **Oppgave:**
  1. Legg til `builder.Services.AddControllers();` og `app.MapControllers();` i `Program.cs`.
  2. Lag en mappe for `Controllers` og `Models`, her skal kontrollerne og modellene leve.
  3. Lag en `OrdersController : ControllerBase` med `[ApiController]` og `[Route("orders")]` i `Contollers`-mappa.
  4. Flytt `POST /orders` over til controlleren. La `GET /menu` ligge igjen som Minimal API.
* **Fokus:** Diskusjon! Hva er fordelene med hver tilnærming? Hvor er valideringen og OpenAPI-metadataene tydeligst? Sjekk Scalar-UI-et – ser endepunktene like ut uavhengig av stil?

---

## Del 2: Datatyper, Regler og Validering

### Oppgave 3: Enums og valgfrie felt
En bestilling trenger mer informasjon for at baristaen skal vite hva som skal lages.
* **Mål:** Gjøre kontrakten strengere og mer domenespesifikk.
* **Oppgave:** Utvid request-objektet for `POST /orders`. Bestillingen skal ha:
  - `Size` (`SMALL`, `MEDIUM`, `LARGE`) – bruk en C# `enum`.
  - `MilkType` – f.eks. `WHOLE`, `SKIMMED`, `OAT`, `SOY`.
  - `ExtraShot` – et **valgfritt** felt (`bool?`).
* **Fokus:**
  - Som default serialiseres enums som tall i JSON. Konfigurer `JsonStringEnumConverter` slik at de blir lesbare strenger – og se hvordan dette gjenspeiles i `/openapi/v1.json`.
  - Nullable reference types (`string?`, `bool?`) styrer hva som blir markert som `required` i OpenAPI. Eksperimenter!

### Oppgave 4: Validering med Data Annotations
La rammeverket ta seg av input-validering, slik at forretningslogikken slipper!
* **Mål:** Beskytte API-et mot dårlig data – deklarativt.
* **Oppgave:** Sett regler for bestillingen:
  - `CustomerName`: påkrevd, ikke blank, mellom 2 og 50 tegn.
  - `Quantity`: minimum 1, maksimum 10.
* **Fokus:**
  - Bruk attributter fra `System.ComponentModel.DataAnnotations`: `[Required]`, `[StringLength]`, `[Range]`, `[RegularExpression]`.
  - I controllers slår valideringen inn automatisk via `[ApiController]`. For Minimal APIs i .NET 10, legg til `builder.Services.AddValidation();` og `.WithValidation()` på endepunktet (eller bruk `FluentValidation`).
  - Verifiser at reglene dukker opp i `/openapi/v1.json` og i Scalar – og at en ugyldig request returnerer **`400 Bad Request`** med en `ValidationProblemDetails`-respons.

---

## Del 3: Tilstand og Feilhåndtering

### Oppgave 5: Hent en spesifikk bestilling (Route Parameters)
Kunden må kunne sjekke kvitteringen sin.
* **Mål:** Hente ut en eksisterende bestilling via id.
* **Oppgave:** Definer `GET /orders/{orderId:guid}`. Bruk `Guid` som type for `orderId`.
* **Fokus:** Route-constraints i .NET (`:guid`, `:int`, `:minlength(3)`) – hvordan vises de i OpenAPI? Hva skjer hvis kallet inneholder en ugyldig Guid?

### Oppgave 6: Standardisert feilhåndtering
Hva skjer når noen prøver å hente en `orderId` som ikke finnes?
* **Mål:** Skape et forutsigbart API når ting går galt.
* **Oppgave:**
  1. Returner `TypedResults.NotFound()` / `Results.Problem(...)` når ordren ikke finnes.
  2. Legg til `builder.Services.AddProblemDetails();` og `app.UseStatusCodePages();` for å få konsistente feilrespons-objekter overalt.
* **Fokus:**
  - .NET har **innebygd støtte for RFC 7807 Problem Details** – du trenger ikke å definere `Problem`-skjemaet selv. Sjekk hvordan `application/problem+json` dukker opp i `/openapi/v1.json` og i Scalar.
  - Bruk `.Produces<ProblemDetails>(StatusCodes.Status404NotFound)` (eller `[ProducesResponseType]` i controllers) for å dokumentere feilresponsene.

### Oppgave 7: Baristaens oppdatering (PATCH vs PUT)
Ordren må endre tilstand mens baristaen jobber.
* **Mål:** Håndtere delvise oppdateringer.
* **Oppgave:** Baristaen må kunne markere en bestilling som under arbeid eller ferdig (`PENDING` → `BREWING` → `READY`).
* **Fokus:** Diskusjon!
  - Bør dette være `PUT /orders/{id}` (erstatt hele ordren), `PATCH /orders/{id}` (delvis oppdatering), eller et eget action-endepunkt som `POST /orders/{id}/status`?
  - Modeller et dedikert request-objekt (f.eks. `UpdateOrderStatusRequest`) som kun inneholder det som skal endres. Hvorfor er dette bedre enn å gjenbruke `Order`-typen?

---

## Del 4: Avansert Bruk (Bonus/Ekspert)

### Oppgave 8: Filtrering og paginering (Query Parameters)
Når kaffebaren blir populær, vokser listen med bestillinger.
* **Mål:** Håndtere store datamengder.
* **Oppgave:** Lag `GET /orders` slik at baristaen kan filtrere:
  - `status` (valgfri) – kun bestillinger med gitt status.
  - `limit` (default 20, max 100).
  - `offset` (default 0).
* **Fokus:**
  - Bruk `[AsParameters]` med en `record OrderQuery(OrderStatus? Status, int Limit = 20, int Offset = 0)` for å binde flere query-parametere ryddig.
  - Sjekk at default-verdiene dukker opp i OpenAPI-spesifikasjonen.

### Oppgave 9: Polymorfisme (Ekspertnivå)
Kaffebaren begynner å selge bakst!
* **Mål:** Utvide domenet med sammensatte typer.
* **Oppgave:** En ordrelinje kan nå være *enten* en `Coffee` (med `MilkType`) *eller* en `Pastry` (med `IsVegan`). Modeller dette som en base-type `OrderItem` med to subtyper.
* **Fokus:**
  - Bruk `[JsonPolymorphic(TypeDiscriminatorPropertyName = "type")]` og `[JsonDerivedType(typeof(Coffee), "coffee")]` / `[JsonDerivedType(typeof(Pastry), "pastry")]`.
  - Hvordan oversettes dette i `/openapi/v1.json`? Får du `oneOf` med en discriminator? Test både serialisering og deserialisering i Scalar.

### Oppgave 10: Autentisering med Microsoft Entra ID (Ekspertnivå)
Kaffebar-personalet skal ikke kunne endre ordrestatus uten å være innlogget!
* **Mål:** Sikre utvalgte endepunkter med JWT-basert autentisering mot Microsoft Entra ID.
* **Oppgave:**
  1. Registrer en app-registrering i Entra ID (eller bruk en eksisterende test-tenant). Noter `TenantId`, `ClientId` og en `Audience` (App ID URI / scope).
  2. Legg til NuGet-pakken `Microsoft.Identity.Web` i prosjektet:
     ```bash
     dotnet add package Microsoft.Identity.Web
     ```
  3. Konfigurer autentisering i `Program.cs`:
     ```csharp
     builder.Services
         .AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
         .AddMicrosoftIdentityWebApi(builder.Configuration.GetSection("AzureAd"));

     builder.Services.AddAuthorization();

     // ...
     app.UseAuthentication();
     app.UseAuthorization();
     ```
  4. Legg til en `AzureAd`-seksjon i `appsettings.json`:
     ```json
     "AzureAd": {
       "Instance": "https://login.microsoftonline.com/",
       "TenantId": "<din-tenant-id>",
       "ClientId": "<din-client-id>",
       "Audience": "api://<din-client-id>"
     }
     ```
  5. Beskytt status-endepunktet fra Oppgave 7:
     - I controlleren: `[Authorize]` på `OrdersController` eller på den spesifikke action-metoden.
     - I Minimal API: `.RequireAuthorization()` på endepunktet.
  6. La `GET /menu` forbli åpen for alle – den skal være anonym.
* **Fokus:**
  - **Diskusjon:** Hvilke endepunkter bør være offentlige, og hvilke skal kreve innlogging? Tenk på rollene *kunde* vs. *barista*.
  - Konfigurer Scalar slik at det støtter Bearer-token: legg ved en gyldig access token i UI-et og test at `401 Unauthorized` returneres når token mangler eller er ugyldig.
  - Bonus: Bruk **scopes** eller **app roles** fra Entra ID til å skille mellom kunder og baristaer. F.eks. krev `Barista`-rollen for å oppdatere ordrestatus:
    ```csharp
    [Authorize(Roles = "Barista")]
    ```
  - Sjekk at OpenAPI-spesifikasjonen reflekterer sikkerhetskravene. Bruk en `IOpenApiDocumentTransformer` for å legge til `securitySchemes` (OAuth2 / Bearer) i `/openapi/v1.json` slik at Scalar viser et "Authorize"-felt.

> 💡 **Tips for testing uten full Entra-oppsett:** Bruk `dotnet user-jwts` for å generere lokale test-tokens som matcher konfigurasjonen din – nyttig hvis dere ikke vil involvere en ekte tenant under workshopen:
>
> ```bash
> dotnet user-jwts create --scope "api://kaffebar/access"
> ```

---

## Oppsummering

Etter denne workshopen har dere sett:

- **Code-first OpenAPI** med .NET 10 sin innebygde `Microsoft.AspNetCore.OpenApi`.
- Hvordan **Scalar** gir et moderne UI for å utforske API-et – uten å skrive ekstra kode.
- Forskjellen på **Minimal APIs** og **Controllers**, og når man velger hva.
- Hvordan **data annotations**, **nullable reference types** og **enums** styrer den genererte OpenAPI-kontrakten.
- Innebygd støtte for **RFC 7807 Problem Details**.
- Polymorfisme via `System.Text.Json` sine `JsonPolymorphic`-attributter.

Lykke til – og god kaffe! ☕
