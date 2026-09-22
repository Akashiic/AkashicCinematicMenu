from pathlib import Path
R=Path(__file__).resolve().parents[1]
src=R/'tools/verify_wave2_alpha2.py'
dst=R/'tools/verify_wave2_alpha3.py'
assert not dst.exists()
s=src.read_text(encoding='utf-8')
s=s.replace('0.7.0-alpha.2','0.7.0-alpha.3')
s=s.replace('-alpha2.log','-alpha3.log')
s=s.replace('wave2-alpha2-gates.json','wave2-alpha3-gates.json')
s=s.replace('WAVE2_ALPHA2_GATES_PASSED','WAVE2_ALPHA3_GATES_PASSED')
s=s.replace('wave2-alpha2-probes','wave2-alpha3-probes')
s=s.replace('secondary-probe-alpha2','secondary-probe-alpha3')
dst.write_text(s,encoding='utf-8',newline='\n')
print('ALPHA3_VERIFIER_READY')
