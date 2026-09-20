from pathlib import Path
import hashlib,json,zipfile
R=Path(__file__).resolve().parents[1];old=R/'dist/AkashicCinematicMenu-0.3.0-alpha.1.jar'
assert hashlib.sha256(old.read_bytes()).hexdigest()=='b038bf26f0197e70d2dbd36f930f49e45c032d050fcea42c462dac9a45df90f2'
backup=R/'evidence/source-alpha1-before-listener-fix.zip';assert not backup.exists()
with zipfile.ZipFile(backup,'x',compression=zipfile.ZIP_DEFLATED) as z:
    for p in (R/'src').rglob('*'):
        if p.is_file():z.write(p,p.relative_to(R).as_posix())
p=R/'src/main/java/br/com/redeakashic/cinematic/client/LateBogoNoticeGate.java';s=p.read_text();assert s.count('final class LateBogoNoticeGate {')==1 and 'public final class' not in s;s=s.replace('final class LateBogoNoticeGate {','public final class LateBogoNoticeGate {');p.write_text(s,newline='\n')
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','src/main/resources/META-INF/MANIFEST.MF','tools/build_native.py']:
    p=R/name;s=p.read_text();assert '0.3.0-alpha.1' in s;p.write_text(s.replace('0.3.0-alpha.1','0.3.0-alpha.2'),newline='\n')
(R/'evidence/alpha1-rejection.json').write_text(json.dumps({'status':'REJECTED_RUNTIME','run':'n06run1','error':'IllegalAccessError from Forge generated event adapter to package-private LateBogoNoticeGate','fix':'Public subscriber class; add bytecode accessibility gate','source_backup':str(backup),'artifact_preserved':str(old),'resize_visual_test_passed_before_failure':True},indent=2))
print('ALPHA1_REJECTED_AND_PRESERVED; ALPHA2_LISTENER_ACCESS_CORRECTED',flush=True)
