import S from "../../styles";
import { fmtRs } from "../../utils";

export default function WorkersTab({ workers, workTypes, workerSummary }) {
    return (
        <div>
            <h1 style={{ margin: "0 0 1.25rem", fontSize:20, fontWeight:600 }}>Job Workers</h1>

            <div style={{ display:"grid", gridTemplateColumns:"repeat(auto-fill,minmax(260px,1fr))", gap:14, marginBottom:"2rem" }}>
                {workers.map(w => {
                    const sum = workerSummary.find(s => s.workerId === w.id) || {};
                    const sent = sum.totalSent     || 0;
                    const recv = sum.totalReceived || 0;
                    const pct = sent > 0 ? Math.round((recv / sent) * 100) : 0;
                    return (
                        <div key={w.id} style={S.card}>

                            <div style={{ display:"flex", alignItems:"center", gap:10, marginBottom:12 }}>
                                <div style={{ width:40, height:40, borderRadius:50, background:"#EEEDFE", display:"flex", alignItems:"center", justifyContent:"center", fontSize:13, fontWeight:600, color:"#534AB7" }}>
                                    {w.name.split(" ").map(x => x[o]).join("").slice(0,2)}
                                </div>
                                <div>
                                    <div style={{ fontWeight:600, fontSize:14 }}>{w.name}</div>
                                    <div style={{ fontSize:11, color:"#888" }}>{w.phone} . {w.totalJobs || 0} jobs</div>
                                </div>
                            </div>

                            <div style={{ display:"grid", gridTemplateColumns:"1fr 1fr 1fr", gap:6, marginBottom:10 }}>
                                {[["Sent",sent, "#185FA5"], ["Received",recv, "#3B6D11"],["Pending",(sum.pending ||0), (sum.pending||0)>0?"#854F0B":"#3B6D11"]].map(([t,v,c]) => (
                                <div key={l} style={{ background : "#f9f9f7", borderRadius:6, padding:"6px 0", textAlign:"center" }}>
                                    <div style={{ fontSize:14, fontWeight:600, color:c }}>{v}</div>
                                    <div style={{ fontSize:10, color:"#888" }}>{l}</div>
                                </div>
                                ))}
                            </div>

                            <div style={{ height:4, background:"#f0f0f0", borderRadius:2, marginBottom:10 }}>
                                <div style={{ height:4, borderRadius:2, background:"#1D9E75", width:`${pct}%`, transition:"width 0.4s" }} />
                            </div>


                            <div style= {{ display:"flex", justifyContent:"space-between", fontSize:12, color:"#888" }}>
                                <span>Earned / Paid / Due</span>
                                <span style={{ fontWeight:600, color:"#111" }}>
                                    {fmtRs(sum.totalEarned)} / {fmtRs(sum.totalPaid)} / <span style={{ color: parseFloat(sum.outstanding||0) > 0 ? "#A32D2D" : "#3B6D11" }}> {fmtRs(sum.outstanding)}</span>
                                </span>
                            </div>

                        </div>
                    );
                })}
            </div>

            <h2 style={{ fontSize:15, fontWeight:600, margin:"0 0 1rem" }}>Work Type Pricing</h2>
            <div style={{ ...S.card, padding:0, overflow:"hidden" }}>
                <table style={{ width:"100%", borderCollapse:"collapse", fontSize:13 }}>
                    <thead><tr>{["Work Type","Price per Piece"].map(h => <th key={h} style={S.th}>{h}</th>)}</tr></thead>
                    <tbody>
                        {workTypes.map(wt => (
                        <tr key={wt.id}>
                            <td style={S.td}>{wt.name}</td>
                            <td style={{ ...S.td, fontWeight:600, color:"#534AB7" }}>₹{wt.pricePerPiece}</td>
                        </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}