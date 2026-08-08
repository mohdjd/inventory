import S from "../../styles";
import { today, statusColor } from "../../utils";
import Badge from "../ui/Badge";
import SelectField from "../ui/SelectField";
export default function DispatchTab({ filtDispatches, workers, searchQ, setSearchQ, filterWId, setFilterWId, setModal }) {
  return (
    <div>
      <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:"1.25rem" }}>
        <h1 style={{ margin:0, fontSize:20, fontWeight:600 }}>Dispatch to Workers</h1>
        <button onClick={() => setModal({ type:"dispatch", data:{ stockItemId:"", workerId:"", workTypeId:"", pricePerPiece:"", sentQty:"", sentDate:today() } })} style={S.btnPri}>
          <i className="ti ti-send" style={{ fontSize:13 }} /> New Dispatch
        </button>
      </div>
      <div style={{ display:"flex", gap:8, marginBottom:14 }}>
        <input value={searchQ} onChange={e => setSearchQ(e.target.value)} placeholder="Search worker, fabric, work type..." style={{ flex:1, ...S.input }} />
        <SelectField value={filterWId} onChange={setFilterWId} options={workers.map(w => ({ value:w.id, label:w.name }))} placeholder="All workers" />
      </div>
      <div style={{ ...S.card, padding:0, overflow:"hidden" }}>
        <div style={{ overflowX:"auto" }}>
          <table style={{ width:"100%", borderCollapse:"collapse", fontSize:13 }}>
            <thead>
              <tr>{["Worker","Fabric / Size","Work Type","Sent","Received","Pending","Rate","Date","Status"].map(h => <th key={h} style={S.th}>{h}</th>)}</tr>
            </thead>
            <tbody>
              {filtDispatches.map(d => (
                <tr key={d.id}>
                  <td style={{ ...S.td, fontWeight:500 }}>{d.workerName}</td>
                  <td style={S.td}>{d.fabric}<div style={{ fontSize:11, color:"#888" }}>{d.size} · {d.weight}</div></td>
                  <td style={S.td}>{d.workTypeName}</td>
                  <td style={{ ...S.td, fontWeight:600 }}>{d.sentQty}</td>
                  <td style={{ ...S.td, color:"#3B6D11", fontWeight:600 }}>{d.receivedQty}</td>
                  <td style={{ ...S.td, color: d.pendingQty > 0 ? "#854F0B" : "#3B6D11", fontWeight:600 }}>{d.pendingQty}</td>
                  <td style={S.td}>₹{d.pricePerPiece}/pc</td>
                  <td style={{ ...S.td, color:"#888" }}>{d.sentDate}</td>
                  <td style={S.td}><Badge color={statusColor(d.status)}>{d.status}</Badge></td>
                </tr>
              ))}
              {!filtDispatches.length && (
                <tr><td colSpan={9} style={{ ...S.td, textAlign:"center", color:"#aaa", padding:"3rem" }}>No dispatches found</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
