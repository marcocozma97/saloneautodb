import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api, formattaEuro } from "../api";
import AutoIllustrazione from "../components/AutoIllustrazione";

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
      <div className="pagina-titolo">
        <h1>Preferiti e avvisi</h1>
        <p>Fissa una soglia su un'auto: quando il prezzo la raggiunge ti mandiamo una mail.</p>
      </div>

      {errore && <p className="errore">{errore}</p>}

      {preferiti.length === 0 && (
        <div className="vuoto">
          <p>Non hai ancora auto preferite.</p>
          <Link to="/" className="bottone">Sfoglia il catalogo</Link>
        </div>
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
      <Link to={"/auto/" + auto.id} className="riga__miniatura">
        <AutoIllustrazione marca={auto.marca} modello={auto.modello} />
      </Link>

      <div>
        <span className="etichetta">{auto.marca}</span>
        <h2><Link to={"/auto/" + auto.id}>{auto.modello}</Link></h2>
        <p className="riga__prezzo">Prezzo attuale <strong>{formattaEuro(auto.prezzo)}</strong></p>
      </div>

      <div className="avviso">
        {avviso ? (
          <>
            <p>
              Avviso a <strong>{formattaEuro(avviso.soglia)}</strong>{" "}
              {avviso.inviato
                ? <span className="stato-scattato">Scattato: ti abbiamo scritto</span>
                : <span className="stato-attivo">Attivo</span>}
            </p>

            <div className="riga-azioni">
              {!avviso.inviato && (
                <>
                  <input type="number" min="1" placeholder="Nuova soglia (€)" value={soglia}
                         onChange={(e) => setSoglia(e.target.value)} />
                  <button className="bottone-piccolo"
                          onClick={() => esegui(() => api.modificaSoglia(avviso.id, Number(soglia)))}>
                    Modifica
                  </button>
                </>
              )}
              <button className="bottone-secondario bottone-piccolo"
                      onClick={() => esegui(() => api.eliminaAvviso(avviso.id))}>
                Elimina avviso
              </button>
            </div>
          </>
        ) : (
          <>
            <p>Nessun avviso su questa auto.</p>
            <div className="riga-azioni">
              <input type="number" min="1" placeholder="Avvisami sotto (€)" value={soglia}
                     onChange={(e) => setSoglia(e.target.value)} />
              <button className="bottone-piccolo"
                      onClick={() => esegui(() => api.creaAvviso(auto.id, Number(soglia)))}>
                Crea avviso
              </button>
            </div>
          </>
        )}
      </div>

      <button className="bottone-pericolo bottone-piccolo"
              onClick={() => esegui(() => api.rimuoviPreferito(preferito.id))}>
        Rimuovi
      </button>

      {errore && <p className="errore">{errore}</p>}
    </article>
  );
}