from pathlib import Path
import sys,json,hashlib,math
import numpy as np
R=Path(__file__).resolve().parents[1];P=R.parents[1];sys.path.insert(0,str(P/'candidates/native-n06/build/audio-codec'))
import soundfile as sf
SR=44100;O=R/'src/main/resources/assets/akashiccinematic/sounds/ritual';O.mkdir(exist_ok=True);E=R/'evidence/audio';E.mkdir(exist_ok=True)
def filt(x,low=60,high=5000):
    n=len(x);f=np.fft.rfftfreq(n,1/SR);response=(1-np.exp(-(f/low)**4))*np.exp(-(f/high)**4);return np.fft.irfft(np.fft.rfft(x)*response,n)
def envelope(t,attack,hold,release):
    return np.sin(np.minimum(1,t/attack)*np.pi/2)**2*np.exp(-np.maximum(t-hold,0)/release)
def choir(seconds,root,seed,attack=.07,hold=.18,release=.24):
    rng=np.random.default_rng(seed);t=np.arange(round(seconds*SR))/SR;out=np.zeros((len(t),2));
    # Wordless harmonic formants, unison/fifth/octave registrations, microdetuned singers.
    for ratio,level in [(1,.55),(1.5,.24),(2,.18)]:
        for singer in range(3):
            hz=root*ratio*2**(rng.uniform(-5,5)/1200);phase=2*np.pi*hz*t+.021*np.sin(2*np.pi*rng.uniform(4.2,5.3)*t+rng.uniform(0,6.28));voice=np.zeros(len(t))
            for k in range(1,min(60,int(6000/hz))):
                f=hz*k;vowel=.07+1.10*np.exp(-.5*((f-440)/95)**2)+.65*np.exp(-.5*((f-820)/135)**2)+.18*np.exp(-.5*((f-2500)/320)**2)
                voice+=vowel/k**1.15*np.sin(k*phase+rng.uniform(-.15,.15))
            drift=1+.045*np.sin(2*np.pi*rng.uniform(.6,1.2)*t+rng.uniform(0,6.28));voice*=envelope(t,attack,hold,release)*level*drift
            pan=rng.uniform(.34,.66);out[:,0]+=voice*np.cos(pan*np.pi/2);out[:,1]+=voice*np.sin(pan*np.pi/2)
    breath=filt(rng.normal(size=len(t)),300,2800)*envelope(t,.035,hold,.12);out+=breath[:,None]*.012
    return out
def bell(n,root,seed,decay=.6):
    rng=np.random.default_rng(seed);t=np.arange(n)/SR;x=np.zeros(n)
    for ratio,weight in [(1,.6),(2,.30),(2.756,.24),(4.07,.11),(5.404,.06),(6.81,.028)]:
        hz=root*ratio*(1+rng.uniform(-.001,.001));x+=weight*np.sin(2*np.pi*hz*t)*np.exp(-t/(decay/(ratio**.36)))
    return x*(1-np.exp(-t/.008))
def room(dry,seconds,seed,wet):
    rng=np.random.default_rng(seed);n=len(dry);m=round(seconds*SR);t=np.arange(m)/SR;length=1<<math.ceil(math.log2(n+m-1));out=dry.copy()
    for channel in range(2):
        ir=filt(rng.normal(size=m),110,3900)*np.exp(-6.9078*t/seconds)*(1-np.exp(-t/.025))
        ir*=.18/max(np.sqrt(np.sum(ir*ir)),1e-9)
        for delay,level in [(31,.24),(47,.19),(71,.15),(103,.11),(137,.08)]:ir[round((delay+channel*2.3)*SR/1000)]+=level
        mid=dry[:,channel]*.65+dry[:,1-channel]*.35
        reflected=np.fft.irfft(np.fft.rfft(mid,length)*np.fft.rfft(ir,length),length)[:n];out[:,channel]+=wet*reflected
    return out
spec=[('focus',1.05,146.832,3201,.025,.05,.14,587.33,.17,1.35,.48,-15),('focus_b',1.10,146.832,3202,.033,.07,.14,440,.14,1.40,.50,-15),('select',1.90,97.999,3210,.055,.13,.25,293.665,.21,2.1,.72,-10),('back',1.40,97.999,3220,.045,.045,.18,196,.07,1.6,.75,-14),('enter',3.60,97.999,3230,.23,.50,.58,293.665,.20,3.2,.83,-10),('connect',2.80,146.832,3240,.13,.30,.42,391.995,.21,2.6,.82,-9)]
reports=[];rendered={}
for name,duration,root,seed,attack,hold,release,bellhz,level,rt,wet,db in spec:
    x=choir(duration,root,seed,attack,hold,release);b=bell(len(x),bellhz,seed+19,decay=.47 if name.startswith('focus') else 1.05)
    x+=np.stack([b,b*.96],axis=1)*level
    if name in ['select','connect']:
        t=np.arange(len(x))/SR;contact=filt(np.random.default_rng(seed+77).normal(size=len(x)),100,900)*np.exp(-t/.022)*(1-np.exp(-t/.002));x+=contact[:,None]*.045
    x=room(x,rt,seed+10,wet);x=np.column_stack([filt(x[:,i],65,5400) for i in range(2)])
    x-=x.mean(axis=0);fade=min(round(.11*SR),len(x)//4);x[-fade:]*=np.cos(np.linspace(0,np.pi/2,fade))[:,None]**2;x[:90]*=np.linspace(0,1,90)[:,None]
    x*=10**(db/20)/max(np.max(np.abs(x)),1e-9);sf.write(O/(name+'.ogg'),x,SR,format='OGG',subtype='VORBIS',compression_level=.20);decoded,rate=sf.read(O/(name+'.ogg'),always_2d=True)
    assert rate==SR and len(decoded)==len(x) and np.isfinite(decoded).all() and np.max(np.abs(decoded))<.65
    mono=decoded.mean(axis=1);rms=np.sqrt(np.mean(decoded**2));tail=np.sqrt(np.mean(decoded[-round(.025*SR):]**2));assert tail<.004
    record={'name':name,'seconds':duration,'frames':len(decoded),'peak_dbfs':float(20*np.log10(np.max(np.abs(decoded)))),'rms_dbfs':float(20*np.log10(rms)),'tail25ms_dbfs':float(20*np.log10(max(tail,1e-12))),'stereo_correlation':float(np.corrcoef(decoded.T)[0,1]),'mono_rms_ratio':float(np.sqrt(np.mean(mono**2))/rms),'sha256':hashlib.sha256((O/(name+'.ogg')).read_bytes()).hexdigest()}
    assert record['mono_rms_ratio']>.75;reports.append(record);rendered[name]=decoded;print(name,record['peak_dbfs'],record['rms_dbfs'],flush=True)
# Compare focus pacing, important click and entrance in a single uncompressed audition; labels/timing in cue sheet.
sequence=['focus','focus_b','select','back','enter','connect'];samples=[];cues=[];cursor=0
for name in sequence:
    silence=np.zeros((round(.5*SR),2));samples.append(silence);cursor+=len(silence);cues.append({'cue':name,'start_seconds':cursor/SR});samples.append(rendered[name]);cursor+=len(rendered[name])
sf.write(E/'Ceremonial-SFX-Audition.wav',np.concatenate(samples),SR,subtype='PCM_16')
events=R/'src/main/resources/assets/akashiccinematic/sounds.json';data=json.loads(events.read_text());before=json.dumps(data['bgm.menu'],sort_keys=True)
for name in ['focus','select','back','enter','connect']:
    names=['focus','focus_b'] if name=='focus' else [name];data['ui.'+name]={'category':'master','sounds':[{'name':'akashiccinematic:ritual/'+n,'stream':False} for n in names]}
assert json.dumps(data['bgm.menu'],sort_keys=True)==before;events.write_text(json.dumps(data,indent=2),encoding='utf-8')
report={'creative_source':'Original wordless additive/formant choir, modal metal, filtered air and stereo diffuse decay; no sampled chant or recorded human singer','runtime_DSP':False,'sr':SR,'cues':reports,'audition_timeline':cues,'BGM_recording_untouched':True,'subjective_listening_approval':'USER_PENDING'}
(R/'evidence/ceremonial-sfx.json').write_text(json.dumps(report,indent=2));print('SFX_PACKAGE_AND_AUDITION_READY',flush=True)
