import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api, formattaEuro } from "../api";

export default function Preferiti() {
  const [preferiti, setPreferiti] = useState([]);
  const [avvisi, setAvvisi] = useState([]);
  const [errore, setErrore] = useState("");
  const [caricamento, setCaricamento] = useState(true);

  const carica = useCallback(() => {
    Promise.all([api.preferiti(), api.avvisi()])
      .then(([datiPreferiti, datiAvvisi]) => {
        setPreferiti(datiPreferiti);
        setAvvisi(datiAvvisi);
      })
      .catch((err) => setErrore(err.message))
      .finally(() => setCaricamento(false));
  }, []);

  useEffect(() => {
    carica();
  }, [carica]);

  if (caricamento) return <p>Caricamento...</p>;

  return (
    <section>
      <h1>Preferiti e avvisi di prezzo</h1>
      {errore && <p className="errore">{errore}</p>}
      {preferiti.length === 0 && (
        <p>Non hai ancora auto preferite. <Link to="/">Sfoglia il catalogo</Link></p>
      )}

      {preferiti.map((preferito) => (
        <RigaPreferito
          key={preferito.id}
          preferito={preferito}
          avviso={avvisi.find((a) => a.autoId === preferito.auto.id)}
          onAggiorna={carica}
        />
      ))}
    </section>
  );
}

function RigaPreferito({ preferito, avviso, onAggiorna }) {
  const [soglia, setSoglia] = useState("");
  const [errore, setErrore] = useState("");
  const auto = preferito.auto;

  async function esegui(azione) {
    setErrore("");
    try {
      await azione();
      setSoglia("");
      onAggiorna();
    } catch (err) {
      setErrore(err.message);
    }
  }

  return (
    <article className="riga">
      <div>
        <h2><Link to={"/auto/" + auto.id}>{auto.marca} {auto.modello}</Link></h2>
        <p>Prezzo attuale: <strong>{formattaEuro(auto.prezzo)}</strong></p>
      </div>

      <div className="avviso">
        {avviso ? (
          <>
            <p>
              Avviso a <strong>{formattaEuro(avviso.soglia)}</strong> ·{" "}
              {avviso.inviato
                ? <span className="stato-scattato">scattato: ti abbiamo scritto</span>
                : <span className="stato-attivo">attivo</span>}
            </p>

            {!avviso.inviato && (
              <div className="riga-azioni">
                <input type="number" min="1" placeholder="Nuova soglia" value={soglia}
                       onChange={(e) => setSoglia(e.target.value)} />
                <button onClick={() => esegui(() => api.modificaSoglia(avviso.id, Number(soglia)))}>
                  Modifica soglia
                </button>
              </div>
            )}

            <button className="bottone-secondario"
                    onClick={() => esegui(() => api.eliminaAvviso(avviso.id))}>
              Elimina avviso
            </button>
          </>
        ) : (
          <div className="riga-azioni">
            <input type="number" min="1" placeholder="Avvisami sotto (€)" value={soglia}
                   onChange={(e) => setSoglia(e.target.value)} />
            <button onClick={() => esegui(() => api.creaAvviso(auto.id, Number(soglia)))}>
              Crea avviso
            </button>
          </div>
        )}
      </div>

      <button className="bottone-pericolo"
              onClick={() => esegui(() => api.rimuoviPreferito(preferito.id))}>
        Rimuovi dai preferiti
      </button>

      {errore && <p className="errore">{errore}</p>}
    </article>
  );
}