import S from "../../styles";

export default function InputField({ value, onChange, type="text", placeholder, min }) {
  return (
      <input
      type={type}
      value={value}
      onChange={(e) => onChange(e.target.value)}
      placeholder={placeholder}
      min={min}
      style={S.input}
      />
  );
}
