from pathlib import Path
import json,zipfile,hashlib,time
P=Path(__file__).resolve().parents[1];D=Path.home()/'AppData/Roaming/.technic/modpacks/forbidden-series';E=P/'evidence-bgm-menu-20260921';E.mkdir(exist_ok=True)
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
report={'at':time.time(),'daily_jars':[],'audio_settings':[],'music_events':[],'bounded_log_reads':[]}
for f in (D/'mods').glob('*CinematicMenu*.jar'):
    with zipfile.ZipFile(f) as z:report['daily_jars'].append({'name':f.name,'sha256':sha(f),'recording_entries':[n for n in z.namelist() if 'bgm' in n and n.endswith('.ogg')]})
report['audio_settings']=[l for l in (D/'options.txt').read_text(errors='replace').splitlines() if l.startswith(('soundCategory_music:','soundCategory_master:','soundCategory_records:','resourcePacks:'))]
for directory in [D/'mods',D/'resourcepacks']:
    for f in directory.glob('*'):
        if not f.is_file() or f.suffix.lower() not in ['.jar','.zip']:continue
        try:
            with zipfile.ZipFile(f) as z:
                for info in z.infolist():
                    if not info.filename.endswith('/sounds.json') or info.file_size>3000000:continue
                    try:events=json.loads(z.read(info).decode('utf-8-sig'))
                    except (ValueError,UnicodeError):continue
                    if not isinstance(events,dict):continue
                    for key,value in events.items():
                        if isinstance(value,dict) and (key.startswith('music') or value.get('category')=='music'):
                            report['music_events'].append({'container':f.name,'asset':info.filename,'event':key,'category':value.get('category'),'replace':value.get('replace',False),'sounds':value.get('sounds',[])})
        except zipfile.BadZipFile:pass
for name in ['latest.log','fml-client-latest.log']:
    f=D/'logs'/name
    if not f.exists():continue
    with f.open('rb') as src:src.seek(max(0,f.stat().st_size-131072));raw=src.read(131072)
    lines=raw.decode('utf-8',errors='replace').splitlines();relevant=[l[:500] for l in lines if any(s.lower() in l.lower() for s in ['BGM','bgm.menu','music.menu','soundtrack'])]
    report['bounded_log_reads'].append({'name':name,'file_bytes':f.stat().st_size,'tail_bytes_read':len(raw),'matches':relevant[-12:]})
(E/'daily-audit.json').write_text(json.dumps(report,indent=2),encoding='utf-8');print(json.dumps({k:v for k,v in report.items() if k!='music_events'},indent=2));print('MUSIC_EVENTS',len(report['music_events']));print(json.dumps([x for x in report['music_events'] if x['event']=='music.menu' or 'etfuturum' in x['container'].lower()],indent=2)[:12000])
