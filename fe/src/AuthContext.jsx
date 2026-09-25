import { createContext, useContext, useState } from "react";
import { cancellaSessione, leggiUtente, salvaSessione } from "./api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  // all'apertura della pagina si legge l'utente salvato
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

  return (
    <AuthContext.Provider value={{ utente, accedi, esci }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}