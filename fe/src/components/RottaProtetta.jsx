import { Navigate } from "react-router-dom";
import { useAuth } from "../AuthContext";

// Solo comodità di navigazione: la protezione vera la fa il backend
export default function RottaProtetta({ children, soloAdmin = false }) {
  const { utente } = useAuth();

  if (!utente) {
    return <Navigate to="/login" replace />;
  }
  if (soloAdmin && utente.ruolo !== "ADMIN") {
    return <Navigate to="/" replace />;
  }
  return children;
}