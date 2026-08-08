import * as api from "../../api/api";
import S from "../../styles";
import Badge from "../ui/Badge";

export default function UsersTab({ users, setModal, save }) {
  return (
    <div>
      <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:"1.25rem" }}>
        <div>
          <h1 style={{ margin:0, fontSize:20, fontWeight:600 }}>User Management</h1>
          <p style={{ margin:0, fontSize:13, color:"#888" }}>Manage who can access this system</p>
        </div>
        <button onClick={() => setModal({ type:"addUser", data:{ username:"", password:"", fullName:"", role:"MANAGER" } })} style={S.btnPri}>
          <i className="ti ti-plus" style={{ fontSize:13 }} /> Add User
        </button>
      </div>

      <div style={{ display:"grid", gridTemplateColumns:"repeat(auto-fill,minmax(280px,1fr))", gap:14 }}>
        {users.map(u => (
          <div key={u.id} style={{ ...S.card, opacity: u.active ? 1 : 0.6 }}>
            <div style={{ display:"flex", alignItems:"center", gap:10, marginBottom:12 }}>
              <div style={{ width:40, height:40, borderRadius:50, background: u.role==="ADMIN" ? "#EEEDFE" : "#E1F5EE", display:"flex", alignItems:"center", justifyContent:"center",
                fontSize:14, fontWeight:700, color: u.role==="ADMIN" ? "#534AB7" : "#0F6E56" }}>
                {u.fullName?.[0]?.toUpperCase()}
              </div>
              <div style={{ flex:1 }}>
                <div style={{ fontWeight:600, fontSize:14 }}>{u.fullName}</div>
                <div style={{ fontSize:11, color:"#888" }}>@{u.username}</div>
              </div>
              <Badge color={u.role === "ADMIN" ? "purple" : "blue"}>{u.role}</Badge>
            </div>
            <div style={{ display:"flex", gap:6, marginTop:8 }}>
              <button onClick={() => save(() => api.toggleUserActive(u.id), "Users")}
                style={{ flex:1, background: u.active ? "#FCEBEB" : "#EAF3DE", color: u.active ? "#A32D2D" : "#3B6D11", border:"none", borderRadius:6, padding:"6px", cursor:"pointer", fontSize:12, fontWeight:500 }}>
                {u.active ? "Deactivate" : "Activate"}
              </button>
              <button onClick={() => setModal({ type:"resetPwd", data:{ userId:u.id, username:u.username, password:"" } })}
                style={{ flex:1, background:"#f0f0f0", color:"#555", border:"none", borderRadius:6, padding:"6px", cursor:"pointer", fontSize:12, fontWeight:500 }}>
                Reset Password
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
