from pathlib import Path
import json,hashlib
R=Path(__file__).resolve().parents[1];source=R/'tools/convert_bgm_streaming.py';s=source.read_text(encoding='utf-8');out=R/'local-assets/akashiccinematic/bgm/menu.ogg'
assert json.loads((R/'evidence/bgm-conversion-state.json').read_text())['phase']=='encoded'
assert out.stat().st_size==4273943,'Output changed; revalidate instead of replacing'
backup=R/'evidence/rejected-estimated-duration.ogg';assert not backup.exists();out.rename(backup)
s=s.replace("    assert out.stat().st_size==3874,'Existing output changed; inspect first';quarantine=E/'incomplete-ogg-header.bin';assert not quarantine.exists();out.rename(quarantine)","    raise RuntimeError('Existing output must be inspected before overwrite')")
anchor="mark('input-verified',path=str(source),sha256=expected)"
function="""def blocks(stream):
    while True:
        chunk=stream.read(44100,dtype='float32',always_2d=True)
        if not len(chunk):break
        yield chunk
"""
assert s.count(anchor)==1;s=s.replace(anchor,function+anchor).replace("src.blocks(blocksize=44100,dtype='float32',always_2d=True)","blocks(src)").replace("decoded.blocks(blocksize=44100,dtype='float32',always_2d=True)","blocks(decoded)")
s=s.replace("gain=min(1.0,10**(-1.5/20)/peak)","assert frames==7138944,'Decoded frames must match verified ffmpeg/source duration'\ngain=min(1.0,10**(-1.5/20)/peak)")
(R/'tools/convert_bgm_exact.py').write_text(s,encoding='utf-8',newline='\n')
(R/'evidence/decoder-audit.json').write_text(json.dumps({'finding':'libsndfile MP3 duration is estimated at7145083 frames; blocks() yielded padded blocks beyond actual7138944 frames. Verified against sequential read and ffprobe161.8808seconds.','correction':'Stream .read() chunks to actual EOF, never trust estimated .frames for buffer length.','rejected_encoded_file':str(backup),'original_preserved':True,'expected_actual_frames':7138944,'no_approved_audio_yet':True},indent=2))
print('MP3_EOF_AUDIT_FIXED; padded output preserved and rejected, exact decoded duration required',flush=True)
