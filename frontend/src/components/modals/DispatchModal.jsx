import * as api from "../../api/api";
import Modal from "../ui/Modal";
import FormField from "../ui/FormField";
import SelectField from "../ui/SelectField";
import InputField from "../ui/InputField";
import ModalFooter from "../ui/ModalFooter";

export default function DispatchModal({ modal, setModal, save, stock, workers, workTypes }) {
  const { data } = modal;
  const set = (k, v) => setModal(m => ({ ...m, data: { ...m.data, [k]: v } }));

  function handleWorkType(v) {
    const wt = workTypes.find(x => String(x.id) === String(v));
    setModal(m => ({ ...m, data: { ...m.data, workTypeId: v, pricePerPiece: wt?.pricePerPiece || "" } }));
  }

  function handleSave() {
    const body = {
      stockItemId: +data.stockItemId,
      workerId: +data.workerId,
      workTypeId: +data.workTypeId,
      sentQty: +data.sentQty,
      sentDate: data.sentDate,
    };
    if (data.pricePerPiece) body.pricePerPiece = +data.pricePerPiece;
    save(() => api.createDispatch(body));
  }

  return (
    <Modal title="Dispatch to Job Worker" onClose={() => setModal(null)}>
      <FormField label="Stock Item">
        <SelectField value={data.stockItemId} onChange={v => set("stockItemId", v)}
            options={stock.map(s => ({ value: s.id, label: `${s.label} . ${s.fabric} . ${s.size} . ${s.weight} . ${s.quantity} pcs` }))}
          placeholder="Select stock" />
      </FormField>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
        <FormField label="Worker">
          <SelectField
            value={data.workerId}
            onChange={v => set("workerId", v)}
            options={workers.map(w => ({ value: w.id, label: w.name }))}
            placeholder="Select worker"
          />
        </FormField>
        <FormField label="Work Type">
          <SelectField
            value={data.workTypeId}
            onChange={handleWorkType}
            options={workTypes.map(w => ({ value: w.id, label: w.name }))}
            placeholder="Select work type"
          />
        </FormField>
      </div>
      <FormField label="Price (Piece ₹)">
        <InputField type="number" min="0" value={data.pricePerPiece} onChange={v => set("pricePerPiece", v)} placeholder="Auto from work type" />
      </FormField>
      <FormField label="Quantity">
        <InputField type="number" min="1" value={data.sentQty} onChange={v => set("sentQty", v)} placeholder="pieces" />
      </FormField>
      <FormField label="Dispatch Date">
        <InputField type="date" value={data.sentDate} onChange={v => set("sentDate", v)} />
      </FormField>
      <ModalFooter onCancel={() => setModal(null)} onSave={handleSave} saveLabel="Dispatch" />
    </Modal>
  );
}