from pathlib import Path
import json,subprocess,re
R=Path(__file__).resolve().parents[1];Q=R.parents[1]/'qa-n08-bgm-6c79c577';hits=json.loads((R/'evidence/sound-event-consumers.json').read_text());out=R/'evidence/sound-hooks';out.mkdir(exist_ok=False)
for row in hits:
    if any(x in row['class'] for x in ['MenuBgm','META-INF','AetherMusicHandler']):continue
    name=row['class'][:-6].replace('/','.');res=subprocess.run(['javap','-p','-c','-classpath',str(Q/'instance/mods'/row['jar']),name],capture_output=True,text=True,timeout=15)
    sections=re.split(r'\n(?=  (?:public|private|protected))',res.stdout);parts=[x for x in sections if 'PlaySoundEvent17)' in x]
    (out/(name+'.txt')).write_text('\n'.join(parts),encoding='utf-8');print(name,len('\n'.join(parts)),flush=True)
