==============================================
Testpraksis hugget i marmor (diskussionsoplæg)
==============================================
kan ikke ændres medmindre du vil.

Unittest
--------
Hvad er en unittest?
--------------------
I en unittest afprøves en klasses metoder med det formål at se om den opfører sig som forventet givet et kontrolleret miljø. En unittest afprøver om en metode givet input A giver output B, det er med andre ord metodens adfærd der afprøves. Dette skal gøres ud fra de klasser af input man kan give den. Dette er validt data og data der ikke er validt, såsom et xml der ikke overholder standarden. Disse to klasser indeholder forskellige muligheder alt efter hvilke data typer en metode tager. For at sikre samme resultat hver gang en unittest køres skal der ikke være afhængigheder til objekter der kan give varierende resultat. Dvs en driftsdatabase eller en webservice kan ikke bruges. Det man gør når der er eksterne afhængigheder er derfor at "mocke" disse. Et mock-objekt eller en mock-klasse efterligner en reel objekts/klasses adfærd men har begrænset funktionalitet til lige præcis det testen skal bruge. 

System under test (SUT)
----------------------- 
Ved en unittest skal man gøre sig det klart hvad man tester og beskrive det i kommentarer i testen. Som udgangspunkt vil det altid være en metode. Der til kan komme interne metoder i klassen og metoder på objekter klassen får som parameter eller selv opretter. 

Jeg finder det svært at sige hvor det er bedst ™  at lægge snittet så man kun har det rigtige i sin SUT, men eksterne afhængigheder er aldrig en del af SUT for unittests. 

Hvad skal testes?
   * Happy paths for at se om klassens metoder kan fungere korrekt
   * metodernes håndtering af ikke validt data
   * Exceptionelle tilstande forårsaget af objekter metoder kalder.

Hvad er højrisiko kode/hvad er den vigtige kode?

hvad skal ikke testes?
   * Den interne logik i en klasses metoder.
   * Tilstande der kun opstår i et driftsmiljø

Brug af mockobjekter, mockklasser og reelle klasser
---------------------------------------------------
Jeg forsøger at vurdere ud fra overskuelighed > robusthed > kodehastighed.

Mock af objekter eller oprettelse af objekter med data metoden under test skal bruge.

Reele objekter:
   * ved ændring af brug af objektet er det stadig brugbart hvis data i det er validt, dvs der kommer ikke en falsk failure.
   * Der skal ikke specificeres den eksakte brug af objektet, hvis der kaldes metoder på det

Mockobjekter:
   * Hvis ikke objektets metoder kaldes er der mindst muligt arbejde
   * Hvis der er ændringer i kaldet af metoder på objektet skal der ændres i testen

Min holdning: Hvis et objekt ikke tilgås i unittesten er det mest overskueligt at mocke det (det er fx en parameter til en anden klasse metoden opretter og bruger). Hvis der skal kaldes metoder på objektet er det mest overskuelige og robuste at oprette det med reel data.

Eksterne afhængigheder mockes med mockobjekter hvis deres adfærd og brugen af dem er enkel, ellers bruges mockklasser. 

Reviewpraksis
-------------
Når en unittest er skrevet skal skribenten oprette en bug på at der skal fortages et review af en anden person. For at sikre at unittesten bliver så overskuelig som muligt skal skribenten skrive dokumentation på det revieweren har af spørgsmål ikke blot forklare det verbalt. Så reviewet skal fortages i minimalt samarbejde med skribenten.


Funktionelle test
-----------------
Hvad er en funktionel test
Disse tjener til at vise at systemet kan løse en givet opgave fx lave anmeldelses relationer mellem en anmeldelse og en eller flere manifestationer af det den anmelder.
Vi skal helst have automatiseret de funktionelle tests af vores system, og disse burde være baseret på krav til systemet.
Vi skal med andre ord have nogle specifikke krav vi kan teste for om opfyldes.

Regressionstest
---------------
Formål: At vise at ændringer i koden ikke har påvirket funktionaliteten af systemet. 
Metode: Kør programmet i ændret og uændret tilstand på samme data og sammenlign data. Dette er enten automatiseret eller gennem personer der verificerer at systemet stadig opfører sig som det skal ifht en check liste eller nogle foruddefinerede brugsscenarier
