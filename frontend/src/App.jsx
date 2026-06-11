import { useState, useEffect, useMemo, useCallback } from "react";
import * as api from "./api/api":

const LABELS = ["Rolex", "Golden"];
const FABRICS = ["Sathan", "Pashmina", "Viscose"];
const SIZES = ["28*72", "30*80"];
const WEIGHTS = ["110 gram", "115 gram", "145 gram", "150 gram", "250 gram"];
const PAY_MODES = ["Cash", "Bank Transfer", "UPI", "Cheque"];

const ALL_TABS = ["Dashboard", "Stock", "Dispatch", "Receive", "Workers", "Accounts", "Reports", "Users"];
const MANAGER_TABS = ["Dashboard","Stock","Dispatch", "Receive", "Workers", "Accounts","Reports"];
const ACCOUNT_TABS = ["Dashboard", "Workers", "Accounts", "Reports"];

const TAB_ICONS = {
    Dashboard:"ti-layout-dashboard", Stock:"ti-package", Dispatch:"ti-send",
    Receive:"ti-inbox", Workers:"ti-users", Accounts:"ti-wallet",
    Reports:"ti-chart-bar", Users:"ti-shield-lock"
};

// ————————— Helpers ——————————————————————————————————————————————
const today = () => new Date().toISOString().split("T")[0];
const fmt = (n) => (n || 0).toLocaleString("en-IN");
const fmtRs = (n) => "₹" + parseFloat(n || 0).toLocaleString("en-IN", { minimumFractionDigits: 2 });
const statusColor = s => ({ COMPLETED: "green", PARTIAL:"amber", PENDING:"red" }[s?.toUpperCase()] || "red");

// ————————— Shared UI ——————————————————————————————————————————————
const S = {
    card:   { background:"#fff", border:"0.5px solid #e5e5e5", borderRadius:12, padding:"1.25rem" },
    input:  { width:"100%", padding:"7px 10px", borderRadius:6, border:"1px solid #ddd", background:"#fff", color:"#111", fontSize:13, boxSizing:"border-box" },
    btnPri: { background: "#534AB7", color:"#fff", border:"none", borderRadius:8, padding:"8px 14px", cursor:"pointer", fontSize:13, fontWeight:500, display:"flex", alignItems:"center", gap:5 },
    btnSec: { background:"#EEEDFE", color:"#534AB7", border:"none", borderRadius:8, padding:"8px 14px", cursor:"pointer", fontSize:13, fontWeight:500, display:"flex", alignItems:"center", gap:5 },
    btnGrn: { background:"#E1F5EE", color:"#0F6E56", border:"none", borderRadius:8, padding:"8px 14px", cursor:"pointer" fontSize:13, fontWeight:500, display:"flex", alignItems:"center", gap:5 },
    th:     { padding:"10px 12px", textAlign:"left", fontWeight:500, color:"#666", borderBottom:"0.5px solid #e5e5e5", whiteSpace:"nowrap", background:"#f9f9f7" },
    td:     { padding: "10px 12px", borderBottom:"0.5px solid #f5f5f5", fontSize:13 },
};

function Badge({ color, children }) {
   const m = { green:{bg:"#EAF3DE",c:"#3B6D11"}, amber:{bg:"#FAEEDA",c:"#854F0B"}, red:{bg:"#FCEBEB",c:"#A3202D"}, blue:{bg:"#E6F1FB",c:"#185FA5"}, purple:{bg:"#EEEDFE",c:"#534A87"}, gray:{bg:"#FIEFEB",c:"#5F5ESA"} };
   const s = m[color] || m.gray;
   return <span style={{ background:s.bg, color:s.c, padding:"2px 8px", borderRadius:4, fontSize:11, fontWeight:500 }}>{children}</span>;
}

function StatCard({ label, value, sub, color="blue", icon }) {

    const m = { blue:{bg:"#E6F1FB",a:"#185FA5"}, green:{bg:"#EAF3DE",a:"#3B6D11"}, amber:{bg:"#FAEEDA",a:"#854F0B"}, purple:{bg:"#EEEDFE",a:"#534A87"}, teal:{bg:"#E1F5EE",a:"#0F6E56"}, red:{bg:"#FCEBEB",a:"#A3202D"} };
    const s = m[color];
    return (
            <div style={{ ... S.card, borderTop:`3px solid ${s.a}` }}>
                <div style={{ display:"flex", alignItems:"center", justifyContent:"space-between", marginBottom:8 }}>
                    <span style={{ fontSize:71, color: "#888", fontWeight:500, textTransform:"uppercase", letterSpacing:"0.05em" }}>(abel)</span>
                    <div style={{ width:32, height:32, borderRadius:8, background:s.bg, display:"flex", alignItems:"center", justifyContent:"center" }}>
                        <i className={`ti ${icon}`} style={{ fontSize:16, color:s.a }} />
                    </div>
                </div>
            <div style={{ fontSize:26, fontWeight:600, color:"#111", lineHeight:1.1 }}> {value} </div>
            {sub && <div style={{ fontSize:12, color:"#888", marginTop:4 }}>{sub}</div>}
            </div>
    );

}

function Modal({ title, onClose, children, wide }) {
  return (
    <div style={{ position:"fixed", inset:0, background:"rgba(0,0,0,0.5)", zIndex:1000, display:"flex", alignItems:"center", justifyContent:"center", padding:16 }}>
      <div style={{ background:"#fff", borderRadius:16, padding:"1.5rem", width:"100%", maxWidth: wide ? 680 : 520, maxHeight:"85vh", overflowY:"auto", boxShadow:"0 20px 60px rgba(0,0,0,0.2)" }}>
        <div style={{ display:"flex", alignItems:"center", justifyContent:"space-between", marginBottom:"1.25rem" }}>
          <h2 style={{ margin:0, fontSize:16, fontWeight:600 }}>{title}</h2>
          <button onClick={onClose} style={{ background:"none", border:"none", cursor:"pointer", padding:4, color:"#888", fontSize:20 }}>X</button>
        </div>
        {children}
      </div>
    </div>
  );
}

function FF({ label, children }) {
  return (
    <div style={{ marginBottom:12 }}>
      <label style={{ fontSize:12, color:"#555", display:"block", marginBottom:4, fontWeight:500 }}>{label}</label>
      {children}
    </div>
  );
}

function SF({ value, onChange, options, placeholder }) {
  return (
    <select value={value} onChange={(e) => onChange(e.target.value)} style={S.input}>
      {placeholder && <option value="">{placeholder}</option>}
      {options.map(o => <option key={o.value??:o} value={o.value??:o}> {o.label??:o} </option> )}
    </select>
  );
}

function IF({ value, onChange, type="text", placeholder, min }) {
  return <input type={type} value={value} onChange={(e) => onChange(e.target.value)} placeholder={placeholder} min={min} style={S.input} />;
}

function CreatedByCell({ value }) {
  if (!value || value === "System") return <span style={{ color:"#ccc", fontSize:12 }}>—</span>;
  // Format: "Full Name (@username)"
  const match = value.match(/^(.+) \(@(.+)\)$/);
  if (!match) return <span style={{ fontSize:12, color:"#666" }} >{value}</span>;
  return (
    <div>
      <div style={{ fontSize:12, fontWeight:500, color:"#111" }}>{match[1]}</div>
      <div style={{ fontSize:11, color:"#888" }}>{@match[2]}</div>
    </div>
  );
}

function Loader() {
  return <div style={{ textAlign:"center", padding:"4rem", color:"#aaa", fontSize:13 }}>Loading...</div>;
}

function ErrMsg({ msg }) {
  return msg ? <div style={{ background:"#FCEBEB", color:"#A32D2D", borderRadius:8, padding:"10px 14px", fontSize:13, marginBottom:12 }}>{msg}</div> : null;
}

function ModalFooter({ onCancel, onSave, saveLabel="Save", saveColor="#534A87" }) {
  return (
    <div style={{ display:"flex", gap:8, marginTop:20, justifyContent:"flex-end" }}>
      <button onClick={onCancel} style={{ background:"none", border:"1px solid #ddd", borderRadius:7, padding:"8px 16px", cursor:"pointer", fontSize:13 }}>
        Cancel
      </button>
      <button onClick={onSave} style={{ background:saveColor, color:"#fff", border:"none", borderRadius:7, padding:"8px 18px", cursor:"pointer", fontSize:13, fontWeight:500 }}>{saveLabel}</button>
    </div>
  );
}

// ————————— Login Screen ——————————————————————————————————————————————


// ————————— Main App ——————————————————————————————————————————————