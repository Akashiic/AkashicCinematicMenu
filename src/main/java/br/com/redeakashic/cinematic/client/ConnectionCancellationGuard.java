package br.com.redeakashic.cinematic.client;
import br.com.redeakashic.cinematic.core.ConnectionWatchPolicy;
import br.com.redeakashic.cinematic.core.ServerTarget;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.ChatComponentText;
import java.lang.reflect.Field;
/** Own-menu connector lifecycle only. No protocol replacement, threads or retry timer. */
public final class ConnectionCancellationGuard {
    public static final ConnectionCancellationGuard INSTANCE=new ConnectionCancellationGuard();
    private Field managerField,cancelledField;
    private Pending pending;
    private long sequence;
    private boolean unavailable;
    private static final class Pending {
        final GuiConnecting screen;final Thread worker;final long id;
        boolean closeRequested,reportedCancel;
        boolean workerKnown=true;
        Pending(GuiConnecting screen,Thread worker,long id){this.screen=screen;this.worker=worker;this.id=id;}
    }
    private ConnectionCancellationGuard(){}
    private static Field find(String... names)throws NoSuchFieldException{
        for(String n:names)try{Field f=GuiConnecting.class.getDeclaredField(n);f.setAccessible(true);return f;}catch(NoSuchFieldException ignored){}
        throw new NoSuchFieldException("GuiConnecting cancellation fields");
    }
    private void preflight()throws ReflectiveOperationException{
        if(managerField==null){managerField=find("field_146371_g","networkManager");cancelledField=find("field_146373_h","cancel");
            if(!NetworkManager.class.isAssignableFrom(managerField.getType())||cancelledField.getType()!=boolean.class)throw new IllegalStateException("Unsupported connector ABI");}
    }
    private static Thread workerFor(GuiConnecting screen)throws IllegalAccessException{
        String prefix=GuiConnecting.class.getName()+"$";
        for(Thread t:Thread.getAllStackTraces().keySet()){
            if(!t.getClass().getName().startsWith(prefix))continue;
            for(Field f:t.getClass().getDeclaredFields())if(GuiConnecting.class.isAssignableFrom(f.getType())){
                f.setAccessible(true);if(f.get(t)==screen)return t;
            }
        }return null;
    }
    public void connect(Minecraft mc,GuiScreen parent){
        poll(mc);
        if(unavailable||!ConnectionWatchPolicy.canAdmit(pending==null?0:1)){
            mc.func_147108_a(new GuiDisconnected(parent,"connect.failed",new ChatComponentText(unavailable?
                "A verificação de cancelamento não está disponível. Consulte o log.":"A conexão anterior ainda está sendo encerrada. Aguarde e tente novamente.")));return;
        }
        try{preflight();Thread.getAllStackTraces();}
        catch(ReflectiveOperationException|RuntimeException ex){unavailable=true;System.err.println("[AkashicCinematic] Connector guard unavailable: "+ex);connect(mc,parent);return;}
        FMLClientHandler handler=FMLClientHandler.instance();handler.setupServerList();
        handler.connectToServer(parent,new ServerData(ServerTarget.NAME,ServerTarget.address()));
        if(mc.field_71462_r instanceof GuiConnecting){
            GuiConnecting g=(GuiConnecting)mc.field_71462_r;
            try{pending=new Pending(g,workerFor(g),++sequence);System.out.println("[AkashicCinematic] Connector watch "+pending.id+" worker="+(pending.worker!=null));}
            catch(ReflectiveOperationException|RuntimeException ex){pending=new Pending(g,null,++sequence);pending.workerKnown=false;unavailable=true;System.err.println("[AkashicCinematic] Connector watch setup failed: "+ex);}
        }
    }
    @SubscribeEvent public void onTick(TickEvent.ClientTickEvent event){if(event.phase==TickEvent.Phase.END)poll(Minecraft.func_71410_x());}
    private void poll(Minecraft mc){
        Pending p=pending;if(p==null)return;
        try{
            boolean cancelled=cancelledField.getBoolean(p.screen);
            boolean returned=mc.field_71441_e==null&&mc.field_71462_r instanceof CinematicMenu;
            if(returned&&!cancelled){cancelledField.setBoolean(p.screen,true);cancelled=true;}
            // Observe termination BEFORE reading the result published by the connector worker.
            boolean done=p.workerKnown&&(p.worker==null||!p.worker.isAlive());
            NetworkManager manager=(NetworkManager)managerField.get(p.screen);
            boolean open=manager!=null&&manager.func_150724_d();
            if(cancelled&&!p.reportedCancel){p.reportedCancel=true;System.out.println("[AkashicCinematic] Cancellation observed "+p.id+" managerPresent="+(manager!=null));}
            ConnectionWatchPolicy.Decision d=ConnectionWatchPolicy.decide(cancelled,returned,manager!=null,open,done,mc.field_71462_r==p.screen,mc.field_71441_e!=null);
            if(d==ConnectionWatchPolicy.Decision.CLOSE_CANCELLED&&!p.closeRequested){
                p.closeRequested=true;manager.func_150718_a(new ChatComponentText("Aborted"));
                System.out.println("[AkashicCinematic] Closing cancelled connector "+p.id+" (asynchronous vanilla NetworkManager.close)");
            }
            if(d==ConnectionWatchPolicy.Decision.RELEASE){
                pending=null;System.out.println("[AkashicCinematic] Connector watch released "+p.id+" cancelled="+cancelled+" channelOpen="+open+" workerFinished="+done);
            }
        }catch(ReflectiveOperationException|RuntimeException ex){
            if(!unavailable)System.err.println("[AkashicCinematic] Connector lifecycle inspection failed: "+ex);
            unavailable=true;
        }
    }
}
