from pathlib import Path
import json,hashlib,zipfile,shutil,time
R=Path(__file__).resolve().parents[1]
P=R.parents[1]
E=R/'evidence'
audit=json.loads((R/'CHECKPOINT_N11.json').read_text())
assert audit['version']=='0.7.0-alpha.4'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
jar=R/'dist/AkashicCinematicMenu-0.7.0-alpha.4.jar'
assert sha(jar)==audit['jar_sha256']
# Offline gallery from actual full-pack runtime captures.
images=[p.name for p in sorted((E/'runtime-review').glob('*.jpg'))]
cards=''.join('<figure><img loading="lazy" src="../evidence/runtime-review/'+n+'"><figcaption>'+n[:-4]+'</figcaption></figure>' for n in images)
html='''<!doctype html><html lang="pt-BR"><meta charset="utf-8"><meta name="viewport" content="width=device-width">
<title>Forbidden · N11 GUI Wave 2</title>
<style>body{margin:0;background:#090d14;color:#e8e4d8;font:16px/1.55 system-ui}main{max-width:1320px;margin:auto;padding:34px 22px}h1,h2{font-family:Georgia,serif}h1{font-size:46px}.meta{color:#c4aa7c}.note{background:#171c25;border-left:3px solid #c4aa7c;padding:16px}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(420px,1fr));gap:18px}figure{margin:0;background:#111720;border:1px solid #403846;padding:8px}img{width:100%;height:auto;display:block}figcaption{padding:8px;color:#acb2bb}</style>
<main><div class="meta">REDE AKASHIC · N11 · 0.7.0-alpha.4</div><h1>GUI Wave 2</h1>
<p>Capturas reais da cópia completa do modpack em 1920×1001. Nenhum mundo foi aberto durante esta matriz.</p>
<div class="note">Options, mundos, idioma, texturas, controles, vídeo, áudio e confirmações compartilham a identidade do Santuário sem substituir a lógica vanilla/modded. O Sair do menu principal fecha diretamente; confirmações destrutivas permanecem.</div>
<h2>Matriz visual</h2><div class="grid">'''+cards+'''</div>
<h2>Estado técnico</h2><p>Cliente QA terminou com exit=0; BGM contínua pelos submenus; zero marcadores monitorados de erro GL/fallback do tema. Leia PARECER_N11.md para as limitações e correções metodológicas preservadas.</p></main></html>'''
(R/'docs/Revisao-N11.html').write_text(html,encoding='utf-8')
print('GALLERY_IMAGES',len(images),flush=True)
# Engineering package: no QA mod and no font binaries.
files=[(jar,'dist/'+jar.name),(R/'README.md','README.md'),(R/'CHECKPOINT_N11.json','CHECKPOINT_N11.json'),
       (R/'docs/PARECER_N11.md','docs/PARECER_N11.md'),(R/'docs/Revisao-N11.html','docs/Revisao-N11.html')]
files += [(x,x.relative_to(R).as_posix()) for x in (R/'src').rglob('*') if x.is_file()]
for name in ['build_native.py','test_core.py','SecondaryTransformerProbe.java','QuitBytecodeProbe.java',
             'SecondaryGraphicsProbe.java','verify_wave2.py','verify_wave2_resume.py','final_audit_n11.py']:
    f=R/'tools'/name
    if f.exists():files.append((f,'tools/'+name))
for name in ['final-audit.json','wave2-alpha4-gates.json','reproducible-build.log',
             'secondary-probe-test-correction.json','alpha3-verifier-correction.json',
             'alpha1-extension.json','runtime-n11-acceptance-v4.json','runtime-n11-observations-v4.json',
             'runtime-quit-foreground.json','runtime-process.json','runtime-encoding-test-correction.json',
             'runtime-createworld-test-correction.json','runtime-createworld-route-variability.json']:
    f=E/name
    if f.exists():files.append((f,'evidence/'+name))
for f in (E/'runtime-review').glob('*.jpg'):files.append((f,'evidence/runtime-review/'+f.name))
out=R/'dist/Forbidden-N11-GUI-Wave2-engineering.zip'
assert not out.exists(),'Existing package requires inspection'
with zipfile.ZipFile(out,'x',compression=zipfile.ZIP_DEFLATED,compresslevel=5) as z:
    for f,name in files:
        assert f.is_file(),f
        assert f.suffix.lower() not in ['.ttf','.otf','.woff','.woff2']
        assert 'cinematicqa' not in name.lower()
        z.write(f,'Forbidden-N11/'+name)
with zipfile.ZipFile(out) as z:assert z.testzip() is None
receipt={'archive':str(out),'sha256':sha(out),'bytes':out.stat().st_size,'jar_sha256':sha(jar),
         'files':len(files),'full_pack_exit':audit['full_pack_process']['exit'],
         'visual_approval':audit['user_visual_approval'],'general_release_approved':False}
(E/'archive-receipt.json').write_text(json.dumps(receipt,indent=2),encoding='utf-8')
print('PACKAGE',json.dumps(receipt),flush=True)
# Update project continuity without claiming user approval.
status=P/'CURRENT_STATUS.json'
previous=json.loads(status.read_text())
(E/'previous-current-status.json').write_text(json.dumps(previous,indent=2),encoding='utf-8')
previous['gui_wave2_candidate']={'candidate':'candidates/native-n11-gui-wave2','version':'0.7.0-alpha.4',
    'jar_sha256':audit['jar_sha256'],'checkpoint':'candidates/native-n11-gui-wave2/CHECKPOINT_N11.json',
    'full_pack_runtime':True,'full_pack_exit':0,'direct_quit_runtime':True,
    'daily_installed_by_this_work':False,'user_visual_approval':'PENDING'}
status.write_text(json.dumps(previous,indent=2,ensure_ascii=False),encoding='utf-8')
# Root report mirror.
shutil.copy2(R/'docs/PARECER_N11.md',P/'NATIVE_REVIEW_N11.md')
print('STATUS_UPDATED_WITH_PENDING_APPROVAL',flush=True)
