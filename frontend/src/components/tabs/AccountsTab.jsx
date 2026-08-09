import * as api from "../../api/api";
import S from "../../styles";
import { fmtRs, today } from "../../utils";
import Badge from "../ui/Badge";
import CreatedByCell from "../ui/CreatedByCell";
import SelectField from "../ui/SelectField";

export default function AccountsTab({
  workerSummary, workers, filtPayments,
  payFilterWId, setPayFilterWId,
  payFromDate, setPayFromDate,
  payToDate, setPayToDate,
  setModal, role, save,
}) {
  return (
    <div>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.25rem" }}>
        <div>
          <h1 style={{ margin: 0, fontSize: 20, fontWeight: 600 }}>Accounts &amp; Payments</h1>
          <p style={{ margin: 0, fontSize: 13, color: "#888" }}>Manage payments sent to job workers</p>
        </div>
          <button onClick={() => setModal({ type: "payment", data: { workerId: "", amount: "", paymentDate: today(), paymentMode: "Cash", referenceNo: "", remarks: "" } })} style={S.btnPri}>
            <i className="ti ti-plus" style={{ fontSize: 13 }} /> Send Payment
          </button>
      </div>

      {/* Outstanding Cards */}
      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(220px, 1fr))", gap: 12, marginBottom: "1.5rem" }}>
        {workerSummary.map(w => (
          <div key={w.workerId} style={{ ...S.card, borderLeft: `3px solid ${parseFloat(w.outstanding||0) > 0 ? "#A32D2D" : "#3B6D11"}` }}>
            <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 10 }}>
              <div style={{ width: 32, height: 32, borderRadius: 50, background: "#EEEDFE", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 11, fontWeight: 600, color: "#534AB7" }}>
                {w.workerName.split(" ").map(x => x[0]).join("").slice(0, 2)}
              </div>
              <div style={{ fontWeight: 600, fontSize: 13 }}>{w.workerName}</div>
            </div>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 6 }}>
                {[["Earned", fmtRs(w.totalEarned), "#185FA5"], ["Paid", fmtRs(w.totalPaid), "#3B6D11"]].map(([l, v, c]) => (
                  <div key={l} style={{ background: "#f9f9f7", borderRadius: 6, padding: "6px 8px" }}>
                    <div style={{ fontSize: 13, fontWeight: 600, color: c }}>{v}</div>
                    <div style={{ fontSize: 10, color: "#888" }}>{l}</div>
                  </div>
                ))}
            </div>
            <div style={{ marginTop: 8, paddingTop: 8, borderTop: "1px solid #f0f0f0", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <span style={{ fontSize: 12, color: "#888" }}>Outstanding</span>
              <span style={{ fontSize: 14, fontWeight: 700, color: parseFloat(w.outstanding || 0) > 0 ? "#A32D2D" : "#3B6D11" }}>{fmtRs(w.outstanding)}</span>
            </div>
            <button
              onClick={() => setModal({ type: "payment", data: { workerId: w.workerId, amount: "", paymentDate: today(), paymentMode: "Cash", referenceNo: "", remarks: "" } })}
              style={{ width: "100%", marginTop: 10, ...S.btnPri, justifyContent: "center", fontSize: 12, padding: "6px" }}>
              Pay Now
            </button>
          </div>
        ))}
      </div>

      {/* Payment history */}
      <div style={{ ...S.card, marginTop: 8 }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 14 }}>
          <h3 style={{ margin: 0, fontSize: 14, fontWeight: 600 }}>Payment History</h3>
          <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <SelectField value={payFilterWId} onChange={setPayFilterWId} options={workers.map(w => ({ value: w.id, label: w.name }))} placeholder="All workers" />
            <input type="date" value={payFromDate} onChange={e => setPayFromDate(e.target.value)} style={{ ...S.input, width: 140 }} />
            <input type="date" value={payToDate} onChange={e => setPayToDate(e.target.value)} style={{ ...S.input, width: 140 }} />
          </div>
        </div>
        <div style={{ overflowX: "auto" }}>
          <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 13 }}>
            <thead>
              <tr>{["Worker", "Amount", "Mode", "Ref No", "Date", "Remarks", "Recorded By", ""].map(h => <th key={h} style={S.th}>{h}</th>)}</tr>
            </thead>
            <tbody>
              {filtPayments.map(p => (
                <tr key={p.id}>
                  <td style={{ ...S.td, fontWeight: 500 }}>{p.workerName}</td>
                  <td style={{ ...S.td, fontWeight: 600, color: "#0F6E56" }}>{fmtRs(p.amount)}</td>
                  <td style={S.td }><Badge color="blue">{p.paymentMode || "-"}</Badge></td>
                  <td style={{ ...S.td, color: "#888" }}>{p.referenceNo || "-"}</td>
                  <td style={{ ...S.td, color: "#888" }}>{p.paymentDate}</td>
                  <td style={{ ...S.td, color: "#888" }}>{p.remarks || "-"}</td>
                  <td style={S.td}><CreatedByCell value={p.createdBy} /></td>
                  <td style={S.td}>
                    {role === "ADMIN" && (
                      <button onClick={() => save(() => api.deletePayment(p.id))}
                          style={{ background: "#FCEBEB", color: "#A32D2D", border: "none", borderRadius: 5, padding: "3px 8px", cursor: "pointer", fontSize: 12 }}>Del</button>
                    )}
                  </td>
                </tr>
              ))}
              {!filtPayments.length && (
                <tr><td colSpan={8} style={{ ...S.td, textAlign: "center", color: "#aaa", padding: "3rem" }}>No payments found</td></tr>
              )}
            </tbody>
          </table>
        </div>
        {filtPayments.length > 0 && (
          <div style={{ marginTop: 12, padding: "10px 12px", background: "#f9f9f7", borderRadius: 8, display: "flex", justifyContent: "flex-end", gap: 24, fontSize: 13 }}>
            <span style={{ color: "#888" }}>Total Shown:</span>
            <span style={{ fontWeight: 700, color: "#0F6E56" }}>{fmtRs(filtPayments.reduce((a, p) => a + parseFloat(p.amount||0), 0))}</span>
          </div>
        )}
      </div>
    </div>
  );
}