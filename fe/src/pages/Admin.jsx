import { useCallback, useEffect, useState } from "react";
import { api, formattaEuro } from "../api";

const FORM_VUOTO = {
  marca: "",
  modello: "",
  anno: "",
  chilometri: "",
  descrizione: "",
  prezzo: "",
  prezzoAcquisto: "",
  pubblicata: false,
};

export default function Admin() {
  const [auto, setAuto] = useState([]);
  const [form, setForm] = useState(FORM_VUOTO);
  const [idInModifica, setIdInModifica] = useState(null);
  const [errore, setErrore] = useState("");
  const [messaggio, setMessaggio] = useState("");

  const carica = useCallback(() => {
    api.adminAuto()
      .then((dati) => setAuto(dati))
      .catch((err) => setErrore(err.message));
  }, []);

  useEffect(() => {
    carica();
  }, [carica]);

  function cambiaCampo(e) {
    const { name, value, type, checked } = e.target;
    setForm({ ...form, [name]: type === "checkbox" ? checked : value });
  }

  function iniziaModifica(a) {
    setIdInModifica(a.id);
    setForm({
      marca: a.marca,
      modello: a.modello,
      anno: a.anno,
      chilometri: a.chilometri,
      descrizione: a.descrizione || "",
      prezzo: a.prezzo,
      prezzoAcquisto: a.prezzoAcquisto,
      pubblicata: a.pubblicata,
    });
    setMessaggio("");
    setErrore("");
    window.scrollTo(0, 0);
  }

  function annulla() {
    setIdInModifica(null);
    setForm(FORM_VUOTO);
  }

  async function salva(e) {
    e.preventDefault();
    setErrore("");
    setMessaggio("");

    const dati = {
      marca: form.marca,
      modello: form.modello,
      anno: Number(form.anno),
      chilometri: Number(form.chilometri),
      descrizione: form.descrizione,
      prezzoAcquisto: Number(form.prezzoAcquisto),
      pubblicata: form.pubblicata,
    };

    try {
      if (idInModifica) {
        // in modifica il prezzo NON si manda: si cambia solo con "Cambia prezzo"
        await api.modificaAuto(idInModifica, dati);
        setMessaggio("Auto modificata");
      } else {
        await api.creaAuto({ ...dati, prezzo: Number(form.prezzo) });
        setMessaggio("Auto creata");
      }
      annulla();
      carica();
    } catch (err) {
      setErrore(err.message);
    }
  }

  return (
    <section>
      <h1>Amministrazione</h1>

      <form className="modulo modulo-largo" onSubmit={salva}>
        <h2>{idInModifica ? "Modifica auto n. " + idInModifica : "Nuova auto"}</h2>

        <label>Marca
          <input name="marca" value={form.marca} onChange={cambiaCampo} maxLength={50} required />
        </label>
        <label>Modello
          <input name="modello" value={form.modello} onChange={cambiaCampo} maxLength={50} required />
        </label>
        <label>Anno
          <input name="anno" type="number" value={form.anno} onChange={cambiaCampo} required />
        </label>
        <label>Chilometri
          <input name="chilometri" type="number" value={form.chilometri} onChange={cambiaCampo} required />
        </label>
        <label>Descrizione
          <textarea name="descrizione" value={form.descrizione} onChange={cambiaCampo} maxLength={2000} rows={4} />
        </label>

        {!idInModifica && (
          <label>Prezzo di vendita (€)
            <input name="prezzo" type="number" value={form.prezzo} onChange={cambiaCampo} required />
          </label>
        )}

        <label>Prezzo d'acquisto (€)
          <input name="prezzoAcquisto" type="number" value={form.prezzoAcquisto} onChange={cambiaCampo} required />
        </label>
        <label className="casella">
          <input name="pubblicata" type="checkbox" checked={form.pubblicata} onChange={cambiaCampo} />
          Pubblicata (se non spuntata è una bozza)
        </label>

        <div className="riga-azioni">
          <button type="submit">{idInModifica ? "Salva modifiche" : "Crea auto"}</button>
          {idInModifica && <button type="button" className="bottone-secondario" onClick={annulla}>Annulla</button>}
        </div>

        {messaggio && <p className="successo">{messaggio}</p>}
        {errore && <p className="errore">{errore}</p>}
      </form>

      <h2>Tutte le auto</h2>
      <div className="tabella-scorrevole">
        <table>
          <thead>
            <tr>
              <th>Id</th>
              <th>Auto</th>
              <th>Stato</th>
              <th>Prezzo</th>
              <th>Acquisto</th>
              <th>Cambia prezzo</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {auto.map((a) => (
              <RigaAdmin key={a.id} auto={a} onModifica={iniziaModifica} onAggiornata={carica} />
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}

function RigaAdmin({ auto, onModifica, onAggiornata }) {
  const [nuovoPrezzo, setNuovoPrezzo] = useState("");
  const [errore, setErrore] = useState("");

  async function cambiaPrezzo() {
    setErrore("");
    try {
      await api.cambiaPrezzo(auto.id, Number(nuovoPrezzo));
      setNuovoPrezzo("");
      onAggiornata();
    } catch (err) {
      setErrore(err.message);
    }
  }

  return (
    <tr>
      <td>{auto.id}</td>
      <td>{auto.marca} {auto.modello} ({auto.anno})</td>
      <td>{auto.pubblicata ? "Pubblicata" : "Bozza"}</td>
      <td>{formattaEuro(auto.prezzo)}</td>
      <td>{formattaEuro(auto.prezzoAcquisto)}</td>
      <td>
        <div className="riga-azioni">
          <input type="number" min="1" value={nuovoPrezzo} placeholder="Nuovo prezzo"
                 onChange={(e) => setNuovoPrezzo(e.target.value)} />
          <button onClick={cambiaPrezzo}>Applica</button>
        </div>
        {errore && <p className="errore">{errore}</p>}
      </td>
      <td>
        <button className="bottone-secondario" onClick={() => onModifica(auto)}>Modifica</button>
      </td>
    </tr>
  );
}