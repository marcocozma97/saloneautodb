import { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext";

export default function Navbar() {
  const { utente, esci } = useAuth();
  const navigate = useNavigate();
  const [aperto, setAperto] = useState(false); // menu su smartphone

  function chiudi() {
    setAperto(false);
  }

  function gestisciUscita() {
    esci();
    chiudi();
    navigate("/");
  }

  return (
    <header className="navbar">
      <div className="navbar__interno">
        <Link to="/" className="logo" onClick={chiudi}>
          <span className="logo__icona" aria-hidden="true">
            <svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor">
              <path d="M5 11l1.6-4.2A2 2 0 0 1 8.5 5.5h7a2 2 0 0 1 1.9 1.3L19 11a2 2 0 0 1 2 2v3a1 1 0 0 1-1 1h-1a2.5 2.5 0 0 1-5 0h-4a2.5 2.5 0 0 1-5 0H4a1 1 0 0 1-1-1v-3a2 2 0 0 1 2-2zm2.2 0h9.6l-1.2-3.3a.8.8 0 0 0-.7-.5H9.1a.8.8 0 0 0-.7.5L7.2 11z" />
            </svg>
          </span>
          Il tuo salone
        </Link>

        <button
          className="navbar__hamburger"
          aria-label="Apri il menu"
          aria-expanded={aperto}
          onClick={() => setAperto(!aperto)}
        >
          <span />
          <span />
          <span />
        </button>

        <nav className={aperto ? "navbar__menu aperto" : "navbar__menu"}>
          <NavLink to="/" end onClick={chiudi}>Catalogo</NavLink>

          {utente && <NavLink to="/preferiti" onClick={chiudi}>Preferiti e avvisi</NavLink>}
          {utente && <NavLink to="/profilo" onClick={chiudi}>Profilo</NavLink>}
          {utente && utente.ruolo === "ADMIN" && (
            <NavLink to="/admin" onClick={chiudi}>Amministrazione</NavLink>
          )}

          {utente ? (
            <div className="utente-chip">
              <span className="avatar" aria-hidden="true">
                {(utente.nome || "?").charAt(0).toUpperCase()}
              </span>
              <span className="utente-chip__nome">{utente.nome}</span>
              <button className="bottone-secondario bottone-piccolo" onClick={gestisciUscita}>
                Esci
              </button>
            </div>
          ) : (
            <>
              <NavLink to="/login" onClick={chiudi}>Accedi</NavLink>
              <Link to="/registrazione" className="bottone bottone-piccolo" onClick={chiudi}>
                Registrati
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}