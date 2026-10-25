"""Executed self-review tests. Does not assert artistic approval or Minecraft compatibility."""
from pathlib import Path
import json,hashlib,platform,statistics,time,sys,argparse,shutil
from playwright.sync_api import sync_playwright
ROOT=Path(__file__).resolve().parents[1]
parser=argparse.ArgumentParser(description=__doc__)
parser.add_argument('mode',choices=['all','layouts','functional'],nargs='?',default='all')
parser.add_argument('--html',type=Path,default=ROOT/'dist/Forbidden-Santuario-R03.html')
parser.add_argument('--chromium',default=shutil.which('chromium'))
args=parser.parse_args()
HTML=args.html
SOURCE=HTML.read_text()
SOURCE_SHA=hashlib.sha256(HTML.read_bytes()).hexdigest()
results=[]; errors=[]; console_errors=[]; requests=[]; browser_version=''; perf={}
mode=args.mode
def check(name, passed, detail=None):
    results.append({'test':name,'status':'PASS' if passed else 'FAIL','detail':detail})
    print(('PASS ' if passed else 'FAIL ')+name,flush=True)
def geometry(page):
    return page.evaluate('''() => {
      const rect=e=>{const r=e.getBoundingClientRect();return {x:r.x,y:r.y,w:r.width,h:r.height,right:r.right,bottom:r.bottom}};
      const side=rect(document.querySelector('.sidebar'));
      const labels=[...document.querySelectorAll('.menu-button')].map(e=>{
       const b=rect(e),l=rect(e.querySelector('.menu-label'));
       const range=document.createRange(); range.selectNodeContents(e.querySelector('.menu-label'));
       const text=range.getBoundingClientRect();
       return {action:e.dataset.action,button:b,label:l,text:{x:text.x,right:text.right,bottom:text.bottom},visible:!!e.offsetParent};
      });
      const range=document.createRange();range.selectNodeContents(document.querySelector('h1'));
      const t=range.getBoundingClientRect();
      return {side,labels,title:{x:t.x,right:t.right},brand:rect(document.querySelector('.brand')),footer:rect(document.querySelector('.side-footer')),
        dock:rect(document.querySelector('.review-dock')),width:innerWidth,height:innerHeight,
        scrollW:document.documentElement.scrollWidth,scrollH:document.documentElement.scrollHeight,frame:rect(document.querySelector('.experience'))};
    }''')
with sync_playwright() as p:
    browser=p.chromium.launch(executable_path=args.chromium,headless=True,args=['--no-sandbox','--disable-dev-shm-usage'])
    browser_version=browser.version
    ctx=browser.new_context(viewport={'width':1920,'height':1080},device_scale_factor=1,reduced_motion='reduce',offline=True)
    page=ctx.new_page()
    page.on('pageerror',lambda e:errors.append(str(e)))
    page.on('console',lambda m:console_errors.append(m.text) if m.type=='error' else None)
    page.on('request',lambda r:requests.append(r.url) if r.url.startswith(('http://','https://')) else None)
    page.set_content(SOURCE,wait_until='load');page.wait_for_timeout(100)
    check('Self-contained HTML renders while browser network is offline',page.evaluate('__FORBIDDEN_AUDIT__.artReady'))
    check('System reduced-motion preference is honored',page.evaluate('__FORBIDDEN_AUDIT__.reducedMotion'))
    check('Default state explicitly has no final user approval',page.evaluate('__FORBIDDEN_AUDIT__.finalApproval') is False)
    if mode in ('all','layouts'):
      for w,h in [(1920,1080),(1366,768),(854,480),(2560,1080),(1024,768),(390,844),(320,568)]:
        page.set_viewport_size({'width':w,'height':h});page.wait_for_timeout(45)
        for lid,expected in [('a',.27),('b',.27),('c',.27)]:
          page.locator('[data-panel-select="'+lid+'"]').click();page.wait_for_timeout(45)
          g=geometry(page);prefix=f'Layout {lid} at {w}x{h}'
          inside=all(b['visible'] and b['button']['x']>=-1 and b['button']['right']<=w+1 and b['button']['y']>=0 and b['button']['bottom']<=g['frame']['bottom']+1 for b in g['labels'])
          check(prefix+' / buttons stay within frame',inside,g)
          contained=all(b['text']['x']>=b['button']['x']-1 and b['text']['right']<=b['button']['right']-5 for b in g['labels'])
          check(prefix+' / labels fit',contained,g['labels'])
          check(prefix+' / title fits sidebar',g['title']['right']<=g['side']['right']+1,g['title'])
          ordered=all(g['labels'][i]['button']['bottom']<=g['labels'][i+1]['button']['y']+1 for i in range(4)) if w>700 else True
          check(prefix+' / no vertical menu overlap',ordered)
          check(prefix+' / no horizontal page overflow',g['scrollW']<=w+1)
          check(prefix+' / footer does not overlap menu',g['footer']['y']>=max(b['button']['bottom'] for b in g['labels'])-1,{'footer_y':g['footer']['y'],'last_button':max(b['button']['bottom'] for b in g['labels'])})
          if w>700:check(prefix+' / desktop sidebar proportion',abs(g['side']['w']/w-expected)<.002,g['side']['w']/w)
          if w in (1920,1366,854,390):
            page.screenshot(path=str(ROOT/f'evidence/layout-{lid}-{w}x{h}.png'),full_page=True)
      page.set_viewport_size({'width':1366,'height':768})
    if mode in ('all','functional'):
      page.set_viewport_size({'width':1366,'height':768})
      for lid,key in [('a','1'),('b','2'),('c','3')]:
        page.keyboard.press(key)
        check('Keyboard selects layout '+lid,page.evaluate('__FORBIDDEN_AUDIT__.panel')==lid)
      page.keyboard.press('1')
      page.locator('[data-action="play"]').focus();page.keyboard.press('ArrowUp')
      check('Arrow-up wraps focus to last menu item',page.evaluate("document.activeElement.dataset.action==='exit'"))
      page.keyboard.press('ArrowDown')
      check('Arrow-down wraps focus to first menu item',page.evaluate("document.activeElement.dataset.action==='play'"))
      page.keyboard.press('End');check('End focuses last menu item',page.evaluate("document.activeElement.dataset.action==='exit'"))
      page.keyboard.press('Home');check('Home focuses first menu item',page.evaluate("document.activeElement.dataset.action==='play'"))
      focus=page.locator('[data-action="play"]').evaluate("e=>({style:getComputedStyle(e).outlineStyle,width:getComputedStyle(e).outlineWidth})")
      check('Keyboard focus is visually outlined',focus['style']!='none' and focus['width']!='0px',focus)
      page.keyboard.press('Enter')
      check('Enter opens the focused route',page.locator('#review-dialog').evaluate('e=>e.open'))
      check('Dialog names itself for assistive technology',bool(page.locator('#review-dialog').get_attribute('aria-labelledby')))
      for _ in range(12): page.keyboard.press('Tab')
      check('Modal keeps keyboard focus inside',page.evaluate("!!document.activeElement.closest('#review-dialog')"))
      page.keyboard.press('Escape');page.wait_for_timeout(60)
      check('Escape closes dialog',not page.locator('#review-dialog').evaluate('e=>e.open'))
      check('Closing dialog restores initiating button focus',page.evaluate("document.activeElement.dataset.action==='play'"))
      for route in ('multiplayer','options','mods','exit'):
        page.locator('[data-action="'+route+'"]').click()
        check(route+' opens its own explicit local panel',page.locator('#review-dialog').evaluate('e=>e.open'))
        if route=='multiplayer': check('No fabricated online status',page.locator('#dialog-content').inner_text().find('NÃO CONSULTADO')>=0)
        if route=='options':
          page.locator('#setting-particles').uncheck();check('Particle toggle works',page.evaluate('__FORBIDDEN_AUDIT__.particles') is False)
          page.locator('#setting-green').fill('68');page.locator('#setting-green').dispatch_event('input')
          check('Moss atmosphere setting changes local style',page.evaluate("document.body.style.getPropertyValue('--moss-level')")=='0.68')
        page.locator('#dialog-back').click();page.wait_for_timeout(35)
      page.locator('#compare-button').click()
      check('Comparison presents exactly three candidates',page.locator('[data-compare]').count()==3)
      page.screenshot(path=str(ROOT/'evidence/comparison-panel.png'))
      page.locator('[data-compare="b"]').click();page.wait_for_timeout(70)
      check('Comparison choice updates the view',page.evaluate('__FORBIDDEN_AUDIT__.panel')=='b')
      check('Comparison choice does not grant final approval',page.evaluate('__FORBIDDEN_AUDIT__.finalApproval') is False)
      page.keyboard.press('v');check('Cinema mode hides evaluation chrome',page.locator('.review-dock').is_hidden())
      page.locator('#restore-tools').click();check('Evaluation tools can be restored',page.locator('.review-dock').is_visible())
      # Motion on: test cancellation and duplicate handling against asynchronous transition.
      page.keyboard.press('m');check('Motion can be enabled explicitly',page.evaluate('__FORBIDDEN_AUDIT__.reducedMotion') is False)
      page.wait_for_timeout(120)
      before=page.evaluate('__FORBIDDEN_AUDIT__.routeCount')
      page.locator('[data-action="play"]').click();page.keyboard.press('Escape');page.wait_for_timeout(360)
      check('Escape cancels pending route without late activation',page.evaluate('__FORBIDDEN_AUDIT__.routeCount')==before and not page.locator('#review-dialog').evaluate('e=>e.open'))
      page.evaluate("document.querySelector('[data-action=play]').click();document.querySelector('[data-action=play]').click();document.querySelector('[data-action=play]').click()")
      page.wait_for_timeout(370)
      check('Repeated clicks open route exactly once',page.evaluate('__FORBIDDEN_AUDIT__.routeCount')==before+1)
      page.locator('#close-dialog').click();page.wait_for_timeout(40)
      before=page.evaluate('__FORBIDDEN_AUDIT__.routeCount')
      page.locator('[data-action="play"]').click();page.evaluate("window.dispatchEvent(new Event('blur'))")
      page.wait_for_timeout(370)
      check('Window blur cancels pending route',page.evaluate('__FORBIDDEN_AUDIT__.routeCount')==before)
      check('Window blur pauses animation loop',page.evaluate('__FORBIDDEN_AUDIT__.paused'))
      page.evaluate("window.dispatchEvent(new Event('focus'))");page.wait_for_timeout(60)
      check('Window focus resumes animation loop',not page.evaluate('__FORBIDDEN_AUDIT__.paused'))
      page.mouse.move(1300,500);page.wait_for_timeout(600)
      matrix=page.locator('#scene-camera').evaluate('e=>getComputedStyle(e).transform')
      check('Pointer produces bounded scenery movement',matrix!='matrix(1, 0, 0, 1, 0, 0)' and matrix!='none',matrix)
      # Dynamic operating-system preference, without a manual override, is separately verified below.
      page.keyboard.press('m');page.wait_for_timeout(60)
      check('Reduced motion resets parallax',page.locator('#scene-camera').evaluate('e=>getComputedStyle(e).transform')=='none')
      n=page.evaluate('__FORBIDDEN_AUDIT__.frameSamples.length');page.wait_for_timeout(130)
      check('Reduced mode has no animation-frame polling',page.evaluate('__FORBIDDEN_AUDIT__.frameSamples.length')==n)
      check('R03 material choice remains pending user approval',page.evaluate('__FORBIDDEN_AUDIT__.finalApproval') is False)
      # Safe fallback when the embedded artwork cannot be decoded.
      page.locator('#cathedral').evaluate("e=>e.src='data:image/webp;base64,AAAA'")
      page.wait_for_timeout(80)
      check('Image error shows a readable fallback',page.locator('#art-fallback').is_visible())
      page.locator('[data-action="options"]').click()
      check('Navigation survives an image failure',page.locator('#review-dialog').evaluate('e=>e.open'))
      page.locator('#close-dialog').click()
      # This deliberate image decoding error is not an unexpected page JS/console failure.
      page.close();page=ctx.new_page()
      page.on('pageerror',lambda e:errors.append(str(e)))
      page.on('console',lambda m:console_errors.append(m.text) if m.type=='error' else None)
      page.on('request',lambda r:requests.append(r.url) if r.url.startswith(('http://','https://')) else None)
      page.set_content(SOURCE,wait_until='load');page.wait_for_timeout(80)
      page.emulate_media(reduced_motion='no-preference');page.wait_for_function('() => __FORBIDDEN_AUDIT__.reducedMotion === false',timeout=5000)
      check('OS motion change updates a non-overridden session',page.evaluate('__FORBIDDEN_AUDIT__.reducedMotion') is False)
      page.emulate_media(reduced_motion='reduce');page.wait_for_function('() => __FORBIDDEN_AUDIT__.reducedMotion === true',timeout=5000)
      check('OS reduced preference can disable motion again',page.evaluate('__FORBIDDEN_AUDIT__.reducedMotion') is True)
      # Timings are browser-only diagnostics, NOT a game or GPU benchmark.
      page.emulate_media(reduced_motion='no-preference');page.mouse.move(1200,400);page.wait_for_timeout(5500)
      samples=page.evaluate('__FORBIDDEN_AUDIT__')
      def stats(v):
        a=sorted(v)
        return {'n':len(a),'median_ms':round(statistics.median(a),4),'p95_ms':round(a[min(len(a)-1,int(len(a)*.95))],4),'max_ms':round(max(a),4)} if a else {}
      perf={'viewport':'1366x768','animation_callback':stats(samples['handlerSamples']),'frame_intervals':stats(samples['frameSamples']),
        'limitation':'Chromium headless in the working container. Callback timings exclude compositor/GPU cost; frame intervals reflect this environment. Not a target-PC/Minecraft benchmark.'}
      check('Browser timing sample recorded',len(samples['frameSamples'])>=50,perf)
    exec((ROOT/'tests/glass_checks.py').read_text(),globals())
    check('No unexpected JavaScript exceptions',not errors,errors)
    check('No external network requests',not requests,requests)
    check('No unexpected console errors',not console_errors,console_errors)
    browser.close()
assert hashlib.sha256(HTML.read_bytes()).hexdigest()==SOURCE_SHA, 'Tested artifact changed during validation'
report={'candidate':'ACM-SANTUARIO-R03.1','html_sha256':hashlib.sha256(HTML.read_bytes()).hexdigest(),'reviewer':'assistant self-review; no independent reviewer',
 'visual_final_approval':'PENDING_USER','test_environment':{'os':platform.platform(),'browser':'Chromium '+browser_version,'execution':'actual Chromium DOM via Playwright set_content, all resources embedded; browser network offline','file_scheme':'NOT_EXECUTED: container Chromium policy blocks file:// navigation','minecraft':'NOT_EXECUTED','user_pc':'NOT_EXECUTED'},
 'pass':sum(x['status']=='PASS' for x in results),'fail':sum(x['status']=='FAIL' for x in results),'performance':perf,'results':results}
(ROOT/f'evidence/qa-{mode}.json').write_text(json.dumps(report,ensure_ascii=False,indent=2))
print('SUMMARY',report['pass'],'passed,',report['fail'],'failed',flush=True)
raise SystemExit(bool(report['fail']))
