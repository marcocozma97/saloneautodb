import { useEffect, useState } from "react";
import { api } from "../api";
import SchedaAuto from "../components/SchedaAuto";

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

  function azzeraRicerca() {
    setTesto("");
    setRicerca("");
  }

  function invertiDirezione() {
    setDirezione(direzione === "asc" ? "desc" : "asc");
  }

  let conteggio = "";
  if (!caricamento && !errore) {
    conteggio = auto.length === 1 ? "1 auto disponibile" : auto.length + " auto disponibili";
  }

  return (
    <section>
      <div className="hero">
        <div>
          <span className="hero__etichetta">Salone online · avvisi di prezzo</span>
          <h1>Trova l'auto giusta,<br />al prezzo giusto.</h1>
          <p>
            Salva le auto che ti piacciono e fissa una soglia di prezzo:
            quando scende, ti scriviamo noi.
          </p>
        </div>

        <form className="hero__ricerca" onSubmit={cerca}>
          <input
            type="text"
            placeholder="Cerca per marca o modello"
            value={testo}
            maxLength={50}
            onChange={(e) => setTesto(e.target.value)}
            aria-label="Cerca per marca o modello"
          />
          <button type="submit">Cerca</button>
        </form>
      </div>

      <div className="filtri">
        <p className="filtri__conteggio">{caricamento ? "Caricamento..." : conteggio}</p>

        <div className="filtri__controlli">
          <label className="campo-inline">
            Ordina per
            {/* i valori sono gli stessi dell'elenco chiuso del backend */}
            <select value={ordina} onChange={(e) => setOrdina(e.target.value)}>
              <option value="recenti">Più recenti</option>
              <option value="prezzo">Prezzo</option>
              <option value="anno">Anno</option>
              <option value="km">Chilometri</option>
              <option value="marca">Marca</option>
            </select>
          </label>

          <button type="button" className="bottone-secondario" onClick={invertiDirezione}>
            {direzione === "asc" ? "↑ Crescente" : "↓ Decrescente"}
          </button>
        </div>
      </div>

      {ricerca && (
        <p className="filtri__ricerca">
          Risultati per «{ricerca}» ·{" "}
          <button type="button" className="link" onClick={azzeraRicerca}>Azzera</button>
        </p>
      )}

      {errore && <p className="errore">{errore}</p>}

      <div className="griglia">
        {caricamento
          ? [1, 2, 3, 4, 5, 6].map((n) => <div key={n} className="scheda-scheletro" />)
          : auto.map((a) => <SchedaAuto key={a.id} auto={a} />)}
      </div>

      {!caricamento && !errore && auto.length === 0 && (
        <div className="vuoto">
          <p>Nessuna auto trovata. Prova con un'altra ricerca.</p>
        </div>
      )}
    </section>
  );
}