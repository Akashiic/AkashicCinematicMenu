from pathlib import Path
r=Path(__file__).resolve().parents[1];p=r/'tools/package_n01.py';s=p.read_text(encoding='utf-8')
anchor='sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()';assert s.count(anchor)==1
s=s.replace(anchor,anchor+'\ndef logtext(p):\n    b=p.read_bytes();return b.decode("utf-16" if b.startswith((bytes([255,254]),bytes([254,255]))) else "utf-8")')
for n in ['core-final.log','probe-release-candidate.log']:
    old="(r/'evidence/"+n+"').read_text()";assert s.count(old)==1;s=s.replace(old,"logtext(r/'evidence/"+n+"')")
p.write_text(s,encoding='utf-8',newline='\n');print('LOG_READER_NOW_HANDLES_VERIFIED_UTF16_BOM')
