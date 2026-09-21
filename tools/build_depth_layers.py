from pathlib import Path
import json,hashlib
import numpy as np
import cv2
from PIL import Image,ImageDraw,ImageFilter
R=Path(__file__).resolve().parents[1];A=R/'src/main/resources/assets/akashiccinematic/native';O=A/'depth';O.mkdir(exist_ok=True);E=R/'evidence/depth';E.mkdir(exist_ok=True)
source=Image.open(A/'backdrop.jpg').convert('RGB');rgb=np.array(source);H,W=rgb.shape[:2];assert (W,H)==(1207,830)
def poly(points):
    im=Image.new('L',(W,H));ImageDraw.Draw(im).polygon(points,fill=255);return np.array(im)
# Original image coordinates, explicitly traced rather than rectangular subject cards.
hero=poly([(590,304),(601,250),(612,203),(624,162),(638,135),(649,128),(660,136),(679,176),(697,222),(714,229),(724,184),(725,156),(736,132),(752,123),(766,121),(780,132),(794,158),(805,190),(821,202),(837,175),(849,134),(853,99),(861,79),(873,73),(884,78),(901,98),(920,119),(936,149),(953,183),(971,224),(987,268),(998,313),(1006,352),(1018,391),(1027,437),(1025,449),(1001,429),(978,409),(958,383),(938,356),(941,391),(950,441),(955,484),(955,514),(932,519),(919,491),(904,463),(894,437),(879,426),(862,433),(849,450),(835,480),(820,508),(798,532),(775,548),(750,546),(729,525),(706,513),(690,510),(689,484),(680,458),(661,443),(646,451),(638,472),(629,501),(615,517),(618,485),(630,447),(638,411),(631,396),(614,425),(601,446),(591,454),(595,419),(590,394),(590,364),(592,339)])
# Halo aperture remains transparent, not a disk of frozen scenery.
halo=Image.new('L',(W,H));d=ImageDraw.Draw(halo);d.ellipse((653,31,844,208),outline=255,width=7);d.line([(739,0),(747,124)],fill=255,width=7);d.line([(831,112),(860,103)],fill=255,width=5)
hero=np.maximum(hero,np.array(halo))
# Visible sky regions, followed by seeded edge-aware refinement against architectural silhouettes.
skyzone=poly([(136,0),(598,0),(597,281),(580,191),(567,202),(558,272),(541,303),(519,258),(504,162),(493,118),(476,130),(461,88),(450,34),(437,12),(421,34),(399,52),(384,66),(366,119),(350,123),(343,181),(317,213),(300,196),(286,147),(273,40),(259,23),(247,103),(234,141),(221,125),(205,162),(186,175),(162,173),(136,183)])
# Clean cloud seeds are kept away from tower edges and the angel.
seeds=Image.fromarray(skyzone);ds=ImageDraw.Draw(seeds)
for x,y,radius in [(165,45,30),(304,34,31),(327,93,11),(525,41,32),(543,111,19),(556,155,8),(305,153,10)]:ds.ellipse((x-radius,y-radius,x+radius,y+radius),fill=255)
mask=np.where(np.array(seeds)>0,cv2.GC_PR_FGD,cv2.GC_BGD).astype('uint8');mask[:,610:]=cv2.GC_BGD;mask[380:,:]=cv2.GC_BGD;mask[:,:123]=cv2.GC_BGD
for x,y,radius in [(165,45,15),(304,34,16),(525,41,20),(543,111,8),(327,93,5)]:cv2.circle(mask,(x,y),radius,cv2.GC_FGD,-1)
cv2.grabCut(rgb,mask,None,np.zeros((1,65),np.float64),np.zeros((1,65),np.float64),4,cv2.GC_INIT_WITH_MASK)
sky=np.where((mask==cv2.GC_FGD)|(mask==cv2.GC_PR_FGD),255,0).astype('uint8');sky[:,610:]=0;sky[380:]=0;sky[hero>0]=0
# Limit restoration to hidden plates; maximum relative travel will stay inside a 16px reveal corridor.
pad=24;expanded=cv2.dilate(hero,np.ones((5,5),np.uint8));architecture=cv2.inpaint(rgb,expanded,5,cv2.INPAINT_TELEA)
# The sky clean plate is reconstructed only for displacement behind silhouettes, not advertised as new artwork.
inv=(sky==0).astype('uint8');dist,labels=cv2.distanceTransformWithLabels(inv,cv2.DIST_L2,5,labelType=cv2.DIST_LABEL_PIXEL)
known=rgb[sky>0];flat=np.zeros((int(labels.max())+1,3),np.uint8);flat[1:len(known)+1]=known
skyplate=flat[labels];skyplate=np.array(Image.fromarray(skyplate).filter(ImageFilter.GaussianBlur(9)));skyplate[sky>0]=rgb[sky>0]
alphaHero=np.array(Image.fromarray(hero).filter(ImageFilter.GaussianBlur(.55)))
alphaArch=np.array(Image.fromarray(255-sky).filter(ImageFilter.GaussianBlur(.45)))
def padded(a):return cv2.copyMakeBorder(a,pad,pad,pad,pad,cv2.BORDER_REFLECT_101)
Image.fromarray(padded(skyplate)).save(O/'sky.png')
Image.fromarray(padded(np.dstack([architecture,alphaArch]))).save(O/'architecture.png')
Image.fromarray(padded(np.dstack([rgb,alphaHero]))).save(O/'angel.png')
for name,a in [('hero-mask',hero),('sky-mask',sky)]:Image.fromarray(a).save(E/(name+'.png'))
colour=rgb.copy();colour[sky>0]=(colour[sky>0]*.45+np.array([30,125,210])*.55).astype('uint8');colour[hero>0]=(colour[hero>0]*.45+np.array([205,65,85])*.55).astype('uint8')
Image.fromarray(colour).save(E/'segmentation-review.jpg',quality=96)
# Neutral composite and extreme positions are rendered from the actual exported RGBA layers.
plates=[Image.open(O/n).convert('RGBA') for n in ['sky.png','architecture.png','angel.png']];offsets=[(.9,.45),(2.8,1.4),(8.0,4.0)]
def compose(mx,my):
    result=Image.new('RGBA',(W,H),(0,0,0,255))
    for plate,(dx,dy) in zip(plates,offsets):
        sample=plate.transform((W,H),Image.Transform.AFFINE,(1,0,pad-dx*mx,0,1,pad-dy*my),resample=Image.Resampling.BICUBIC)
        result=Image.alpha_composite(result,sample)
    return result.convert('RGB')
neutral=compose(0,0);neutral.save(E/'neutral.png');delta=np.abs(np.array(neutral).astype('int16')-rgb);review=[]
for x,y,name in [(-1,-1,'upper-left'),(1,1,'lower-right'),(1,-1,'upper-right'),(-1,1,'lower-left')]:
    frame=compose(x,y);frame.save(E/(name+'.jpg'),quality=96);review.append(frame.resize((604,415)))
strip=Image.new('RGB',(1208,830));[strip.paste(im,((i%2)*604,(i//2)*415)) for i,im in enumerate(review)];strip.save(E/'extremes.jpg',quality=96)
frames=[compose(np.sin(k*2*np.pi/48),np.cos(k*2*np.pi/48)*.7).resize((724,498)) for k in range(48)];frames[0].save(E/'parallax-review.webp',save_all=True,append_images=frames[1:],duration=60,loop=0,quality=86)
props={'sourceWidth':W,'sourceHeight':H,'pad':pad,'maxRelativeTravelX':7.1,'maxRelativeTravelY':3.55};(O/'layers.properties').write_text('\n'.join(f'{k}={v}' for k,v in props.items())+'\n',encoding='ascii')
report={'source':str(A/'backdrop.jpg'),'source_sha256':hashlib.sha256((A/'backdrop.jpg').read_bytes()).hexdigest(),'source_size':[W,H],'layer_size':[W+2*pad,H+2*pad],'padding':pad,'neutral_mean_abs_rgb':delta.mean(axis=(0,1)).tolist(),'neutral_changed_pixels':float(np.mean(delta.max(axis=2)>3)),'hero_pixels':int((hero>0).sum()),'sky_pixels':int((sky>0).sum()),'max_offset_source_pixels':offsets,'inpaint_method':'Telea5; nearest-cloud extension under architecture','limitations':'Clean plates only qualified for bounded motion corridor; not a full3D scene, no invented unseen buildings','visual_gate':'PENDING_INSPECTION'}
(R/'evidence/depth-build.json').write_text(json.dumps(report,indent=2));print(json.dumps(report,indent=2),flush=True)
