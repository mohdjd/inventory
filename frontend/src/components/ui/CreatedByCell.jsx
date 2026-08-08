export default function CreatedByCell({ value }) {
  if (!value || value === "System") return <span style={{ color:"#ccc", fontSize:12 }}>—</span>;
  // Format: "Full Name (@username)"
  const match = value.match(/^(.+) \(@(.+)\)$/);
  if (!match) return <span style={{ fontSize:12, color:"#666" }} >{value}</span>;
  return (
    <div>
      <div style={{ fontSize:12, fontWeight:500, color:"#111" }}>{match[1]}</div>
      <div style={{ fontSize:11, color:"#888" }}>@{match[2]}</div>
    </div>
  );
}
