import * as api from "../../api/api";
import Modal from "../ui/Modal";
import FormField from "../ui/FormField";
import InputField from "../ui/InputField";
import ModalFooter from "../ui/ModalFooter";

export default function ResetPwdModal({ modal, setModal, save }) {
  const { data } = modal;
  const setF = (k, v) => setModal(m => ({ ...m, data: { ...m.data, [k]: v } }));

  return (
    <Modal title={`Reset Password — @${data.username}`} onClose={() => setModal(null)}>
      <FormField label="New Password">
        <InputField type="password" value={data.password} onChange={v => setF("password", v)} placeholder="min 6 characters" />
      </FormField>
      <ModalFooter
        onCancel={() => setModal(null)}
        onSave={() => save(() => api.resetPassword(data.userId, data.password, "Users"))}
        saveLabel="Reset Password"
        saveColor="#854F0B"
      />
    </Modal>
  );
}