import { useState, useEffect, useMemo, useCallback } from "react";
import * as api from "./api/api";
import { ALL_TABS, MANAGER_TABS, ACCOUNT_TABS } from "./constants";
import { today } from "./utils";

import ErrMsg        from "./components/ui/ErrMsg";
import Loader         from "./components/ui/Loader";
import LoginScreen    from "./components/LoginScreen";
import Header         from "./components/Header";

import DashboardTab from "./components/tabs/DashboardTab";
import StockTab     from "./components/tabs/StockTab";
import DispatchTab  from "./components/tabs/DispatchTab";
import ReceiveTab   from "./components/tabs/ReceiveTab";
import WorkersTab   from "./components/tabs/WorkersTab";
import AccountsTab  from "./components/tabs/AccountsTab";
import ReportsTab   from "./components/tabs/ReportsTab";
import UsersTab     from "./components/tabs/UsersTab";

import AddStockModal   from "./components/modals/AddStockModal";
import DispatchModal   from "./components/modals/DispatchModal";
import ReceiveModal    from "./components/modals/ReceiveModal";
import PaymentModal    from "./components/modals/PaymentModal";
import AddUserModal    from "./components/modals/AddUserModal";
import ResetPwdModal   from "./components/modals/ResetPwdModal";

export default function App() {
  const [authUser, setAuthUser] = useState(() => {
    const t = localStorage.getItem("token");
    if (!t) return null;
    try { const p = JSON.parse(atob(t.split(".")[1])); return { token:t, username:p.sub }; }
    catch { return null; }
  });

  const [activeTab,     setActiveTab]     = useState("Dashboard");
  const [modal,         setModal]         = useState(null);
  const [searchQ,       setSearchQ]       = useState("");
  const [filterWId,     setFilterWId]     = useState("");
  const [payFilterWId,  setPayFilterWId]  = useState("");
  const [payFromDate,   setPayFromDate]   = useState("");
  const [payToDate,     setPayToDate]     = useState("");

  const [dashboard,     setDashboard]     = useState(null);
  const [stock,         setStock]         = useState([]);
  const [dispatches,    setDispatches]    = useState([]);
  const [pending,       setPending]       = useState([]);
  const [workers,       setWorkers]       = useState([]);
  const [workTypes,     setWorkTypes]     = useState([]);
  const [workerSummary,setWorkerSummary]= useState([]);
  const [payments,      setPayments]      = useState([]);
  const [users,         setUsers]         = useState([]);
  const [loading,       setLoading]       = useState(false);
  const [error,         setError]         = useState("");

  const role = authUser?.role || "MANAGER";
  const tabs = role === "ADMIN" ? ALL_TABS : role === "ACCOUNT" ? ACCOUNT_TABS : MANAGER_TABS;

  const load = useCallback(async (tab) => {
    setLoading(true); setError("");
    try {
      if (tab === "Dashboard") {
        const [d, w] = await Promise.all([api.getDashboard(), api.getWorkers()]);
        setDashboard(d); setWorkers(w);
      } else if (tab === "Stock") {
        setStock(await api.getStock());
      } else if (tab === "Dispatch") {
        const [d, w, wt] = await Promise.all([api.getDispatches(), api.getWorkers(), api.getWorkTypes()]);
        setDispatches(d); setWorkers(w); setWorkTypes(wt);
      } else if (tab === "Receive") {
        setPending(await api.getPending());
      } else if (tab === "Workers") {
        const [ws, wt, sum] = await Promise.all([api.getWorkers(), api.getWorkTypes(), api.getWorkerSummary()]);
        setWorkers(ws); setWorkTypes(wt); setWorkerSummary(sum);
      } else if (tab === "Accounts") {
        const [ws, pays, sum] = await Promise.all([api.getWorkers(), api.getPayments(), api.getWorkerSummary()]);
        setWorkers(ws); setPayments(pays); setWorkerSummary(sum);
      } else if (tab === "Reports") {
        const [s, d, pays] = await Promise.all([api.getStock(), api.getDispatches(), api.getPayments()]);
        setStock(s); setDispatches(d); setPayments(pays);
      } else if (tab === "Users") {
        setUsers(await api.getUsers());
      }
    } catch (e) { setError(e.message); }
    finally { setLoading(false); }
  }, []);

  useEffect(() => { if (authUser) load(activeTab); }, [activeTab, authUser, load]);

  // Preload for modals
  useEffect(() => {
    if (!authUser) return;
    api.getWorkers().then(setWorkers).catch(() => {});
    api.getWorkTypes().then(setWorkTypes).catch(() => {});
    api.getStock().then(setStock).catch(() => {});
  }, [authUser]);

  function logout() { localStorage.removeItem("token"); setAuthUser(null); }

  async function save(fn, afterTab) {
    try { await fn(); setModal(null); load(afterTab || activeTab); }
    catch (e) { setError(e.message); }
  }

  const filtDispatches = useMemo(() => dispatches.filter(d =>
    (!filterWId || String(d.workerId) === String(filterWId)) &&
    (!searchQ   || [d.workerName, d.fabric, d.workTypeName].some(v => v?.toLowerCase().includes(searchQ.toLowerCase())))
  ), [dispatches, filterWId, searchQ]);

  const filtPayments = useMemo(() => payments.filter(p =>
    (!payFilterWId || String(p.workerId) === String(payFilterWId)) &&
    (!payFromDate  || p.paymentDate >= payFromDate) &&
    (!payToDate    || p.paymentDate <= payToDate)
  ), [payments, payFilterWId, payFromDate, payToDate]);

  if (!authUser) return <LoginScreen onLogin={u => setAuthUser(u)} />;

  return (
    <div style={{ fontFamily:"-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif", minHeight:"100vh", background:"#f5f5f3" }}>
      <Header tabs={tabs} activeTab={activeTab} setActiveTab={setActiveTab} authUser={authUser} role={role} logout={logout} />

      <div style={{ padding:"1.5rem", maxWidth:1400, margin:"0 auto" }}>
        <ErrMsg msg={error} />
        {loading ? <Loader /> : (
          <>
            {activeTab === "Dashboard" && <DashboardTab dashboard={dashboard} setModal={setModal} />}
            {activeTab === "Stock"     && <StockTab     stock={stock} setModal={setModal} />}
            {activeTab === "Dispatch"  && (
              <DispatchTab
                filtDispatches={filtDispatches} workers={workers}
                searchQ={searchQ} setSearchQ={setSearchQ}
                filterWId={filterWId} setFilterWId={setFilterWId}
                setModal={setModal}
              />
            )}
            {activeTab === "Receive"   && <ReceiveTab pending={pending} setModal={setModal} />}
            {activeTab === "Workers"   && <WorkersTab  workers={workers} workTypes={workTypes} workerSummary={workerSummary} />}
            {activeTab === "Accounts" && (
              <AccountsTab
                workerSummary={workerSummary} workers={workers} filtPayments={filtPayments}
                payFilterWId={payFilterWId} setPayFilterWId={setPayFilterWId}
                payFromDate={payFromDate}     setPayFromDate={setPayFromDate}
                payToDate={payToDate}         setPayToDate={setPayToDate}
                setModal={setModal} role={role} save={save}
              />
            )}
            {activeTab === "Reports" && (
              <ReportsTab stock={stock} dispatches={dispatches} payments={payments} workerSummary={workerSummary} workTypes={workTypes} />
            )}
            {activeTab === "Users" && role === "ADMIN" && (
              <UsersTab users={users} setModal={setModal} save={save} />
            )}
          </>
        )}
      </div>

      {modal?.type === "addStock" && <AddStockModal   modal={modal} setModal={setModal} save={save} />}
      {modal?.type === "dispatch" && <DispatchModal   modal={modal} setModal={setModal} save={save} stock={stock} workers={workers} workTypes={workTypes} />}
      {modal?.type === "receive"  && <ReceiveModal    modal={modal} setModal={setModal} save={save} pending={pending} />}
      {modal?.type === "payment"  && <PaymentModal    modal={modal} setModal={setModal} save={save} workers={workers} workerSummary={workerSummary} />}
      {modal?.type === "addUser"  && <AddUserModal    modal={modal} setModal={setModal} save={save} />}
      {modal?.type === "resetPwd" && <ResetPwdModal   modal={modal} setModal={setModal} save={save} />}
    </div>
  );
}
