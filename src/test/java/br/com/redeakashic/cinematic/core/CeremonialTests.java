package br.com.redeakashic.cinematic.core;
public final class CeremonialTests {
    private static int checks;private static void require(boolean b,String n){checks++;if(!b)throw new AssertionError(n);}
    public static void main(String[] args){
        int[][] sizes={{854,480},{1024,768},{1366,768},{1920,1001},{1920,1080},{2560,1440},{3440,1440},{640,1000}};
        for(int[] size:sizes)for(int x=-2;x<=2;x++)for(int y=-2;y<=2;y++){
            DepthCamera d=new DepthCamera();d.update(size[0],size[1],x,y);
            for(int i=0;i<3;i++){require(d.u(i,0)>=0&&d.u(i,1)<=1,"horizontal padded coverage");require(d.v(i,0)>=0&&d.v(i,1)<=1,"vertical padded coverage");require(d.u(i,1)>d.u(i,0)&&d.v(i,1)>d.v(i,0),"noninverted crop");}
        }
        DepthCamera center=new DepthCamera(),right=new DepthCamera();center.update(1920,1080,0,0);right.update(1920,1080,1,1);
        require(center.u(0,0)==center.u(1,0)&&center.u(1,0)==center.u(2,0),"one neutral crop");
        double a=center.u(0,0)-right.u(0,0),b=center.u(1,0)-right.u(1,0),c=center.u(2,0)-right.u(2,0);require(c>b&&b>a&&a>0,"actual independent depth");
        boolean invalid=false;try{right.update(854,480,Double.NaN,0);}catch(IllegalArgumentException e){invalid=true;}require(invalid,"NaN rejected");
        for(int i=0;i<1000;i++){double t=i*.037;double glow=HeroButtonState.glow(t,.8,true);require(glow>=0&&glow<=1,"bounded luminous envelope");require(HeroButtonState.shimmer(t,false)==-2,"no reduced-motion sweep");require(HeroButtonState.glow(t,.3,false)==HeroButtonState.glow(0,.3,false),"no reduced-motion idle pulse");}
        require(HeroButtonState.press(.11,true)>.99,"press feedback maximum");require(HeroButtonState.press(.25,true)==0,"press ends");require(HeroButtonState.press(.11,false)==0,"reduced press static");
        require(HeroButtonState.glow(Double.NaN,Double.NaN,true)>=0,"invalid visual inputs finite");
        System.out.println("RESULT ceremonial_checks="+checks+" failures=0");
    }
}
