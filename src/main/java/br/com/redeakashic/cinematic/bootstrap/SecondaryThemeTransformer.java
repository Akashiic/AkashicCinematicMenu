package br.com.redeakashic.cinematic.bootstrap;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
/** Narrow fail-open skin hooks for vanilla GUI primitives. No screen actions are replaced. */
public final class SecondaryThemeTransformer implements IClassTransformer {
    private static final String SCREEN="net.minecraft.client.gui.GuiScreen";
    private static final String BUTTON="net.minecraft.client.gui.GuiButton";
    private static final String SLOT="net.minecraft.client.gui.GuiSlot";
    private static final String HOOK="br/com/redeakashic/cinematic/client/SecondaryScreenTheme";
    @Override public byte[] transform(String name,String mapped,byte[] input){
        if(input==null||Boolean.getBoolean("akashic.cinematic.disabled"))return input;
        String target=SCREEN.equals(mapped)||BUTTON.equals(mapped)||SLOT.equals(mapped)?mapped:name;
        if(!SCREEN.equals(target)&&!BUTTON.equals(target)&&!SLOT.equals(target))return input;
        try{
            ClassNode c=new ClassNode();new ClassReader(input).accept(c,0);
            boolean changed;
            if(SCREEN.equals(target))changed=screen(c);
            else if(BUTTON.equals(target))changed=button(c);
            else changed=slot(c);
            if(!changed)return input;
            ClassWriter out=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(out);
            System.out.println("[AkashicCinematic] Secondary GUI primitive themed: "+target);
            return out.toByteArray();
        }catch(RuntimeException|LinkageError ex){
            System.err.println("[AkashicCinematic] Secondary theme splice rejected: "+target+" "+ex);
            return input;
        }
    }
    private static boolean screen(ClassNode c){
        MethodNode method=null;int count=0;
        for(Object value:c.methods){MethodNode m=(MethodNode)value;
            if(m.desc.equals("(I)V")&&(m.name.equals("func_146278_c")||m.name.equals("drawWorldBackground"))){method=m;count++;}
        }
        if(count!=1||method==null)return false;
        for(AbstractInsnNode n=method.instructions.getFirst();n!=null;n=n.getNext())
            if(n instanceof MethodInsnNode&&((MethodInsnNode)n).owner.equals(HOOK))return false;
        LabelNode vanilla=new LabelNode();InsnList head=new InsnList();
        head.add(new VarInsnNode(Opcodes.ALOAD,0));head.add(new VarInsnNode(Opcodes.ILOAD,1));
        head.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"drawBackground","(Lnet/minecraft/client/gui/GuiScreen;I)Z",false));
        head.add(new JumpInsnNode(Opcodes.IFEQ,vanilla));head.add(new InsnNode(Opcodes.RETURN));head.add(vanilla);
        method.instructions.insert(head);return true;
    }
    private static boolean button(ClassNode c){
        MethodNode method=null;int methods=0,fields=0;
        for(Object value:c.methods){MethodNode m=(MethodNode)value;
            if(m.desc.equals("(Lnet/minecraft/client/Minecraft;II)V")&&(m.name.equals("func_146112_a")||m.name.equals("drawButton"))){method=m;methods++;}
        }
        if(methods!=1||method==null)return false;
        for(AbstractInsnNode n=method.instructions.getFirst();n!=null;n=n.getNext())if(n instanceof FieldInsnNode){
            FieldInsnNode f=(FieldInsnNode)n;
            if(f.getOpcode()==Opcodes.GETSTATIC&&f.owner.equals("net/minecraft/client/gui/GuiButton")&&buttonTexture(f.name))fields++;
        }
        if(fields!=1)return false;
        for(AbstractInsnNode n=method.instructions.getFirst();n!=null;){AbstractInsnNode next=n.getNext();
            if(n instanceof FieldInsnNode){FieldInsnNode f=(FieldInsnNode)n;
                if(f.getOpcode()==Opcodes.GETSTATIC&&f.owner.equals("net/minecraft/client/gui/GuiButton")&&buttonTexture(f.name))
                    method.instructions.set(f,new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"buttonTexture","()Lnet/minecraft/util/ResourceLocation;",false));
            }n=next;
        }return true;
    }
    /** SRG name at runtime; MCP alias only so the CI probe can run against RFG's Forge-patched (MCP-named) jar. */
    private static boolean buttonTexture(String n){return n.equals("field_146122_a")||n.equals("buttonTextures");}
    private static boolean dirt(AbstractInsnNode n){
        if(!(n instanceof FieldInsnNode)||n.getOpcode()!=Opcodes.GETSTATIC)return false;FieldInsnNode f=(FieldInsnNode)n;
        return f.owner.equals("net/minecraft/client/gui/Gui")&&(f.name.equals("field_110325_k")||f.name.equals("optionsBackground"));
    }
    private static boolean drawScreen(MethodNode m){return m.desc.equals("(IIF)V")&&(m.name.equals("func_148128_a")||m.name.equals("drawScreen"));}
    private static boolean overlayBackground(MethodNode m){return m.desc.equals("(IIII)V")&&(m.name.equals("func_148136_c")||m.name.equals("overlayBackground"));}
    /** Forge 1.7.10 moves the list's container background out of drawScreen into this method (Forge-added, never obfuscated). */
    private static boolean containerBackground(MethodNode m){return m.desc.equals("(Lnet/minecraft/client/renderer/Tessellator;)V")&&m.name.equals("drawContainerBackground");}
    private static int dirtRefs(MethodNode m){int n=0;for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext())if(dirt(i))n++;return n;}
    /**
     * Texture swap. Exact anchors: one reference in overlayBackground and one in the container background, which is in
     * drawScreen (vanilla) or in drawContainerBackground (Forge). Any other count: class untouched (fail-open).
     */
    private static boolean slot(ClassNode c){
        int overlay=0,container=0,overlays=0;
        for(Object value:c.methods){MethodNode m=(MethodNode)value;
            if(overlayBackground(m)){overlays++;overlay+=dirtRefs(m);}
            else if(drawScreen(m)||containerBackground(m))container+=dirtRefs(m);
        }
        if(overlays!=1||overlay!=1||container!=1)return false;
        for(Object value:c.methods){MethodNode m=(MethodNode)value;
            if(!(overlayBackground(m)||drawScreen(m)||containerBackground(m)))continue;
            for(AbstractInsnNode n=m.instructions.getFirst();n!=null;){AbstractInsnNode next=n.getNext();
                if(dirt(n))m.instructions.set(n,new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"listTexture","()Lnet/minecraft/util/ResourceLocation;",false));
                n=next;
            }
        }
        int hooks=listRepaint(c);
        if(hooks==0)System.err.println("[AkashicCinematic] GuiSlot repaint anchors diverged; list texture swap only");
        else System.out.println("[AkashicCinematic] GuiSlot repaint hooks: "+hooks);
        return true;
    }
    /**
     * Second, independent splice (fail-open): after the container background's Tessellator.draw() (in drawScreen for
     * vanilla, in drawContainerBackground for Forge), and at every RETURN of overlayBackground, call the theme so the
     * frame is repainted under/around the list. Exact anchors: one overlayBackground(IIII)V, exactly one listTexture()
     * call in the container method, the first Tessellator.draw()I after it followed by POP, and at least one RETURN.
     * Any divergence: nothing is inserted.
     */
    static int listRepaint(ClassNode c){
        MethodNode overlay=null,container=null;int overlays=0,containers=0;
        for(Object value:c.methods){MethodNode m=(MethodNode)value;
            if(overlayBackground(m)){overlay=m;overlays++;}
            for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext())if(n instanceof MethodInsnNode){
                MethodInsnNode mi=(MethodInsnNode)n;
                if(mi.owner.equals(HOOK)&&(mi.name.equals("listBackgroundDrawn")||mi.name.equals("listOverlayDrawn")))return 0;
                if(mi.owner.equals(HOOK)&&mi.name.equals("listTexture")&&(drawScreen(m)||containerBackground(m))){container=m;containers++;}
            }
        }
        if(overlays!=1||containers!=1)return 0;
        AbstractInsnNode anchor=null;
        for(AbstractInsnNode n=container.instructions.getFirst();n!=null;n=n.getNext())
            if(n instanceof MethodInsnNode&&((MethodInsnNode)n).owner.equals(HOOK)&&((MethodInsnNode)n).name.equals("listTexture"))anchor=n;
        MethodInsnNode tessDraw=null;
        for(AbstractInsnNode n=anchor.getNext();n!=null;n=n.getNext())if(n instanceof MethodInsnNode){
            MethodInsnNode mi=(MethodInsnNode)n;
            if(mi.owner.equals("net/minecraft/client/renderer/Tessellator")&&(mi.name.equals("func_78381_a")||mi.name.equals("draw"))&&mi.desc.equals("()I")){tessDraw=mi;break;}
        }
        if(tessDraw==null||tessDraw.getNext()==null||tessDraw.getNext().getOpcode()!=Opcodes.POP)return 0;
        java.util.List<AbstractInsnNode> returns=new java.util.ArrayList<AbstractInsnNode>();
        for(AbstractInsnNode n=overlay.instructions.getFirst();n!=null;n=n.getNext())if(n.getOpcode()==Opcodes.RETURN)returns.add(n);
        if(returns.isEmpty())return 0;
        InsnList after=new InsnList();after.add(new VarInsnNode(Opcodes.ALOAD,0));
        after.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"listBackgroundDrawn","(Lnet/minecraft/client/gui/GuiSlot;)V",false));
        container.instructions.insert(tessDraw.getNext(),after);
        for(AbstractInsnNode ret:returns){InsnList call=new InsnList();
            call.add(new VarInsnNode(Opcodes.ALOAD,0));call.add(new VarInsnNode(Opcodes.ILOAD,1));call.add(new VarInsnNode(Opcodes.ILOAD,2));
            call.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"listOverlayDrawn","(Lnet/minecraft/client/gui/GuiSlot;II)V",false));
            overlay.instructions.insertBefore(ret,call);}
        return 1+returns.size();
    }
}
