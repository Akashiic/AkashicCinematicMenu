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
        listMock(1920,1080,"MUNDOS",false,"simulacao-singleplayer-1920x1080");
        listMock(1920,1080,"TEXTURAS",true,"simulacao-texturas-1920x1080");
        listMock(1366,768,"IDIOMA",false,"simulacao-idioma-1366x768");
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
    /**
     * SIMULACAO das telas com lista (GuiSlot) depois dos ganchos: frame + superficie translucida nas proporcoes reais
     * da GuiSlot (escala GUI 3 em 1080p, 2 em 768p). Texto e botoes do vanilla sao aproximados com a nossa fonte.
     */
    private static void listMock(int w,int h,String section,boolean packs,String name)throws Exception{
        GL11.surface(w,h);SecondaryFrameRenderer r=new SecondaryFrameRenderer();UiTypography t=new UiTypography();
        int scale=h>=1000?3:2;int gw=w/scale,gh=h/scale;
        try{
            r.draw(w,h,section);
            int top=32,bottom=packs?gh-51:gh-64;
            int[][] lists=packs?new int[][]{{gw/2-4-200,gw/2-4},{gw/2+4,gw/2+4+200}}:new int[][]{{0,gw}};
            String[][] rows=packs?new String[][]{{"Aether b1.7.3 Textures","Faithful 32x"},{"High Texture Attempt","Default"}}
                :new String[][]{{"Mundo Forbidden","Criativo de testes","Sobrevivência Natal"}};
            for(int k=0;k<lists.length;k++){
                double x=lists[k][0]*scale,y=top*scale,rw=(lists[k][1]-lists[k][0])*scale,rh=(bottom-top)*scale;
                r.listSurface(w,h,x,y,rw,rh);
                if(packs)t.centered(k==0?"Pacotes Disponíveis":"Pacotes Selecionados",x+rw/2,y+6*scale,8*scale,rw,false,0,0xFFFFFF,1);
                String[] items=rows[packs?k:0];
                for(int i=0;i<items.length;i++){
                    double ry=y+(packs?20:4)*scale+i*36*scale,rx=packs?x+6*scale:w/2-110*scale,rwid=packs?rw-12*scale:220*scale;
                    if(i==0){NativeVisuals.rect(rx-2*scale,ry-2*scale,rwid+4*scale,34*scale,0x808080,1);NativeVisuals.rect(rx-scale,ry-scale,rwid+2*scale,32*scale,0x000000,1);}
                    NativeVisuals.rect(rx+2*scale,ry+2*scale,28*scale,28*scale,0x5A7A3A,.9);
                    t.draw(items[i],rx+36*scale,ry+3*scale,9*scale,rwid-40*scale,false,0,0xFFFFFF,1);
                    t.draw(packs?"Descrição do pacote":"17/11/2026  ·  Sobrevivência",rx+36*scale,ry+16*scale,7*scale,rwid-40*scale,false,0,0x808080,1);
                }
            }
            if(!packs)t.centered(section.equals("IDIOMA")?"Idioma...":"Selecionar Mundo",w/2,8*scale,9*scale,w,false,0,0xFFFFFF,1);
            String[] buttons=packs?new String[]{"Abrir pasta","Concluído"}:new String[]{"Jogar","Criar Novo Mundo","Renomear","Apagar","Recriar","Cancelar"};
            int cols=packs?2:(buttons.length==6?3:2);
            for(int i=0;i<buttons.length;i++){
                int bw=packs?150:(i<2?150:72),col=packs?i:(i<2?i:i-2),row=packs?0:(i<2?0:1);
                double bx=packs?(w/2+(i==0?-154:4)*scale):(i<2?w/2+(i==0?-154:4)*scale:w/2+(-154+(i-2)*77)*scale);
                double by=(gh-(packs?48:52)+row*24)*scale;
                NativeVisuals.rect(bx,by,bw*scale,20*scale,NativeVisuals.SLATE,.95);NativeVisuals.rect(bx,by,bw*scale,scale,NativeVisuals.GOLD,.6);
                t.centered(buttons[i],bx+bw*scale/2.0,by+6*scale,8*scale,bw*scale,false,0,0xE8E4D8,1);
            }
            t.draw("SIMULAÇÃO (texto e botões vanilla aproximados)",12,h-28,12,w,false,0,0xFF6060,.9);
        }finally{r.close();t.close();}
        save(w,h,name);
    }
    private static void save(int w,int h,String name)throws Exception{
        BufferedImage im=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);float[] fb=GL11.fb;
        for(int y=0;y<h;y++)for(int x=0;x<w;x++){int i=(y*w+x)*3;int r=Math.round(Math.max(0,Math.min(1,fb[i]))*255),g=Math.round(Math.max(0,Math.min(1,fb[i+1]))*255),b=Math.round(Math.max(0,Math.min(1,fb[i+2]))*255);im.setRGB(x,y,(r<<16)|(g<<8)|b);}
        ImageIO.write(im,"png",new File(out,name+".png"));
    }
}
