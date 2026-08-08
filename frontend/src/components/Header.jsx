import { TAB_ICONS } from "./constants";

export default function Header({ tabs, activeTab, setActiveTab, authUser, role, logout }) {
  return (
    <div style={{ background: "#fff", borderBottom: "0.5px solid #e5e5e5", padding: "0 1.5rem", display: "flex", alignItems: "center", gap: 12, height: 56, position: "sticky", top: 0, zIndex: 100 }}>
      <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
        <div style={{ width: 32, height: 32, borderRadius: 8, background: "#EEEDFE", display: "flex", alignItems: "center", justifyContent: "center" }}>
          <i className="ti ti-needle-thread" style={{ fontSize: 17, color: "#534AB7" }} />
        </div>
        <div>
          <div style={{ fontSize: 13, fontWeight: 600, color: "#111", lineHeight: 1.2 }}>Stoles Inventory</div>
          <div style={{ fontSize: 10, color: "#888" }}>Job Work Management</div>
        </div>
      </div>

      <nav style={{ display: "flex", gap: 2, marginLeft: "auto", overflowX: "auto" }}>
        {tabs.map((t) => (
          <button key={t} onClick={() => setActiveTab(t)} style={{
            background: activeTab === t ? "#EEEDFE" : "none",
            color: activeTab === t ? "#534AB7" : "#666",
            border: "none", borderRadius: 6, padding: "6px 10px", cursor: "pointer", fontSize: 12,
            fontWeight: activeTab === t ? 600 : 400,
            display: "flex", alignItems: "center", gap: 4, whiteSpace: "nowrap",
          }}>
            <i className={`ti ${TAB_ICONS[t]}`} style={{ fontSize: 13 }} /> {t}
          </button>
        ))}
      </nav>

      <div style={{ display: "flex", alignItems: "center", gap: 8, marginLeft: 8, borderLeft: "1px solid #eee", paddingLeft: 12, flexShrink: 0 }}>
        <div style={{ width: 28, height: 28, borderRadius: 50, background: "#534AB7", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 11, fontWeight: 600, color: "#fff" }}>
          {authUser?.username?.[0]?.toUpperCase()}
        </div>
        <div style={{ fontSize: 12 }}>
          <div style={{ fontWeight: 500, color: "#111" }}>{authUser?.username}</div>
          <div style={{ color: "#888", fontSize: 10 }}>{role}</div>
        </div>
        <button onClick={logout} title="Logout" style={{ background: "none", border: "none", cursor: "pointer", color: "#aaa", padding: 4 }}>
          <i className="ti ti-logout" style={{ fontSize: 16 }} />
        </button>
      </div>
    </div>
  );
}