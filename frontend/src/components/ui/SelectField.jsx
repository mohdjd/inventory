import S from "../../styles";

export default function SelectField({ value, onChange, options, placeholder }) {
  return (
    <select value={value} onChange={(e) => onChange(e.target.value)} style={S.input}>
      {placeholder && <option value="">{placeholder}</option>}
      {options.map(o => <option key={o.value ?? o} value={o.value??o}> {o.label ?? o} </option> )}
    </select>
  );
}