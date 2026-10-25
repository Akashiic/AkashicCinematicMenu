from pathlib import Path
import hashlib,json,subprocess,sys,zipfile,time
P=Path(__file__).resolve().parents[1];Q=P/'qa-n02';R=P/'candidates/native-n08-bgm-r1';E=Q/'evidence/n08bgm1';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
process=json.loads((E/'process.json').read_text());checks=json.loads((E/'acceptance.json').read_text());assert process.get('exit')==0 and len(checks)==15 and all(x['passed'] for x in checks)
expected='c9248a3060139e5334facd6186e9c09f6826af650508e6c20d1438bc52975b0d';jar=R/'dist/AkashicCinematicMenu-0.5.0-alpha.1.jar';assert sha(jar)==expected
runs=[]
for label,args in [('core',[sys.executable,str(R/'tools/test_core.py')]),('fade',['java','-ea','-cp',str(R/'build/core-tests'),'br.com.redeakashic.cinematic.core.MusicCoreTests']),('playback',['java','-ea','-cp',str(R/'build/core-tests'),'br.com.redeakashic.cinematic.core.BgmPlaybackTests']),('rebuild',[sys.executable,str(R/'tools/build_native.py')])]:
    result=subprocess.run(args,capture_output=True,text=True,timeout=65);(R/'evidence'/(label+'-verified.log')).write_text(result.stdout+result.stderr,encoding='utf-8');assert result.returncode==0,label;runs.append({'test':label,'exit':result.returncode})
assert sha(jar)==expected
state=dict(l.split('=',1) for l in (E/'state.txt').read_text().splitlines() if '=' in l);assert state['bgmStarts']=='0' and state['bgmBlocked']=='0' and state['glErrors']=='0'
with zipfile.ZipFile(jar) as z:
    assert z.testzip() is None;assert 'assets/akashiccinematic/sounds/bgm/menu.ogg' not in z.namelist();assert not any('cinematicqa' in n or n.endswith(('.ttf','.otf','.woff','.woff2')) for n in z.namelist())
assert sha(P/'candidates/native-n07/dist/AkashicCinematicMenu-0.4.0-alpha.3.jar')=='14b366d1ce85be558d11fb4bf0ed81f6c043de1fc39bf065856e8477716cf8ac'
report={'checkpoint':'ACM-N08-BGM-R1-2026-09-21','version':'0.5.0-alpha.1','jar_sha256':expected,'jar_bytes':jar.stat().st_size,'classes':73,'built_with_real_apis':True,'reproducible_build':True,'runtime':process,'runtime_checks':15,'aether_render_exercised':True,'vanilla_loading_render_exercised':True,'terrain_hook_exercised':False,'theme_visual_inspection':'Aether and vanilla screenshots inspected; no dirt; native progress/messages/tips retained','bgm_source_provided':True,'bgm_source_sha256':'1e7a442de0759fb7a9b605c3f70d0fecdb7c3e1da428796e41df9a717d668408','bgm_converted_sha256':'66db58689c1745385109a2ee9e5b5757f2d57f3f692d20ed13e9a7d084999db3','exact_track_in_PC_runtime':False,'actual_music_playback_approved':False,'audio_runtime_scope':'Missing recording: no BGM start, no vanilla suppression; MUSIC mute changes state. A mute test without a voice is NOT evidence of stopping audible playback.','tests':runs,'pure_fade_checks':1557,'pure_playback_scenarios':18,'pure_playback_assertions':124,'original_N07_preserved':True,'general_release_approved':False,'verdict':'TRANSITION_TARGETS_TESTED__EXACT_MUSIC_PLAYBACK_PENDING_TRANSFER'}
(R/'CHECKPOINT_N08_BGM.json').write_text(json.dumps(report,indent=2),encoding='utf-8');(E/'final-audit.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
print('AUDIT',json.dumps(report),flush=True)
files=[(jar,'dist/'+jar.name),(R/'README.md','README.md'),(R/'CHECKPOINT_N08_BGM.json','CHECKPOINT_N08_BGM.json')]
files += [(p,p.relative_to(R).as_posix()) for p in (R/'src').rglob('*') if p.is_file()]
for name in ['build_native.py','test_core.py']:files.append((R/'tools'/name,'tools/'+name))
for name in ['core-verified.log','fade-verified.log','playback-verified.log','rebuild-verified.log','native-classpath.json','staging-receipt.json','runtime-candidate.json']:files.append((R/'evidence'/name,'evidence/'+name))
for name in ['acceptance.json','final-audit.json','process.json']:files.append((E/name,'runtime/'+name))
for p in (E/'review').glob('*.jpg'):files.append((p,'runtime/captures/'+p.name))
out=R/'dist/AkashicCinematicMenu-N08-BGM-code-only-engineering.zip';assert not out.exists(),'Inspect existing package before replacement'
with zipfile.ZipFile(out,'x',compression=zipfile.ZIP_DEFLATED,compresslevel=6) as z:
    for p,n in files:assert p.is_file();z.write(p,'AkashicCinematicMenu-N08/'+n)
with zipfile.ZipFile(out) as z:assert z.testzip() is None;assert not any(n.endswith(('.ttf','.otf','.woff','.woff2')) for n in z.namelist())
receipt={'archive':str(out),'sha256':sha(out),'bytes':out.stat().st_size,'music_track_included':False,'jar_sha256':expected};(R/'evidence/archive-receipt.json').write_text(json.dumps(receipt,indent=2))
review=(R/'README.md').read_text(encoding='utf-8')+'\n## Continuidade\nArtefato-base testado e pacote verificados; versão N07 preservada. Antes de novo runtime, verificar áudio e hash do JAR resultante. Não solicitar novamente o MP3 que já está anexado.\n'
(P/'NATIVE_REVIEW_N08_BGM.md').write_text(review,encoding='utf-8')
status=json.loads((P/'CURRENT_STATUS.json').read_text());assert status['active_candidate']=='candidates/native-n07';shutil=None
(R/'evidence/previous-current-status.json').write_text(json.dumps(status,indent=2));status['pending_integration']={'candidate':'candidates/native-n08-bgm-r1','version':'0.5.0-alpha.1','report':'NATIVE_REVIEW_N08_BGM.md','track_received_in_chat':True,'track_transfer_to_PC_pending':True,'runtime_scope':'Aether/vanilla themes and absent-audio fallback tested; exact recording playback not tested'}
(P/'CURRENT_STATUS.json').write_text(json.dumps(status,indent=2),encoding='utf-8');print('ARCHIVE_VERIFIED',json.dumps(receipt),flush=True)
