from pathlib import Path
r=Path(__file__).resolve().parents[1]
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','tools/build_native.py','src/main/resources/META-INF/MANIFEST.MF']:
    p=r/name;s=p.read_text(encoding='utf-8');assert '0.2.0-alpha.2' in s,name;p.write_text(s.replace('0.2.0-alpha.2','0.2.0-alpha.3'),encoding='utf-8',newline='\n')
p=r/'src/main/java/br/com/redeakashic/cinematic/client/ConnectionCancellationGuard.java';s=p.read_text(encoding='utf-8')
s=s.replace('boolean closeRequested,reportedCancel;','boolean closeRequested,reportedCancel;\n        boolean workerKnown=true;')
s=s.replace('catch(ReflectiveOperationException|RuntimeException ex){unavailable=true;System.err.println("[AkashicCinematic] Connector watch setup failed: "+ex);}', 'catch(ReflectiveOperationException|RuntimeException ex){pending=new Pending(g,null,++sequence);pending.workerKnown=false;unavailable=true;System.err.println("[AkashicCinematic] Connector watch setup failed: "+ex);}')
s=s.replace('            NetworkManager manager=(NetworkManager)managerField.get(p.screen);','            // Observe termination BEFORE reading the result published by the connector worker.\n            boolean done=p.workerKnown&&(p.worker==null||!p.worker.isAlive());\n            NetworkManager manager=(NetworkManager)managerField.get(p.screen);').replace('            boolean done=p.worker==null||!p.worker.isAlive();\n','')
p.write_text(s,encoding='utf-8',newline='\n')
p=r/'src/main/java/br/com/redeakashic/cinematic/client/CinematicMenu.java';s=p.read_text(encoding='utf-8');old='''                FMLClientHandler handler = FMLClientHandler.instance();
                handler.setupServerList();
                handler.connectToServer(this, new ServerData(ServerTarget.NAME, ServerTarget.address()));''';assert s.count(old)==1;s=s.replace(old,'                ConnectionCancellationGuard.INSTANCE.connect(field_146297_k, this);').replace('import cpw.mods.fml.client.FMLClientHandler;\n','').replace('import net.minecraft.client.multiplayer.ServerData;\n','');p.write_text(s,encoding='utf-8',newline='\n')
p=r/'src/main/java/br/com/redeakashic/cinematic/client/ClientProxy.java';s=p.read_text(encoding='utf-8');anchor='            MinecraftForge.EVENT_BUS.register(new MenuHook(config, event.getModLog()));';assert s.count(anchor)==1;s=s.replace(anchor,anchor+'\n            cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(ConnectionCancellationGuard.INSTANCE);');p.write_text(s,encoding='utf-8',newline='\n')
p=r/'tools/build_native.py';s=p.read_text();anchor='    for p in deps:\n';assert s.count(anchor)==1;s=s.replace(anchor,"    if not opt.classpath_json:\n        deps.append(home/'AppData/Roaming/.technic/libraries/io/netty/netty-all/4.0.10.Final/netty-all-4.0.10.Final.jar')\n"+anchor);p.write_text(s,newline='\n')
print('N03 integrated: cancel guard, one pending connector, same native FML route, unchanged art')
