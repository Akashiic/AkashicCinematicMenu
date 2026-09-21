from pathlib import Path
import sys,hashlib,json,shutil
R=Path(__file__).resolve().parents[1];P=R.parents[1]
codec=P/'candidates/native-n06/build/audio-codec';assert codec.exists(),'Existing build-only codec not found'
sys.path.insert(0,str(codec));import soundfile as sf;import numpy as np
expected='1e7a442de0759fb7a9b605c3f70d0fecdb7c3e1da428796e41df9a717d668408'
files=[p for p in (Path.home()/'Downloads').glob('*.mp3') if p.stat().st_size==6475232]
source=next(p for p in files if hashlib.sha256(p.read_bytes()).hexdigest()==expected)
destination=R/'local-assets/akashiccinematic/bgm';destination.mkdir(parents=True,exist_ok=True);out=destination/'menu.ogg';assert not out.exists(),'Inspect existing conversion rather than overwrite'
a,rate=sf.read(source,dtype='float32',always_2d=True);assert rate==44100 and a.shape[1]==2 and np.isfinite(a).all()
peak=float(np.max(np.abs(a)));gain=min(1.0,10**(-1.5/20)/peak);processed=a*gain
sf.write(out,processed,rate,format='OGG',subtype='VORBIS',compression_level=.25)
decoded,sr=sf.read(out,dtype='float32',always_2d=True);assert sr==rate and decoded.shape==a.shape and np.isfinite(decoded).all()
outpeak=float(np.max(np.abs(decoded)));assert outpeak<.99,'Clipping/headroom gate failed'
report={'source_path':str(source),'uploaded_file_alias':'BGM.mp3','input_sha256':expected,'source_bytes':source.stat().st_size,'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'output_bytes':out.stat().st_size,'format':'Ogg Vorbis','sample_rate':sr,'channels':2,'frames':len(decoded),'duration_seconds':len(decoded)/sr,'input_peak':peak,'constant_gain':gain,'gain_db':float(20*np.log10(gain)),'output_peak_dbfs':float(20*np.log10(outpeak)),'speed_changed':False,'reverb_added':False,'fade_baked_into_recording':False,'streaming_runtime':True,'codec_version':sf.__version__,'libsndfile_version':sf.__libsndfile_version__,'correlation':float(np.corrcoef(processed.ravel(),decoded.ravel())[0,1]),'distribution':'local supplied file; not included inside code JAR','scope':'User requested this exact uploaded recording; source preserved'}
(R/'evidence/supplied-bgm.json').write_text(json.dumps(report,indent=2),encoding='utf-8');print(json.dumps(report,indent=2),flush=True)
