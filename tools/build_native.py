"""Offline native build using inspected, existing development artifacts (never stubs).

Default paths match the audited Windows workspace. Else provide
--classpath-json pointing to a JSON array of real SRG Minecraft/Forge/LWJGL/log4j jars.
Minecraft must use SRG member names; the adapter source intentionally uses that ABI.
No dependency is copied into the output jar. No game installation is modified.
"""
from pathlib import Path
import argparse, hashlib, json, os, shutil, struct, subprocess, sys, tempfile, zipfile

ROOT = Path(__file__).resolve().parents[1]
VERSION = '0.7.0-alpha.4'

def sha(p):
    with Path(p).open('rb') as stream:
        return hashlib.file_digest(stream, 'sha256').hexdigest() if hasattr(hashlib, 'file_digest') else hashlib.sha256(stream.read()).hexdigest()

def pick(root, pattern):
    paths = sorted(root.glob(pattern))
    if not paths:
        raise FileNotFoundError(str(root / pattern))
    return paths[0]

def main():
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument('--classpath-json', type=Path)
    opt = ap.parse_args()
    home = Path.home()
    if opt.classpath_json:
        deps = [Path(x).resolve(strict=True) for x in json.loads(opt.classpath_json.read_text())]
    else:
        base = home / 'AkashicDev/CubicChunks-Crucible/source/build/rfg'
        cache = home / '.gradle/caches/modules-2/files-2.1'
        deps = [base/'srg_merged_minecraft.jar', base/'recompiled_minecraft-1.7.10.jar',
                pick(cache/'org.lwjgl.lwjgl/lwjgl/2.9.1', '*/*.jar'),
                pick(cache/'org.apache.logging.log4j/log4j-api/2.0-beta9', '*/*.jar'), home/'AkashicDev/ThePatcher-1.1.0-beta.4/lib/asm-9.8.jar', home/'AkashicDev/ThePatcher-1.1.0-beta.4/lib/asm-tree-9.8.jar', home/'AkashicDev/CubicChunks-Crucible/bootstrap-probe/libraries/io/github/cruciblemc/launchwrapper/1.13/launchwrapper-1.13.jar']
    if not opt.classpath_json:
        deps.append(home/'AppData/Roaming/.technic/libraries/io/netty/netty-all/4.0.10.Final/netty-all-4.0.10.Final.jar')
    for p in deps:
        if not p.is_file(): raise FileNotFoundError(p)
    # Confirm this really is SRG Minecraft and a real Forge development artifact.
    with zipfile.ZipFile(deps[0]) as z:
        b=z.read('net/minecraft/client/gui/GuiScreen.class')
        if b'func_73863_a' not in b: raise ValueError('Expected SRG GuiScreen')
    if not any('net/minecraftforge/client/event/GuiOpenEvent.class' in zipfile.ZipFile(p).namelist() for p in deps):
        raise ValueError('Real Forge classes missing')
    evidence=ROOT/'evidence'; evidence.mkdir(exist_ok=True)
    (evidence/'native-classpath.json').write_text(json.dumps([{'path':str(p),'sha256':sha(p)} for p in deps],indent=2))
    build=ROOT/'build'; build.mkdir(exist_ok=True)
    staging=Path(tempfile.mkdtemp(prefix='native-',dir=build)); classes=staging/'classes';classes.mkdir()
    compiler=shutil.which('javac')
    if not compiler: raise RuntimeError('JDK 17+ required to run this build; output targets Java 8')
    sources=sorted((ROOT/'src/main/java').rglob('*.java'))
    cp=os.pathsep.join(str(p) for p in deps)
    args=['--release','8','-encoding','UTF-8','-Xlint:all,-options,-path','-Werror','-cp',cp,'-d',str(classes)]+[str(p) for p in sources]
    argfile=staging/'javac.args'
    argfile.write_text('\n'.join('"'+x.replace('\\','/')+'"' for x in args),encoding='utf-8')
    result=subprocess.run([compiler,'@'+str(argfile)],cwd=ROOT,capture_output=True,text=True,timeout=90)
    (evidence/'native-compile.log').write_text(result.stdout+result.stderr,encoding='utf-8')
    print(result.stdout+result.stderr,flush=True)
    if result.returncode: raise SystemExit(result.returncode)
    entries={}
    for f in classes.rglob('*.class'):
        data=f.read_bytes()
        if struct.unpack('>H',data[6:8])[0] != 52: raise ValueError('Non-Java8 class '+str(f))
        name=f.relative_to(classes).as_posix()
        if not name.startswith('br/com/redeakashic/cinematic/'): raise ValueError('Foreign class in output')
        entries[name]=data
    for f in (ROOT/'src/main/resources').rglob('*'):
        if f.is_file(): entries[f.relative_to(ROOT/'src/main/resources').as_posix()]=f.read_bytes()
    jar=staging/('AkashicCinematicMenu-'+VERSION+'.jar')
    with zipfile.ZipFile(jar,'w') as z:
        for name,data in sorted(entries.items()):
            info=zipfile.ZipInfo(name,(2026,9,20,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED
            z.writestr(info,data,compresslevel=9)
    dist=ROOT/'dist'; dist.mkdir(exist_ok=True)
    final=dist/jar.name
    os.replace(jar, final)
    report={'status':'COMPILED_NOT_RUNTIME_APPROVED','version':VERSION,'jar':final.name,
            'sha256':sha(final),'bytes':final.stat().st_size,'classes':len([n for n in entries if n.endswith('.class')]),
            'class_major_version':52,'compiled_against_real_artifacts':True,
            'stubs_used':False,'splash_overlay_implemented':True,'installed_in_game':False,
            'runtime_tested':False,'compiler':compiler}
    (evidence/'native-build.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
    print(json.dumps(report,indent=2),flush=True)

if __name__=='__main__':
    main()
