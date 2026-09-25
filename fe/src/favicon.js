/* =========================================================
   FAVICON: automobile vista frontalmente disegnata in CSS
   ---------------------------------------------------------
   Le forme sono rettangoli neutri: colori, sfumature, angoli
   arrotondati e proporzioni arrivano tutti dal foglio di
   stile qui sotto, che usa la stessa palette di index.css.
   L'immagine viene incapsulata in un data-URI e impostata
   come icona della scheda del browser.
   ========================================================= */

const stileAuto = `
  .sfondo { fill: url(#sfumaturaSfondo); }
  .scocca { fill: #ffffff; }
  .tettuccio { fill: #eef0fb; }
  .vetro { fill: #12162b; }
  .specchietto { fill: #eef0fb; }
  .faro { fill: #ffd166; }
  .griglia { fill: #12162b; }
  .paraurti { fill: #c9cde4; }
  .ruota { fill: #12162b; }
  .targa { fill: #ff3d7f; }
`;

const disegnoAuto = `
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64" width="64" height="64">
  <defs>
    <linearGradient id="sfumaturaSfondo" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0" stop-color="#5b50f0"/>
      <stop offset="1" stop-color="#4338ca"/>
    </linearGradient>
    <style>${stileAuto}</style>
  </defs>

  <rect class="sfondo" x="0" y="0" width="64" height="64" rx="14"/>

  <!-- tettuccio e parabrezza -->
  <rect class="tettuccio" x="17" y="12" width="30" height="18" rx="7"/>
  <rect class="vetro" x="21" y="16" width="22" height="11" rx="4"/>

  <!-- specchietti retrovisori -->
  <rect class="specchietto" x="8" y="26" width="8" height="5" rx="2.5"/>
  <rect class="specchietto" x="48" y="26" width="8" height="5" rx="2.5"/>

  <!-- scocca anteriore -->
  <rect class="scocca" x="9" y="28" width="46" height="21" rx="7"/>

  <!-- fari -->
  <rect class="faro" x="13" y="33" width="12" height="6" rx="3"/>
  <rect class="faro" x="39" y="33" width="12" height="6" rx="3"/>

  <!-- calandra e targa -->
  <rect class="griglia" x="26" y="33" width="12" height="6" rx="3"/>
  <rect class="targa" x="26" y="42" width="12" height="5" rx="2"/>

  <!-- paraurti -->
  <rect class="paraurti" x="11" y="43" width="13" height="5" rx="2.5"/>
  <rect class="paraurti" x="40" y="43" width="13" height="5" rx="2.5"/>

  <!-- pneumatici -->
  <rect class="ruota" x="10" y="47" width="12" height="7" rx="3"/>
  <rect class="ruota" x="42" y="47" width="12" height="7" rx="3"/>
</svg>`;

/** Imposta l'automobile come icona della scheda del browser. */
export function applicaFavicon() {
  const sorgente =
    "data:image/svg+xml," + encodeURIComponent(disegnoAuto.trim());

  let collegamento = document.querySelector('link[rel="icon"]');
  if (!collegamento) {
    collegamento = document.createElement("link");
    collegamento.rel = "icon";
    document.head.appendChild(collegamento);
  }
  collegamento.type = "image/svg+xml";
  collegamento.href = sorgente;
}

export default applicaFavicon;
