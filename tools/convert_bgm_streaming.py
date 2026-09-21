from pathlib import Path
import sys,json,hashlib,time
R=Path(__file__).resolve().parents[1];P=R.parents[1];sys.path.insert(0,str(P/'candidates/native-n06/build/audio-codec'))
import soundfile as sf
import numpy as np
E=R/'evidence';state=E/'bgm-conversion-state.json'
def mark(phase,**kwargs):state.write_text(json.dumps({'phase':phase,'at':time.time(),**kwargs},indent=2));print(phase,kwargs,flush=True)
expected='1e7a442de0759fb7a9b605c3f70d0fecdb7c3e1da428796e41df9a717d668408';files=[p for p in (Path.home()/'Downloads').glob('*.mp3') if p.stat().st_size==6475232];source=next(p for p in files if hashlib.sha256(p.read_bytes()).hexdigest()==expected)
out=R/'local-assets/akashiccinematic/bgm/menu.ogg';out.parent.mkdir(parents=True,exist_ok=True)
if out.exists():
    assert out.stat().st_size==3874,'Existing output changed; inspect first';quarantine=E/'incomplete-ogg-header.bin';assert not quarantine.exists();out.rename(quarantine)
mark('input-verified',path=str(source),sha256=expected)
peak=0.0;frames=0;ss=0.0
with sf.SoundFile(source) as src:
    rate,channels=src.samplerate,src.channels;assert rate==44100 and channels==2
    for block in src.blocks(blocksize=44100,dtype='float32',always_2d=True):
        assert np.isfinite(block).all();peak=max(peak,float(np.max(np.abs(block))));frames+=len(block);ss+=float(np.sum(block.astype('float64')**2))
gain=min(1.0,10**(-1.5/20)/peak);mark('analysed',frames=frames,peak=peak,gain=gain)
with sf.SoundFile(source) as src,sf.SoundFile(out,'w',samplerate=rate,channels=channels,format='OGG',subtype='VORBIS',compression_level=.25) as dst:
    count=0
    for block in src.blocks(blocksize=44100,dtype='float32',always_2d=True):
        dst.write(block*gain);count+=len(block)
        if count%(44100*30)==0:mark('encoding',frames=count)
mark('encoded',bytes=out.stat().st_size)
decoded_frames=0;outpeak=0.;error=0.;reference_energy=0.
with sf.SoundFile(out) as decoded,sf.SoundFile(source) as src:
    assert decoded.samplerate==rate and decoded.channels==channels
    for block in decoded.blocks(blocksize=44100,dtype='float32',always_2d=True):
        original=src.read(len(block),dtype='float32',always_2d=True)*gain;assert original.shape==block.shape and np.isfinite(block).all()
        decoded_frames+=len(block);outpeak=max(outpeak,float(np.max(np.abs(block))));error+=float(np.sum((block.astype('float64')-original)**2));reference_energy+=float(np.sum(original.astype('float64')**2))
assert decoded_frames==frames and outpeak<.99
report={'input_sha256':expected,'input_path':str(source),'upload_alias':'BGM.mp3','output_path':str(out),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'output_bytes':out.stat().st_size,'duration_seconds':frames/rate,'frames':frames,'sample_rate':rate,'channels':channels,'input_peak':peak,'gain_db':float(20*np.log10(gain)),'constant_gain':gain,'output_peak_dbfs':float(20*np.log10(outpeak)),'decoded_error_snr_db':float(10*np.log10(reference_energy/error)),'codec':'Ogg Vorbis','soundfile_version':sf.__version__,'libsndfile_version':sf.__libsndfile_version__,'format_conversion_only':True,'no_tempo_or_reverb_change':True,'no_fade_baked_in':True,'bounded_working_chunks':44100,'previous_partial_preserved':'evidence/incomplete-ogg-header.bin'}
(E/'supplied-bgm.json').write_text(json.dumps(report,indent=2),encoding='utf-8');mark('validated',frames=frames,bytes=out.stat().st_size,sha256=report['output_sha256']);print(json.dumps(report,indent=2),flush=True)
