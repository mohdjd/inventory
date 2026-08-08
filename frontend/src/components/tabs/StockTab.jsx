import S from "../../styles";
import { fmt, today } from "../../utils";
import Badge from "../ui/Badge";
import CreatedByCell from "../ui/CreatedByCell";

export default function StockTab({ stock, setModal }) {
  return (
    <div>
      <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:"1.25rem" }}>
        <h1 style={{ margin:0, fontSize:20, fontWeight:600 }}>Plain Stock</h1>
        <button onClick={() => setModal({ type:"addStock", data:{ label:"", fabric:"", size:"", weight:"", quantity:"", purchaseDate:today() } })} style={S.btnPri}>
          <i className="ti ti-plus" style={{ fontSize:13 }} /> Add Purchase
        </button>
      </div>
      <div style={{ ...S.card, padding:0, overflow:"hidden" }}>
        <div style={{ overflowX:"auto" }}>
          <table style={{ width:"100%", borderCollapse:"collapse", fontSize:13 }}>
            <thead>
              <tr>{["Label","Fabric","Size","Weight","Qty (pcs)","Date","Created By",""].map(h => <th key={h} style={S.th}>{h}</th>)}</tr>
            </thead>
            <tbody>
              {stock.map(s => (
                <tr key={s.id}>
                  <td style={S.td}><Badge color="purple">{s.label}</Badge></td>
                  <td style={S.td}>{s.fabric}</td>
                  <td style={S.td}>{s.size}</td>
                  <td style={S.td}>{s.weight}</td>
                  <td style={{ ...S.td, fontWeight:600 }}>{fmt(s.quantity)}</td>
                  <td style={{ ...S.td, color:"#888" }}>{s.purchaseDate}</td>
                  <td style={S.td}><CreatedByCell value={s.createdBy} /></td>
                  <td style={S.td}>
                    <button
                      onClick={() => setModal({ type:"dispatch", data:{ stockItemId:s.id, workerId:"", workTypeId:"", pricePerPiece:"", sentQty:"", sentDate:today() } })}
                      style={{ ...S.btnGrn, padding:"3px 10px", fontSize:12 }}>
                      Dispatch
                    </button>
                  </td>
                </tr>
              ))}
              {!stock.length && (
                <tr><td colSpan={8} style={{ ...S.td, textAlign:"center", color:"#aaa", padding:"3rem" }}>No stock entries yet</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
