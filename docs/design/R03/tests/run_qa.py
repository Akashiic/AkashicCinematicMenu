from pathlib import Path
import subprocess,sys,json,time
r=Path(__file__).resolve().parents[1]
start=time.time()
with (r/'evidence/qa-all.log').open('w') as f:
 result=subprocess.run([sys.executable,str(r/'tests/qa.py'),'all'],stdout=f,stderr=subprocess.STDOUT)
(r/'evidence/qa-run-status.json').write_text(json.dumps({'exit_code':result.returncode,'elapsed_seconds':time.time()-start}))
