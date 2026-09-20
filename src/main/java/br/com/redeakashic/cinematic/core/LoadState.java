package br.com.redeakashic.cinematic.core;
/** A stage-local snapshot, never a synthetic global percent or a readiness assertion. */
public final class LoadState {
    public final String title,detail,branch;
    public final int step,total;
    public LoadState(String title,String detail,String branch,int step,int total){
        this.title=clean(title,96);this.detail=clean(detail,160);this.branch=clean(branch,160);
        this.total=Math.max(0,total);this.step=Math.max(0,Math.min(step,this.total));
    }
    public static String clean(String s,int max){
        if(s==null)return "";StringBuilder b=new StringBuilder();
        for(int i=0;i<s.length()&&b.length()<max;i++){
            char c=s.charAt(i);if(c=='\u00a7'){if(i+1<s.length())i++;continue;}
            if(c<' '||c==127)c=' ';b.append(c);
        }return b.toString().trim();
    }
    public double fraction(){return total==0?Double.NaN:(double)step/total;}
    public String percent(){return total==0?"...":Integer.toString((int)Math.floor(100.0*step/total))+"%";}
    public static LoadState unknown(){return new LoadState("O santuário desperta.","Aguardando informações do carregador.","FORGE / INICIALIZAÇÃO",0,0);}
}
