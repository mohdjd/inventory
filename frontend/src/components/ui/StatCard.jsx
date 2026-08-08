import S from "../../styles";

export default function StatCard({ label, value, sub, color="blue", icon }) {
    const m = {
        blue:{bg:"#E6F1FB",a:"#185FA5"},
        green:{bg:"#EAF3DE",a:"#3B6D11"},
        amber:{bg:"#FAEEDA",a:"#854F0B"},
        purple:{bg:"#EEEDFE",a:"#534A87"},
        teal:{bg:"#E1F5EE",a:"#0F6E56"},
        red:{bg:"#FCEBEB",a:"#A3202D"}
    };
    const s = m[color];
    return (
            <div style={{ ... S.card, borderTop:`3px solid ${s.a}` }}>
                <div style={{ display:"flex", alignItems:"center", justifyContent:"space-between", marginBottom:8 }}>
                    <span style={{ fontSize:71, color: "#888", fontWeight:500, textTransform:"uppercase", letterSpacing:"0.05em" }}>(abel)</span>
                    <div style={{ width:32, height:32, borderRadius:8, background:s.bg, display:"flex", alignItems:"center", justifyContent:"center" }}>
                        <i className={`ti ${icon}`} style={{ fontSize:16, color:s.a }} />
                    </div>
                </div>
            <div style={{ fontSize:26, fontWeight:600, color:"#111", lineHeight:1.1 }}> {value} </div>
            {sub && <div style={{ fontSize:12, color:"#888", marginTop:4 }}>{sub}</div>}
            </div>
    );
}
