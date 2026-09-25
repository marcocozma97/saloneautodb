export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer__interno">
        <div>
          <strong>Il tuo salone</strong>
          <p>Progetto didattico · © {new Date().getFullYear()}</p>
        </div>
        <nav>
          <a href="/privacy.html">Privacy Policy</a>
          <a href="/cookie.html">Cookie Policy</a>
        </nav>
      </div>
    </footer>
  );
}