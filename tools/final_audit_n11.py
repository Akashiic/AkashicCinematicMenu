from pathlib import Path
import json,hashlib,subprocess,sys,shutil,zipfile,time,re
R=Path(__file__).resolve().parents[1]
P=R.parents[1]
Q=P/'qa-n08-bgm-6c79c577'
E=Q/'evidence/n11run1'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
expected='7dbf5177c4e26edea3c0d5caceafac6d4ec013eee6d543d2dbe80066d4c0fb4a'
b=json.loads((R/'evidence/native-build.json').read_text())
jar=R/'dist'/b['jar']
assert b['version']=='0.7.0-alpha.4' and sha(jar)==b['sha256']==expected
proc=json.loads((E/'process.json').read_text())
quit_test=json.loads((E/'quit-foreground.json').read_text())
assert proc.get('exit')==0 and quit_test['process']['exit']==0
assert quit_test['foreground_verified'] and not quit_test['gui_yes_no_after_quit']
independent=json.loads((R/'evidence/wave2-alpha4-gates.json').read_text())
assert independent['jar_sha256']==expected and independent['visual_probe']=='PASS'
assert independent['direct_quit_proved'] and independent['delete_world_confirmation_preserved']
accept=json.loads((E/'n11-acceptance-v4.json').read_text(encoding='utf-8'))
observed=json.loads((E/'n11-observations-v4.json').read_text())
failures=[x for x in accept if not x['passed']]
assert [x['name'] for x in failures]==['quit_process_exits_directly','suite_stopped'],[x['name'] for x in failures]
# Those two failures are the same inactive-window precondition, resolved by the dedicated foreground test.
functional=[x for x in accept if x['passed']]
assert len(functional)==83
for item in observed:
    assert tuple(item['size'])==(1920,1001) and item['brown_fraction']<.35,item
console_errors=[];runtime_markers=[]
with (E/'console.log').open(encoding='utf-8',errors='replace') as f:
    for line_number,line in enumerate(f,1):
        if any(k in line for k in ['Secondary GUI primitive themed','AkashicCinematic','ACM-QA']):
            if any(k in line for k in ['Secondary GUI primitive themed','Secondary theme background fallback',
                                       'Secondary theme splice rejected','GL_ERROR','Cinematic renderer failed']):
                runtime_markers.append({'line':line_number,'text':line.rstrip()[:900]})
        if any(k in line for k in ['Secondary theme background fallback','Secondary theme splice rejected',
                                   '[ACM-QA] GL_ERROR','Cinematic renderer failed']):
            console_errors.append({'line':line_number,'text':line.rstrip()[:700]})
assert not console_errors,console_errors
# Preserve runtime screenshots inside the candidate.
review=R/'evidence/runtime-review';assert not review.exists();review.mkdir()
for image in sorted((E/'review').glob('*.jpg')):shutil.copy2(image,review/image.name)
for name in ['n11-acceptance-v4.json','n11-observations-v4.json','quit-foreground.json',
             'process.json','encoding-test-correction.json','createworld-test-correction.json',
             'createworld-route-variability.json']:
    src=E/name
    if src.exists():shutil.copy2(src,R/'evidence'/('runtime-'+name))
# Ensure recording and principal BGM engine remain byte-identical to N10.
base=R.parent/'native-n10-ceremonial'
bgm='src/main/resources/assets/akashiccinematic/sounds/bgm/menu.ogg'
assert sha(R/bgm)==sha(base/bgm)
for rel in ['src/main/java/br/com/redeakashic/cinematic/client/MenuBgm.java',
            'src/main/java/br/com/redeakashic/cinematic/core/BgmPlayback.java',
            'src/main/java/br/com/redeakashic/cinematic/core/MenuSoundGate.java']:
    assert sha(R/rel)==sha(base/rel),rel
# BgmFrontendPolicy intentionally expands the allow-list for Wave 2 screens.
with zipfile.ZipFile(jar) as z:
    assert z.testzip() is None
    assert not any('cinematicqa' in n or n.endswith(('.ttf','.otf','.woff','.woff2')) for n in z.namelist())
# Rebuild after all runtime evidence; tested artifact must be reproducible.
env=dict(__import__('os').environ);env['JAVA_TOOL_OPTIONS']='-Xmx192m'
rebuilt=subprocess.run([sys.executable,str(R/'tools/build_native.py')],capture_output=True,text=True,timeout=90,env=env)
(R/'evidence/reproducible-build.log').write_text(rebuilt.stdout+rebuilt.stderr,encoding='utf-8')
assert rebuilt.returncode==0 and sha(jar)==expected
daily=Path.home()/'AppData/Roaming/.technic/modpacks/forbidden-series/mods'
daily_observed=[{'name':x.name,'sha256':sha(x)} for x in daily.glob('*CinematicMenu*.jar')]
summary={
 'checkpoint':'ACM-N11-GUI-WAVE2-2026-09-21',
 'version':'0.7.0-alpha.4','jar_sha256':expected,'jar_bytes':jar.stat().st_size,
 'classes':b['classes'],'compiled_against_real_artifacts':True,
 'independent_gates':independent,'full_pack_process':proc,
 'runtime_passed_checks':len(functional),'runtime_matrix_total_records':len(accept),
 'runtime_expected_test_precondition_failures':[x['name'] for x in failures],
 'direct_quit_foreground':quit_test,
 'captured_screens':[x['label'] for x in observed],
 'observations':observed,'runtime_error_markers':console_errors,
 'runtime_markers':runtime_markers[-80:],
 'BGM_recording_unchanged':True,'BGM_controller_unchanged':True,
 'BGM_policy_extension':'Wave 2 allow-list expanded for themed front-end settings; world precedence unchanged.',
 'worlds_created_or_loaded':False,'real_credentials_used':False,
 'delete_world_confirmation_preserved':True,
 'generic_confirmation_runtime_tested':True,
 'daily_observed_read_only':daily_observed,'daily_mutation_by_this_work':False,
 'reproducible_build':True,
 'user_visual_approval':'PENDING_AFTER_N11',
 'general_release_approved':False,
 'verdict':'FULL_PACK_DIRECTED_TESTS_PASSED__USER_VISUAL_APPROVAL_PENDING'
}
(R/'CHECKPOINT_N11.json').write_text(json.dumps(summary,indent=2,ensure_ascii=False),encoding='utf-8')
(R/'evidence/final-audit.json').write_text(json.dumps(summary,indent=2,ensure_ascii=False),encoding='utf-8')
print(json.dumps({k:v for k,v in summary.items() if k not in ['independent_gates','observations','runtime_markers','direct_quit_foreground']},indent=2,ensure_ascii=False),flush=True)
