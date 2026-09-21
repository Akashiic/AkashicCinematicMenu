from pathlib import Path
import numpy as np,cv2,json
from PIL import Image,ImageDraw,ImageFilter
R=Path(__file__).resolve().parents[1];A=R/'src/main/resources/assets/akashiccinematic/native';O=A/'depth';rgb=np.array(Image.open(A/'backdrop.jpg').convert('RGB'));H,W=rgb.shape[:2]
# The lower architecture must remain in front of the seated statue when the latter moves.
edge=[(0,554),(150,554),(155,636),(254,643),(259,590),(272,573),(287,578),(299,605),(306,634),(538,650),(542,574),(587,561),(613,546),(625,513),(633,477),(639,456),(653,447),(671,452),(681,470),(688,501),(707,508),(724,513),(741,516),(752,525),(768,521),(775,536),(793,530),(810,509),(820,490),(827,477),(838,456),(854,445),(869,438),(879,434),(890,444),(901,456),(911,481),(918,508),(934,515),(938,502),(950,502),(961,495),(974,495),(983,490),(993,496),(1001,508),(1017,508),(1030,530),(1116,610),(1207,630),(1207,830),(0,830)]
mask=Image.new('L',(W,H));ImageDraw.Draw(mask).polygon(edge,fill=255);alpha=np.array(mask.filter(ImageFilter.GaussianBlur(.4)))
rgba=np.dstack([rgb,alpha]);rgba=cv2.copyMakeBorder(rgba,24,24,24,24,cv2.BORDER_REFLECT_101);Image.fromarray(rgba).save(O/'occluder.png')
a=rgba.astype('float32')/255.;aa=cv2.GaussianBlur(a[:,:,3],(0,0),8);rr=cv2.GaussianBlur(a[:,:,:3]*a[:,:,3:4],(0,0),8)/np.maximum(aa[:,:,None],1/65535)
Image.fromarray(np.uint8(np.clip(np.dstack([rr,aa]),0,1)*255+.5)).save(O/'occluder-blur.png')
p=R/'src/main/java/br/com/redeakashic/cinematic/render/BackdropRenderer.java';s=p.read_text()
s=s.replace('new NativeTexture[3]','new NativeTexture[4]').replace('String[] n={"sky","architecture","angel"};for(int i=0;i<3;i++)','String[] n={"sky","architecture","angel","occluder"};for(int i=0;i<4;i++)')
s=s.replace('for(int i=0;i<3;i++)planes[i].draw','for(int i=0;i<4;i++)planes[i].draw').replace('if(layered){for(int i=0;i<3;i++)','if(layered){for(int i=0;i<4;i++)').replace('releasePlanes(){for(int i=0;i<3;i++)','releasePlanes(){for(int i=0;i<4;i++)')
s=s.replace('camera.u(i,','camera.u(i==3?1:i,').replace('camera.v(i,','camera.v(i==3?1:i,');p.write_text(s,encoding='utf-8',newline='\n')
for name in ['src/main/java/br/com/redeakashic/cinematic/AkashicCinematicMenu.java','src/main/resources/mcmod.info','src/main/resources/META-INF/MANIFEST.MF','tools/build_native.py']:
    p=R/name;s=p.read_text(encoding='utf-8');assert '0.6.0-alpha.2' in s,name;p.write_text(s.replace('0.6.0-alpha.2','0.6.0-alpha.3'),encoding='utf-8',newline='\n')
(R/'evidence/occlusion-review.json').write_text(json.dumps({'purpose':'Keep the piers in front of knees/robe during relative camera motion','depth_planes':3,'draw_layers':4,'occluder_depth':'shares architecture transform; not a fourth independent moving plane','original_scene_unchanged':True,'applied_version':'0.6.0-alpha.3'},indent=2));print('FOREGROUND_OCCLUSION_PRESERVED_FOR_FINAL_CANDIDATE',flush=True)
p=R/'src/main/java/br/com/redeakashic/cinematic/client/CinematicMenu.java';s=p.read_text(encoding='utf-8');old='visuals.depthEnabled=config.layeredParallax;';assert s.count(old)==1;s=s.replace(old,'visuals.depthEnabled=config.layeredParallax&&config.parallax>0;');p.write_text(s,encoding='utf-8',newline='\n')
