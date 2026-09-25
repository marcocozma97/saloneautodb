import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { api, formattaEuro } from "../api";
import { useAuth } from "../AuthContext";

export default function DettaglioAuto() {
  const { id } = useParams();
  const { utente } = useAuth();
  const [auto, setAuto] = useState(null);
  const [errore, setErrore] = useState("");
  const [messaggio, setMessaggio] = useState("");

  useEffect(() => {
    api.dettaglioAuto(id)
      .then((dati) => setAuto(dati))
      .catch((err) => setErrore(err.message));
  }, [id]);

  async function aggiungiAiPreferiti() {
    setErrore("");
    setMessaggio("");
    try {
      await api.aggiungiPreferito(auto.id);
      setMessaggio("Auto aggiunta ai preferiti. Ora puoi impostare un avviso di prezzo.");
    } catch (err) {
      setErrore(err.message);
    }
  }

  if (errore && !auto) return <p className="errore">{errore}</p>;
  if (!auto) return <p>Caricamento...</p>;

  return (
    <section className="dettaglio">
      <Link to="/">← Torna al catalogo</Link>
      <h1>{auto.marca} {auto.modello}</h1>
      <p>Anno {auto.anno} · {Number(auto.chilometri).toLocaleString("it-IT")} km</p>
      <p className="prezzo">{formattaEuro(auto.prezzo)}</p>

      {/* la descrizione è TESTO. Niente dangerouslySetInnerHTML:
          se contiene <script> o <img onerror>, React la mostra così com'è. */}
      <p className="descrizione">{auto.descrizione}</p>

      {utente ? (
        <button onClick={aggiungiAiPreferiti}>Aggiungi ai preferiti</button>
      ) : (
        <p><Link to="/login">Accedi</Link> per salvare l'auto e ricevere un avviso quando il prezzo scende.</p>
      )}

      {messaggio && <p className="successo">{messaggio} <Link to="/preferiti">Vai ai preferiti</Link></p>}
      {errore && <p className="errore">{errore}</p>}
    </section>
  );
}