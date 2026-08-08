import * as api from "../../api/api";
import Modal from "../ui/Modal";
import FormField from "../ui/FormField";
import SelectField from "../ui/SelectField";
import InputField from "../ui/InputField";
import ModalFooter from "../ui/ModalFooter";

export default function AddUserModal({ modal, setModal, save }) {
  const { data } = modal;
  const setF = (k, v) => setModal(m => ({ ...m, data: { ...m.data, [k]: v } }));

  return (
    <Modal title="Add New User" onClose={() => setModal(null)}>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
        <FormField label="Full Name"><InputField value={data.fullName} onChange={v => setF("fullName", v)} placeholder="e.g. Ramesh Kumar" /></FormField>
        <FormField label="Username"><InputField value={data.username} onChange={v => setF("username", v)} placeholder="e.g. ramesh" /></FormField>
        <FormField label="Password"><InputField type="password" value={data.password} onChange={v => setF("password", v)} placeholder="min 6 characters" /></FormField>
        <FormField label="Role"> <SelectField value={data.role} onChange={v => setF("role", v)} options={["ADMIN", "MANAGER", "ACCOUNT"]} /></FormField>
      </div>
      <ModalFooter
        onCancel={() => setModal(null)}
        onSave={() => save(() => api.createUser(data, "Users"))}
        saveLabel="Create User"
      />
    </Modal>
  );
}