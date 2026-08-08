import S from "../../styles";
import { fmt, fmtRs, statusColor, today } from "../../utils";
import Badge from "../ui/Badge";
import StatCard from "../ui/StatCard"

export default function DashboardTab({ dashboard, setModal}) {
    if (!dashboard) return null;
    return (
        <div>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.25rem" }}>
                <div>
                    <h1 style={{ margin: 0, fontSize: 20, fontWeight: 600 }}>Dashboard</h1>
                    <p style={{ margin: 0, fontSize: 13, color: "#888" }}>Overview of stoles inventory &amp; job work</p>
                </div>
                <div style={{ display: "flex", gap:8}}>
                    <button onClick={() => setModal({ type: "addStock", data: { label: "", fabric: "", size: "", weight: "", quantity: "", purchaseDate: today() } })} style ={S.btnSec}>
                        <i className="ti ti-plus" style={{ fontSize: 13 }} />Add Stock
                    </button>
                    <button onClick={() => setModal({ type: "dispatch", data: { stockItemId: "", workerId: "", workTypeId: "", pricePerPiece: "", sentQty: "", sentDate: today() } })} style ={S.btnPri}>
                        <i className="ti ti-plus" style={{ fontSize: 13 }} />Dispatch
                    </button>
                </div>
            </div>

            <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(155px, 1fr))", gap: 12, marginBottom: "1.5rem" }}>
                <StatCard label="Total Stock"           value={fmt(dashboard.totalStockPieces)}         sub="pieces purchased"  color="purple"    icon="ti-package"  />
                <StatCard label="Available"             value={fmt(dashboard.availableStock)}           sub="ready to dispatch"  color="teal"    icon="ti-stack-2"  />
                <StatCard label="Sent to Workers"       value={fmt(dashboard.totalSent)}                sub="pieces dispatch"  color="blue"    icon="ti-send"  />
                <StatCard label="Received Back"         value={fmt(dashboard.totalReceived)}            sub="pieces completed"  color="green"    icon="ti-inbox"  />
                <StatCard label="Pending at Workers"    value={fmt(dashboard.totalPendingAtWorker)}     sub="in progress"  color="amber"    icon="ti-clock"  />
                <StatCard label="Outstanding"           value={fmtRs(dashboard.totalOutstanding)}       sub="payment due"  color="red"    icon="ti-alert-circle"  />

            </div>

            <div>
                <div style={S.card}>
                    <h3 style={{ margin: "0 0 1rem", fontSize: 12, fontWeight: 600, color:"#888", textTransform:"uppercase", letterSpacing:"0.05em" }}>Worker Outstanding</h3>
                      {(dashboard.workerSummary || []).length === 0
                          ? <p>No active dispatches</p>
                          : (dashboard.workerSummary || []).map( w=> (
                        <div  key={w.workerId} style={{ display:"flex", alignItems: "center", gap: 8, marginBottom:10 }}>
                            <div style={{ width: 30, height:30, boarderRadius:50, background:"#EEEDFE", display:"flex", alignItems:"center", justifyContent:"center", fontSize:11, fontWeight:600, color:"#534AB7", flexShrink:0 }}>
                                {w.workerName.split(" ").map(x => x[0]).join("").slice(0,2)}
                            </div>

                            <div style={{ flex:1, minWidth:0 }}>
                                <div style={{fontSize: 13, fontWeight: 500, color:"#111"}}>{w.workerName}</div>
                                <div style={{fontSize: 11, color:"#888"}}>{fmtRs(w.totalEarned)} earned - {fmtRs(w.totalPaid)} paid</div>
                            </div>

                            <span style={{fontSize: 12, fontWeight: 600, color:parseFloat(w.outstanding||0 ? "#A32D2D" : "#3B6D11", flexShrink:0 )}}>
                            {fmtRs(w.outstanding)} due
                            </span>
                        </div>
                      ))}
                </div>

                <div style={S.card}>
                   <h3 style={{ margin: "0 0 1rem", fontSize: 12, fontWeight: 600, color:"#888", textTransform:"uppercase", letterSpacing:"0.05em" }}>Recent Dispatch</h3>
                         {(dashboard.recentDispatch || []).map(d => (
                             <div  key={d.id} style={{ display:"flex", alignItems: "center", justifyContent:"space-between", paddingBottom:8, marginBottom:8, boarderBottom:"0.05px solid #f0f0f0"}}>
                                <div>
                                    <div style={{fontSize: 13, fontWeight: 500, color:"#111"}}>{d.workerName}</div>
                                    <div style={{fontSize: 11, color:"#888"}}>{d.fabric} - {d.workerTypeName)} </div>
                                </div>
                                <div style={{textAlign:"right" }}>
                                    <div>{d.quantity} pcs</div>
                                    <Badge color={statusColor(d.status)}>{d.status}</Badge>
                                </div>
                             </div>
                         ))}
                </div>
            </div>
        </div>
    );
}