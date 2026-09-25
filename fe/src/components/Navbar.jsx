import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext";

export default function Navbar() {
  const { utente, esci } = useAuth();
  const navigate = useNavigate();

  function gestisciUscita() {
    esci();
    navigate("/");
  }

  return (
    <header className="navbar">
      <Link to="/" className="logo">Il tuo salone</Link>

      <nav>
        <NavLink to="/">Catalogo</NavLink>

        {utente && <NavLink to="/preferiti">Preferiti e avvisi</NavLink>}
        {utente && <NavLink to="/profilo">Profilo</NavLink>}
        {utente && utente.ruolo === "ADMIN" && <NavLink to="/admin">Amministrazione</NavLink>}

        {!utente && <NavLink to="/login">Accedi</NavLink>}
        {!utente && <NavLink to="/registrazione">Registrati</NavLink>}

        {utente && (
          <>
            {/* React mostra il nome come testo: anche "<script>" resta innocuo */}
            <span className="saluto">Ciao, {utente.nome}</span>
            <button className="bottone-secondario" onClick={gestisciUscita}>Esci</button>
          </>
        )}
      </nav>
    </header>
  );
}