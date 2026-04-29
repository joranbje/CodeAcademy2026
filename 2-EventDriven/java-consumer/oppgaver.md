## Oppgaver
Målet med denne workshoppen er at du skal klare å lage og kjøre en docker-container som leser meldinger fra en fanout rabbitmq kø, samt
at du har forstått alle de forskjellige kø-typene RabbitMQ har å tilby. 
### Forarbeid
1. Sørg for at du får startet en lokal instans av rabbitmq i docker. Bruk docker-compose fila.
   Default så kan du kommunisere med rabbitmq på port 5672.
2. Gå til http://localhost:15672 og logg deg på med guest/guest. Lag deg en ny bruker dersom du har lyst til det(man bør unngå å bruke default user/pw)
3. Gjør deg litt kjent inne i admin-panelet. Hvor ser man connections? Exchanger? Køer?
4. Gjør deg kjent med innholdet i `RabbitMQConnectionHelper.java`
5. Gjør deg kjent med innholdet i `RabbitMQConfiguration.java`

### FANOUT
1. Lag deg et java-program 'PUBLISHER' som opprett en exchange av typen fanout. Skriv kode som med jevne mellomrom poster data til denne exchangen, f.eks hvert 5 sekund.
2. Ser du "deg selv" under connections?
3. Lag deg et nytt java-program 'SUBSCRIBER 1' som deklarerer og binder en kø til exchangen du lagde. Les fra køen og se at du mottar meldingene fra exchangen
4. Lag deg enda et nytt java program 'SUBSCRIBER 2' som deklarer og binder en ny kø til den samme exchangen. Sørg for at autoDelete er true. Denne skal motta nøyaktig de samme meldingene som den forrige køen.
5. Hva skjer når du stopper java-programmet 'SUBSCRIBER 2'?
6. Hva skjer når du starter java-programmet 'SUBSCRIBER 2' igjen?
7. Hva skjer hvis 'SUBSCRIBER 1' og 'SUBSCRIBER 2' lytter på samme kø?
7. Slett køene og exchangene dine.

### DIRECT
1. Lag deg en ny exchange i PUBLISHER av typen direct. Skriv kode som med jevne mellomrom poster data til denne exchangen, f.eks hvert 5 sekund.
2. Skriv om 'SUBSCRIBER 1' til å deklarerer og binder en kø til den nye exchangen. Bruk en fornuftig routingkey. Mottar du meldingene?
3. Skriv om 'SUBSCRIBER 2' til å deklarerer og binder en kø til den nye exchangen. Konfigurer med en annen routingKey. Mottar du meldingene? Hvorfor ikke?
4. Slett køene og exchangene dine.

### TOPIC
1. Lag deg en ny exchange i PUBLISHER av typen direct. Skriv kode som med jevne mellomrom poster data til denne exchangen, f.eks hvert 5 sekund.
   Post annen hver melding med routingkey = 'soprasteria' og routingkey = 'soprasteria.secret'.
2. Skriv om 'SUBSCRIBER 1' til å deklarerer og binder en kø til den nye exchangen. Bruk routingKey = 'soprasteria.applications'. Hvilke meldinger mottar du?
3. Skriv om 'SUBSCRIBER 2' til å deklarerer og binder en kø til den nye exchangen. Bruk routingKey = 'soprasteria.*'. Hvilke meldinger mottar du?
3. Skriv om 'SUBSCRIBER 3' til å deklarerer og binder en kø til den nye exchangen. Bruk routingKey = 'soprasteria.secret'. Hvilke meldinger mottar du?
4. Hvilken subscriber mottar meldingene hvis du endrer PUBLISHER til å poste en melding med routingKey = 'soprasteria.secret.unicorn' ?
5. Slett køene og exchangene dine.

### HEADER
1. Lag deg en ny exchange i PUBLISHER av typen header. Skriv kode som med jevne mellomrom poster data til denne exchangen, f.eks hvert 5 sekund.
   Post en melding med headere "type"="vehicle", "color":"red", en annen melding med headere "type"="vehicle", "color":"blue" og en tredje melding med headere "type"="bike", "color":"purple"
2. Skriv om 'SUBSCRIBER 1' til å deklarerer og binder en kø til den nye exchangen. Bind køen med header "type"="vehicle", "color":"red", "x-match":"all". Hvilke meldinger mottar du?
3. Skriv om 'SUBSCRIBER 2' til å deklarerer og binder en kø til den nye exchangen. Bind køen med header "type"="vehicle", "color":"purple", "x-match":"any". Hvilke meldinger mottar du?
4. Slett køene og exchangene dine.


### Ferdig dockerprodukt
1. Lag deg en `Dockerfile` som bygger hele applikasjonene.
2. Applikasjonen skal ha en main-metode som kobler seg på en fanout kø og parser disse meldingene.
3. Skriv ut meldingene til consollen
4. Lag også en publisher metode som publiserer meldinger med jevne mellomrom til exchangen.

### Hvis du trenger en utfordring
1. Lag deg en program som publiserer og consumerer mange hundre meldinger i sekundet
2. Øk prefetch counten til noe stort
3. Hvordan kan du sørge for disconnecte gracefully når man har satt en høy prefetch count? Hva skjer med meldingene som er prefetched, når consumeren f.eks feiler eller restarter? Er meldingene tapt? Hint: ShutDownSignal
4. Prøv å send en fil over rabbitmq. Hvordan får man til det?