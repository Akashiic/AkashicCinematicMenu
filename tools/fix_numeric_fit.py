from pathlib import Path
import importlib.util,json
r=Path(__file__).resolve().parents[1]
p=r/'src/main/java/br/com/redeakashic/cinematic/render/BitmapText.java';s=p.read_text(encoding='utf-8');anchor='    @Override public void close()';assert s.count(anchor)==1 and 'void drawFit' not in s
method='''    void drawFit(String text,double x,double y,double fontSize,double maxWidth,int rgb,double alpha){
        String s=text==null?"":text;double measured=width(s,fontSize/size);
        double fit=measured>maxWidth?fontSize*maxWidth/measured:fontSize;
        draw(s,x,y,fit,maxWidth+.01,rgb,alpha);
    }
''';s=s.replace(anchor,method+anchor);p.write_text(s,encoding='utf-8',newline='\n')
p=r/'src/main/java/br/com/redeakashic/cinematic/render/NativeVisuals.java';s=p.read_text(encoding='utf-8');assert 'serif.draw(state.percent()' in s;s=s.replace('serif.draw(state.percent()','serif.drawFit(state.percent()').replace('body.draw(state.step+','body.drawFit(state.step+');p.write_text(s,encoding='utf-8',newline='\n')
p=r/'tools/export_art.py';s=p.read_text(encoding='utf-8');s=s.replace("a.font_dir/'arial.ttf',24)","a.font_dir/'arial.ttf',12)");p.write_text(s,encoding='utf-8',newline='\n')
spec=importlib.util.spec_from_file_location('art',p);m=importlib.util.module_from_spec(spec);spec.loader.exec_module(m);out=r/'src/main/resources/assets/akashiccinematic/native';m.font_atlas(out,'body',Path('C:/Windows/Fonts/arial.ttf'),12)
report=json.loads((r/'evidence/art-export.json').read_text());report['assets']={f.relative_to(out).as_posix():{'bytes':f.stat().st_size,'sha256':m.sha(f.read_bytes())} for f in sorted(out.rglob('*')) if f.is_file()};report['total_bytes']=sum(v['bytes'] for v in report['assets'].values());report['body_atlas_size']=12;(r/'evidence/art-export.json').write_text(json.dumps(report,indent=2))
print('NUMERIC_FIT_CORRECTED; BODY_ATLAS_12PX')
