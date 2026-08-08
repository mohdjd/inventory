export default function ErrMsg({ msg }) {
  if (!msg) return null;
  return (
      <div style={{ background:"#FCEBEB", color:"#A32D2D", borderRadius:8, padding:"10px 14px", fontSize:13, marginBottom:12 }}>
          {msg}
      </div>
      );
}