import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import org.objectweb.asm.util.*;
import br.com.redeakashic.cinematic.bootstrap.SplashTransformer;
import br.com.redeakashic.cinematic.core.LoadState;
public final class TransformerProbe {
    static int checks;
    static final String TARGET="cpw.mods.fml.client.SplashProgress$3", HOOK="br/com/redeakashic/cinematic/bootstrap/SplashHooks";
    static void check(boolean b,String n){checks++;if(!b)throw new AssertionError(n);}
    static ClassNode parse(byte[] b){ClassNode n=new ClassNode();new ClassReader(b).accept(n,0);return n;}
    static byte[] bytes(ClassNode c){ClassWriter w=new ClassWriter(0);c.accept(w);return w.toByteArray();}
    static String trace(ClassNode c){Textifier t=new Textifier();c.accept(new TraceClassVisitor(null,t,null));return t.getText().toString();}
    public static void main(String[] args)throws Exception{
        byte[] original;
        try(ZipFile z=new ZipFile(args[0]);java.io.InputStream in=z.getInputStream(z.getEntry(TARGET.replace('.','/')+".class"))){java.io.ByteArrayOutputStream o=new java.io.ByteArrayOutputStream();byte[] buf=new byte[8192];int n;while((n=in.read(buf))!=-1)o.write(buf,0,n);original=o.toByteArray();}
        SplashTransformer t=new SplashTransformer();
        check(t.transform("other","other",original)==original,"non-target untouched");check(t.transform(TARGET,TARGET,null)==null,"null untouched");
        for(String k:new String[]{"akashic.cinematic.disabled","akashic.cinematic.disableSplash"}){System.setProperty(k,"true");check(t.transform(TARGET,TARGET,original)==original,"disable switch "+k);System.clearProperty(k);}
        byte[] transformed=t.transform(TARGET,TARGET,original);check(transformed!=original,"real Forge transformed");check(t.transform(TARGET,TARGET,transformed)==transformed,"idempotence");
        ClassNode patched=parse(transformed);int frames=0,finishes=0,clears=0;
        for(MethodNode m:patched.methods){
            new Analyzer<BasicValue>(new BasicVerifier()).analyze(patched.name,m);
            for(AbstractInsnNode n=m.instructions.getFirst();n!=null;){AbstractInsnNode next=n.getNext();if(n instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)n;
                if(call.name.equals("clearGL"))clears++;
                if(call.owner.equals(HOOK)){
                    check(m.name.equals("run")&&call.desc.equals("()V"),"hook signature/location");
                    if(call.name.equals("frame")){frames++;AbstractInsnNode a=call.getNext();while(a.getOpcode()<0)a=a.getNext();check(a instanceof FieldInsnNode&&((FieldInsnNode)a).name.equals("mutex"),"draw before semaphore");}
                    if(call.name.equals("finish")){finishes++;check(clears==1,"cleanup after pause site");AbstractInsnNode a=call.getNext();while(a.getOpcode()<0)a=a.getNext();check(a instanceof MethodInsnNode&&((MethodInsnNode)a).name.equals("clearGL"),"cleanup before final GL release");}
                    m.instructions.remove(call);
                }
            }n=next;}
        }
        check(frames==1&&finishes==1,"one frame and one cleanup hook");check(trace(parse(original)).equals(trace(patched)),"ALL original instructions/fields/handlers retained");
        ClassNode unsupported=parse(original);for(MethodNode m:unsupported.methods)if(m.name.equals("run"))for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext())if(n instanceof MethodInsnNode&&((MethodInsnNode)n).name.equals("acquireUninterruptibly"))((MethodInsnNode)n).name="unexpectedAcquire";
        byte[] altered=bytes(unsupported);check(t.transform(TARGET,TARGET,altered)==altered,"unsupported version retained");
        byte[] malformed={1,2,3};check(t.transform(TARGET,TARGET,malformed)==malformed,"malformed class retained");
        check(Double.isNaN(LoadState.unknown().fraction()),"unknown has no percent");
        check(new LoadState(null,null,null,-1,-5).total==0,"invalid total becomes unknown");
        check(new LoadState("x","y","z",Integer.MAX_VALUE,Integer.MAX_VALUE).percent().equals("100%"),"percent without integer overflow");
        check(new LoadState("x","y","z",999,7).step==7,"stage step clamped");
        check(LoadState.clean("A\u00a7cB\nC",9).equals("AB C"),"strip formatting/control chars");
        check(LoadState.clean("0123456789",4).equals("0123"),"bounded labels");
        System.out.println("RESULT checks="+checks+" failures=0; real Forge bytecode validated; no Minecraft runtime claimed");
    }
}
