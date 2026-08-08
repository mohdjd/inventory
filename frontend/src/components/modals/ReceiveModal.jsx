import * as api from "../../api/api";
import Modal from "../ui/Modal";
import FormField from "../ui/FormField";
import SelectField from "../ui/SelectField";
import InputField from "../ui/InputField";
import ModalFooter from "../ui/ModalFooter";

export default function ReceiveModal({ modal, setModal, save, pending }) {
  const { data } = modal;
  const set = (k, v) => setModal(m => ({ ...m, data: { ...m.data, [k]: v } }));

  return (
    <Modal title="Record Receipt from Worker" onClose={() => setModal(null)}>
      <FormField label="Select Dispatch">
        <SelectField
          value={data.dispatchId}
          onChange={v => set("dispatchId", v)}
          options={pending.map(d => ({ value: d.id, label: `${d.workerName} - ${d.fabric} - ${d.workTypeName} - Pending: ${d.pendingQty} pcs` }))}
          placeholder="Select dispatch"
        />
      </FormField>

      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
        <FormField label="Qty Received Now">
          <InputField type="number" min="1" value={data.quantity} onChange={v => set("quantity", v)} placeholder="pieces" />
        </FormField>
        <FormField label="Receipt Date">
          <InputField type="date" value={data.receivedDate} onChange={v => set("receivedDate", v)} />
        </FormField>
      </div>

      <ModalFooter
        onCancel={() => setModal(null)}
        onSave={() => save(() => api.recordReceipt(data.dispatchId, { quantity: +data.quantity, receivedDate: data.receivedDate }))}
        saveLabel="Save Receipt"
        saveColor="#0F6E56"
      />
    </Modal>
  );
}