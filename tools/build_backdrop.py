from pathlib import Path
import base64,hashlib,io,json,re
from PIL import Image
R=Path(__file__).resolve().parents[1];file=Path.home()/'Downloads/Forbidden-Inicializacao-S01.html';data=file.read_text(encoding='utf-8')
s=re.search(r'<script[^>]*id="approved-menu"[^>]*>(.*?)</script>',data,re.S);assert s
menu=base64.b64decode(s.group(1)).decode();image=re.search(r'id="cathedral"\s+src="data:image/[^;]+;base64,([A-Za-z0-9+/=]+)',menu);assert image
im=Image.open(io.BytesIO(base64.b64decode(image.group(1)))).convert('RGB');dest=R/'src/main/resources/assets/akashiccinematic/native/backdrop.jpg';im.save(dest,quality=95,subsampling=0)
thumb=im.copy();thumb.thumbnail((1000,650));thumb.save(R/'evidence/backdrop-inspection.jpg',quality=91)
(R/'evidence/backdrop-origin.json').write_text(json.dumps({'source_html_sha256':hashlib.sha256(file.read_bytes()).hexdigest(),'source_tag':'cathedral','size':im.size,'asset_sha256':hashlib.sha256(dest.read_bytes()).hexdigest()},indent=2))
print('BACKGROUND_EXTRACTED',im.size,hashlib.sha256(dest.read_bytes()).hexdigest(),flush=True)
