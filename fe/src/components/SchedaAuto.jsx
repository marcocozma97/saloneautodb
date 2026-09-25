import { useState } from "react";
import { Link } from "react-router-dom";
import { formattaEuro } from "../api";
import AutoIllustrazione from "./AutoIllustrazione";

// Scheda del catalogo con effetto 3D: si inclina seguendo il mouse
export default function SchedaAuto({ auto }) {
  const [stile, setStile] = useState({});

  function muovi(e) {
    const riquadro = e.currentTarget.getBoundingClientRect();
    // posizione del mouse da -0.5 a +0.5 rispetto al centro della scheda
    const x = (e.clientX - riquadro.left) / riquadro.width - 0.5;
    const y = (e.clientY - riquadro.top) / riquadro.height - 0.5;
    setStile({
      transform:
        "perspective(900px) rotateY(" + x * 10 + "deg) rotateX(" + -y * 10 + "deg) translateY(-4px)",
    });
  }

  function esci() {
    setStile({});
  }

  return (
    <Link
      to={"/auto/" + auto.id}
      className="scheda-auto"
      style={stile}
      onMouseMove={muovi}
      onMouseLeave={esci}
    >
      <div className="scheda-auto__immagine">
        <AutoIllustrazione marca={auto.marca} modello={auto.modello} />
      </div>

      <div className="scheda-auto__corpo">
        <span className="etichetta">{auto.marca}</span>
        <h3>{auto.modello}</h3>

        <div className="chips">
          <span className="chip">{auto.anno}</span>
          <span className="chip">{Number(auto.chilometri).toLocaleString("it-IT")} km</span>
        </div>

        <div className="scheda-auto__piede">
          <p className="prezzo">{formattaEuro(auto.prezzo)}</p>
          <span className="freccia" aria-hidden="true">→</span>
        </div>
      </div>
    </Link>
  );
}