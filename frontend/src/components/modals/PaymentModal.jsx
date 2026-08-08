import * as api from "../../api/api";
import { PAY_MODES } from "../../constants";
import { fmtRs } from "../../utils";
import Modal from "../ui/Modal";
import FormField from "../ui/FormField";
import SelectField from "../ui/SelectField";
import InputField from "../ui/InputField";
import ModalFooter from "../ui/ModalFooter";

export default function PaymentModal({ modal, setModal, save, workers, workerSummary }) {
  const { data } = modal;
  const set = (k, v) => setModal(m => ({ ...m, data: { ...m.data, [k]: v } }));

  const sum = data.workerId ? workerSummary.find(s => String(s.workerId) === String(data.workerId)) : null;

  function handleSave() {
    save(() => api.createPayment({
      workerId: +data.workerId,
      amount: +data.amount,
      paymentDate: data.paymentDate,
      paymentMode: data.paymentMode,
      referenceNo: data.referenceNo,
      remarks: data.remarks,
    }));
  }

  return (
    <Modal title="Send Payment to Worker" onClose={() => setModal(null)}>
      <FormField label="Worker">
        <SelectField value={data.workerId} onChange={v => set("workerId", v)} options={workers.map(w => ({ value: w.id, label: w.name }))} placeholder="Select worker" />
      </FormField>

      {sum && (
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8, marginBottom: 12 }}>
          {[
            ["Earned",      fmtRs(sum.totalEarned), "#185FA5"],
            ["Paid",        fmtRs(sum.totalPaid),   "#386D11"],
            ["Outstanding", fmtRs(sum.outstanding), parseFloat(sum.outstanding || 0) > 0 ? "#A32D20" : "#386D11"],
          ].map(([l, v, c]) => (
            <div key={l} style={{ background: "#f9f9f7", borderRadius: 8, padding: "10px", textAlign: "center" }}>
              <div style={{ fontSize: 15, fontWeight: 700, color: c }}>{v}</div>
              <div style={{ fontSize: 11, color: "#888" }}>{l}</div>
            </div>
          ))}
        </div>
      )}

      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
        <FormField label="Amount ₹"> <InputField type="number" min="0.01" value={data.amount} onChange={v => set("amount", v)} placeholder="0.00" /> </FormField>
        <FormField label="Payment Date"> <InputField type="date" value={data.paymentDate} onChange={v => set("paymentDate", v)} /> </FormField>
        <FormField label="Payment Mode"> <SelectField value={data.paymentMode} onChange={v => set("paymentMode", v)} options={PAY_MODES} /> </FormField>
        <FormField label="Reference No"> <InputField value={data.referenceNo} onChange={v => set("referenceNo", v)} placeholder="UTR / Cheque No" /> </FormField>
        <div style={{ gridColumn : "1/-1" }}>
         <FormField label="Remarks"> <InputField value={data.remarks} onChange={v => set("remarks", v)} placeholder="Optional note" /> </FormField>
        </div>
      </div>
      <ModalFooter onCancel={() => setModal(null)} onSave={handleSave} saveLabel="Send Payment" saveColor="#34A34F" />
    </Modal>
  );
}