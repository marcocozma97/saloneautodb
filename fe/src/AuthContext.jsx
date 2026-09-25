import { createContext, useContext, useState } from "react";
import { cancellaSessione, leggiToken, leggiUtente, salvaSessione } from "./api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  // all'apertura della pagina leggiamo l'utente salvato (se c'è)
  const [utente, setUtente] = useState(leggiUtente());

  function accedi(rispostaLogin) {
    const datiUtente = { nome: rispostaLogin.nome, ruolo: rispostaLogin.ruolo };
    salvaSessione(rispostaLogin.token, datiUtente);
    setUtente(datiUtente);
  }

  function esci() {
    cancellaSessione();
    setUtente(null);
  }

  // dopo la modifica del profilo aggiorniamo il nome mostrato, tenendo lo stesso token
  function aggiornaNome(nuovoNome) {
    const datiUtente = { ...utente, nome: nuovoNome };
    salvaSessione(leggiToken(), datiUtente);
    setUtente(datiUtente);
  }

  return (
    <AuthContext.Provider value={{ utente, accedi, esci, aggiornaNome }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}