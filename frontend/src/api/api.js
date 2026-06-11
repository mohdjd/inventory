const BASE = "/api";

function getToken() { return localStorage.getItem("token"); }

async function request(method, path, body) {
  const headers = { "Content-Type": "application/json" };
  const token = getToken();
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const res = await fetch(`${BASE}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (res.status === 401) {
    localStorage.removeItem("token");
    window.location.reload();
    return;
  }

  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: res.statusText  }));
    throw new Error(err.error || "Request failed");
  }
  if (res.status === 204) return null;
  return res.json();
}

// Auth
export const login = (data) => request("POST", "/auth/login", data);

// Users
export const getUsers = () => request("GET", "/users");
export const createUser = (data) => request("POST", "/users", data);
export const toggleUserActive = (id) => request("PATCH", `/users/${id}/toggle-active`);
export const resetPassword = (id, pwd) => request("PATCH", `/users/${id}/reset-password`, pwd);

// Dashboard
export const getDashboard = () => request("GET", "/dashboard");

// Stock
export const getStock = () => request("GET", "/stock");
export const createStock = (data) => request("POST", "/stock", data);
export const updateStock = (id, d) => request("PUT", `/stock/${id}`, d);
export const deleteStock = (id) => request("DELETE", `/stock/${id}`);

// Workers
export const getWorkers = () => request("GET", "/workers");
export const getWorkerSummary = () => request("GET", "/workers/summary");
export const createWorker = (data) => request("POST", "/workers", data);
export const updateWorker = (id, d) => request("PUT", `/workers/${id}`, d);
export const deleteWorker = (id) => request("DELETE", `/workers/${id}`);

// Work Types
export const getWorkTypes = () => request("GET", "/work-types");
export const createWorkType = (data) => request("POST", "/work-types", data);
export const updateWorkType = (id, d) => request("PUT", `/work-types/${id}`, d);
export const deleteWorkType = (id) => request("DELETE", `/work-types/${id}`);

// Dispatches
export const getDispatches = (p) => request("GET", `/dispatches?${new URLSearchParams(p || {})}`);
export const getPending = () => request("GET", "/dispatches/pending");
export const createDispatch = (data) => request("POST", "/dispatches", data);
export const recordReceipt = (id, d) => request("PATCH", `/dispatches/${id}/receive`, d);

// Payments
export const getPayments = (p) => request("GET", `/payments?${new URLSearchParams(p || {})}`);
export const createPayment = (data) => request("POST", "/payments", data);
export const deletePayment = (id) => request("DELETE", `/payments/${id}`);
