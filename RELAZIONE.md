# Relazione – Il tuo salone

In questa relazione spiego come ho difeso il salone dagli attacchi, le scelte che ho fatto sugli avvisi di prezzo, tre attacchi che ho provato davvero contro il mio salone e il limite, cioè quello che non ho protetto.

## 1. Attacchi e difese

`salone/...` sta per `salone/src/main/java/com/epicode/salone`. I numeri di riga si riferiscono alla versione consegnata.

| # | Attacco | Difesa | File | Riga |
|---|---|---|---|---|
| 1 | SQL injection nella ricerca (`q=' OR '1'='1`) | Il testo è un parametro legato (`:testo`), mai concatenato nella query | `salone/.../repository/AutoRepository.java` | 18 |
| 2 | SQL injection nell'ordinamento (`ordina=prezzo; DROP TABLE auto`) | Il campo arriva dal client e non si può legare come parametro: lo confronto con un elenco chiuso di valori ammessi, altrimenti 400 | `salone/.../service/AutoService.java` | 134 |
| 3 | Ordinare per prezzo d'acquisto per dedurre un dato riservato | `prezzoAcquisto` non è nell'elenco chiuso | `salone/.../service/AutoService.java` | 22 |
| 4 | XSS nella pagina tramite la descrizione dell'auto | React mostra la descrizione come testo, niente `dangerouslySetInnerHTML` | `fe/src/pages/DettaglioAuto.jsx` | 87 |
| 5 | XSS nella pagina tramite il nome utente | React mostra il nome come testo | `fe/src/components/Navbar.jsx` | 57 |
| 6 | XSS nella mail tramite il nome utente o i dati dell'auto | `HtmlUtils.htmlEscape` su ogni valore prima di inserirlo nel template HTML | `salone/.../service/EmailService.java` | 34 |
| 7 | Mass assignment in registrazione (`"ruolo": "ADMIN"`) | Il DTO non ha il ruolo; il server imposta sempre `USER` | `salone/.../service/AuthService.java` | 44 |
| 8 | Mass assignment nel profilo (ruolo, email, id) | Il DTO contiene solo il nome; l'utente arriva dal token | `salone/.../service/UtenteService.java` | 37 |
| 9 | Mass assignment negli avvisi (`inviato`, `utenteId`) | Il DTO contiene solo auto e soglia; `inviato` e token li decide il server | `salone/.../service/AvvisoService.java` | 70 |
| 10 | Un utente normale cambia il prezzo | `/api/admin/**` richiede il ruolo ADMIN: risposta 403 | `salone/.../config/SecurityConfig.java` | 43 |
| 11 | Leggere o modificare l'avviso di un altro (`/api/avvisi/7`) | Ricerca per id e proprietario insieme: risposta 404 | `salone/.../service/AvvisoService.java` | 108 |
| 12 | Cancellare il preferito di un altro | Ricerca per id e proprietario insieme: risposta 404 | `salone/.../service/PreferitoService.java` | 68 |
| 13 | Vedere una bozza dal catalogo pubblico | Il dettaglio pubblico cerca solo tra le auto pubblicate: una bozza risponde 404 | `salone/.../service/AutoService.java` | 62 |
| 14 | Indovinare o riusare il link di disattivazione | Token UUID casuale al posto dell'id; l'avviso viene cancellato all'uso | `salone/.../service/AvvisoService.java` | 72 |
| 15 | CSRF da un sito esterno | Token nell'header `Authorization` e non in un cookie; CORS solo dall'indirizzo esatto del frontend | `salone/.../config/SecurityConfig.java` | 31 e 61 |
| 16 | Token falsificato o scaduto | Firma verificata; se non è valida la richiesta è anonima | `salone/.../security/JwtFilter.java` | 41 |
| 17 | Dati personali letti dentro il JWT | Nel token metto solo id e scadenza | `salone/.../security/JwtService.java` | 31 |
| 18 | Scoprire le email registrate dal login | Stesso messaggio per email inesistente e password sbagliata | `salone/.../service/AuthService.java` | 56 |
| 19 | Furto del database con password leggibili | Password salvate con BCrypt | `salone/.../service/AuthService.java` | 43 |
| 20 | Segreti nella repository | Solo variabili d'ambiente, senza valori di ripiego; `.idea` esclusa da Git | `salone/src/main/resources/application.yml` | 7, 20, 33, 37 |
| 21 | Email o password nei log | Nei log scrivo solo gli id | `salone/.../event/PrezzoCambiatoListener.java` | 49 e 52 |
| 22 | Due mail per due cambi di prezzo ravvicinati | `UPDATE ... SET inviato = true WHERE id = ? AND inviato = false`: spedisco solo se aggiorna una riga | `salone/.../repository/AvvisoRepository.java` (usato alla riga 41 di `PrezzoCambiatoListener.java`) | 42 |

## 2. Le mie scelte sugli avvisi

### Quando parte la mail

Un avviso lega un utente a un'auto, con la soglia e il segno `inviato`. Scatta solo quando il prezzo **attraversa** la soglia: prima era sopra, adesso è uguale o sotto. Nella query lo scrivo così: `soglia < vecchioPrezzo AND soglia >= nuovoPrezzo AND inviato = false`.

Da questa regola seguono i casi della slide:
- se l'amministratore salva di nuovo lo stesso prezzo, nessuna soglia viene attraversata e non parte niente;
- se lo abbassa ancora, la soglia era già stata attraversata e l'avviso è già `inviato`: non parte niente;
- se il prezzo sale, non pubblico nemmeno l'evento.

Per rendere la regola coerente ho aggiunto due vincoli alla creazione: l'avviso si può creare **solo su un'auto dei preferiti**, e la soglia deve essere **sotto il prezzo attuale**, altrimenti non potrebbe mai essere attraversata.

### L'avviso che riparte

Ho scelto che **un avviso non riparte mai**: una volta `inviato`, resta inviato anche se il prezzo risale e poi riscende. È la regola della slide, «ogni avviso manda una sola mail». Per lo stesso motivo non permetto di cambiare la soglia di un avviso già inviato: se l'utente vuole essere avvisato di nuovo, elimina l'avviso e ne crea uno nuovo. Così ogni avviso corrisponde sempre a una sola mail, e non c'è nessun caso ambiguo.

### Una sola mail anche con due modifiche ravvicinate

Se l'amministratore cambia il prezzo due volte in un attimo, due eventi potrebbero trovare lo stesso avviso ancora `inviato = false`. Per questo non controllo il segno e poi lo scrivo in due momenti diversi: lo prendo in un colpo solo con

```sql
UPDATE avvisi SET inviato = true WHERE id = ? AND inviato = false
```

Il database esegue questa istruzione una alla volta sulla stessa riga: il primo evento aggiorna **1** riga e spedisce, il secondo aggiorna **0** righe e non spedisce niente.

### La mail parte dopo il salvataggio

Il cambio di prezzo pubblica un evento `PrezzoCambiatoEvent`. Chi lo ascolta ha `@TransactionalEventListener(phase = AFTER_COMMIT)` e `@Async` (`PrezzoCambiatoListener.java`, righe 32-33):
- `AFTER_COMMIT`: il listener parte solo se il nuovo prezzo è stato davvero salvato. Se il salvataggio fallisce, l'evento viene scartato e non parte niente;
- `@Async`: il listener gira in un altro thread, quindi l'amministratore riceve subito la risposta e non aspetta Gmail.

### Se Gmail non risponde

Ho scelto che **l'avviso resta inviato e la mail è persa**. I motivi:
- la regola «una mail per avviso» resta sempre rispettata, anche quando qualcosa va storto;
- rimettere `inviato = false` non farebbe ripartire la mail subito: con la regola dell'attraversamento ripartirebbe solo se il prezzo risalisse sopra la soglia e poi riscendesse, cioè forse mai, e con un prezzo diverso;
- l'utente non perde del tutto l'informazione: nella pagina «Preferiti e avvisi» l'avviso compare come «Scattato»;
- nel log resta traccia dell'errore, con i soli id dell'avviso e dell'auto e il tipo di errore, senza l'indirizzo email.

Ho messo anche un timeout di 5 secondi sulla connessione a Gmail, così un server che non risponde non tiene bloccato il thread.

## 3. Tre attacchi provati contro il mio salone

Per le prove ho usato Postman con due utenti registrati, Marco (id 2) e Luca, e l'amministratore. Nei token ho tenuto solo l'inizio, perché sono credenziali, e ho nascosto gli indirizzi email.

### Attacco 1: modificare l'avviso di un altro utente

Luca ha un avviso con id **7** sulla Toyota Yaris (auto 4, prezzo 19.500 €), con soglia **18.000 €**. Io, con il token di Marco, provo a cambiargli la soglia.

Richiesta:

```http
PUT /api/avvisi/7 HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOi... (token di Marco)
Content-Type: application/json

{ "soglia": 1 }
```

Risposta:

```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{ "status": 404, "messaggio": "Avviso non trovato" }
```

La stessa richiesta su un avviso che non esiste (`PUT /api/avvisi/999`) dà **la stessa identica risposta**: Marco non riesce nemmeno a sapere che l'avviso di Luca esiste.

Controllo poi con il token di Luca che il suo avviso sia intatto:

```http
GET /api/avvisi/7 HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOi... (token di Luca)
```

```http
HTTP/1.1 200 OK
Content-Type: application/json

{ "id": 7, "autoId": 4, "marca": "Toyota", "modello": "Yaris", "prezzoAttuale": 19500, "soglia": 18000, "inviato": false, ... }
```

La difesa è in `AvvisoService.java` alla riga 108: l'avviso lo cerco con `findByIdAndUtenteId`, e l'id dell'utente arriva sempre dal token, mai dalla richiesta.

![Marco prova a modificare l'avviso di Luca: 404](docs/screenshots/attacco1-avviso-altrui.png)

![L'avviso di Luca è intatto: soglia 18.000](docs/screenshots/attacco1-controllo.png)

### Attacco 2: SQL injection nell'ordinamento

Il campo su cui ordinare arriva dal client e finisce nell'`ORDER BY`, dove non si può usare un parametro legato. Da visitatore non loggato provo a inserirci un comando SQL.

Richiesta:

```http
GET /api/auto?ordina=prezzo%3B%20DROP%20TABLE%20auto&direzione=asc HTTP/1.1
Host: localhost:8080
```

(il valore decodificato di `ordina` è `prezzo; DROP TABLE auto`)

Risposta:

```http
HTTP/1.1 400 Bad Request
Content-Type: application/json

{ "status": 400, "messaggio": "Ordinamento non ammesso. Valori validi: prezzo, anno, km, marca, recenti" }
```

Subito dopo, `GET /api/auto` senza parametri risponde **200** con tutte e quattro le auto: la tabella non è stata toccata. La difesa è la mappa `CAMPI_ORDINABILI` in `AutoService.java` (riga 22, usata alla riga 134): dal client arriva solo una chiave, e nella query entra il nome del campo scritto da me. Il messaggio di errore non ripete il valore ricevuto.

Ho provato anche la ricerca con `q=' OR '1'='1`: la risposta è **200** con una lista vuota `[]`, perché il testo viene cercato letteralmente come parametro e non cambia la query (`AutoRepository.java`, riga 18).

![SQL injection nell'ordinamento: 400](docs/screenshots/attacco2-sql-injection.png)

### Attacco 3: XSS nella mail tramite il nome utente

Il nome dell'utente finisce nella mail HTML. Con il token di Marco provo a cambiarlo con del codice.

Richiesta:

```http
PUT /api/profilo HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOi... (token di Marco)
Content-Type: application/json

{ "nome": "<b>Marco</b><img src=x onerror=\"alert('XSS')\">" }
```

Risposta:

```http
HTTP/1.1 200 OK
Content-Type: application/json

{ "id": 2, "nome": "<b>Marco</b><img src=x onerror=\"alert('XSS')\">", "email": "(nascosta)", "ruolo": "USER" }
```

Il nome viene salvato così com'è: non lo modifico in ingresso, lo proteggo **in uscita**.

Poi Marco crea un avviso (id 8) sull'Alfa Romeo Giulia con soglia 18.000 €, e come amministratore abbasso il prezzo a 15.000 €. Nella mail che arriva a Marco il saluto è:

```
Buone notizie, <b>Marco</b><img src=x onerror="alert('XSS')">!
Il prezzo di Alfa Romeo Giulia è sceso a 15.000 €.
La tua soglia era 18.000 €.
```

Il nome compare come **testo**, senza grassetto e senza immagine. Aprendo in Gmail «Mostra originale» si vede che nel codice HTML della mail c'è `&lt;b&gt;Marco&lt;/b&gt;&lt;img ...`: i caratteri speciali sono stati trasformati da `HtmlUtils.htmlEscape` in `EmailService.java` alla riga 34.

Anche nel sito lo stesso nome compare come testo nella barra in alto (`Navbar.jsx`, riga 57), perché React non interpreta mai l'HTML dei valori.

![Il nome con codice HTML viene salvato](docs/screenshots/attacco3-profilo.png)

![Nella mail il nome è mostrato come testo](docs/screenshots/attacco3-mail.png)

## 4. Il limite: quello che non ho protetto

Il limite principale è che **il login non ha un limite di tentativi**. Un attaccante può provare molte password di seguito sullo stesso account (brute force) e il server risponde sempre, a ogni tentativo. BCrypt rallenta ogni prova, ma non la blocca. Per proteggerlo servirebbe contare i tentativi falliti per email e per indirizzo IP e bloccare per qualche minuto dopo, per esempio, cinque errori.

Altri limiti che conosco:
- la **registrazione rivela le email già registrate**: il login usa un messaggio unico, ma la registrazione risponde «Email già registrata»;
- il **token è nel localStorage**: oggi le difese contro l'XSS impediscono di leggerlo, ma se un giorno entrasse una vulnerabilità XSS uno script potrebbe rubarlo;
- il **token non si revoca prima della scadenza**: «Esci» lo cancella solo dal browser, e una copia resterebbe valida fino a 120 minuti (tranne se l'account viene eliminato, perché a ogni richiesta controllo che l'utente esista);
- **non verifico l'email in registrazione**: ci si può registrare con l'indirizzo di un altro, che poi riceverebbe le mail degli avvisi.