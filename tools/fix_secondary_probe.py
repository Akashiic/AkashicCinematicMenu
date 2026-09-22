from pathlib import Path
import json
R=Path(__file__).resolve().parents[1]
p=R/'tools/SecondaryGraphicsProbe.java'
s=p.read_text(encoding='utf-8')
old='            byte[] first=pixels(1366,768);draw(renderer,1366,768,"CONFIGURAÇÕES");\n'
new='            draw(renderer,1366,768,"CONFIGURAÇÕES");byte[] first=pixels(1366,768);draw(renderer,1366,768,"CONFIGURAÇÕES");\n'
assert s.count(old)==1
p.write_text(s.replace(old,new),encoding='utf-8',newline='\n')
report={
    'failed_run':'secondary-graphics.log',
    'finding':'The determinism assertion sampled 1366x768 from the previous 2560x1440 framebuffer before rendering the first 1366 frame.',
    'product_changed':False,
    'correction':'Render two consecutive 1366x768 CONFIGURAÇÕES frames before comparing pixels.',
    'prior_failure_preserved':True
}
(R/'evidence/secondary-probe-test-correction.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
print('PROBE_CORRECTED_PRODUCT_UNCHANGED')
