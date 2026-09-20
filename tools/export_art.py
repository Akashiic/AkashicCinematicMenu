"""Build native PNG layers from approved HTML. Browser ONLY at build time; no bundled fonts."""
from pathlib import Path
import argparse, base64, hashlib, io, json, math, re
from PIL import Image, ImageDraw, ImageFont
from playwright.sync_api import sync_playwright
ROOT=Path(__file__).resolve().parents[1]
PROFILES={'hd':(1366,768),'compact':(854,480),'classic':(1024,768),'wide':(2048,864)}
EXPECTED='c0158907051846c5b123d1502932c4e5c58bb1fb7d187c3c87b01efea7fb96a3'
def sha(b): return hashlib.sha256(b).hexdigest()
def write_props(path, props):
    path.write_text('\n'.join(str(k)+'='+str(v) for k,v in props.items())+'\n',encoding='ascii')
def rect(page, selector):
    b=page.locator(selector).bounding_box()
    return dict(x=math.floor(b['x']),y=math.floor(b['y']),width=math.ceil(b['width']),height=math.ceil(b['height']))
def crop(image,b):return image.crop((b['x'],b['y'],b['x']+b['width'],b['y']+b['height']))
def font_atlas(folder, name, font_path, size):
    font=ImageFont.truetype(str(font_path),size)
    cell=64;im=Image.new('RGBA',(1024,896));d=ImageDraw.Draw(im)
    props={'cell':cell,'size':size,'line':size+6,'first':32,'last':255}
    for c in range(32,256):
        k=c-32;x=(k%16)*cell;y=(k//16)*cell
        d.text((x+2,y+font.getmetrics()[0]+2),chr(c),font=font,fill=(255,255,255,255),anchor='ls')
        props[str(c)]=round(d.textlength(chr(c),font=font),3)
    im.save(folder/(name+'.png'));write_props(folder/(name+'.properties'),props)
def main():
    ap=argparse.ArgumentParser();ap.add_argument('--html',type=Path,required=True);ap.add_argument('--browser');ap.add_argument('--font-dir',type=Path,default=Path('C:/Windows/Fonts'));a=ap.parse_args()
    raw=a.html.read_bytes();assert sha(raw)==EXPECTED,'Unapproved startup input'
    boot=raw.decode('utf-8');m=re.search(r'<script[^>]*id="approved-menu"[^>]*>(.*?)</script>',boot,re.S);assert m
    menu=base64.b64decode(m.group(1).strip()).decode('utf-8')
    assert sha(menu.encode())=='61be236468878e24128038fbf947695218787858d7feca9c39399aa6367651d5'
    strip=lambda s:re.sub(r'<script\b[^>]*>[\s\S]*?</script>','',s,flags=re.I)
    menu=strip(menu).replace('data-ready="false"','data-ready="true"').replace('data-motion="full"','data-motion="reduced"')
    boot=strip(boot).replace('data-reduced="false"','data-reduced="true"').replace('data-quiet="false"','data-quiet="true"')
    out=ROOT/'src/main/resources/assets/akashiccinematic/native';out.mkdir(parents=True,exist_ok=True)
    ev=ROOT/'evidence/art-export';ev.mkdir(parents=True,exist_ok=True)
    common='*,:before,:after{animation:none!important;transition:none!important;caret-color:transparent!important} noscript{display:none!important}'
    menu_css=common+' .review-dock,.review-caption,.layout-caption,#restore-tools,.glass-status,.skip-link{display:none!important} #dust,.mist{display:none!important}'
    boot_css=common+' .lab,.controls,#particles,.mist,.menu-return,.focus-note{display:none!important} .hero,.loader{animation:none!important;opacity:1!important;transform:none!important}'
    report={'startup_sha256':sha(raw),'browser_network_requests':[],'profiles':{},'blur':'build-time rasterized approved panel B; not runtime framebuffer blur'}
    with sync_playwright() as pw:
        browser=pw.chromium.launch(headless=True,**({'executable_path':a.browser} if a.browser else {}))
        report['browser']=browser.version
        context=browser.new_context(offline=True,device_scale_factor=1,reduced_motion='reduce')
        for name,(w,h) in PROFILES.items():
            folder=out/name;folder.mkdir(exist_ok=True);page=context.new_page();page.set_viewport_size({'width':w,'height':h})
            page.on('request',lambda r:report['browser_network_requests'].append(r.url) if r.url.startswith(('http:','https:')) else None)
            page.set_content(menu,wait_until='load');page.add_style_tag(content=menu_css)
            page.locator('.offline-label').evaluate("e=>e.textContent='THE END OF FORBIDDEN'")
            page.evaluate('()=>Promise.all(Array.from(document.images).map(i=>i.decode()))')
            page.mouse.move(w-1,h-1);page.screenshot(path=str(ev/(name+'-menu-reference.png')))
            props={'width':w,'height':h,'sidebar':round(w*.27),'count':4}
            boxes=[rect(page,f'.menu-button:nth-of-type({i+1})') for i in range(4)]
            for i,b in enumerate(boxes):
                for k,v in b.items():props[f'button.{i}.{k}']=v
                page.mouse.move(w-1,h-1);page.screenshot(path=str(folder/f'button-{i}.png'),clip=b)
                page.locator(f'.menu-button:nth-of-type({i+1})').hover();page.screenshot(path=str(folder/f'button-{i}-hover.png'),clip=b)
            page.mouse.move(w-1,h-1);page.add_style_tag(content='.menu-button{visibility:hidden!important}')
            im=Image.open(io.BytesIO(page.screenshot())).convert('RGB');split=props['sidebar']
            im.crop((0,0,split,h)).save(folder/'panel.png');im.crop((split,0,w,h)).save(folder/'scene.jpg',quality=96,subsampling=0)
            page.set_content(boot,wait_until='load');page.add_style_tag(content=boot_css)
            page.evaluate('()=>Promise.all(Array.from(document.images).map(i=>i.decode()))')
            page.locator('.footer-right').evaluate("e=>e.innerHTML='<b>FORGE 1.7.10</b><br>CARREGAMENTO LOCAL'")
            page.locator('#progress-note').evaluate("e=>e.textContent='Progresso da etapa ativa / Forge'")
            fields={'title':'#stage-title','detail':'#stage-detail','percent':'.percentage','progress':'#progress','branch':'.stage-track','steps':'#phase-number','time':'#time-label'}
            for n,sel in fields.items():
                for k,v in rect(page,sel).items():props[f'{n}.{k}']=v
            page.add_style_tag(content='.stage-copy,.percentage,.stage-track,#phase-number,#time-label,.progress-fill{visibility:hidden!important}')
            page.screenshot(path=str(folder/'boot.png'))
            write_props(folder/'layout.properties',props);report['profiles'][name]=props
            page.close();print('EXPORTED',name,flush=True)
        context.close();browser.close()
    font_atlas(out,'body',a.font_dir/'arial.ttf',12);font_atlas(out,'serif',a.font_dir/'georgia.ttf',36)
    files={p.relative_to(out).as_posix():{'bytes':p.stat().st_size,'sha256':sha(p.read_bytes())} for p in sorted(out.rglob('*')) if p.is_file()}
    report['assets']=files;report['total_bytes']=sum(v['bytes'] for v in files.values());assert not report['browser_network_requests']
    (ROOT/'evidence/art-export.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
    print('ASSETS_OK',len(files),report['total_bytes'],flush=True)
if __name__=='__main__':main()
