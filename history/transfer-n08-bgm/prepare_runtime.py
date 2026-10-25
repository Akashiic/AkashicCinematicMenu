from pathlib import Path
import hashlib,json,shutil,subprocess,os,zipfile
P=Path(__file__).resolve().parents[1];Q=P/'qa-n02';R=P/'candidates/native-n08-bgm-r1';E=Q/'evidence/n08-setup';E.mkdir(exist_ok=False)
assert json.loads((Q/'evidence/n07release2/process.json').read_text()).get('exit')==0
source=Q/'tools/QAHarnessV7.java';s=source.read_text(encoding='utf-8').replace('QAHarnessV7','QAHarnessV8').replace('HARNESS_V7_REGISTERED','HARNESS_V8_REGISTERED')
anchor='        if(!observedBoot){';assert s.count(anchor)==1
extra='''        Class<?> bgm=Class.forName("br.com.redeakashic.cinematic.client.MenuBgm");s+="\\nbgmStatus="+field(bgm,"status")+"\\nbgmResource="+field(bgm,"resourceStatus")+"\\nbgmGain="+field(bgm,"gain")+"\\nbgmStarts="+field(bgm,"starts")+"\\nbgmStops="+field(bgm,"stops")+"\\nbgmBlocked="+field(bgm,"blocked");
        Object bgmSound=field(bgm,"lastSound");s+="\\nbgmPlaying="+(bgmSound!=null&&mc.func_147118_V().func_147692_c((net.minecraft.client.audio.ISound)bgmSound));
        s+="\\nmusicVolume="+mc.field_71474_y.func_151438_a(net.minecraft.client.audio.SoundCategory.MUSIC);
        Class<?> transition=Class.forName("br.com.redeakashic.cinematic.client.TransitionTheme");s+="\\naetherFrames="+field(transition,"aetherFrames")+"\\nvanillaLoadingFrames="+field(transition,"vanillaFrames")+"\\nterrainFrames="+field(transition,"terrainFrames")+"\\ntransitionOrigin="+field(transition,"lastOrigin");
'''
s=s.replace(anchor,extra+anchor)
anchor='                case "close":mc.func_71400_g();break;';assert s.count(anchor)==1
cases='''                case "musicvolume":
                    float mv=Float.parseFloat(a[2]);if(!Float.isFinite(mv)||mv<0||mv>1)throw new IllegalArgumentException("music volume");mc.field_71474_y.func_151439_a(net.minecraft.client.audio.SoundCategory.MUSIC,mv);mc.func_147118_V().func_147684_a(net.minecraft.client.audio.SoundCategory.MUSIC,mv);break;
                case "loadingdemo":
                    if(mc.field_71441_e!=null)throw new IllegalStateException("world");
                    net.minecraft.client.LoadingScreenRenderer loader=mc.field_71461_s;
                    if(!loader.getClass().getName().equals("com.gildedgames.the_aether.client.gui.AetherLoadingScreen"))throw new IllegalStateException("Unexpected active loader "+loader.getClass().getName());
                    loader.func_73721_b("TESTE LOCAL - transição preservada");loader.func_73720_a("Preparando a travessia / teste dirigido");Thread.sleep(110);loader.func_73718_a(39);
                    net.minecraft.client.shader.Framebuffer lf=(net.minecraft.client.shader.Framebuffer)field(loader,"framebuffer");ScreenShotHelper.func_148260_a(root.toFile(),lf.field_147621_c,lf.field_147618_d,lf);log("LOADING_CAPTURE_COMPLETE aether "+lf.field_147621_c+"x"+lf.field_147618_d);break;
                case "vanillaloadingdemo":
                    if(mc.field_71441_e!=null)throw new IllegalStateException("world");
                    net.minecraft.client.LoadingScreenRenderer vl=new net.minecraft.client.LoadingScreenRenderer(mc);vl.func_73721_b("TESTE LOCAL - carregador vanilla");vl.func_73720_a("Preservação de progresso e mensagem");Thread.sleep(110);vl.func_73718_a(61);
                    net.minecraft.client.shader.Framebuffer vf=(net.minecraft.client.shader.Framebuffer)field(vl,"field_146588_g");ScreenShotHelper.func_148260_a(root.toFile(),vf.field_147621_c,vf.field_147618_d,vf);vf.func_147608_a();log("LOADING_CAPTURE_COMPLETE vanilla");break;
'''
s=s.replace(anchor,cases+anchor)
source=Q/'tools/QAHarnessV8.java';source.write_text(s,encoding='utf-8',newline='\n');out=Q/'build/harness-v8';out.mkdir(exist_ok=False)
deps=[x['path'] for x in json.loads((R/'evidence/native-classpath.json').read_text())]
result=subprocess.run(['javac','--release','8','-Xlint:-options','-encoding','UTF-8','-cp',os.pathsep.join(deps),'-d',str(out),str(source)],capture_output=True,text=True,timeout=45);(E/'compile.log').write_text(result.stdout+result.stderr);assert result.returncode==0,result.stderr
with zipfile.ZipFile(E/'qa-driver-v8.jar','w',compression=zipfile.ZIP_DEFLATED) as z:
    for p in out.rglob('*.class'):z.write(p,p.relative_to(out).as_posix())
print('QA_HARNESS_COMPILED_NOT_DEPLOYED',flush=True)
