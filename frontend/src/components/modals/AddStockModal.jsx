import * as api from "../../api/api";
import { LABELS, FABRICS, SIZES, WEIGHTS } from "../../constants";
import Modal from "../ui/Modal";
import FormField from "../ui/FormField";
import SelectField from "../ui/SelectField";
import InputField from "../ui/InputField";
import ModalFooter from "../ui/ModalFooter";

export default function AddStockModal({ modal, setModal, save }) {
  const { data } = modal;
  const setF = (k, v) => setModal(m => ({ ...m, data: { ...m.data, [k]: v } }));

  return (
    <Modal title="Add Stock Purchase" onClose={() => setModal(null)}>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
        <FormField label="Label"> <SelectField value={data.label} onChange={v => setF("label", v)} options={LABELS} placeholder="Select" /> </FormField>
        <FormField label="Fabric"> <SelectField value={data.fabric} onChange={v => setF("fabric", v)} options={FABRICS} placeholder="Select" /> </FormField>
        <FormField label="Size"> <SelectField value={data.size} onChange={v => setF("size", v)} options={SIZES} placeholder="Select" /> </FormField>
        <FormField label="Weight"> <SelectField value={data.weight} onChange={v => setF("weight", v)} options={WEIGHTS} placeholder="Select" /> </FormField>
        <FormField label="Qty (pcs)"> <InputField type="number" min="1" value={data.quantity} onChange={v => setF("quantity", v)} placeholder="e.g. 500" /> </FormField>
        <FormField label="Date"> <InputField type="date" value={data.purchaseDate} onChange={v => setF("purchaseDate", v)} /> </FormField>
      </div>
      <ModalFooter
        onCancel={() => setModal(null)}
        onSave={() => save(() => api.createStock({ ...data, quantity: +data.quantity }))}
        saveLabel="Save Stock"
      />
    </Modal>
  );
}