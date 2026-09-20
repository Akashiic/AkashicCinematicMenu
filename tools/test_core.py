from pathlib import Path
import subprocess, sys, shutil
root = Path(__file__).resolve().parents[1]
out = root / 'build/core-tests'
out.mkdir(parents=True, exist_ok=True)
evidence = root / 'evidence'; evidence.mkdir(exist_ok=True)
files = sorted((root/'src/main/java/br/com/redeakashic/cinematic/core').glob('*.java')) + sorted((root/'src/test/java').rglob('*.java'))
args = ['javac', '--release', '8', '-Xlint:all,-options', '-Werror', '-encoding', 'UTF-8', '-d', str(out)] + [str(p) for p in files]
a = subprocess.run(args, capture_output=True, text=True)
(evidence/'core-compile.log').write_text(a.stdout+a.stderr, encoding='utf-8')
if a.returncode:
    print(a.stdout+a.stderr); raise SystemExit(a.returncode)
b = subprocess.run(['java','-ea','-cp',str(out),'br.com.redeakashic.cinematic.core.CoreTests'], capture_output=True,text=True)
(evidence/'core-tests.log').write_text(b.stdout+b.stderr, encoding='utf-8')
print(b.stdout+b.stderr)
raise SystemExit(b.returncode)
