from pathlib import Path
import hashlib,json,zipfile
R=Path(__file__).resolve().parents[1];old=R/'dist/AkashicCinematicMenu-0.3.0-alpha.2.jar';assert hashlib.sha256(old.read_bytes()).hexdigest()=='a5a37075fb429373b9dc3fc7d67017010b1e8d11b1b3e342331230741f3cfde0'
backup=R/'evidence/source-alpha2-before-textfit.zip';assert not backup.exists()
with zipfile.ZipFile(backup,'x',compression=zipfile.ZIP_DEFLATED) as z:
    for p in (R/'src').rglob('*'):
        if p.is_file():z.write(p,p.relative_to(R).as_posix())
p=R/'src/main/java/br/com/redeakashic/cinematic/client/ConnectionScreenTheme.java';s=p.read_text(encoding='utf-8')
old='                int top=Math.max(12,gh/2-58),boxWidth=Math.min(gw-24,Math.max(300,gw*2/3)),left=(gw-boxWidth)/2;';assert s.count(old)==1
new='''                int widest=mc.field_71466_p.func_78256_a(title),buttonBottom=0;
                for(String line:lines)widest=Math.max(widest,mc.field_71466_p.func_78256_a(line));
                for(GuiButton button:buttons)if(button.field_146125_m)buttonBottom=Math.max(buttonBottom,button.field_146129_i+button.field_146121_g);
                int top=Math.max(12,gh/2-58),boxWidth=Math.min(gw-16,Math.max(Math.max(280,gw*2/3),widest+32)),left=(gw-boxWidth)/2;'''
s=s.replace(old,new);old='                int bottom=Math.min(gh-10,Math.max(bodyBottom+16,gh/2+90));';assert s.count(old)==1;s=s.replace(old,'                int bottom=Math.min(gh-8,Math.max(bodyBottom+16,buttonBottom+14));');p.write_text(s,encoding='utf-8',newline='\n')
p=R/'src/main/java/br/com/redeakashic/cinematic/client/CinematicMenu.java';s=p.read_text(encoding='utf-8')
s=s.replace('requestAction(MenuLayout.action(index));','requestAction(MenuLayout.action(index),"mouse:"+mx+","+my);').replace('requestAction(MenuLayout.action(focus));','requestAction(MenuLayout.action(focus),"key:"+key);')
s=s.replace('private void requestAction(MenuAction action){','private void requestAction(MenuAction action,String source){')
old='        if(flow.request(action))UiAudio.play(action==MenuAction.PLAY?br.com.redeakashic.cinematic.core.UiCueGate.Cue.CONNECT:br.com.redeakashic.cinematic.core.UiCueGate.Cue.SELECT);';assert old in s
s=s.replace(old,'        if(flow.request(action)){System.out.println("[AkashicCinematic] ACCEPTED_INPUT "+source+" action="+action);UiAudio.play(action==MenuAction.PLAY?br.com.redeakashic.cinematic.core.UiCueGate.Cue.CONNECT:br.com.redeakashic.cinematic.core.UiCueGate.Cue.SELECT);}');p.write_text(s,encoding='utf-8',newline='\n')
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','src/main/resources/META-INF/MANIFEST.MF','tools/build_native.py']:
    p=R/name;s=p.read_text();assert '0.3.0-alpha.2' in s;p.write_text(s.replace('0.3.0-alpha.2','0.3.0-alpha.3'),newline='\n')
(R/'evidence/alpha2-review.json').write_text(json.dumps({'visual_issue':'Disconnected message exceeded the card width; corrected to measured text width plus padding','runtime_suite':'Interrupted by unscheduled navigation; no source of that input assigned without telemetry','changes':'Text-fit card, content height and accepted-input origin logs','preserved_jar':str(old),'source_backup':str(backup)},indent=2))
print('ALPHA3: CONTENT_FIT_PANEL_AND_INPUT_DIAGNOSTICS; PREDECESSOR_PRESERVED',flush=True)
