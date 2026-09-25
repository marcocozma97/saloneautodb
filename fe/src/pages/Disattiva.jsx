import { useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { api } from "../api";

export default function Disattiva() {
  const [parametri] = useSearchParams();
  const token = parametri.get("token");
  const [stato, setStato] = useState("attesa"); // attesa | fatto | errore
  const [errore, setErrore] = useState("");

  async function conferma() {
    try {
      await api.disattivaAvviso(token);
      setStato("fatto");
    } catch (err) {
      setErrore(err.message);
      setStato("errore");
    }
  }

  if (!token) {
    return <p className="errore">Link non valido.</p>;
  }

  return (
    <section className="modulo">
      <h1>Disattiva avviso di prezzo</h1>

      {stato === "attesa" && (
        <>
          <p>Vuoi davvero disattivare questo avviso? Non riceverai più mail per questa auto.</p>
          <button onClick={conferma}>Sì, disattiva</button>
        </>
      )}

      {stato === "fatto" && (
        <p className="successo">Avviso disattivato. <Link to="/">Torna al catalogo</Link></p>
      )}

      {stato === "errore" && <p className="errore">{errore}</p>}
    </section>
  );
}