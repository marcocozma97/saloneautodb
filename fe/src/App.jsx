import { Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import RottaProtetta from "./components/RottaProtetta";
import Catalogo from "./pages/Catalogo";
import DettaglioAuto from "./pages/DettaglioAuto";
import Login from "./pages/Login";
import Registrazione from "./pages/Registrazione";
import Preferiti from "./pages/Preferiti";
import Profilo from "./pages/Profilo";
import Admin from "./pages/Admin";
import Disattiva from "./pages/Disattiva";
import NonTrovata from "./pages/NonTrovata";

export default function App() {
  return (
    <>
      <Navbar />
      <main className="contenitore">
        <Routes>
          <Route path="/" element={<Catalogo />} />
          <Route path="/auto/:id" element={<DettaglioAuto />} />
          <Route path="/login" element={<Login />} />
          <Route path="/registrazione" element={<Registrazione />} />
          <Route path="/disattiva" element={<Disattiva />} />

          <Route
            path="/preferiti"
            element={<RottaProtetta><Preferiti /></RottaProtetta>}
          />
          <Route
            path="/profilo"
            element={<RottaProtetta><Profilo /></RottaProtetta>}
          />
          <Route
            path="/admin"
            element={<RottaProtetta soloAdmin={true}><Admin /></RottaProtetta>}
          />

          <Route path="*" element={<NonTrovata />} />
        </Routes>
      </main>
    </>
  );
}