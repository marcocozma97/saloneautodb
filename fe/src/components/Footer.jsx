import "./Footer.css";

export default function Footer() {
  return (
    <footer className="footer">
      <span>© {new Date().getFullYear()} Il tuo salone · progetto didattico</span>
      <nav>
        <a href="/privacy.html">Privacy Policy</a>
        <a href="/cookie.html">Cookie Policy</a>
      </nav>
    </footer>
  );
}