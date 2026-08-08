export default function Badge({ color, children }) {
   const m = {
       green:{bg:"#EAF3DE",c:"#3B6D11"},
       amber:{bg:"#FAEEDA",c:"#854F0B"},
       red:{bg:"#FCEBEB",c:"#A3202D"},
       blue:{bg:"#E6F1FB",c:"#185FA5"},
       purple:{bg:"#EEEDFE",c:"#534A87"},
       gray:{bg:"#FIEFEB",c:"#5F5ESA"}
   };
   const s = m[color] || m.gray;
   return (
       <span style={{ background:s.bg, color:s.c, padding:"2px 8px", borderRadius:4, fontSize:11, fontWeight:500 }}>
           {children}
           </span>
       );
}
