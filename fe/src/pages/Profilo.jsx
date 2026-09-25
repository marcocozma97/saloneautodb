import { useEffect, useState } from "react";
import { api } from "../api";

export default function Profilo() {
  const [profilo, setProfilo] = useState(null);
  const [errore, setErrore] = useState("");

  useEffect(() => {
    api.profilo()
      .then((dati) => setProfilo(dati))
      .catch((err) => setErrore(err.message));
  }, []);

  if (errore) return <p className="errore">{errore}</p>;
  if (!profilo) return <p>Caricamento...</p>;

  return (
    <section className="modulo">
      <h1>Il mio profilo</h1>
      <p><strong>Nome:</strong> {profilo.nome}</p>
      <p><strong>Email:</strong> {profilo.email}</p>
      <p><strong>Ruolo:</strong> {profilo.ruolo === "ADMIN" ? "Amministratore" : "Utente"}</p>
    </section>
  );
}