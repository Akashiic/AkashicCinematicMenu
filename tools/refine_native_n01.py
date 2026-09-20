from pathlib import Path
r=Path(__file__).resolve().parents[1]
p=r/'src/main/java/br/com/redeakashic/cinematic/render/NativeVisuals.java';s=p.read_text(encoding='utf-8');assert 'layout.n("percent.y")+37' in s;s=s.replace('layout.n("percent.y")+37','layout.n("percent.y")+(layout.profile.equals("compact")?39:47)');p.write_text(s,encoding='utf-8',newline='\n')
p=r/'src/main/java/br/com/redeakashic/cinematic/bootstrap/SplashHooks.java';s=p.read_text(encoding='utf-8');s=s.replace('public static volatile boolean finished;','public static volatile boolean finished;\n    public static volatile boolean rendered;');s=s.replace('elapsedSeconds=(now-started)/1.0e9;','elapsedSeconds=(now-started)/1.0e9;');old='visuals.startup(last,elapsedSeconds,1,!Boolean.getBoolean("akashic.cinematic.reduceMotion"));';assert s.count(old)==1;s=s.replace(old,old+'rendered=true;').replace('failed=true;System.err','failed=true;rendered=false;System.err');p.write_text(s,encoding='utf-8',newline='\n')
p=r/'src/main/java/br/com/redeakashic/cinematic/client/CinematicMenu.java';s=p.read_text(encoding='utf-8');s=s.replace('fadeFromBoot=!handoffSeen&&SplashHooks.finished;','fadeFromBoot=!handoffSeen&&SplashHooks.finished&&SplashHooks.rendered;');p.write_text(s,encoding='utf-8',newline='\n')
p=r/'tools/NativeProbe.java';s=p.read_text(encoding='utf-8');old='check(l.n("count")==4,p+" four actions");';assert old in s
new=old+'''
                int pw=l.width,ph=l.height;Display.setDisplayMode(new DisplayMode(pw,ph));
                try(RawScope scope=new RawScope(pw,ph)){
                    NativeVisuals nv=new NativeVisuals(p);nv.transform(pw,ph);nv.menu(0,0,0,1,new double[]{0,1,0,0},false,false);
                    capture(new File(out,"menu-"+p+".png"),pw,ph);
                    nv.startup(new LoadState("Etapa concluída","TESTE ISOLADO / rótulo de conclusão","Somente esta etapa chegou ao total; não é prontidão global.",100,100),45,1,false);
                    capture(new File(out,"startup-"+p+".png"),pw,ph);nv.close();check(NativeTexture.live==0,"release "+p);
                }check(GL11.glGetError()==GL11.GL_NO_ERROR,"profile GL "+p);
'''
s=s.replace(old,new);p.write_text(s,encoding='utf-8',newline='\n')
p=r/'tools/run_probe.py';s=p.read_text();s=s.replace("'-encoding','UTF-8','-cp'","'-encoding','UTF-8','-Xlint:-options','-cp'");p.write_text(s,newline='\n')
print('REVIEW_FIXES_APPLIED_AND_FOUR_PROFILE_TESTS_ADDED')
