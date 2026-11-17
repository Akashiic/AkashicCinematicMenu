import br.com.redeakashic.cinematic.core.LoadState;
import br.com.redeakashic.cinematic.render.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;

/**
 * Amostras da edicao de Natal renderizadas pelo PROPRIO codigo do pacote render (NativeVisuals,
 * BackdropRenderer, HeroButton, SecondaryFrameRenderer) sobre o rasterizador em software de tools/preview/stubs.
 * Uso: java -cp <classes>:src/main/resources NatalPreview <pasta-de-saida>
 */
public final class NatalPreview {
    private static File out;
    public static void main(String[] args) throws Exception {
        out=new File(args[0]);out.mkdirs();
        LoadState mods=new LoadState("Carregando mods","Inicializando o Thaumcraft","FORGE / Pré-inicialização",142,318);
        LoadState unknown=LoadState.unknown();
        int[][] sizes={{1920,1080},{1366,768},{2560,1080},{1280,800},{1024,768},{854,480}};
        for(int[] s:sizes){
            menu(s[0],s[1],0,0,new double[]{1,0,0,0,0,0},true,"menu-"+s[0]+"x"+s[1]);
            boot(s[0],s[1],mods,"boot-"+s[0]+"x"+s[1]);
        }
        menu(1920,1080,0,0,new double[]{0,0,1,0,0,0},true,"menu-1920x1080-hover-multijogador");
        menu(1920,1080,0,0,new double[]{1,0,0,0,0,0},false,"menu-1920x1080-movimento-reduzido");
        menu(1920,1080,0,0,new double[]{0,0,0,0,0,1},true,"menu-1920x1080-hover-discord");
        menu(1366,768,0,0,new double[]{0,0,0,0,0,1},true,"menu-1366x768-hover-discord");
        boot(1920,1080,unknown,"boot-1920x1080-sem-total");
        // Varredura de paralaxe (mouse da esquerda/cima para direita/baixo) para animacao.
        for(int i=0;i<24;i++){double t=i/23.0*2*Math.PI;menuFrame(960,540,Math.cos(t),Math.sin(t)*.8,i*.18,new double[]{1,0,0,0,0,0},true,String.format("parallax-%02d",i));}
        for(int[] s:new int[][]{{1920,1080},{1366,768}}){transition(s[0],s[1]);secondary(s[0],s[1]);}
        System.out.println("PREVIEW_WRITTEN "+out.getAbsolutePath());
    }
    private static void menu(int w,int h,double px,double py,double[] hl,boolean motion,String name)throws Exception{menuFrame(w,h,px,py,4.2,hl,motion,name);}
    private static void menuFrame(int w,int h,double px,double py,double age,double[] hl,boolean motion,String name)throws Exception{
        GL11.surface(w,h);NativeVisuals v=new NativeVisuals("responsive");
        try{v.transform(w,h);v.menu(age,px,py,1,hl,motion,true);}finally{v.close();}save(w,h,name);
    }
    private static void boot(int w,int h,LoadState s,String name)throws Exception{
        GL11.surface(w,h);NativeVisuals v=new NativeVisuals("responsive");
        try{v.transform(w,h);v.startup(s,83,1,true);}finally{v.close();}save(w,h,name);
    }
    private static void transition(int w,int h)throws Exception{
        GL11.surface(w,h);BackdropRenderer b=new BackdropRenderer();UiTypography t=new UiTypography();
        try{NativeVisuals.transition(b,t,w,h);}finally{b.close();t.close();}save(w,h,"transicao-"+w+"x"+h);
    }
    private static void secondary(int w,int h)throws Exception{
        GL11.surface(w,h);SecondaryFrameRenderer r=new SecondaryFrameRenderer();
        try{r.draw(w,h,"CONFIGURAÇÕES");}finally{r.close();}save(w,h,"secundaria-"+w+"x"+h);
    }
    private static void save(int w,int h,String name)throws Exception{
        BufferedImage im=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);float[] fb=GL11.fb;
        for(int y=0;y<h;y++)for(int x=0;x<w;x++){int i=(y*w+x)*3;int r=Math.round(Math.max(0,Math.min(1,fb[i]))*255),g=Math.round(Math.max(0,Math.min(1,fb[i+1]))*255),b=Math.round(Math.max(0,Math.min(1,fb[i+2]))*255);im.setRGB(x,y,(r<<16)|(g<<8)|b);}
        ImageIO.write(im,"png",new File(out,name+".png"));
    }
}
