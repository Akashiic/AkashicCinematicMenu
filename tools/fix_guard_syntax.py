from pathlib import Path
r=Path(__file__).resolve().parents[1];p=r/'src/main/java/br/com/redeakashic/cinematic/client/ConnectionCancellationGuard.java';s=p.read_text(encoding='utf-8')
old='        throw new NoSuchFieldException("GuiConnecting cancellation fields");\n    private void preflight()';assert s.count(old)==1
p.write_text(s.replace(old,'        throw new NoSuchFieldException("GuiConnecting cancellation fields");\n    }\n    private void preflight()'),encoding='utf-8',newline='\n');print('Fixed missing method boundary; compilation to be rerun')
