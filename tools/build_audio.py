from pathlib import Path
import sys,json,hashlib
R=Path(__file__).resolve().parents[1];sys.path.insert(0,str(R/'build/audio-codec'))
import numpy as np
import soundfile as sf
sr=44100;rng=np.random.default_rng(1710);folder=R/'src/main/resources/assets/akashiccinematic/sounds/ui';folder.mkdir(parents=True,exist_ok=True)
specs={'focus':(.13,[660,990],.14),'select':(.29,[220,440,662],.23),'back':(.22,[330,220],.16),'enter':(.85,[110,220,330,554.37],.24),'connect':(.46,[164.81,329.63,493.88],.23)}
report={};preview=[]
for name,(seconds,freqs,peak) in specs.items():
    t=np.arange(round(seconds*sr))/sr;attack=.04 if name=='enter' else .012
    env=np.minimum(1,t/attack)**2*np.exp(-t/(seconds*.34));env*=np.sin(np.pi/2*np.minimum(1,(seconds-t)/.05))**2;signal=np.zeros(len(t))
    for i,f in enumerate(freqs):
        shift=1-.09*t/seconds if name=='back' else 1+.1*t/seconds if name=='connect' else np.ones(len(t))
        phase=2*np.pi*np.cumsum(f*shift)/sr;signal+=np.sin(phase+i*.3)*env/(i+1)**1.7
    noise=np.convolve(rng.standard_normal(len(t)),np.ones(11)/11,'same');signal+=noise*.035*np.exp(-t/.022)*np.minimum(1,t/.004)
    signal[0]=0;signal[-128:]*=np.linspace(1,0,128);signal*=peak/max(abs(signal))
    path=folder/(name+'.ogg');sf.write(path,signal,sr,format='OGG',subtype='VORBIS')
    decoded,rate=sf.read(path);assert rate==sr and len(decoded)>0 and np.isfinite(decoded).all() and max(abs(decoded))<.45
    report[name]={'seconds':seconds,'sample_rate':rate,'channels':1,'peak_dbfs':float(20*np.log10(max(abs(decoded)))),'rms_dbfs':float(20*np.log10(np.sqrt(np.mean(decoded**2)))),'sha256':hashlib.sha256(path.read_bytes()).hexdigest(),'bytes':path.stat().st_size}
    preview.extend([signal,np.zeros(int(sr*.55))])
sf.write(R/'evidence/Preview-SFX.wav',np.concatenate(preview),sr,subtype='PCM_16')
manifest={'ui.'+name:{'category':'master','sounds':[{'name':'akashiccinematic:ui/'+name,'stream':False}]} for name in specs}
(folder.parents[1]/'sounds.json').write_text(json.dumps(manifest,indent=2))
(R/'evidence/audio-validation.json').write_text(json.dumps({'generator':'deterministic original synthesis','soundfile':sf.__version__,'libsndfile':sf.__libsndfile_version__,'files':report,'subjective_approval':'PENDING_USER_LISTENING'},indent=2))
print('FIVE_ORIGINAL_VORBIS_CUES_DECODED_AND_VALIDATED',sum(x['bytes'] for x in report.values()),flush=True)
