package br.com.redeakashic.cinematic.bootstrap;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
/** Background-only splice. Native progress, tips, networking, presentation and timing remain intact. */
public final class LoadingThemeTransformer implements IClassTransformer {
    private static final String AETHER="com.gildedgames.the_aether.client.gui.AetherLoadingScreen";
    private static final String LOADING="net.minecraft.client.LoadingScreenRenderer",TERRAIN="net.minecraft.client.gui.GuiDownloadTerrain";
    private static final String HOOK="br/com/redeakashic/cinematic/client/TransitionTheme";
    @Override public byte[] transform(String name,String mapped,byte[] bytes){
        String target=AETHER.equals(mapped)||LOADING.equals(mapped)||TERRAIN.equals(mapped)?mapped:name;
        if(bytes==null||!(AETHER.equals(target)||LOADING.equals(target)||TERRAIN.equals(target))||Boolean.getBoolean("akashic.cinematic.disabled"))return bytes;
        try{
            ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);boolean terrain=TERRAIN.equals(target);MethodNode method=null;
            for(Object object:c.methods){MethodNode m=(MethodNode)object;
                if((terrain&&m.desc.equals("(IIF)V")&&(m.name.equals("func_73863_a")||m.name.equals("drawScreen")))||(!terrain&&m.desc.equals("(I)V")&&(m.name.equals("func_73718_a")||m.name.equals("setLoadingProgress"))))method=m;}
            if(method==null)return bytes;AbstractInsnNode anchor=null;boolean dirt=false;int candidates=0;
            for(AbstractInsnNode n=method.instructions.getFirst();n!=null;n=n.getNext()){
                if(n instanceof FieldInsnNode){FieldInsnNode f=(FieldInsnNode)n;if(f.owner.equals("net/minecraft/client/gui/Gui")&&(f.name.equals("field_110325_k")||f.name.equals("optionsBackground")))dirt=true;}
                if(!(n instanceof MethodInsnNode))continue;MethodInsnNode call=(MethodInsnNode)n;if(call.owner.equals(HOOK))return bytes;
                if(terrain&&(call.name.equals("func_146278_c")||call.name.equals("drawBackground"))&&call.desc.equals("(I)V")){anchor=n;candidates++;}
                if(!terrain&&dirt&&call.owner.equals("net/minecraft/client/renderer/Tessellator")&&(call.name.equals("func_78381_a")||call.name.equals("draw"))&&call.desc.equals("()I")){
                    AbstractInsnNode after=n.getNext();while(after!=null&&after.getOpcode()<0)after=after.getNext();
                    if(after==null||after.getOpcode()!=Opcodes.POP)return bytes;anchor=after;candidates++;dirt=false;
                }
            }
            if(candidates!=1||anchor==null){System.err.println("[AkashicCinematic] Transition hook skipped; unsupported shape: "+target);return bytes;}
            InsnList insert=new InsnList();
            if(terrain)insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"terrain","()V",false));
            else{
                String field=AETHER.equals(target)?"framebuffer":"field_146588_g";boolean found=false;
                for(Object o:c.fields){FieldNode f=(FieldNode)o;if(f.name.equals(field)&&f.desc.equals("Lnet/minecraft/client/shader/Framebuffer;"))found=true;}
                if(!found)return bytes;
                insert.add(new VarInsnNode(Opcodes.ALOAD,0));insert.add(new FieldInsnNode(Opcodes.GETFIELD,c.name,field,"Lnet/minecraft/client/shader/Framebuffer;"));
                insert.add(new LdcInsnNode(AETHER.equals(target)?"aether":"vanilla-loading"));
                insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"loading","(Lnet/minecraft/client/shader/Framebuffer;Ljava/lang/String;)V",false));
            }
            method.instructions.insert(anchor,insert);ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(writer);
            System.out.println("[AkashicCinematic] Background-only theme installed for "+target);return writer.toByteArray();
        }catch(RuntimeException|LinkageError ex){System.err.println("[AkashicCinematic] Transition hook rejected: "+target+" "+ex);return bytes;}
    }
}
