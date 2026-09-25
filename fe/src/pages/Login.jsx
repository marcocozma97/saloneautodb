import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api";
import { useAuth } from "../AuthContext";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errore, setErrore] = useState("");
  const { accedi } = useAuth();
  const navigate = useNavigate();

  async function invia(e) {
    e.preventDefault();
    setErrore("");
    try {
      const risposta = await api.login({ email: email, password: password });
      accedi(risposta);
      navigate(risposta.ruolo === "ADMIN" ? "/admin" : "/");
    } catch (err) {
      setErrore(err.message);
    }
  }

  return (
    <section className="modulo">
      <h1>Accedi</h1>
      <form onSubmit={invia}>
        <label>
          Email
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label>
          Password
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </label>
        <button type="submit">Accedi</button>
      </form>
      {errore && <p className="errore">{errore}</p>}
      <p>Non hai un account? <Link to="/registrazione">Registrati</Link></p>
    </section>
  );
}