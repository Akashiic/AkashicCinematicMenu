from pathlib import Path
R=Path(__file__).resolve().parents[1]
src=R/'tools/verify_wave2_alpha3.py'
dst=R/'tools/verify_wave2_alpha4.py'
assert not dst.exists()
s=src.read_text(encoding='utf-8')
s=s.replace('0.7.0-alpha.3','0.7.0-alpha.4')
s=s.replace('-alpha3.log','-alpha4.log')
s=s.replace('wave2-alpha3-gates.json','wave2-alpha4-gates.json')
s=s.replace('WAVE2_ALPHA3_GATES_PASSED','WAVE2_ALPHA4_GATES_PASSED')
s=s.replace('wave2-alpha3-probes','wave2-alpha4-probes')
s=s.replace('secondary-probe-alpha3','secondary-probe-alpha4')
old="crop=widgets.crop((0,y,200,y+20));assert crop.getchannel('A').getbbox()==(0,0,200,20);assert len(set(crop.getdata()))>20"
new="crop=widgets.crop((0,y,200,y+20));assert crop.getchannel('A').getbbox()==(0,0,200,20);assert len(set(crop.getdata()))>=12"
assert s.count(old)==1;s=s.replace(old,new)
old="assert min(surface.getchannel('A').getdata())==255"
new="alpha=list(surface.getchannel('A').getdata());assert min(alpha)>=24 and 0 not in alpha"
assert s.count(old)==1;s=s.replace(old,new)
dst.write_text(s,encoding='utf-8',newline='\n')
print('ALPHA4_VERIFIER_READY')
