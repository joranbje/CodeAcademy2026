## Oppgaver
Målet med denne workshoppen er at du skal klare å lage og kjøre en docker-container som leser meldinger fra en fanout rabbitmq kø, samt at du har forstått alle de forskjellige kø-typene RabbitMQ har å tilby.

### Forarbeid
0. Sørg for å ha installert `.net10 SDK`
1. Sørg for at du får startet en lokal instans av rabbitmq i docker. Bruk docker-compose fila
   Default så kan du kommunisere med rabbitmq på port 5672
2. Gå til http://localhost:15672 og logg deg på med guest/guest. Lag deg en ny bruker dersom du har lyst til det (man bør unngå å bruke default user/pw)
3. Gjør deg litt kjent inne i admin-panelet. Hvor ser man connections? Exchanger? Køer?
4. Gjør deg kjent med innholdet i `Common`-prosjektet og hvordan `ConnectionHelper.cs` setter opp kobling til RabbitMQ med `appsettings.json`
5. Gjør deg kjent med innholdet i `Consumer` og `Producer` prosjektet. Det er her du kommer til å jobbe mest

### Arkitektur
`Consumer` og `Producer` er begge konsollapplikasjoner og kan spinnes opp hver for seg. Avhengig av om du bruker Visual Studio eller VS Code, så kan disse kjøres via terminalen eller ved å kjøre hvert enkelt prosjekt separat. MERK: Kan hende du må konfigurere dem til å kjøre samtidig selv, om du ønsker dette. 

`Infrastructure` er et prosjekt som kan hjelpe deg i gang med å lagre data i en postgres database. Dette er arbeid du kan begynne på om du blir ferdig med de andre oppgavene. 

### FANOUT
1. Utvid `Producer` til å opprette en `Fanout` exchange. Sørg for at meldinger publiseres til denne exchangen med et gitt intervall, feks 2 sekunder. `channel.ExchangeDeclareAsync`, `channel.QueueDeclareAsync` og `channel.BasicPublishAsync` kan komme hendig med her.
2. Sjekk RabbitMQ dashboardet og se at meldingene blir sendt. 
3. Utvid `Consumer` til å konsumere meldinger fra køen. Du må deklarere og binde konsumenten til en fanout exchangen. Her kan `channel.QueueDeclareAsync`, `channel.QueueBindAsync()`, `channel.BasicAckAsync` og `channel.BasicConsumeAsync` være nyttig. 
4. Lag deg enda et nytt C#-program `Consumer-2` som deklarerer og binder en ny kø til den samme exchangen. Sørg for at `autoDelete: true`. Denne skal motta nøyaktig de samme meldingene som den forrige køen.
5. Hva skjer når du stopper programmet `Consumer-2`?
6. Hva skjer når du starter programmet `Consumer-2` igjen?
7. Hva skjer hvis `Consumer` og `Consumer-2` lytter på samme kø?
8. Slett køene og exchangene dine.

### DIRECT
1. Lag deg en ny exchange i `Producer` av typen direct. Skriv kode som med jevne mellomrom poster data til denne exchangen, f.eks hvert 5 sekund.
2. Skriv om `Consumer` til å deklarere og binde en kø til den nye exchangen. Bruk en fornuftig routingkey. Mottar du meldingene?
3. Skriv om `Consumer-2` til å deklarere og binde en kø til den nye exchangen. Konfigurer med en annen routingKey. Mottar du meldingene? Hvorfor ikke?
4. Slett køene og exchangene dine.

### TOPIC
1. Lag deg en ny exchange i `Producer` av typen topic. Skriv kode som med jevne mellomrom poster data til denne exchangen, f.eks hvert 5 sekund.
   Post annen hver melding med routingKey = 'idem.public' og routingKey = 'idem.public.reply'.
2. Skriv om `Consumer` til å deklarere og binde en kø til den nye exchangen. Bruk routingKey = 'idem.private'. Hvilke meldinger mottar du?
3. Skriv om `Consumer-2` til å deklarere og binde en kø til den nye exchangen. Bruk routingKey = 'idem.*'. Hvilke meldinger mottar du?
4. Lage en `Consumer-3` som deklarere og binde en kø til den nye exchangen. Bruk routingKey = 'idem.public.reply'. Hvilke meldinger mottar du?
5. Hvilken subscriber mottar meldingene hvis du endrer `Producer` til å poste en melding med routingKey = 'idem.public.reply.mention' ?
6. Slett køene og exchangene dine.

### HEADER
1. Lag deg en ny exchange i `Producer` av typen header. Skriv kode som med jevne mellomrom poster data til denne exchangen, f.eks hvert 5 sekund.
   Post en melding med headere "author"="alice", "visibility":"public", en annen melding med headere "author"="alice", "visibility":"private" og en tredje melding med headere "author"="bob", "visibility":"draft". Bruk `BasicProperties` for å sette headere.
2. Skriv om `Consumer` til å deklarere og binde en kø til den nye exchangen. Bind køen med header "author"="alice", "visibility":"public", "x-match":"all". Hvilke meldinger mottar du?
3. Skriv om `Consumer-2` til å deklarere og binde en kø til den nye exchangen. Bind køen med header "author"="alice", "visibility":"draft", "x-match":"any". Hvilke meldinger mottar du?
4. Slett køene og exchangene dine.

### Ferdig dockerprodukt
1. Utvid `Dockerfile` til å bygge og kjøre konsollapplikasjonene
2. Applikasjonen skal ha en `Main`-metode som kobler seg på en fanout kø og parser disse meldingene.
3. Skriv ut meldingene til konsollen.
4. Lag også en `Producer`-metode som publiserer meldinger med jevne mellomrom til exchangen.

### Hvis du trenger en utfordring
1. Lag deg et program som publiserer og consumerer mange hundre meldinger i sekundet.
2. Øk prefetch counten til noe stort med `channel.BasicQosAsync()`.
3. Hvordan kan du sørge for å disconnecte gracefully når man har satt en høy prefetch count? Hva skjer med meldingene som er prefetched, når consumeren f.eks feiler eller restarter? Er meldingene tapt? Hint: `CancelKeyPress` / `AppDomain.CurrentDomain.ProcessExit`.
4. Prøv å send en fil over rabbitmq. Hvordan får man til det?
