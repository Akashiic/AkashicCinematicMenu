from pathlib import Path
R=Path(__file__).resolve().parents[1];S=R/'src/main/java/br/com/redeakashic/cinematic'
p=S/'client/BogoWarningCompat.java';s=p.read_text(encoding='utf-8');a=s.index('            if(messages.isEmpty()');b=s.index('            if(suppressed++',a);s=s[:a]+'            if(!br.com.redeakashic.cinematic.core.BogoNoticePolicy.knownConflict(messages))return false;\n'+s[b:];p.write_text(s,encoding='utf-8',newline='\n')
p=S/'render/NativeVisuals.java';s=p.read_text();s=s.replace('    private NativeTexture panel,scene,boot;','    private NativeTexture panel,scene,boot;\n    private BackdropRenderer backdrop;')
old='        double s=layout.scale(w,h);rect(0,0,w,h,0x070D09,1);';assert s.count(old)==1;s=s.replace(old,'        double s=layout.scale(w,h);if(backdrop==null)backdrop=new BackdropRenderer();backdrop.draw(w,h,1);')
s=s.replace('    @Override public void close(){','    @Override public void close(){\n        if(backdrop!=null){backdrop.close();backdrop=null;}');p.write_text(s,encoding='utf-8',newline='\n')
p=S/'render/BackdropRenderer.java';s=p.read_text();a=s.index('        double scale=');b=s.index('\n    }',a)
s=s[:a]+'''        br.com.redeakashic.cinematic.core.CoverFit crop=new br.com.redeakashic.cinematic.core.CoverFit(art.width,art.height,width,height);
        art.draw(0,0,width,height,crop.u0,crop.v0,crop.u1,crop.v1,alpha,0xFFFFFF);'''+s[b:];p.write_text(s,encoding='utf-8',newline='\n')
p=S/'client/ConnectionScreenTheme.java';s=p.read_text();old='                int bodyTop=top+35,bodyBottom=bodyTop+lines.size()*11;';assert old in s;s=s.replace(old,'                int bodyTop=connecting?top+35:gh/2-lines.size()*9/2,bodyBottom=bodyTop+lines.size()*9;\n                if(!connecting)top=bodyTop-30;')
s=s.replace('y+=11;','y+=9;');p.write_text(s,encoding='utf-8',newline='\n')
print('Full-bleed background independent of UI fit; Bogo policy unit-testable; long-error baseline preserved')
