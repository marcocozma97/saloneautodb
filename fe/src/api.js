// Indirizzo del backend. In locale è localhost:8080
const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

const CHIAVE_TOKEN = "salone_token";
const CHIAVE_UTENTE = "salone_utente";

// ---------- SESSIONE NEL LOCALSTORAGE ----------

export function salvaSessione(token, utente) {
  localStorage.setItem(CHIAVE_TOKEN, token);
  localStorage.setItem(CHIAVE_UTENTE, JSON.stringify(utente));
}

export function leggiToken() {
  return localStorage.getItem(CHIAVE_TOKEN);
}

export function leggiUtente() {
  const testo = localStorage.getItem(CHIAVE_UTENTE);
  if (!testo) return null;
  try {
    return JSON.parse(testo);
  } catch {
    return null;
  }
}

export function cancellaSessione() {
  localStorage.removeItem(CHIAVE_TOKEN);
  localStorage.removeItem(CHIAVE_UTENTE);
}

// ---------- LA FUNZIONE UNICA PER TUTTE LE CHIAMATE ----------

async function apiFetch(percorso, opzioni = {}) {
  // le rotte pubbliche (login, registrazione, disattiva) partono senza token
  const token = opzioni.pubblica ? null : leggiToken();

  const headers = {};
  if (opzioni.body !== undefined) {
    headers["Content-Type"] = "application/json";
  }
  if (token) {
    headers["Authorization"] = "Bearer " + token;
  }

  const risposta = await fetch(API_URL + percorso, {
    method: opzioni.method || "GET",
    headers: headers,
    body: opzioni.body !== undefined ? JSON.stringify(opzioni.body) : undefined,
  });

  // token scaduto o non più valido: torniamo al login
  if (risposta.status === 401 && token) {
    cancellaSessione();
    window.location.href = "/login";
    throw new Error("Sessione scaduta, accedi di nuovo");
  }

  if (!risposta.ok) {
    let messaggio = "Errore " + risposta.status;
    try {
      const dati = await risposta.json();
      if (dati.messaggio) messaggio = dati.messaggio;
    } catch {
      // la risposta non era JSON: teniamo il messaggio generico
    }
    throw new Error(messaggio);
  }

  // 204 No Content: nessun corpo da leggere
  if (risposta.status === 204) return null;

  return risposta.json();
}

// ---------- LE CHIAMATE DELL'APPLICAZIONE ----------

export const api = {
  // autenticazione e profilo
  registrazione: (dati) =>
    apiFetch("/api/auth/registrazione", { method: "POST", body: dati, pubblica: true }),
  login: (dati) =>
    apiFetch("/api/auth/login", { method: "POST", body: dati, pubblica: true }),
  profilo: () => apiFetch("/api/profilo"),
  aggiornaProfilo: (nome) =>
    apiFetch("/api/profilo", { method: "PUT", body: { nome: nome } }),
  eliminaAccount: () => apiFetch("/api/profilo", { method: "DELETE" }), // Parte 8

  // catalogo pubblico
  catalogo: (testo, ordina, direzione) => {
    // URLSearchParams codifica i valori
    const parametri = new URLSearchParams({ ordina: ordina, direzione: direzione });
    if (testo) parametri.set("q", testo);
    return apiFetch("/api/auto?" + parametri.toString());
  },
  dettaglioAuto: (id) => apiFetch("/api/auto/" + encodeURIComponent(id)),

  // preferiti
  preferiti: () => apiFetch("/api/preferiti"),
  aggiungiPreferito: (autoId) =>
    apiFetch("/api/preferiti", { method: "POST", body: { autoId: autoId } }),
  rimuoviPreferito: (id) => apiFetch("/api/preferiti/" + id, { method: "DELETE" }),

  // avvisi
  avvisi: () => apiFetch("/api/avvisi"),
  creaAvviso: (autoId, soglia) =>
    apiFetch("/api/avvisi", { method: "POST", body: { autoId: autoId, soglia: soglia } }),
  modificaSoglia: (id, soglia) =>
    apiFetch("/api/avvisi/" + id, { method: "PUT", body: { soglia: soglia } }),
  eliminaAvviso: (id) => apiFetch("/api/avvisi/" + id, { method: "DELETE" }),
  disattivaAvviso: (token) =>
    apiFetch("/api/avvisi/disattiva", { method: "POST", body: { token: token }, pubblica: true }),

  // amministratore
  adminAuto: () => apiFetch("/api/admin/auto"),
  creaAuto: (dati) => apiFetch("/api/admin/auto", { method: "POST", body: dati }),
  modificaAuto: (id, dati) => apiFetch("/api/admin/auto/" + id, { method: "PUT", body: dati }),
  cambiaPrezzo: (id, prezzo) =>
    apiFetch("/api/admin/auto/" + id + "/prezzo", { method: "PATCH", body: { prezzo: prezzo } }),
};

// 18000 -> "18.000 €"
export function formattaEuro(valore) {
  return Number(valore).toLocaleString("it-IT") + " €";
}