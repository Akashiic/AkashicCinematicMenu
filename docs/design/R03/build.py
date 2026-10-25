"""Build one portable offline HTML. Standard library only; never modifies Minecraft."""
from pathlib import Path
import argparse, base64, hashlib
ROOT=Path(__file__).resolve().parent

def build(output: Path) -> str:
    html=(ROOT/'src/index.html').read_text(encoding='utf-8')
    css=(ROOT/'src/styles.css').read_text(encoding='utf-8')
    js=(ROOT/'src/app.js').read_text(encoding='utf-8')
    for p in sorted((ROOT/'assets').glob('*.webp')):
        uri='data:image/webp;base64,'+base64.b64encode(p.read_bytes()).decode('ascii')
        html=html.replace('../assets/'+p.name,uri)
        css=css.replace('../assets/'+p.name,uri)
    html=html.replace('<link rel="stylesheet" href="styles.css">','<style>\n'+css+'\n</style>')
    html=html.replace('<script src="app.js" defer></script>','<script>\n'+js+'\n</script>')
    if any(token in html for token in ('https://','http://','font-face','../assets/')):
        raise ValueError('Unexpected external reference or incomplete asset embedding')
    output.parent.mkdir(parents=True,exist_ok=True)
    output.write_text(html,encoding='utf-8',newline='\n')
    return hashlib.sha256(output.read_bytes()).hexdigest()

if __name__=='__main__':
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output',type=Path,default=ROOT/'dist/Forbidden-Santuario-R03.html')
    args=parser.parse_args()
    digest=build(args.output)
    print(f'{args.output}\n{args.output.stat().st_size} bytes\nSHA-256 {digest}')
