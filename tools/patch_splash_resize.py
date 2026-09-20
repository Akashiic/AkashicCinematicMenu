from pathlib import Path
R=Path(__file__).resolve().parents[1];p=R/'src/main/java/br/com/redeakashic/cinematic/bootstrap/SplashTransformer.java';s=p.read_text();anchor='            // Only the final clearGL releases our textures;';assert s.count(anchor)==1
s=s.replace(anchor,'            // Exactly one present at the existing Forge semaphore-protected site.\n            run.instructions.set(updates.get(0),new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"present","()V",false));\n'+anchor);p.write_text(s,encoding='utf-8',newline='\n')
p=R/'src/main/java/br/com/redeakashic/cinematic/bootstrap/SplashHooks.java';s=p.read_text(encoding='utf-8');anchor='    public static LoadState sample(){';assert s.count(anchor)==1
code='''    /** Kept in the existing excluded bootstrap package: never redirected by Angelica. */
    public static void present(){
        // LWJGL updates cached width/height/wasResized here. swapBuffers + processMessages alone does not.
        // Called at the original FML present site; no second swap and no new lock/context owner.
        Display.update();
    }
'''
s=s.replace(anchor,code+anchor).replace('    private static long started,lastPoll;','    private static long started,lastPoll;\n    private static int previousWidth,previousHeight,geometryLogs;')
anchor='        try(RawScope scope=new RawScope(w,h)){';assert s.count(anchor)==1
s=s.replace(anchor,'        if((w!=previousWidth||h!=previousHeight)&&geometryLogs++<40){previousWidth=w;previousHeight=h;System.out.println("[AkashicCinematic] SPLASH_GEOMETRY "+w+"x"+h);}\n'+anchor)
p.write_text(s,encoding='utf-8',newline='\n');print('Resize delta: one native Display.update at existing present site; bounded geometry logs')
