from pathlib import Path
R=Path(__file__).resolve().parents[1];p=R/'src/main/java/br/com/redeakashic/cinematic/render/UiTypography.java';s=p.read_text()
old='        double measured=width(text,serif,size,tracking);if(measured>maxWidth){double fit=maxWidth/measured;size*=fit;tracking*=fit;}'
assert s.count(old)==1
new='''        double measured=width(text,serif,size,tracking);
        if(measured>maxWidth && size*maxWidth/measured<(serif?12:9)){
            while(text.length()>1&&width(text+"...",serif,size,tracking)>maxWidth)text=text.substring(0,text.length()-1);
            text+="...";measured=width(text,serif,size,tracking);
        }
        if(measured>maxWidth){double fit=maxWidth/measured;size*=fit;tracking*=fit;}'''
p.write_text(s.replace(old,new),encoding='utf-8',newline='\n');print('Long status labels elide instead of shrinking below readable minimum; full source remains Forge log')
