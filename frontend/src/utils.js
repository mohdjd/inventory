export const today = () => new Date().toISOString().split("T")[0];
export const fmt = (n) => (n || 0).toLocaleString("en-IN");
export const fmtRs = (n) => "₹" + parseFloat(n || 0).toLocaleString("en-IN", { minimumFractionDigits: 2 });
export const statusColor = s => ({ COMPLETED: "green", PARTIAL:"amber", PENDING:"red" }[s?.toUpperCase()] || "red");
