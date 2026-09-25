import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api";

export default function Registrazione() {
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errore, setErrore] = useState("");
  const navigate = useNavigate();

  async function invia(e) {
    e.preventDefault();
    setErrore("");
    try {
      // il ruolo lo decide il server
      await api.registrazione({ nome: nome, email: email, password: password });
      navigate("/login");
    } catch (err) {
      setErrore(err.message);
    }
  }

  return (
    <section className="modulo">
      <h1>Registrati</h1>
      <form onSubmit={invia}>
        <label>
          Nome
          <input type="text" value={nome} maxLength={50} onChange={(e) => setNome(e.target.value)} required />
        </label>
        <label>
          Email
          <input type="email" value={email} maxLength={100} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label>
          Password (almeno 8 caratteri)
          <input type="password" value={password} minLength={8} maxLength={72}
                 onChange={(e) => setPassword(e.target.value)} required />
        </label>
        <button type="submit">Crea account</button>
      </form>
      {errore && <p className="errore">{errore}</p>}
      <p>Hai già un account? <Link to="/login">Accedi</Link></p>
    </section>
  );
}