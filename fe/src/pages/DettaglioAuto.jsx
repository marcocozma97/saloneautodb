import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { api, formattaEuro } from "../api";
import { useAuth } from "../AuthContext";
import AutoIllustrazione from "../components/AutoIllustrazione";

export default function DettaglioAuto() {
  const { id } = useParams();
  const { utente } = useAuth();
  const [auto, setAuto] = useState(null);
  const [errore, setErrore] = useState("");
  const [messaggio, setMessaggio] = useState("");
  const [rotazione, setRotazione] = useState(0);

  useEffect(() => {
    api.dettaglioAuto(id)
      .then((dati) => setAuto(dati))
      .catch((err) => setErrore(err.message));
  }, [id]);

  // effetto 3D: l'auto ruota seguendo il mouse sulla vetrina
  function muoviVetrina(e) {
    const riquadro = e.currentTarget.getBoundingClientRect();
    const x = (e.clientX - riquadro.left) / riquadro.width - 0.5;
    setRotazione(x * 30);
  }

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

  if (errore && !auto) {
    return (
      <div className="vuoto">
        <p>{errore}</p>
        <Link to="/" className="bottone">Torna al catalogo</Link>
      </div>
    );
  }
  if (!auto) return <p>Caricamento...</p>;

  return (
    <section className="dettaglio">
      <div
        className="vetrina"
        onMouseMove={muoviVetrina}
        onMouseLeave={() => setRotazione(0)}
      >
        <div
          className="vetrina__auto"
          style={{ transform: "perspective(1000px) rotateY(" + rotazione + "deg)" }}
        >
          <AutoIllustrazione marca={auto.marca} modello={auto.modello} />
        </div>
        <div className="vetrina__pedana" />
        <p className="vetrina__suggerimento">Muovi il mouse sulla vetrina per ruotare l'auto</p>
      </div>

      <div className="dettaglio__info">
        <Link to="/" className="link-indietro">← Torna al catalogo</Link>

        <span className="etichetta">{auto.marca}</span>
        <h1>{auto.modello}</h1>
        <p className="prezzo prezzo--grande">{formattaEuro(auto.prezzo)}</p>

        <div className="specifiche">
          <div>
            <span>Anno</span>
            <strong>{auto.anno}</strong>
          </div>
          <div>
            <span>Chilometri</span>
            <strong>{Number(auto.chilometri).toLocaleString("it-IT")} km</strong>
          </div>
        </div>

        {auto.descrizione && (
          <div className="scheda-testo">
            <h2>Descrizione</h2>
            <p className="descrizione">{auto.descrizione}</p>
          </div>
        )}

        <div className="dettaglio__azioni">
          {utente ? (
            <button onClick={aggiungiAiPreferiti}>♥ Aggiungi ai preferiti</button>
          ) : (
            <p>
              <Link to="/login">Accedi</Link> per salvare l'auto e ricevere un avviso
              quando il prezzo scende.
            </p>
          )}
        </div>

        {messaggio && (
          <p className="successo">
            {messaggio} <Link to="/preferiti">Vai ai preferiti</Link>
          </p>
        )}
        {errore && <p className="errore">{errore}</p>}
      </div>
    </section>
  );
}