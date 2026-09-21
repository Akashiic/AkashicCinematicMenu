from pathlib import Path
R=Path(__file__).resolve().parents[1];p=R/'src/main/java/br/com/redeakashic/cinematic/client/MenuBgm.java';s=p.read_text(encoding='utf-8')
s=s.replace('import java.util.List;','import java.util.List;\nimport java.util.ArrayDeque;')
s=s.replace('    private MenuTrack sound;private long started,retryAt;','    private final ArrayDeque<ISound> observedVanilla=new ArrayDeque<ISound>();\n    private MenuTrack sound;private long started,retryAt,lastPlaying;')
old='            event.result=null;blocked++;\n        }';assert s.count(old)==1
s=s.replace(old,'            event.result=null;blocked++;\n        }else if("minecraft".equals(loc.func_110624_b())&&loc.func_110623_a().startsWith("music.")){\n            synchronized(observedVanilla){if(!observedVanilla.contains(event.sound)){while(observedVanilla.size()>=8)observedVanilla.removeFirst();observedVanilla.addLast(event.sound);}}\n        }')
anchor='    private void silenceTicker(Minecraft mc)throws ReflectiveOperationException{';assert s.count(anchor)==1
s=s.replace(anchor,anchor+'\n        ISound[] previous;synchronized(observedVanilla){previous=observedVanilla.toArray(new ISound[0]);observedVanilla.clear();}\n        for(ISound voice:previous)mc.func_147118_V().func_147683_b(voice);')
s=s.replace('boolean playing=mc.func_147118_V().func_147692_c(sound);if(playing)heard=true;','boolean playing=mc.func_147118_V().func_147692_c(sound);if(playing){lastPlaying=now;if(now-started>2_000_000_000L)heard=true;}')
s=s.replace('if(now-started>10_000_000_000L&&!playing){','if(now-started>10_000_000_000L&&!playing&&now-lastPlaying>1_500_000_000L){')
s=s.replace('heard=false;started=now;','heard=false;started=now;lastPlaying=now;')
p.write_text(s,encoding='utf-8',newline='\n');print('Bounded vanilla voice ownership and sustained-missing playback grace added; no stopAllSounds calls',flush=True)
