import { Link } from "react-router-dom";

export default function NonTrovata() {
  return (
    <section>
      <h1>Pagina non trovata</h1>
      <p><Link to="/">Torna al catalogo</Link></p>
    </section>
  );
}