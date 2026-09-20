from pathlib import Path
import json,importlib.util
r=Path(__file__).resolve().parents[1]
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','tools/build_native.py','src/main/resources/META-INF/MANIFEST.MF']:
    p=r/name;s=p.read_text(encoding='utf-8');assert '0.1.0-alpha.2' in s,name
    p.write_text(s.replace('0.1.0-alpha.2','0.2.0-alpha.1'),encoding='utf-8',newline='\n')
p=r/'src/main/resources/mcmod.info';data=json.loads(p.read_text());data[0]['description']='Native Santuário menu and O Despertar splash overlay. Candidate: isolated runtime validation pending.';p.write_text(json.dumps(data,indent=2),encoding='utf-8')
p=r/'src/main/resources/META-INF/MANIFEST.MF';s=p.read_text();s=s.rstrip()+'\nFMLCorePlugin: br.com.redeakashic.cinematic.bootstrap.CinematicLoadingPlugin\nFMLCorePluginContainsFMLMod: true\n\n';p.write_text(s,newline='\n')
p=r/'tools/build_native.py';s=p.read_text();old="pick(cache/'org.apache.logging.log4j/log4j-api/2.0-beta9', '*/*.jar')]";assert s.count(old)==1
new="pick(cache/'org.apache.logging.log4j/log4j-api/2.0-beta9', '*/*.jar'), home/'AkashicDev/ThePatcher-1.1.0-beta.4/lib/asm-9.8.jar', home/'AkashicDev/ThePatcher-1.1.0-beta.4/lib/asm-tree-9.8.jar', home/'AkashicDev/CubicChunks-Crucible/bootstrap-probe/libraries/io/github/cruciblemc/launchwrapper/1.13/launchwrapper-1.13.jar']"
s=s.replace(old,new).replace("'native_splash_replaced':False","'splash_overlay_implemented':True");p.write_text(s,newline='\n')
p=r/'tools/export_art.py';s=p.read_text();assert "anchor='lt'" in s
s=s.replace("(x+2,y+2),chr(c)","(x+2,y+font.getmetrics()[0]+2),chr(c)").replace("anchor='lt'","anchor='ls'");p.write_text(s,encoding='utf-8',newline='\n')
spec=importlib.util.spec_from_file_location('export_art',p);m=importlib.util.module_from_spec(spec);spec.loader.exec_module(m)
out=r/'src/main/resources/assets/akashiccinematic/native';m.font_atlas(out,'body',Path('C:/Windows/Fonts/arial.ttf'),24);m.font_atlas(out,'serif',Path('C:/Windows/Fonts/georgia.ttf'),36)
report=json.loads((r/'evidence/art-export.json').read_text());report['assets']={f.relative_to(out).as_posix():{'bytes':f.stat().st_size,'sha256':m.sha(f.read_bytes())} for f in sorted(out.rglob('*')) if f.is_file()};report['total_bytes']=sum(v['bytes'] for v in report['assets'].values());report['glyph_baseline']='corrected to shared font ascent';(r/'evidence/art-export.json').write_text(json.dumps(report,indent=2))
print('VERSION_AND_FONT_BASELINE_UPDATED',report['total_bytes'],flush=True)
