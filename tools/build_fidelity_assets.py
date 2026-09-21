from pathlib import Path
from PIL import Image,ImageFont,ImageDraw,ImageFilter
import numpy as np,json,hashlib,math
R=Path(__file__).resolve().parents[1];O=R/'src/main/resources/assets/akashiccinematic/native';E=R/'evidence';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
source=R.parent/'native-n06/src/main/resources/assets/akashiccinematic/native/backdrop.jpg'
im=Image.open(source).convert('RGB');v=np.asarray(im,dtype=np.float32);y=v[:,:,0]*.2126+v[:,:,1]*.7152+v[:,:,2]*.0722
neutral=y[:,:,None]*np.array([.97,.99,1.035])+np.array([0,1,3])*(1-y[:,:,None]/255)
colour=neutral*.82+v*.18
warm=(v[:,:,0]>v[:,:,1]*1.07)&(v[:,:,1]>v[:,:,2]*1.08)&(y>65)
colour[warm]=v[warm]*.55+neutral[warm]*.45
out=Image.fromarray(np.clip(colour,0,255).astype('uint8'));out.save(O/'backdrop.jpg',quality=98,subsampling=0);out.filter(ImageFilter.GaussianBlur(14)).save(O/'backdrop-blur.jpg',quality=97,subsampling=0)
thumb=out.copy();thumb.thumbnail((1000,700));thumb.save(E/'palette-scene-review.jpg',quality=95)
report={'source_size':im.size,'source_sha256':sha(source),'synthetic_upscale':False,'tokens':{'obsidian':'090D14','slate':'171C25','ivory':'E8E4D8','gold':'C4AA7C','muted':'ACB2BB','celeste':'DCEBFA'},'font_atlases':[]}
(O/'fonts').mkdir(exist_ok=True)
def atlas(family,size):
    font=ImageFont.truetype('C:/Windows/Fonts/'+('georgia.ttf' if family=='serif' else 'arial.ttf'),size)
    glyphs=[];x=2;top=2;shelf=0;width=2048
    for c in range(32,256):
        box=font.getbbox(chr(c),anchor='ls');gw=max(1,box[2]-box[0]);gh=max(1,box[3]-box[1])
        if x+gw+4>width:x=2;top+=shelf+4;shelf=0
        glyphs.append((c,x,top,gw,gh,box[0],box[1],font.getlength(chr(c))))
        x+=gw+4;shelf=max(shelf,gh)
    height=2**math.ceil(math.log2(top+shelf+4));image=Image.new('RGBA',(width,height));draw=ImageDraw.Draw(image)
    props={'size':size,'ascent':font.getmetrics()[0],'height':height,'width':width}
    for c,x,y,w,h,bx,by,advance in glyphs:
        draw.text((x-bx,y-by),chr(c),font=font,fill=(255,255,255,255),anchor='ls');props[str(c)]=','.join(map(str,[x,y,w,h,bx,by,round(advance,4)]))
    name=f'{family}-{size}';image.save(O/'fonts'/(name+'.png'));(O/'fonts'/(name+'.properties')).write_text('\n'.join(str(k)+'='+str(v) for k,v in props.items())+'\n',encoding='ascii')
    report['font_atlases'].append({'name':name,'pixels':[width,height],'nominal_size':size,'glyphs':224,'gpu_rgba_bytes':width*height*4})
for family,sizes in [('body',[16,24,36,48]),('serif',[24,48,72,96,144])]:
    for size in sizes:atlas(family,size)
report['generated_files']={p.relative_to(O).as_posix():{'bytes':p.stat().st_size,'sha256':sha(p)} for p in list((O/'fonts').glob('*'))+[O/'backdrop.jpg',O/'backdrop-blur.jpg']}
(E/'fidelity-assets.json').write_text(json.dumps(report,indent=2),encoding='utf-8');print('FIDELITY_ASSETS',len(report['font_atlases']),'atlases; scene',im.size,'not upscaled',flush=True)
