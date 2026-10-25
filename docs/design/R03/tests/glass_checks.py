# Executed by qa.py inside its actual browser session; imports are standard except PIL/numpy for pixels.
import io, math, re
import numpy as np
from PIL import Image
from playwright.sync_api import TimeoutError as PlaywrightTimeout
page.close();page=ctx.new_page()
page.on('pageerror',lambda e:errors.append(str(e)))
page.on('console',lambda m:console_errors.append(m.text) if m.type=='error' else None)
page.on('request',lambda r:requests.append(r.url) if r.url.startswith(('http://','https://')) else None)
page.set_viewport_size({'width':1366,'height':768});page.emulate_media(reduced_motion='reduce')
page.set_content(SOURCE,wait_until='load')
check('R03 / default is B and the composition stays Layout 1',page.evaluate("__FORBIDDEN_AUDIT__.panel==='b' && __FORBIDDEN_AUDIT__.layout==='1'"))
check('R03 / both scenic images decoded offline',page.evaluate("__FORBIDDEN_AUDIT__.artReady && __FORBIDDEN_AUDIT__.panelArtReady"))
check('R03 / backdrop filter is supported by test browser',page.evaluate('__FORBIDDEN_AUDIT__.blurSupported'))
base=None;deltas={}
for panel,blur,alpha in [('a',10,.36),('b',16,.46),('c',24,.58)]:
    page.locator(f'[data-panel-select="{panel}"]').click()
    state=page.locator('.side-surface').evaluate("e=>({blur:getComputedStyle(e).backdropFilter,opacity:getComputedStyle(e).opacity,image:getComputedStyle(e).backgroundImage})")
    text=page.locator('.brand').evaluate("e=>({filter:getComputedStyle(e).filter,opacity:getComputedStyle(e).opacity})")
    geom=geometry(page)
    if base is None:base=geom
    same=geom['side']==base['side'] and geom['labels']==base['labels'] and geom['brand']==base['brand']
    check(f'R03 / panel {panel} changes no navigation geometry',same)
    check(f'R03 / panel {panel} applies {blur}px blur on separate surface',f'blur({blur}px)' in state['blur'] and state['opacity']=='1' and 'url(' not in state['image'],state)
    check(f'R03 / panel {panel} leaves typography unfiltered',text['filter']=='none' and text['opacity']=='1',text)
    page.evaluate('document.activeElement.blur()')
    rendered=np.array(Image.open(io.BytesIO(page.screenshot())).convert('RGB')).astype(np.int16)
    guard=page.add_style_tag(content='.side-surface {backdrop-filter:none!important; -webkit-backdrop-filter:none!important}')
    clear=np.array(Image.open(io.BytesIO(page.screenshot())).convert('RGB')).astype(np.int16)
    guard.evaluate('e=>e.remove()')
    d=float(np.abs(rendered[:,:368]-clear[:,:368]).mean());deltas[panel]=round(d,4)
    check(f'R03 / panel {panel} blur changes rendered pixels, not only CSS metadata',d>1,deltas[panel])
# Compare R02 and R03 essential layout and unmodified right scene in the same browser.
old=ctx.new_page();old.set_viewport_size({'width':1366,'height':768});old.emulate_media(reduced_motion='reduce')
old.set_content((ROOT/'baseline/Forbidden-Layouts-R02.html').read_text(),wait_until='load')
page.keyboard.press('2');page.wait_for_timeout(100)
go=geometry(old);gn=geometry(page)
check('R03 / preserves original R02 Layout 1 sidebar, branding and all five button rectangles',go['side']==gn['side'] and go['labels']==gn['labels'] and go['brand']==gn['brand'])
# Hidden chrome is confined to validation captures, not the distributed file.
for q in (old,page):
    q.evaluate('async()=>{await Promise.all([...document.images].map(x=>x.decode().catch(()=>null)))}')
    q.evaluate('document.activeElement.blur()')
    q.add_style_tag(content='.review-caption,.review-dock,.layout-caption,.glass-status {visibility:hidden!important}')
oldshot=np.array(Image.open(io.BytesIO(old.screenshot())).convert('RGB')).astype(np.int16)
newshot=np.array(Image.open(io.BytesIO(page.screenshot())).convert('RGB')).astype(np.int16)
scene_delta=float(np.abs(oldshot[5:767,380:]-newshot[5:767,380:]).mean())
check('R03 / right-hand scenic framing and artwork are pixel-identical to R02 in fixed test',scene_delta==0,scene_delta)
# Exact previous panel view (same markup footprint, material only).
page.keyboard.press('b')
beforeshot=np.array(Image.open(io.BytesIO(page.screenshot())).convert('RGB')).astype(np.int16)
reference_delta=float(np.abs(oldshot[:,:368]-beforeshot[:,:368]).mean())
check('R03 / Before toggle recreates original panel pixels',reference_delta<.15,reference_delta)
page.keyboard.press('b');check('R03 / Before toggle returns to the selected material',page.evaluate("!__FORBIDDEN_AUDIT__.reference && __FORBIDDEN_AUDIT__.panel==='b'"))
old.close();page.close();page=ctx.new_page();page.set_viewport_size({'width':1366,'height':768});page.set_content(SOURCE,wait_until='load')
# Manual reduced transparency.
page.locator('[data-action="options"]').click();page.locator('#setting-transparency').check()
check('R03 / transparency option disables backdrop blur',page.evaluate("__FORBIDDEN_AUDIT__.glassMode==='solid' && getComputedStyle(document.querySelector('.side-surface')).backdropFilter==='none'"))
page.locator('#close-dialog').click();check('R03 / solid fallback is visibly labelled',page.locator('#glass-status').is_visible())
page.keyboard.press('1');check('R03 / material shortcut cannot override reduced-transparency preference',page.evaluate("__FORBIDDEN_AUDIT__.glassMode==='solid'"))
page.locator('[data-action="options"]').click();page.locator('#setting-transparency').uncheck();page.locator('#close-dialog').click()
check('R03 / glass can be restored after solid mode',page.evaluate("__FORBIDDEN_AUDIT__.glassMode==='live'"))
# Native media emulation (not source substitution).
prefpage=ctx.new_page();prefpage.set_viewport_size({'width':1366,'height':768})
cdp=ctx.new_cdp_session(prefpage)
cdp.send('Emulation.setEmulatedMedia',{'features':[{'name':'prefers-reduced-motion','value':'reduce'},{'name':'prefers-reduced-transparency','value':'reduce'}]})
prefpage.set_content(SOURCE,wait_until='load')
check('R03 / honors OS reduced-transparency media on initial load',prefpage.evaluate("matchMedia('(prefers-reduced-transparency: reduce)').matches && __FORBIDDEN_AUDIT__.glassMode==='solid'"))
cdp.send('Emulation.setEmulatedMedia',{'features':[{'name':'prefers-reduced-motion','value':'reduce'},{'name':'prefers-reduced-transparency','value':'no-preference'}]})
prefpage.wait_for_timeout(60)
check('R03 / follows non-overridden transparency media changes',prefpage.evaluate("__FORBIDDEN_AUDIT__.glassMode==='live'"));prefpage.close()
# Fault-injected missing feature, using same HTML bytes; not an old-browser certification.
fpage=ctx.new_page();fpage.evaluate("window.__savedCSSSupports=CSS.supports.bind(CSS); CSS.supports=(...args)=>args.some(x=>String(x).includes('backdrop-filter'))?false:window.__savedCSSSupports(...args)")
fpage.set_content(SOURCE,wait_until='load')
check('R03 / simulated unsupported filter gets readable fallback',fpage.evaluate("__FORBIDDEN_AUDIT__.glassMode==='fallback' && getComputedStyle(document.querySelector('.side-surface')).backdropFilter==='none'"))
fpage.locator('[data-action="play"]').click();check('R03 / fallback retains navigation',fpage.locator('#review-dialog').evaluate('e=>e.open'));fpage.close()
# Panel artwork failing has no effect on navigation or the main scene.
page.locator('#panel-art').evaluate("e=>e.src='data:image/webp;base64,AAAA'");page.wait_for_timeout(60)
check('R03 / missing underlay leaves primary scenic art ready',page.evaluate('!__FORBIDDEN_AUDIT__.panelArtReady && __FORBIDDEN_AUDIT__.artReady'))
page.locator('[data-action="options"]').click();check('R03 / missing underlay retains options',page.locator('#review-dialog').evaluate('e=>e.open'));page.locator('#close-dialog').click()
# Hash is a whitelisted candidate ID, never executable content.
page.evaluate("location.hash='panel=__proto__'");page.wait_for_timeout(50)
check('R03 / rejects non-whitelisted panel ID',page.evaluate("__FORBIDDEN_AUDIT__.panel==='a' && __FORBIDDEN_AUDIT__.layout==='1'"))
page.evaluate("location.hash='panel=c'");page.wait_for_timeout(50)
check('R03 / recognizes valid hash candidate',page.evaluate("__FORBIDDEN_AUDIT__.panel==='c'"))
page.close();page=ctx.new_page();page.set_viewport_size({'width':1366,'height':768});page.set_content(SOURCE,wait_until='load')
# Per-preset timing: native headless diagnostics, not a claim about GPU or the user's PC.
def stat(v):
    a=sorted(v)
    return {'n':len(a),'median_ms':round(statistics.median(a),3),'p95_ms':round(a[min(len(a)-1,int(len(a)*.95))],3),'max_ms':round(max(a),3)} if a else {}
glass_perf={}
for key in 'abc':
    q=ctx.new_page();q.set_viewport_size({'width':1366,'height':768});q.emulate_media(reduced_motion='no-preference');q.set_content(SOURCE,wait_until='load')
    q.locator(f'[data-panel-select="{key}"]').click();q.mouse.move(1200,520);q.wait_for_timeout(2600)
    sm=q.evaluate('__FORBIDDEN_AUDIT__');glass_perf[key]={'callback':stat(sm['handlerSamples']),'frame_intervals':stat(sm['frameSamples'])};q.close()
perf['glass_variants']=glass_perf
check('R03 / samples all three material timings independently',all(v['frame_intervals'].get('n',0)>30 for v in glass_perf.values()),glass_perf)
# Pixel-based background luminance probes. Conservative bounding boxes, not a full WCAG certification.
select='.brand h1,.brand-overline,.brand-poem,.menu-label,.network-name,.side-footer p,.offline-label,.menu-overline'
contrast=[]
def luminance(rgb):
    lin=np.where(rgb<=.04045,rgb/12.92,((rgb+.055)/1.055)**2.4)
    return lin@np.array([.2126,.7152,.0722])
for w,h in [(1920,1080),(1366,768),(854,480),(390,844)]:
    page.set_viewport_size({'width':w,'height':h});page.wait_for_timeout(30)
    for key in 'abc':
        page.locator(f'[data-panel-select="{key}"]').click();page.evaluate('document.activeElement.blur()')
        data=page.evaluate('''sel=>[...document.querySelectorAll(sel)].filter(e=>e.offsetParent).map(e=>{const range=document.createRange();range.selectNodeContents(e);const r=range.getBoundingClientRect();let op=1;for(let a=e;a;a=a.parentElement)op*=Number(getComputedStyle(a).opacity);return {tag:e.className||e.tagName,color:getComputedStyle(e).color,op,r:{x:r.x,y:r.y,w:r.width,h:r.height}}})''',select)
        guard=page.add_style_tag(content=select+','+','.join(x+' *' for x in select.split(','))+' {color:transparent!important;text-shadow:none!important}')
        ar=np.array(Image.open(io.BytesIO(page.screenshot(full_page=True))).convert('RGB')).astype(float)/255;guard.evaluate('e=>e.remove()')
        row=[]
        for d in data:
            z=d['r'];x=max(0,math.floor(z['x']));y=max(0,math.floor(z['y']));x2=min(ar.shape[1],math.ceil(z['x']+z['w']));y2=min(ar.shape[0],math.ceil(z['y']+z['h']));bg=ar[y:y2,x:x2]
            if not bg.size:continue
            rgb=np.array([float(v) for v in re.findall(r'[\d.]+',d['color'])][:3])/255;fg=rgb*d['op']+bg*(1-d['op']);fl=luminance(fg);bl=luminance(bg)
            ratios=(np.maximum(fl,bl)+.05)/(np.minimum(fl,bl)+.05);row.append({'element':d['tag'],'minimum':float(ratios.min())})
        primary=[v['minimum'] for v in row if v['element']=='menu-label']
        check(f'R03 / {key} at {w}x{h}, primary labels exceed 4.5:1 on sampled backdrop',bool(primary) and min(primary)>=4.5,round(min(primary),3))
        secondary=[v['minimum'] for v in row if v['element']!='H1']
        check(f'R03 / {key} at {w}x{h}, secondary lettering meets 4.5:1 in sampled frame',min(secondary)>=4.5,round(min(secondary),3))
        contrast.append({'viewport':[w,h],'panel':key,'samples':row})
(ROOT/'evidence/glass-diagnostics.json').write_text(json.dumps({'candidate_sha256':hashlib.sha256(HTML.read_bytes()).hexdigest(),'blur_pixel_deltas':deltas,'scene_delta_R02':scene_delta,'original_panel_delta_R02':reference_delta,'contrast':contrast,'method':'Computed text RGB against screenshot with text/shadows removed; minimum over text bounding rectangles; selected fixed frames only. Not complete WCAG conformance.'},ensure_ascii=False,indent=2))
