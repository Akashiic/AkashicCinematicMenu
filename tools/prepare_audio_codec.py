from pathlib import Path
import subprocess,sys,json
R=Path(__file__).resolve().parents[1];vendor=R/'build/audio-codec';assert not vendor.exists();vendor.mkdir(parents=True)
args=[sys.executable,'-m','pip','install','--disable-pip-version-check','--no-input','--only-binary=:all:','--no-deps','--target',str(vendor),'--report',str(R/'evidence/audio-codec-packages.json'),'soundfile==0.13.1','cffi==1.17.1','pycparser==2.22']
r=subprocess.run(args,capture_output=True,text=True,timeout=90);(R/'evidence/audio-codec-setup.log').write_text(r.stdout+r.stderr,encoding='utf-8');print('LOCAL_BUILD_CODEC',r.returncode,flush=True);assert r.returncode==0
print('Pinned build-only codec; not installed globally and not included in the mod')
