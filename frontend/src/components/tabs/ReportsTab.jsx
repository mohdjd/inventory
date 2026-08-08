import S from "../../styles";
import { FABRICS } from "../../constants";
import { fmt, fmtRs } from "../../utils";

export default function ReportsTab({ stock, dispatches, payments, workerSummary, workTypes }) {
  const totalStock  = stock.reduce((a,s) => a + (s.quantity||0), 0);
  const totalSent   = dispatches.reduce((a,d) => a + (d.sentQty||0), 0);
  const totalRec    = dispatches.reduce((a,d) => a + (d.receivedQty||0), 0);
  const totalEarned = dispatches.reduce((a,d) => a + (d.receivedQty||0) * (d.pricePerPiece||0), 0);
  const totalPaid   = payments.reduce((a,p) => a + parseFloat(p.amount||0), 0);

  return (
    <div>
      <h1 style={{ margin:"0 0 1.5rem", fontSize:20, fontWeight:600 }}>Reports &amp; Summary</h1>

      <div style={{ display:"grid", gridTemplateColumns:"repeat(auto-fit,minmax(140px,1fr))", gap:10, marginBottom:16 }}>
        {[
          ["Purchased", totalStock,                    "#534AB7"],
          ["Dispatched", totalSent,                     "#185FA5"],
          ["Received",  totalRec,                       "#3B6D11"],
          ["Pending",   totalSent - totalRec,            "#854F0B"],
          ["Available", totalStock - totalSent,          "#0F6E56"],
          ["Earned",    fmtRs(totalEarned),              "#534AB7"],
          ["Paid",      fmtRs(totalPaid),                "#3B6D11"],
          ["Outstanding",fmtRs(totalEarned-totalPaid),   "#A32D2D"],
        ].map(([l,v,c]) => (
          <div key={l} style={{ background:"#fff", border:"0.5px solid #e5e5e5", borderRadius:8, padding:"12px", textAlign:"center" }}>
            <div style={{ fontSize:18, fontWeight:700, color:c }}>{typeof v === "number" ? fmt(v) : v}</div>
            <div style={{ fontSize:11, color:"#888", marginTop:2 }}>{l}</div>
          </div>
        ))}
      </div>

      <div style={{ display:"grid", gridTemplateColumns:"1fr 1fr", gap:16, marginBottom:16 }}>
        <div style={S.card}>
          <h3 style={{ margin:"0 0 1rem", fontSize:12, fontWeight:600, color:"#888", textTransform:"uppercase", letterSpacing:"0.05em" }}>Stock by Fabric</h3>
          {FABRICS.map(f => {
            const qty = stock.filter(s => s.fabric === f).reduce((a,s) => a + (s.quantity||0), 0);
            const pct = totalStock > 0 ? Math.round((qty / totalStock) * 100) : 0;
            return (
              <div key={f} style={{ marginBottom:10 }}>
                <div style={{ display:"flex", justifyContent:"space-between", fontSize:13, marginBottom:3 }}>
                  <span>{f}</span><span style={{ fontWeight:500 }}>{fmt(qty)} pcs</span>
                </div>
                <div style={{ height:6, background:"#f0f0f0", borderRadius:3 }}>
                  <div style={{ height:6, borderRadius:3, background:"#7F77DD", width:`${pct}%` }} />
                </div>
              </div>
            );
          })}
        </div>

        <div style={S.card}>
          <h3 style={{ margin:"0 0 1rem", fontSize:12, fontWeight:600, color:"#888", textTransform:"uppercase", letterSpacing:"0.05em" }}>Work by Type</h3>
          {workTypes.map(wt => {
            const total = dispatches.filter(d => d.workTypeName === wt.name).reduce((a,d) => a + (d.sentQty||0), 0);
            const pct   = totalSent > 0 ? Math.round((total / totalSent) * 100) : 0;
            return (
              <div key={wt.id} style={{ marginBottom:10 }}>
                <div style={{ display:"flex", justifyContent:"space-between", fontSize:13, marginBottom:3 }}>
                  <span>{wt.name}</span><span style={{ fontWeight:500 }}>{total} pcs</span>
                </div>
                <div style={{ height:6, background:"#f0f0f0", borderRadius:3 }}>
                  <div style={{ height:6, borderRadius:3, background:"#1D9E75", width:`${pct}%` }} />
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <div style={S.card}>
        <h3 style={{ margin:"0 0 1rem", fontSize:12, fontWeight:600, color:"#888", textTransform:"uppercase", letterSpacing:"0.05em" }}>Payment by Worker</h3>
        <div style={{ overflowX:"auto" }}>
          <table style={{ width:"100%", borderCollapse:"collapse", fontSize:13 }}>
            <thead>
              <tr>{["Worker","Earned","Paid","Outstanding","# Payments"].map(h => <th key={h} style={S.th}>{h}</th>)}</tr>
            </thead>
            <tbody>
              {workerSummary.length > 0 ? workerSummary.map(w => (
                <tr key={w.workerId}>
                  <td style={{ ...S.td, fontWeight:500 }}>{w.workerName}</td>
                  <td style={{ ...S.td, color:"#185FA5", fontWeight:600 }}>{fmtRs(w.totalEarned)}</td>
                  <td style={{ ...S.td, color:"#3B6D11", fontWeight:600 }}>{fmtRs(w.totalPaid)}</td>
                  <td style={{ ...S.td, fontWeight:700, color: parseFloat(w.outstanding||0) > 0 ? "#A32D2D" : "#3B6D11" }}>{fmtRs(w.outstanding)}</td>
                  <td style={S.td}>{payments.filter(p => p.workerId === w.workerId).length}</td>
                </tr>
              )) : <tr><td colSpan={5} style={{ ...S.td, textAlign:"center", color:"#aaa", padding:"2rem" }}>No data</td></tr>}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
