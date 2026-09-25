# Il tuo salone

Consegna D5 – Epicode. Un mini salone di automobili con gli avvisi di prezzo: backend in **Spring Boot**, frontend in **React**, database **PostgreSQL**.

## Cosa fa

- Chi non ha fatto l'accesso sfoglia le auto pubblicate, con **ricerca** per marca o modello e **ordinamento** per prezzo, anno, chilometri, marca o data di inserimento.
- L'utente registrato aggiunge le auto ai **preferiti** e su ognuna può fissare una **soglia di prezzo**.
- L'**amministratore** vede anche le bozze e il prezzo d'acquisto, crea e modifica le auto e ne cambia il prezzo.
- Quando il nuovo prezzo scende alla soglia o sotto, all'utente parte **una mail**, una sola per avviso. Dal link nella mail l'utente può disattivare l'avviso.
- **Privacy Policy** e **Cookie Policy** sono raggiungibili da ogni pagina, e dal profilo si può **eliminare l'account**.

## Tecnologie

| Parte | Tecnologie |
|---|---|
| Backend | Java 21, Spring Boot 4, Spring Web MVC, Spring Data JPA (Hibernate), Spring Security, Jakarta Validation, JJWT, Spring Mail (Gmail SMTP), Spring Boot Actuator, Maven |
| Database | PostgreSQL |
| Frontend | React 19, Vite, React Router, CSS scritto a mano |
| Strumenti | IntelliJ IDEA, pgAdmin, Postman, Git e GitHub |

## Struttura della repository

```
├── salone/        backend Spring Boot
├── fe/            frontend React
├── docs/          screenshot delle prove
├── README.md      questo file
└── RELAZIONE.md   attacchi, difese e scelte
```

## Indirizzi

Il deploy su Render era una parte aggiuntiva e ho scelto di non farlo: il salone gira in locale.

- Sito: http://localhost:5173
- API: http://localhost:8080 (stato del servizio: http://localhost:8080/actuator/health)

## Avvio in locale

### Cosa serve

- JDK 21
- PostgreSQL con pgAdmin
- Node.js (versione LTS)
- Un account Gmail con la verifica in due passaggi attiva e una **password per le app** (la password normale di Gmail non funziona da un'applicazione)

### 1. Database

In pgAdmin creo un database vuoto chiamato `salone`. Le tabelle non le creo a mano: le genera Hibernate al primo avvio (`ddl-auto: update`).

### 2. Backend

1. Apro la cartella `salone` in IntelliJ.
2. In **Run → Edit Configurations → SaloneApplication → Environment variables** imposto le variabili della tabella qui sotto.
3. Avvio `SaloneApplication`. In console compare `Started SaloneApplication` e, al primo avvio, `Account amministratore creato`.

In alternativa, da terminale nella cartella `salone`, dopo aver impostato le stesse variabili d'ambiente: `mvnw.cmd spring-boot:run` su Windows oppure `./mvnw spring-boot:run` su Mac e Linux.

### 3. Frontend

```bash
cd fe
npm install
npm run dev
```

Poi apro http://localhost:5173. La porta è bloccata sulla 5173 in `vite.config.js`, perché il backend accetta richieste dal browser solo da quell'indirizzo (CORS).

## Variabili d'ambiente

Nessun valore segreto è scritto nel codice, nemmeno come valore di ripiego: chi avvia il progetto deve impostarle.

### Backend

| Variabile | Obbligatoria | Significato |
|---|---|---|
| `DB_PASSWORD` | sì | Password dell'utente `postgres` del database locale |
| `JWT_SECRET` | sì | Chiave segreta con cui il server firma i token di accesso. Almeno 32 caratteri casuali |
| `ADMIN_PASSWORD` | sì | Password dell'amministratore, creato in automatico al primo avvio |
| `MAIL_USERNAME` | sì | Indirizzo Gmail da cui partono gli avvisi |
| `MAIL_PASSWORD` | sì | Password per le app di quell'account Gmail (16 lettere) |
| `ALLOWED_ORIGIN` | no | Indirizzo esatto del frontend accettato dal CORS. Se manca vale `http://localhost:5173` |

### Frontend

| Variabile | Obbligatoria | Significato |
|---|---|---|
| `VITE_API_URL` | no | Indirizzo del backend. Se manca vale `http://localhost:8080` |

## Account amministratore

- Email: `admin@salone.it`
- Password: quella impostata in `ADMIN_PASSWORD`

Gli utenti normali si registrano dal sito: il ruolo lo decide sempre il server.

## API

| Metodo | Indirizzo | Chi può usarlo | A cosa serve |
|---|---|---|---|
| POST | `/api/auth/registrazione` | tutti | Crea un account utente |
| POST | `/api/auth/login` | tutti | Restituisce il token di accesso |
| GET | `/api/auto` | tutti | Catalogo, con `q`, `ordina` e `direzione` |
| GET | `/api/auto/{id}` | tutti | Dettaglio di un'auto pubblicata |
| POST | `/api/avvisi/disattiva` | tutti (con il token della mail) | Disattiva un avviso dal link della mail |
| GET | `/actuator/health` | tutti | Stato del servizio |
| GET / PUT / DELETE | `/api/profilo` | utente | Legge il profilo, cambia il nome, elimina l'account |
| GET / POST | `/api/preferiti` | utente | Elenco e aggiunta dei preferiti |
| DELETE | `/api/preferiti/{id}` | utente | Toglie un preferito (e il suo avviso) |
| GET / POST | `/api/avvisi` | utente | Elenco e creazione degli avvisi |
| GET / PUT / DELETE | `/api/avvisi/{id}` | utente | Legge, cambia la soglia o elimina un proprio avviso |
| GET / POST | `/api/admin/auto` | admin | Tutte le auto (bozze comprese) e creazione |
| GET / PUT | `/api/admin/auto/{id}` | admin | Dettaglio completo e modifica |
| PATCH | `/api/admin/auto/{id}/prezzo` | admin | Cambio prezzo: è l'unico punto che può far partire le mail |

## Le mie scelte principali

Le spiego nel dettaglio in [RELAZIONE.md](RELAZIONE.md). In breve:

- **Il client manda solo DTO.** Registrazione, profilo, auto e avvisi ricevono classi con i soli campi ammessi: se qualcuno aggiunge `ruolo`, `inviato` o `utenteId` alla richiesta, viene ignorato.
- **Il token contiene solo il mio id.** Il ruolo lo leggo dal database a ogni richiesta, così se un account viene eliminato il suo token smette subito di funzionare.
- **Le risorse degli altri non esistono.** Avvisi e preferiti li cerco per id e proprietario insieme: chi prova l'id di un altro riceve 404, come per un id inesistente.
- **La mail parte dopo il salvataggio e una volta sola.** Il cambio prezzo pubblica un evento, che ascolto dopo il commit in un altro thread; il segno "inviato" lo prendo con un solo `UPDATE ... WHERE inviato = false`.
- **Se Gmail non risponde, l'avviso resta inviato** e la mail è persa: preferisco questo a rischiare un doppione.
- **Un avviso si crea solo su un preferito e con una soglia sotto il prezzo attuale**, perché scatta quando il prezzo *attraversa* la soglia.
- **Niente testo trasformato in codice.** React mostra tutto come testo (non uso `dangerouslySetInnerHTML`) e nella mail faccio l'escape di ogni valore.
- **Il token sta nell'header, non in un cookie**, quindi un sito esterno non può usarlo al posto mio (CSRF).
- **Le immagini delle auto sono disegni SVG** che genero io, con un colore diverso per ogni modello: niente foto esterne, quindi niente problemi di copyright e nessuna richiesta verso altri siti.

## Privacy e cookie

Le due pagine sono in `fe/public/privacy.html` e `fe/public/cookie.html`, e sono collegate nel footer di ogni pagina. Le ho fatte generare e poi le ho corrette confrontandole campo per campo con le entità, così descrivono esattamente i dati che questa applicazione salva.

## Autore

Marco Cozma – Consegna D5, Epicode