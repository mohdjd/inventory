import S from "../../styles";
import { today, statusColor } from "../../utils";
import Badge from "../ui/Badge";

export default function ReceiveTab({ pending, setModal }) {
  return (
    <div>
      <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:"1.25rem" }}>
        <h1 style={{ margin:0, fontSize:20, fontWeight:600 }}>Receive from Workers</h1>
        <button onClick={() => setModal({ type:"receive", data:{ dispatchId:"", quantity:"", receivedDate:today() } })} style={S.btnPri}>
          <i className="ti ti-inbox" style={{ fontSize:13 }} /> Record Receipt
        </button>
      </div>

      <div style={{ display:"grid", gridTemplateColumns:"repeat(auto-fill, minmax(280px,1fr))", gap:12 }}>
        {pending.map(d => (
          <div key={d.id} style={S.card}>
            <div style={{ display:"flex", justifyContent:"space-between", alignItems:"flex-start", marginBottom:8 }}>
              <div style={{ fontWeight:600, fontSize:14 }}>{d.workerName}</div>
              <Badge color={statusColor(d.status)}>{d.status}</Badge>
            </div>
            <div style={{ fontSize:12, color:"#888", marginBottom:10 }}>{d.fabric} · {d.workTypeName} · {d.sentDate}</div>
            <div style={{ display:"grid", gridTemplateColumns:"1fr 1fr 1fr", gap:6, marginBottom:10 }}>
              {[["Sent",d.sentQty,"#185FA5"],["Received",d.receivedQty,"#3B6D11"],["Pending",d.pendingQty,"#854F0B"]].map(([l,v,c]) => (
                <div key={l} style={{ background:"#f9f9f7", borderRadius:6, padding:"6px", textAlign:"center" }}>
                  <div style={{ fontSize:16, fontWeight:600, color:c }}>{v}</div>
                  <div style={{ fontSize:10, color:"#888" }}>{l}</div>
                </div>
              ))}
            </div>
            <div style={{ height:4, background:"#f0f0f0", borderRadius:2, marginBottom:10 }}>
              <div style={{ height:4, borderRadius:2, background:"#1D9E75", width:`${d.sentQty > 0 ? Math.round((d.receivedQty / d.sentQty) * 100) : 0}%` }} />
            </div>
            <button onClick={() => setModal({ type:"receive", data:{ dispatchId:d.id, quantity:"", receivedDate:today() } })}
              style={{ width:"100%", ...S.btnGrn, justifyContent:"center" }}>
              Record Receipt
            </button>
          </div>
        ))}
        {!pending.length && (
          <div style={{ gridColumn:"1/-1", textAlign:"center", padding:"3rem", color:"#aaa" }}>
            <i className="ti ti-circle-check" style={{ fontSize:32, display:"block", marginBottom:8, color:"#3B6D11" }} />All dispatches received!
          </div>
        )}
      </div>
    </div>
  );
}
