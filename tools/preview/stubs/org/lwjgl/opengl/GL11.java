package org.lwjgl.opengl;
import java.nio.ByteBuffer;
import java.util.*;
/**
 * Rasterizador OpenGL 1.x em software, SOMENTE para pre-visualizacao fora do jogo.
 * Cobre o subconjunto usado pelo pacote render: quads/strips/linhas, cor por vertice, textura bilinear
 * (clamp-to-edge), blend SRC_ALPHA/ONE_MINUS_SRC_ALPHA e matriz modelview 2D (translate/rotate).
 * Nao substitui o teste no jogo (Angelica, drivers, RawScope nao sao exercitados).
 */
public final class GL11 {
    private GL11(){}
    public static final int GL_TEXTURE_2D=0x0DE1,GL_QUADS=7,GL_TRIANGLE_STRIP=5,GL_LINES=1,GL_SMOOTH=0x1D01,GL_FLAT=0x1D00,
        GL_MAX_TEXTURE_SIZE=0x0D33,GL_TEXTURE_MIN_FILTER=0x2801,GL_TEXTURE_MAG_FILTER=0x2800,GL_LINEAR=0x2601,
        GL_TEXTURE_WRAP_S=0x2802,GL_TEXTURE_WRAP_T=0x2803,GL_UNPACK_ALIGNMENT=0x0CF5,GL_UNPACK_ROW_LENGTH=0x0CF2,
        GL_UNPACK_SKIP_PIXELS=0x0CF4,GL_UNPACK_SKIP_ROWS=0x0CF3,GL_RGBA8=0x8058,GL_RGBA=0x1908,GL_UNSIGNED_BYTE=0x1401,GL_SCISSOR_TEST=0x0C11;
    public static int width=1,height=1;public static float[] fb=new float[3];
    public static void surface(int w,int h){width=w;height=h;fb=new float[w*h*3];}
    private static final class Tex{int w,h;float[] p;}
    private static final Map<Integer,Tex> textures=new HashMap<Integer,Tex>();private static int nextId=1,bound;
    private static boolean texturing,scissor;private static int scX,scY,scW,scH;
    public static void glScissor(int x,int y,int w,int h){scX=x;scY=y;scW=w;scH=h;}private static double r=1,g=1,b=1,a=1,u,v;
    private static int mode=-1;private static final List<double[]> verts=new ArrayList<double[]>();
    private static double[] m={1,0,0,1,0,0};private static final Deque<double[]> stack=new ArrayDeque<double[]>();
    public static void glEnable(int cap){if(cap==GL_TEXTURE_2D)texturing=true;if(cap==GL_SCISSOR_TEST)scissor=true;}
    public static void glDisable(int cap){if(cap==GL_TEXTURE_2D)texturing=false;if(cap==GL_SCISSOR_TEST)scissor=false;}
    public static void glColor4d(double R,double G,double B,double A){r=R;g=G;b=B;a=A;}
    public static void glTexCoord2d(double U,double V){u=U;v=V;}
    public static void glShadeModel(int m){}
    public static void glLineWidth(float w){}
    public static void glTexParameteri(int t,int p,int v){}
    public static void glPixelStorei(int p,int v){}
    public static int glGetInteger(int p){return p==GL_MAX_TEXTURE_SIZE?8192:0;}
    public static int glGenTextures(){int id=nextId++;textures.put(id,new Tex());return id;}
    public static void glDeleteTextures(int id){textures.remove(id);}
    public static void glBindTexture(int t,int id){bound=id;}
    public static void glTexImage2D(int t,int level,int internal,int w,int h,int border,int format,int type,ByteBuffer buf){
        Tex x=textures.get(bound);x.w=w;x.h=h;x.p=new float[w*h*4];for(int i=0;i<w*h*4;i++)x.p[i]=(buf.get(i)&255)/255f;
    }
    public static void glPushMatrix(){stack.push(m.clone());}
    public static void glPopMatrix(){m=stack.pop();}
    public static void glTranslated(double x,double y,double z){m[4]+=m[0]*x+m[2]*y;m[5]+=m[1]*x+m[3]*y;}
    public static void glRotated(double deg,double x,double y,double z){double c=Math.cos(Math.toRadians(deg)),s=Math.sin(Math.toRadians(deg));
        double a0=m[0]*c+m[2]*s,a1=m[1]*c+m[3]*s,a2=-m[0]*s+m[2]*c,a3=-m[1]*s+m[3]*c;m[0]=a0;m[1]=a1;m[2]=a2;m[3]=a3;}
    public static void glBegin(int md){mode=md;verts.clear();}
    public static void glVertex2d(double x,double y){verts.add(new double[]{m[0]*x+m[2]*y+m[4],m[1]*x+m[3]*y+m[5],u,v,r,g,b,a});}
    public static void glEnd(){
        Tex t=texturing?textures.get(bound):null;
        if(mode==GL_QUADS)for(int i=0;i+3<verts.size();i+=4){tri(verts.get(i),verts.get(i+1),verts.get(i+2),t);tri(verts.get(i),verts.get(i+2),verts.get(i+3),t);}
        else if(mode==GL_TRIANGLE_STRIP)for(int i=0;i+2<verts.size();i++)tri(verts.get(i),verts.get(i+1),verts.get(i+2),t);
        else if(mode==GL_LINES)for(int i=0;i+1<verts.size();i+=2)line(verts.get(i),verts.get(i+1));
        verts.clear();mode=-1;
    }
    private static double edge(double[] p,double[] q,double x,double y){return (q[0]-p[0])*(y-p[1])-(q[1]-p[1])*(x-p[0]);}
    private static boolean topLeft(double[] p,double[] q,double sign){double dx=(q[0]-p[0])*sign,dy=(q[1]-p[1])*sign;return (dy==0&&dx>0)||dy<0;}
    private static void tri(double[] p0,double[] p1,double[] p2,Tex t){
        double area=edge(p0,p1,p2[0],p2[1]);if(area==0)return;double sign=area>0?1:-1;
        int x0=Math.max(0,(int)Math.floor(Math.min(p0[0],Math.min(p1[0],p2[0])))),x1=Math.min(width-1,(int)Math.ceil(Math.max(p0[0],Math.max(p1[0],p2[0]))));
        int y0=Math.max(0,(int)Math.floor(Math.min(p0[1],Math.min(p1[1],p2[1])))),y1=Math.min(height-1,(int)Math.ceil(Math.max(p0[1],Math.max(p1[1],p2[1]))));
        boolean t0=topLeft(p1,p2,sign),t1=topLeft(p2,p0,sign),t2=topLeft(p0,p1,sign);
        for(int y=y0;y<=y1;y++)for(int x=x0;x<=x1;x++){
            double cx=x+.5,cy=y+.5,w0=edge(p1,p2,cx,cy)*sign,w1=edge(p2,p0,cx,cy)*sign,w2=edge(p0,p1,cx,cy)*sign;
            if(w0<0||w1<0||w2<0||(w0==0&&!t0)||(w1==0&&!t1)||(w2==0&&!t2))continue;
            double s=w0+w1+w2;w0/=s;w1/=s;w2/=s;double[] c=new double[8];for(int k=2;k<8;k++)c[k]=p0[k]*w0+p1[k]*w1+p2[k]*w2;
            double R=c[4],G=c[5],B=c[6],A=c[7];
            if(t!=null){float[] q=sample(t,c[2],c[3]);R*=q[0];G*=q[1];B*=q[2];A*=q[3];}
            blend(x,y,R,G,B,A);
        }
    }
    private static float[] sample(Tex t,double U,double V){
        double fx=U*t.w-.5,fy=V*t.h-.5;int ix=(int)Math.floor(fx),iy=(int)Math.floor(fy);double ax=fx-ix,ay=fy-iy;float[] o=new float[4];
        for(int k=0;k<4;k++){o[k]=(float)((texel(t,ix,iy,k)*(1-ax)+texel(t,ix+1,iy,k)*ax)*(1-ay)+(texel(t,ix,iy+1,k)*(1-ax)+texel(t,ix+1,iy+1,k)*ax)*ay);}return o;
    }
    private static float texel(Tex t,int x,int y,int k){x=Math.max(0,Math.min(t.w-1,x));y=Math.max(0,Math.min(t.h-1,y));return t.p[(y*t.w+x)*4+k];}
    private static void blend(int x,int y,double R,double G,double B,double A){
        if(x<0||y<0||x>=width||y>=height)return;
        if(scissor){int gy=height-1-y;if(x<scX||x>=scX+scW||gy<scY||gy>=scY+scH)return;}
        A=Math.max(0,Math.min(1,A));int i=(y*width+x)*3;
        fb[i]=(float)(R*A+fb[i]*(1-A));fb[i+1]=(float)(G*A+fb[i+1]*(1-A));fb[i+2]=(float)(B*A+fb[i+2]*(1-A));
    }
    private static void line(double[] p,double[] q){
        double dx=q[0]-p[0],dy=q[1]-p[1];int n=(int)Math.ceil(Math.max(Math.abs(dx),Math.abs(dy)));
        for(int i=0;i<=n;i++){double t=n==0?0:(double)i/n;blend((int)Math.floor(p[0]+dx*t),(int)Math.floor(p[1]+dy*t),p[4],p[5],p[6],p[7]);}
    }
}
