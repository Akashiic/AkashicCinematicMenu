from pathlib import Path
import json,hashlib,subprocess,os,sys,zipfile,time,shutil
R=Path(__file__).resolve().parents[1];P=R.parents[1];E=R/'evidence';audit=json.loads((R/'CHECKPOINT_N10.json').read_text());version=audit['version'];sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
assert audit['verdict']=='BUILD_AND_NATIVE_RENDERER_GATES_PASSED__FULL_PACK_RUNTIME_PENDING';jar=Path(audit['jar']);assert sha(jar)==audit['jar_sha256']
report=f'''# Parecer N10 — {version}

Candidata produzida em21/09/2026, com autorrevisão do assistente. A aprovação visual e sonora final permanece do usuário.

## Identidade
JAR: `{jar.name}`; {jar.stat().st_size}bytes; {audit['classes']}classes Java8.
SHA-256: `{audit['jar_sha256']}`.
Base preservada: N09/0.5.2-alpha.1, com a BGM confirmada funcional pelo usuário. Gravação, evento de música e código do controlador permaneceram idênticos.

## Produção
Jogar recebeu gradiente carmesim, texto branco, brilho de borda, respiração lenta, uma faixa de luz periódica e pressão visual de220ms. Nenhuma animação dispara a conexão ou altera a área clicável.
O quadro principal agora usa três profundidades: céu/névoa, arquitetura e anjo/halo. A arquitetura dianteira usa uma máscara de ocultação no mesmo plano, para continuar na frente dos joelhos e do manto.
A camera compartilha um enquadramento cover, mas cada profundidade tem deslocamento próprio. O anjo percorre aproximadamente±13px horizontais em1920px de largura; arquitetura±4.5px e céu±1.4px, antes dos efeitos de recorte.
São imagens RGBA separadas; não é apenas um deslocamento da imagem inteira. As margens de24px e os limites de movimento foram verificados matematicamente.
Seis arquivos SFX originais cobrem foco(duas variantes), seleção, retorno, chegada ao menu e conexão. Há coro sintetizado sem palavras, metal ressonante, ar e cauda de ambiente. Não se afirma ter gravado canto gregoriano humano.
A reverberação é pré-calculada; a reprodução usa o motor do Minecraft. Há limite de vozes e liberação suave de90ms nas substituições, sem DSP de convolução em tempo real.

## Inspeção e correções
A primeira faixa de brilho tinha marcas entre segmentos. Foi reprovada e substituída por uma faixa contínua com interpolação de alfa.
O contorno do anjo foi refinado em uma faixa estreita; o halo continua com abertura transparente. Foi acrescentada ocultação frontal para evitar sobreposição incorreta da estátua sobre pilares.
A recomposição neutra final tem erro médio absoluto de{audit['neutral_mean_absolute_channel_error']:.4f}por canal na escala0–255; não é uma alegação de identidade pixel a pixel.
'''
report+='''
## Verificação executada
48 cenários/17489 asserções do núcleo anterior, 4807 verificações novas de câmera/animação, 1785 de geometria, 140 da política de UI, 1557 de música/fade, 18 cenários/124 asserções de playback e32 da classificação de sons passaram.
O renderizador Java foi executado numa superfície pbuffer privada na Intel UHD Graphics/OpenGL4.6. Não abriu outra janela nem iniciou o modpack completo.
As276 verificações do probe incluem repetição de estado/erros GL, cinco resoluções(854x480,1024x768,1366x768,1920x1001,2560x1440), coordenadas de botões, extremos de câmera e pixels estáveis em movimento reduzido.
A ausência simulada de uma camada produziu imagem igual ao caminho estático e liberou os recursos parcialmente criados. Esse JAR incompleto é fixture e não faz parte da distribuição.
Os6OGGs foram decodificados integralmente pelo JOrbis das bibliotecas do pack, sem erro e com duração correta. Pico abaixo de-8dBFS e compatibilidade mono foram conferidos na análise dos arquivos.
As contagens incluem verificações repetidas; não equivalem a milhares de tipos independentes de defeito.

## Limitações que impedem liberação geral
Esta candidata não foi executada dentro do modpack completo nem se avaliou subjetivamente o novo áudio pelo SoundHandler. Havia clientes Java do usuário ativos e apenas1–2GiB de memória física livre; não foram encerrados para abrir outra instância.
O probe demonstra o renderizador e seus recursos, não compatibilidade total de transformação com Angelica/Forge e todos os demais mods.
Os tempos de desenho no pbuffer variaram com a carga do computador. Não são benchmark de FPS do jogo nem comprovação de60FPS.
A arte original permanece1207x830. Os planos são2.5D, com pequenas regiões escondidas restauradas para o movimento limitado; não um modelo3D nem uma cena reconstruída para câmera livre.
O blur é pré-processado por camada. Não foi acrescentado pós-processamento de framebuffer.
Não há promessa de apresentar novos quadros durante parada de toda a JVM/driver. O episódio de encerramento antecipado de versões históricas não foi resolvido por esta candidata.
A nova paleta do CTA e a sonoridade aguardam a aprovação do usuário. A N09 instalada não foi substituída por este trabalho.

## Referência metodológica
A reconstrução local das faixas ocultas emprega inpainting Telea do OpenCV. Esse método usa vizinhança de pixels; ele não recupera informação histórica real das áreas ocultas. Referência: https://docs.opencv.org/4.x/df/d3d/tutorial_py_inpainting.html

## Parecer
Compilação, análise de recursos, testes de lógica e renderizador nativo: passaram no escopo declarado. Homologação completa no pack e aprovação artística: pendentes.
'''
(R/'docs/PARECER_N10.md').write_text(report,encoding='utf-8',newline='\n');(P/'NATIVE_REVIEW_N10.md').write_text(report,encoding='utf-8',newline='\n')
html='''<!doctype html><html lang="pt-BR"><meta charset="utf-8"><meta name="viewport" content="width=device-width"><title>Forbidden · N10</title><style>body{margin:0;background:#090d14;color:#e8e4d8;font:17px/1.65 system-ui}main{max-width:1040px;margin:auto;padding:40px 24px}h1{font:48px Georgia;margin:.3em 0}h2{font:28px Georgia}small{color:#acb2bb}img{max-width:100%;display:block;border:1px solid #473844}button,a{color:#f7e6eb}button{background:#74122d;border:1px solid #d097a8;padding:12px 22px;cursor:pointer}audio{width:100%;margin:16px 0}.note{padding:18px;border-left:3px solid #c4aa7c;background:#171c25}</style><main><small>REDE AKASHIC · CANDIDATA @VERSION@</small><h1>O Santuário, em movimento.</h1><p>Captação do renderizador Java/OpenGL em contexto isolado. Não é uma simulação CSS nem uma gravação do modpack completo.</p><img id="motion" src="../evidence/native-probe-@VERSION@/N10-Movimento-Nativo.webp" alt="Movimento das camadas e brilho carmesim do botão Jogar"><p><button id="toggle">Pausar movimento</button></p><h2>Assinatura sonora</h2><p>Na ordem: foco A, foco B, seleção, retorno, chegada ao menu e conexão. Coro sintetizado sem palavras; esta audição não é captura do SoundHandler.</p><audio controls preload="none" src="../evidence/audio/Ceremonial-SFX-Audition.wav"></audio><h2>Separação real da arte</h2><img src="../evidence/depth/actual-plates.jpg" alt="Céu, arquitetura, anjo e máscara de ocultação"><p>Três profundidades. A quarta máscara preserva a arquitetura diante dos joelhos. As regiões escondidas só foram preparadas para o deslocamento curto permitido.</p><p class="note">A BGM e o código que a controla permaneceram intactos. A instalação diária não foi alterada. Leia o parecer antes de promover esta candidata.</p><p><a href="PARECER_N10.md">Parecer e limites dos testes</a></p></main><script>const img=document.querySelector('#motion'),btn=document.querySelector('#toggle');let paused=false;btn.onclick=()=>{paused=!paused;img.src='../evidence/native-probe-@VERSION@/'+(paused?'menu-1366x768.jpg':'N10-Movimento-Nativo.webp');btn.textContent=paused?'Retomar movimento':'Pausar movimento'};if(matchMedia('(prefers-reduced-motion: reduce)').matches)btn.click();</script></html>'''
(R/'docs/Revisao-N10.html').write_text(html.replace('@VERSION@',version),encoding='utf-8')
env=dict(os.environ);env['JAVA_TOOL_OPTIONS']='-Xmx192m'
run=subprocess.run([sys.executable,str(R/'tools/build_native.py')],capture_output=True,text=True,timeout=90,env=env);(E/'reproducible-build.log').write_text(run.stdout+run.stderr,encoding='utf-8');assert run.returncode==0 and sha(jar)==audit['jar_sha256']
files=[(jar,'dist/'+jar.name),(R/'README.md','README.md'),(R/'CHECKPOINT_N10.json','CHECKPOINT_N10.json')]
files += [(f,f.relative_to(R).as_posix()) for name in ['src','docs'] for f in (R/name).rglob('*') if f.is_file()]
for name in ['build_native.py','test_core.py','build_depth_layers.py','refine_depth_edges.py','build_ceremonial_sfx.py','verify_n10.py','CeremonialGraphicsProbe.java','run_graphics_probe.py','DepthFallbackProbe.java','CeremonialCodecProbe.java','run_resilience_probes.py','final_audit_n10.py']:
    files.append((R/'tools'/name,'tools/'+name))
for name in ['final-audit.json','baseline.json','build-audit.json','depth-build.json','depth-refinement.json','occlusion-review.json','ceremonial-sfx.json','visual-review-1.json','reproducible-build.log','native-classpath.json','depth-fallback-probe.log','jorbis-cues.log','native-probe-'+version+'.log','probe-process-'+version+'.json']:
    files.append((E/name,'evidence/'+name))
files.append((E/'audio/Ceremonial-SFX-Audition.wav','evidence/audio/Ceremonial-SFX-Audition.wav'));files.append((E/'depth/actual-plates.jpg','evidence/depth/actual-plates.jpg'))
for f in (E/('native-probe-'+version)).iterdir():
    if f.suffix in ['.jpg','.webp']:files.append((f,f.relative_to(R).as_posix()))
archive=R/'dist/Forbidden-N10-Ceremonial-engineering.zip';assert not archive.exists(),'Existing package requires revalidation, not blind overwrite'
with zipfile.ZipFile(archive,'x',compression=zipfile.ZIP_DEFLATED,compresslevel=5) as z:
    for f,name in files:
        assert f.is_file(),f;assert f.suffix.lower() not in ['.ttf','.otf','.woff','.woff2'];assert 'cinematicqa' not in name.lower();z.write(f,'Forbidden-N10/'+name)
with zipfile.ZipFile(archive) as z:assert z.testzip() is None
receipt={'archive':str(archive),'sha256':sha(archive),'bytes':archive.stat().st_size,'jar_sha256':sha(jar),'rebuild_identical':True,'preview_html':'docs/Revisao-N10.html','full_modpack_runtime':False,'artistic_approval':'PENDING'}
(E/'archive-receipt.json').write_text(json.dumps(receipt,indent=2),encoding='utf-8')
status=P/'CURRENT_STATUS.json';previous=json.loads(status.read_text());(E/'previous-current-status.json').write_text(json.dumps(previous,indent=2))
previous['user_confirmed_baseline']={'candidate':'candidates/native-n09-audio-fixed','version':'0.5.2-alpha.1','music_functional_confirmed':True,'jar_sha256':audit['baseline_sha256']}
previous['ceremonial_candidate']={'candidate':'candidates/native-n10-ceremonial','version':version,'jar_sha256':sha(jar),'checkpoint':'candidates/native-n10-ceremonial/CHECKPOINT_N10.json','renderer_tested':True,'full_modpack_runtime':False,'installed_by_this_work':False,'user_approval':'PENDING'}
status.write_text(json.dumps(previous,indent=2),encoding='utf-8');print('DELIVERY_VERIFIED',json.dumps(receipt),flush=True)
