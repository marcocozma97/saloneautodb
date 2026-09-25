import { useId } from "react";

// coppie di colori (chiaro, scuro) per la carrozzeria
const PALETTE = [
  ["#7c73ff", "#4338ca"],
  ["#ff6b81", "#c9184a"],
  ["#34d399", "#047857"],
  ["#fbbf24", "#d97706"],
  ["#60a5fa", "#1d4ed8"],
  ["#f472b6", "#be185d"],
  ["#cbd5e1", "#64748b"],
  ["#475569", "#0f172a"],
];

// stessa marca e stesso modello = sempre lo stesso colore
function coloreDa(testo) {
  let hash = 0;
  for (let i = 0; i < testo.length; i++) {
    hash = (hash * 31 + testo.charCodeAt(i)) % 1000003;
  }
  return PALETTE[hash % PALETTE.length];
}

// Illustrazione originale di un'auto vista di lato, disegnata in SVG.
// Nessuna foto esterna: niente copyright e nessuna richiesta ad altri siti.
export default function AutoIllustrazione({ marca = "", modello = "" }) {
  // id unico per i gradienti (ogni scheda ha il suo), senza caratteri speciali
  const id = "auto" + useId().replace(/[^a-zA-Z0-9_-]/g, "");
  const [chiaro, scuro] = coloreDa(marca + modello);

  return (
    <svg viewBox="0 0 320 160" role="img" aria-label={"Illustrazione di " + marca + " " + modello}>
      <defs>
        <linearGradient id={id + "-carrozzeria"} x1="0" y1="0" x2="0" y2="1">
          <stop offset="0" stopColor={chiaro} />
          <stop offset="1" stopColor={scuro} />
        </linearGradient>
        <linearGradient id={id + "-vetri"} x1="0" y1="0" x2="1" y2="1">
          <stop offset="0" stopColor="#e0e7ff" />
          <stop offset="1" stopColor="#64748b" />
        </linearGradient>
      </defs>

      {/* ombra a terra */}
      <ellipse cx="164" cy="142" rx="136" ry="9" fill="#151a33" opacity="0.18" />

      {/* carrozzeria */}
      <path
        d="M22 110 L22 90 Q22 78 36 76 L78 72 Q100 44 134 40 L198 40 Q224 42 248 68
           L288 76 Q304 80 306 94 L306 110 Q306 118 298 118 L30 118 Q22 118 22 110 Z"
        fill={"url(#" + id + "-carrozzeria)"}
      />

      {/* finestrini */}
      <path d="M88 72 Q108 50 136 46 L160 46 L160 72 Z" fill={"url(#" + id + "-vetri)"} />
      <path d="M172 46 L197 46 Q218 48 238 70 L172 72 Z" fill={"url(#" + id + "-vetri)"} />

      {/* riflesso sulla fiancata */}
      <path d="M40 82 Q150 70 292 84" stroke="#ffffff" strokeOpacity="0.28" strokeWidth="3" fill="none" />

      {/* porte e maniglie */}
      <line x1="166" y1="74" x2="166" y2="114" stroke="#000000" strokeOpacity="0.15" strokeWidth="2" />
      <rect x="112" y="84" width="16" height="4" rx="2" fill="#000000" opacity="0.2" />
      <rect x="178" y="84" width="16" height="4" rx="2" fill="#000000" opacity="0.2" />

      {/* fari */}
      <path d="M292 84 L305 88 L305 96 L292 94 Z" fill="#fff7cc" />
      <path d="M22 86 L32 84 L32 94 L22 96 Z" fill="#ff4d6d" />

      {/* ruote */}
      {[86, 244].map((cx) => (
        <g key={cx}>
          <circle cx={cx} cy="118" r="23" fill="#11142a" />
          <circle cx={cx} cy="118" r="12" fill="#d7dae8" />
          <circle cx={cx} cy="118" r="4" fill="#11142a" />
        </g>
      ))}
    </svg>
  );
}