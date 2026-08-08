export default function ModalFooter({ onCancel, onSave, saveLabel="Save", saveColor="#534A87" }) {
  return (
    <div style={{ display:"flex", gap:8, marginTop:20, justifyContent:"flex-end" }}>
      <button onClick={onCancel} style={{ background:"none", border:"1px solid #ddd", borderRadius:7, padding:"8px 16px", cursor:"pointer", fontSize:13 }}>Cancel</button>
      <button onClick={onSave} style={{ background:saveColor, color:"#fff", border:"none", borderRadius:7, padding:"8px 18px", cursor:"pointer", fontSize:13, fontWeight:500 }}>{saveLabel}</button>
    </div>
  );
}