# Forberedelser til Samling 3: Contract-First API Design

Hei alle sammen, og velkommen til tredje samling! 

Denne gangen skal vi bygge et Kaffebar-API, og vi skal gjøre det "contract-first" (hvor vi designer OpenAPI-spesifikasjonen før vi skriver koden). For at vi skal komme raskest mulig i gang med den morsomme delen på selve dagen, er det supert om dere gjør noen små forberedelser.

## 🛠 Forutsetninger
Sørg for at du har riktig Java-versjon installert på maskinen din:
* **Java 21 Runtime Environment** (Vi har brukt [Eclipse Temurin 21](https://adoptium.net/) til dette oppsettet).

## 🚀 Slik gjør du deg klar til samlingen

**1. Oppdater GitHub-forken din**
Startkoden for denne samlingen er lagt til i hovedrepoet. Siden du forket dette i Samling 2, må du synkronisere din fork for å få den nye koden:
* Gå til din fork av repoet på GitHub.
* Klikk på **"Sync fork"** i dropdown-menyen rett over fillisten, og oppdater branchen din.
* *Tips: Hvis du får problemer med merge-konflikter eller synkronisering, er det aller enkleste å bare slette forken din og forke hovedrepoet på nytt.*

**2. Hent koden lokalt og bygg prosjektet**
Når forken er oppdatert på GitHub, puller du endringene ned til din lokale maskin (`git pull`).
For å verifisere at oppsettet ditt fungerer og at Maven laster ned nødvendige plugins for kodegenerering, kjører du følgende kommando i terminalen (fra rotmappen i prosjektet):

```bash
mvn clean install
```

*Går denne gjennom uten feil (Build Success), er maskinen din 100 % klar for workshop!*

**3. Åpne prosjektet i IDE (Viktig!)**
**NB!** Når dere åpner opp prosjektet i IntelliJ eller lignende, så er det lurt om dere åpner det fra selve prosjektmappen. Gå frem ved å enten velge prosjektmappen direkte, eller velg `pom.xml`-filen inne i mappen. Da skjønner IDE-en umiddelbart at dette er et Maven-prosjekt og setter opp alt riktig.

**4. Tyvtitt på oppgavene**
Ta gjerne en kikk i filen `oppgaver.md` som nå ligger i repoet. Her finner du en beskrivelse av kaffebar-domenet og oppgavene vi skal bryne oss på. Du trenger ikke begynne å kode noe ennå, men det er nyttig å skumlese for å få litt kontekst.

