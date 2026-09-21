from pathlib import Path
import json,hashlib
R=Path(__file__).resolve().parents[1];J=R/'src/main/java/br/com/redeakashic/cinematic'
p=J/'render/NativeVisuals.java';s=p.read_text(encoding='utf-8');s=s.replace('    public static long menuFrames,startupFrames;','    public double playPressAge=-1;public boolean depthEnabled=true,heroAnimation=true;\n    public static long menuFrames,startupFrames;')
s=s.replace('        background(1,motion?px:0,motion?py:0);backdrop.glass','        prepareMenu();backdrop.drawDepth(layout.width,layout.height,1,motion?px:0,motion?py:0,motion&&depthEnabled);backdrop.glass')
old='''            rect(x,y,bw,bh,SLATE,(.18+hot*.40)*visibility);outline(x,y,bw,bh,GOLD,(.18+hot*.48)*visibility);
            if(hot>.005)rect(x,y,2,bh,GOLD,hot*visibility);
            text("0"+(i+1),x+12*s,y+(bh-10*s)/2,9*s,24*s,false,1*s,GOLD,visibility*.88);
            text(actions[i],x+36*s,y+(bh-19*s)/2-1,19*s,bw-48*s,true,1*s,IVORY,visibility);'''
assert s.count(old)==1
new='''            if(i==0)HeroButton.draw(x,y,bw,bh,age,hot,playPressAge,visibility,motion&&heroAnimation);
            else{rect(x,y,bw,bh,SLATE,(.18+hot*.40)*visibility);outline(x,y,bw,bh,GOLD,(.18+hot*.48)*visibility);if(hot>.005)rect(x,y,2,bh,GOLD,hot*visibility);}
            text("0"+(i+1),x+12*s,y+(bh-10*s)/2,9*s,24*s,false,1*s,i==0?0xFFF5F8:GOLD,visibility*.88);
            text(actions[i],x+36*s,y+(bh-19*s)/2-1,19*s,bw-(i==0?67:48)*s,true,1*s,i==0?0xFFFFFF:IVORY,visibility);'''
s=s.replace(old,new);p.write_text(s,encoding='utf-8',newline='\n')
p=J/'client/CinematicMenu.java';s=p.read_text(encoding='utf-8');s=s.replace('    private double visitAge;','    private double visitAge,playPressedAt=Double.NEGATIVE_INFINITY;')
s=s.replace('                double px=mx*','                layout.resize(w,h);\n                double px=mx*')
s=s.replace('                visuals.transform(w,h);','                visuals.transform(w,h);visuals.playPressAge=clock.elapsed()-playPressedAt;visuals.depthEnabled=config.layeredParallax;visuals.heroAnimation=config.animatedPlay;')
s=s.replace('if(flow.request(action)){System.out.println','if(flow.request(action)){if(action==MenuAction.PLAY)playPressedAt=clock.elapsed();System.out.println')
p.write_text(s,encoding='utf-8',newline='\n')
p=J/'client/MenuConfig.java';s=p.read_text();s=s.replace('themeConnections,themeTransitions;','themeConnections,themeTransitions,layeredParallax,animatedPlay;')
s=s.replace('        reducedMotion=c.get','        layeredParallax=c.get("motion","layeredParallax",true,"Separate sky, architecture and angel; reduced motion uses approved static art.").getBoolean(true);\n        animatedPlay=c.get("motion","animatedPlayButton",true,"Gentle crimson gradient and white glint; reduced motion removes idle animation.").getBoolean(true);\n        reducedMotion=c.get')
p.write_text(s,encoding='utf-8',newline='\n')
print('N10_CLIENT_AND_RENDERER_INTEGRATED; BGM/route implementation unchanged',flush=True)
