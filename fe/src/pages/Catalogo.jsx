import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api, formattaEuro } from "../api";

export default function Catalogo() {
  const [auto, setAuto] = useState([]);
  const [testo, setTesto] = useState("");        // quello che l'utente sta scrivendo
  const [ricerca, setRicerca] = useState("");    // quello che ha confermato con "Cerca"
  const [ordina, setOrdina] = useState("recenti");
  const [direzione, setDirezione] = useState("desc");
  const [errore, setErrore] = useState("");
  const [caricamento, setCaricamento] = useState(true);

  // si ricarica quando cambia la ricerca confermata o l'ordinamento
  useEffect(() => {
    setCaricamento(true);
    setErrore("");
    api.catalogo(ricerca, ordina, direzione)
      .then((dati) => setAuto(dati))
      .catch((err) => setErrore(err.message))
      .finally(() => setCaricamento(false));
  }, [ricerca, ordina, direzione]);

  function cerca(e) {
    e.preventDefault();
    setRicerca(testo.trim());
  }

  return (
    <section>
      <h1>Le nostre auto</h1>

      <form className="barra-ricerca" onSubmit={cerca}>
        <input
          type="text"
          placeholder="Cerca per marca o modello"
          value={testo}
          maxLength={50}
          onChange={(e) => setTesto(e.target.value)}
        />
        <button type="submit">Cerca</button>

        {/* i valori delle select sono gli stessi dell'elenco chiuso del backend */}
        <select value={ordina} onChange={(e) => setOrdina(e.target.value)}>
          <option value="recenti">Più recenti</option>
          <option value="prezzo">Prezzo</option>
          <option value="anno">Anno</option>
          <option value="km">Chilometri</option>
          <option value="marca">Marca</option>
        </select>

        <select value={direzione} onChange={(e) => setDirezione(e.target.value)}>
          <option value="asc">Crescente</option>
          <option value="desc">Decrescente</option>
        </select>
      </form>

      {errore && <p className="errore">{errore}</p>}
      {caricamento && <p>Caricamento...</p>}
      {!caricamento && !errore && auto.length === 0 && <p>Nessuna auto trovata.</p>}

      <div className="griglia">
        {auto.map((a) => (
          <article key={a.id} className="scheda">
            <h2>{a.marca} {a.modello}</h2>
            <p>{a.anno} · {Number(a.chilometri).toLocaleString("it-IT")} km</p>
            <p className="prezzo">{formattaEuro(a.prezzo)}</p>
            <Link to={"/auto/" + a.id}>Vedi dettagli</Link>
          </article>
        ))}
      </div>
    </section>
  );
}