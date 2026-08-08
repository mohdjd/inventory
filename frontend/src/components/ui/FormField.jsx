export default function FormField({ label, children }) {
  return (
    <div style={{ marginBottom:12 }}>
      <label style={{ fontSize:12, color:"#555", display:"block", marginBottom:4, fontWeight:500 }}>{label}</label>
      {children}
    </div>
  );
}
