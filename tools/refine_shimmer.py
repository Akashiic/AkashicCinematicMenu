from pathlib import Path
import json,zipfile
R=Path(__file__).resolve().parents[1];p=R/'src/main/java/br/com/redeakashic/cinematic/render/HeroButton.java';s=p.read_text()
old='''        if(band>-.5)for(int i=0;i<32;i++){
            double mid=(i+.5)/32.0,energy=Math.exp(-Math.pow((mid-band)/.085,2));
            NativeVisuals.rect(x+i*w/32,y+1,w/32+.05,h-2,0xFFFFFF,a*energy*(.08+hot*.13));
        }'''
assert s.count(old)==1
new='''        if(band>-.5){
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            for(int i=0;i<=64;i++){
                double u=i/64.0,energy=Math.exp(-Math.pow((u-band)/.085,2));
                GL11.glColor4d(1,1,1,a*energy*(.075+hot*.12));
                GL11.glVertex2d(x+u*w,y+1);GL11.glVertex2d(x+u*w,y+h-1);
            }GL11.glEnd();
        }'''
(R/'evidence/hero-alpha1-rejected.java.txt').write_text(s);p.write_text(s.replace(old,new),newline='\n')
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','src/main/resources/META-INF/MANIFEST.MF','tools/build_native.py']:
    p=R/name;s=p.read_text(encoding='utf-8');assert '0.6.0-alpha.1' in s;p.write_text(s.replace('0.6.0-alpha.1','0.6.0-alpha.2'),encoding='utf-8',newline='\n')
(R/'evidence/visual-review-1.json').write_text(json.dumps({'inspection':'Native pbuffer captures at five resolutions','rejected':'White shimmer had visible band steps and overlapping strip seams','correction':'Single smooth triangle strip, shared edges and per-vertex gaussian alpha','other_observations':'Background layers and fixed CTA bounds retained; BGM unchanged','pack_runtime':'Not executed; other user Java clients active with limited memory'},indent=2));print('SHIMMER_FIXED_FOR_ALPHA2; no claim that initial capture passed visual inspection',flush=True)
