import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api";
import { useAuth } from "../AuthContext";

export default function Profilo() {
  const [profilo, setProfilo] = useState(null);
  const [nome, setNome] = useState("");
  const [errore, setErrore] = useState("");
  const [messaggio, setMessaggio] = useState("");
  const { aggiornaNome, esci } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    api.profilo()
      .then((dati) => {
        setProfilo(dati);
        setNome(dati.nome);
      })
      .catch((err) => setErrore(err.message));
  }, []);

  async function salvaNome(e) {
    e.preventDefault();
    setErrore("");
    setMessaggio("");
    try {
      const aggiornato = await api.aggiornaProfilo(nome);
      setProfilo(aggiornato);
      aggiornaNome(aggiornato.nome);
      setMessaggio("Nome aggiornato");
    } catch (err) {
      setErrore(err.message);
    }
  }

  // "elimina il mio account" cancella avvisi e preferiti, poi nessuna mail
  async function eliminaAccount() {
    const conferma = window.confirm(
      "Vuoi davvero eliminare il tuo account?\n" +
      "Verranno cancellati anche tutti i tuoi preferiti e i tuoi avvisi di prezzo, " +
      "e non riceverai più nessuna mail. L'operazione non si può annullare."
    );
    if (!conferma) return;

    setErrore("");
    try {
      await api.eliminaAccount();
      esci(); // cancella anche token e dati dal localStorage
      navigate("/", { replace: true });
    } catch (err) {
      setErrore(err.message);
    }
  }

  if (errore && !profilo) return <p className="errore">{errore}</p>;
  if (!profilo) return <p>Caricamento...</p>;

  return (
    <section className="modulo">
      <h1>Il mio profilo</h1>
      <p><strong>Email:</strong> {profilo.email}</p>
      <p><strong>Ruolo:</strong> {profilo.ruolo === "ADMIN" ? "Amministratore" : "Utente"}</p>

      <form onSubmit={salvaNome}>
        <label>
          Nome
          <input type="text" value={nome} maxLength={50}
                 onChange={(e) => setNome(e.target.value)} required />
        </label>
        <button type="submit">Salva nome</button>
      </form>

      {messaggio && <p className="successo">{messaggio}</p>}
      {errore && <p className="errore">{errore}</p>}

      {profilo.ruolo !== "ADMIN" && (
        <>
          <h2>Elimina il mio account</h2>
          <p>
            Cancella il tuo account, i tuoi preferiti e i tuoi avvisi. Dopo non riceverai
            più nessuna mail dal salone.
          </p>
          <button className="bottone-pericolo" onClick={eliminaAccount}>
            Elimina il mio account
          </button>
        </>
      )}

      <p>
        Leggi come trattiamo i tuoi dati nella <a href="/privacy.html">Privacy Policy</a>.
      </p>
    </section>
  );
}