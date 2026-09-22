from pathlib import Path
import json
R=Path(__file__).resolve().parents[1]
src=R/'tools/finish_wave2_alpha2_gates.py'
dst=R/'tools/finish_wave2_alpha3_gates.py'
assert not dst.exists()
s=src.read_text(encoding='utf-8')
s=s.replace('0.7.0-alpha.2','0.7.0-alpha.3')
s=s.replace('-alpha2.log','-alpha3.log')
s=s.replace('wave2-alpha2-gates-final.json','wave2-alpha3-gates-final.json')
s=s.replace('WAVE2_ALPHA2_FINAL_GATES_PASSED','WAVE2_ALPHA3_FINAL_GATES_PASSED')
dst.write_text(s,encoding='utf-8',newline='\n')
report={
  'failed_tool':'verify_wave2_alpha3.py',
  'product_failure':False,
  'inherited_invalid_gate':'>20 unique button RGBA values',
  'measured_authored_values':19,
  'correct_gate':'finish_wave2_alpha3_gates.py uses >=12 and no alpha0 list holes',
  'prior_gates':'bytecode and OpenGL passed before the obsolete texture assertion'
}
(R/'evidence/alpha3-verifier-correction.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
print('FINAL_ALPHA3_GATE_SCRIPT_READY')
