from pathlib import Path
import hashlib, json, shutil, subprocess
root=Path.home()/'AkashicDev/AkashicCinematicMenu'
candidate=root/'candidates/navigation-r04'
rows=sorted(p.relative_to(root).as_posix()+' '+hashlib.sha256(p.read_bytes()).hexdigest() for p in (root/'src').rglob('*') if p.is_file())
assert hashlib.sha256(('\n'.join(rows)+'\n').encode()).hexdigest()=='bf97f95278e8e475c8732fb878361a5dadab46f900e49508f656330d54cad8db','Baseline changed: inspect before proceeding'
assert not candidate.exists(),'Candidate exists: inspect instead of overwriting'
candidate.mkdir(parents=True)
for directory in ['src','tools']: shutil.copytree(root/directory,candidate/directory)
base='src/main/java/br/com/redeakashic/cinematic/'
def edit(relative,old,new):
    p=candidate/relative; text=p.read_text(encoding='utf-8'); assert old in text,relative+' missing expected text'; p.write_text(text.replace(old,new),encoding='utf-8',newline='\n')
edit(base+'core/MenuAction.java','SERVERS, SINGLEPLAYER, OPTIONS, MODS, QUIT','PLAY, SINGLEPLAYER, OPTIONS, QUIT')
edit(base+'core/MenuLayout.java','COUNT = 5','COUNT = MenuAction.values().length')
edit(base+'client/CinematicMenu.java','import cpw.mods.fml.client.GuiModList;','import cpw.mods.fml.client.FMLClientHandler;\nimport br.com.redeakashic.cinematic.core.ServerTarget;\nimport net.minecraft.client.multiplayer.ServerData;')
edit(base+'client/CinematicMenu.java','import net.minecraft.client.gui.GuiMultiplayer;\n','')
edit(base+'client/CinematicMenu.java','"JOGAR", "MUNDO SOLO", "OPCOES", "MODS", "SAIR"','"JOGAR", "SINGLEPLAYER", "OPCOES", "SAIR"')
edit(base+'client/CinematicMenu.java','if (i == 0) { ScenePainter.text(field_146289_q, "SERVIDORES", 277, y + 15, .6, 0xBFA1A5, a * .8); }','// Direct Play no longer opens a server-list screen.')
edit(base+'client/CinematicMenu.java','case SERVERS: field_146297_k.func_147108_a(new GuiMultiplayer(this)); break;','case PLAY:\n                FMLClientHandler handler = FMLClientHandler.instance();\n                // This direct route does not instantiate GuiMultiplayer. Forge needs these maps initialized.\n                handler.setupServerList();\n                handler.connectToServer(this, new ServerData(ServerTarget.NAME, ServerTarget.address()));\n                break;')
edit(base+'client/CinematicMenu.java','            case MODS: field_146297_k.func_147108_a(new GuiModList(this)); break;\n','')
server='package br.com.redeakashic.cinematic.core;\n\n/** Owner-specified destination. No DNS, network, URL parsing or side effects here. */\npublic final class ServerTarget {\n    public static final String NAME = "The End of Forbidden";\n    public static final String HOST = "br2.xmxcloud.net";\n    public static final int PORT = 10511;\n    private ServerTarget() { }\n    public static String address() { return HOST + ":" + PORT; }\n}\n'
(candidate/(base+'core/ServerTarget.java')).write_text(server,encoding='utf-8',newline='\n')
test='src/test/java/br/com/redeakashic/cinematic/core/CoreTests.java'
edit(test,'MenuAction.SERVERS','MenuAction.PLAY')
edit(test,'MenuAction.MODS','MenuAction.SINGLEPLAYER')
edit(test,'nextFocus(4,1)==0','nextFocus(3,1)==0')
edit(test,'nextFocus(0,-1)==4','nextFocus(0,-1)==3')
edit(test,'action whitelist has five routes','action whitelist has four routes')
edit(test,'for(int i=0;i<5;i++)','for(int i=0;i<MenuLayout.COUNT;i++)')
edit(test,'MenuLayout.action(5)','MenuLayout.action(4)')
extra='''        test("exact direct server endpoint", () -> {check(ServerTarget.HOST.equals("br2.xmxcloud.net"));check(ServerTarget.PORT==10511);check(ServerTarget.address().equals("br2.xmxcloud.net:10511"));});
        test("four routes in requested order", () -> {check(MenuLayout.COUNT==4);check(MenuLayout.action(0)==MenuAction.PLAY);check(MenuLayout.action(1)==MenuAction.SINGLEPLAYER);check(MenuLayout.action(2)==MenuAction.OPTIONS);check(MenuLayout.action(3)==MenuAction.QUIT);});
        test("removed routes rejected", () -> {bad(()->MenuAction.valueOf("MODS"));bad(()->MenuAction.valueOf("SERVERS"));});
        test("direct play handoff executes once after transition", () -> {MenuFlow f=new MenuFlow(false,false);f.advance(.22);check(f.request(MenuAction.PLAY));check(!f.request(MenuAction.PLAY));check(f.consume()==null);f.advance(.22);check(f.consume()==MenuAction.PLAY);check(f.consume()==null);});
'''
edit(test,'        System.out.println("RESULT scenarios=',extra+'        System.out.println("RESULT scenarios=')
for p in list((candidate/'src').rglob('*'))+[candidate/'tools/build_native.py']:
    if p.is_file():
        s=p.read_text(encoding='utf-8')
        if '0.1.0-alpha.1' in s: p.write_text(s.replace('0.1.0-alpha.1','0.1.0-alpha.2'),encoding='utf-8',newline='\n')
(candidate/'evidence').mkdir(exist_ok=True)
report={'candidate':'navigation-r04','version':'0.1.0-alpha.2','server':'br2.xmxcloud.net:10511','actions':['PLAY','SINGLEPLAYER','OPTIONS','QUIT'],'nativeRuntimeTested':False,'installedInGame':False,'artPortedFromHTML':False,'originalAlphaPreserved':True}
(candidate/'ROUTING_CHECKPOINT.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
print('CANDIDATE_PREPARED',str(candidate),flush=True)
for tool in ['test_core.py','build_native.py']:
    result=subprocess.run(['python',str(candidate/'tools'/tool)],cwd=candidate,capture_output=True,text=True,timeout=110)
    (candidate/'evidence'/('run-'+tool+'.log')).write_text(result.stdout+result.stderr,encoding='utf-8')
    print(tool,'EXIT',result.returncode,(result.stdout+result.stderr)[-1500:],flush=True)
    if result.returncode: raise SystemExit(result.returncode)
