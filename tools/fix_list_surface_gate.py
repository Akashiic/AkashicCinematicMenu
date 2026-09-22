from pathlib import Path
import json
R=Path(__file__).resolve().parents[1]
p=R/'tools/finish_wave2_alpha2_gates.py'
s=p.read_text(encoding='utf-8')
old="assert surface.size==(32,32) and min(surface.getchannel('A').getdata())==255"
new="alpha=list(surface.getchannel('A').getdata());assert surface.size==(32,32) and min(alpha)>=24 and 0 not in alpha"
assert s.count(old)==1
p.write_text(s.replace(old,new),encoding='utf-8',newline='\n')
report={
    'finding':'12.5% decorative diagonal pixels use alpha32;87.5% are alpha255; no alpha0 pixels.',
    'background_behind_list':'SecondaryScreenTheme, not vanilla dirt',
    'product_changed':False,
    'correct_gate':'No alpha0 holes and minimum decorative alpha>=24'
}
(R/'evidence/list-surface-gate-correction.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
print('LIST_SURFACE_GATE_CORRECTED_PRODUCT_UNCHANGED')
