import { useState } from "react";
import * as api from "../api/api";
import S from "../styles";
import ErrMsg from "./ui/ErrMsg";
import FormField from "./ui/FormField";

export default function LoginScreen({ onLogin }) {
  const [u, setU] = useState("");
  const [p, setP] = useState("");
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);

  async function submit(e) {
    e.preventDefault();
    setLoading(true);
    setErr("");
    try {
      const res = await api.login({ username: u, password: p });
      localStorage.setItem("token", res.token);
      onLogin(res);
    } catch (e) { setErr(e.message); }
    finally { setLoading(false); }
  }

  return (
    <div style={{ minHeight:"100vh", background: "linear-gradient(135deg,#1a1645 0%,#534AB7 50%,#7C74E0 100%)", display: "flex", alignItems: "center", justifyContent: "center", padding: 16 }}>
      <div style={{ background: "#fff", borderRadius: 20, padding: "2.5rem", width: "100%", maxWidth: 400, boxShadow: "0 25px 80px rgba(0,0,0,0.3)" }}>
        <div style={{ textAlign: "center", marginBottom: "2rem" }}>
          <div style={{ width: 56, height: 56, borderRadius: 14, background: "#EEEDFE", display: "flex", alignItems: "center", justifyContent: "center", margin: "0 auto 12px" }}>
            <i className="ti ti-needle-thread" style={{ fontSize: 26, color: "#534AB7" }} />
          </div>
          <h1 style={{ margin: 0, fontSize: 22, fontWeight: 700, color: "#111" }}>Stoles Inventory</h1>
          <p style={{ margin: "6px 0 0", fontSize: 13, color: "#888" }}>Job Work Management System</p>
        </div>
        <ErrMsg msg={err} />
        <form onSubmit={submit}>
          <FormField label="Username">
            <input value={u} onChange={(e) => setU(e.target.value)} placeholder="Enter username" autoFocus
             style={{ ...S.input, padding: "10px 12px", fontSize: 14 }} />
          </FormField>
          <FormField label="Password">
            <input type="password" value={p} onChange={(e) => setP(e.target.value)} placeholder="Enter password"
             style={{ ...S.input, padding: "10px 12px", fontSize: 14 }} />
          </FormField>
          <button type="submit" disabled={loading}
            style={{ width: "100%", background: "#534AB7", color: "#fff", border: "none", borderRadius: 8, padding: "11px", cursor: "pointer", fontSize: 14, fontWeight: 600, marginTop: 8, opacity: loading ? 0.7 : 1 }}>
            {loading ? "Signing in..." : "Sign In"}
          </button>
        </form>
        <p style={{ textAlign: "center", fontSize: 12, color: "#aaa", marginTop: 16 }}>Default: admin</p>
      </div>
    </div>
  );
}
