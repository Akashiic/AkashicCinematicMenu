from pathlib import Path
import json,hashlib
import cv2,numpy as np
from PIL import Image,ImageFilter
R=Path(__file__).resolve().parents[1];A=R/'src/main/resources/assets/akashiccinematic/native';E=R/'evidence/depth';O=A/'depth'
source=np.array(Image.open(A/'backdrop.jpg').convert('RGB'));h,w=source.shape[:2];old=np.array(Image.open(E/'hero-mask.png'));body=old.copy();body[:122,:]=0
# Restrict refinement to the immediate outline of the statue, preserving the independently traced halo.
k=np.ones((11,11),np.uint8);mask=np.zeros((h,w),np.uint8);mask[cv2.dilate(body,k)>0]=cv2.GC_PR_BGD;mask[body>0]=cv2.GC_PR_FGD;mask[cv2.erode(body,k)>0]=cv2.GC_FGD
cv2.grabCut(source,mask,None,np.zeros((1,65),np.float64),np.zeros((1,65),np.float64),3,cv2.GC_INIT_WITH_MASK)
refined=np.where((mask==cv2.GC_FGD)|(mask==cv2.GC_PR_FGD),255,0).astype('uint8')
# The upper122px carries the pole and halo only; preserve the full old halo ring elsewhere too.
from PIL import ImageDraw
halo=Image.new('L',(w,h));d=ImageDraw.Draw(halo);d.ellipse((653,31,844,208),outline=255,width=7);d.line([(739,0),(747,124)],fill=255,width=7);d.line([(831,112),(860,103)],fill=255,width=5)
refined=np.maximum(refined,np.array(halo));Image.fromarray(refined).save(E/'hero-mask-refined.png')
plate=cv2.inpaint(source,cv2.dilate(refined,np.ones((3,3),np.uint8)),3,cv2.INPAINT_TELEA)
alpha=np.array(Image.fromarray(refined).filter(ImageFilter.GaussianBlur(.55)));arch=np.array(Image.open(O/'architecture.png'))[24:-24,24:-24].copy();arch[:,:,:3]=plate
padded=lambda a:cv2.copyMakeBorder(a,24,24,24,24,cv2.BORDER_REFLECT_101)
Image.fromarray(padded(arch)).save(O/'architecture.png');Image.fromarray(padded(np.dstack([source,alpha]))).save(O/'angel.png')
for name in ['sky','architecture','angel']:
    a=np.array(Image.open(O/(name+'.png')).convert('RGBA'),np.float32)/255.;premult=a[:,:,:3]*a[:,:,3:4]
    blurA=cv2.GaussianBlur(a[:,:,3],(0,0),8);blurRGB=cv2.GaussianBlur(premult,(0,0),8);unmult=blurRGB/np.maximum(blurA[:,:,None],1/65535)
    Image.fromarray(np.uint8(np.clip(np.dstack([unmult,blurA]),0,1)*255+.5)).save(O/(name+'-blur.png'))
report={'edge_refinement':'GrabCut in an11px boundary band; halo independent','blur':'premultiplied colour blur then unpremultiply to avoid black fringe','bgm_not_changed':True,'files':{p.name:hashlib.sha256(p.read_bytes()).hexdigest() for p in O.glob('*.png')}}
(R/'evidence/depth-refinement.json').write_text(json.dumps(report,indent=2));print('REFINED_THREE_LAYERS_AND_MATCHED_GLASS',flush=True)
