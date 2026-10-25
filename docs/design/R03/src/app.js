/* Forbidden / R03. Offline visual study. No server calls, game integration or approval writes. */
(() => {
  'use strict';
  const $ = (q) => document.querySelector(q);
  const $$ = (q) => Array.from(document.querySelectorAll(q));
  const body = document.body;
  const panels = Object.freeze({
    a: Object.freeze({name:'Cristal mineral', blur:10, alpha:.36, description:'10 PX DE BLUR · TINTA 36%', copy:'Mais transparente. A arquitetura permanece mais presente, com as arestas suavizadas.'}),
    b: Object.freeze({name:'Vidro fumê', blur:16, alpha:.46, description:'16 PX DE BLUR · TINTA 46%', copy:'Um equilíbrio entre profundidade e leitura. O concreto aparece como um volume filtrado.'}),
    c: Object.freeze({name:'Véu de pedra', blur:24, alpha:.58, description:'24 PX DE BLUR · TINTA 58%', copy:'Mais reservado e denso. A paisagem se transforma em luz difusa atrás da navegação.'})
  });
  const audit = {version:'R03.1', baseLayout:'R02.1 / 01 — Santuário', baseCompositionChosenByUser:true, panel:'b', reference:false, blurSupported:false, glassMode:'live', panelArtReady:false, renderer:'DOM/CSS + Canvas2D', statue:'approved 2D scenic reference, not a 3D mesh', finalApproval:false, layout:'1', reducedMotion:false, particles:true, phase:'idle', routeCount:0, switches:0, frameSamples:[], handlerSamples:[], artReady:false, paused:false};
  // Read-only snapshot is provided for reproducible QA, not for declaring user approval.
  Object.defineProperty(window, '__FORBIDDEN_AUDIT__', {get: () => JSON.parse(JSON.stringify(audit))});
  const menuButtons = $$('.menu-button');
  const panelButtons = $$('[data-panel-select]');
  const panelScene = $('.panel-scenery');
  const transparencyQuery = matchMedia('(prefers-reduced-transparency: reduce)');
  const blurSupported = CSS.supports('backdrop-filter','blur(1px)') || CSS.supports('-webkit-backdrop-filter','blur(1px)');
  let reduceTransparency = transparencyQuery.matches;
  let transparencyOverride = false;
  let reference = false;
  audit.blurSupported = blurSupported;
  const dialog = $('#review-dialog');
  const scene = $('#scene-camera');
  const environment = $('.environment');
  const canvas = $('#dust');
  const context = canvas.getContext('2d', {alpha:true});
  let motionQuery = matchMedia('(prefers-reduced-motion: reduce)');
  let userMotionOverride = false;
  let reduced = motionQuery.matches;
  let particles = true;
  let activePanel = 'b';
  let operation = null;
  let operationToken = 0;
  let replayTimer = null;
  let lastTrigger = null;
  let pointer = {x:0,y:0};
  let smooth = {x:0,y:0};
  let previousTime = null;
  let raf = null;
  let visible = !document.hidden;
  let focused = true;
  let elapsed = 0;
  let width = 1, height = 1, dpr = 1;
  let mistOverride = null;
  const dust = Array.from({length:28},(_,i)=>({
    x:((i*2654435761>>>0)%997)/997,
    y:((i*1597334677>>>0)%991)/991,
    size:.55+(i%4)*.23, speed:.005+(i%5)*.0015,
    phase:i*.731
  }));
  function announce(message) { $('#announcement').textContent=message; }
  function cancelOperation() {
    operationToken++;
    if(operation!==null) clearTimeout(operation);
    operation=null;
    body.classList.remove('departing','layout-changing');
    if(!dialog.open) audit.phase='idle';
    menuButtons.forEach(b=>b.removeAttribute('aria-busy'));
  }
  function replay() {
    if(replayTimer!==null) clearTimeout(replayTimer);
    body.classList.remove('intro');
    if(reduced) return;
    // Restart only the visual entrance; no fake startup progress is shown.
    void body.offsetWidth;
    body.classList.add('intro');
    replayTimer=setTimeout(()=>{body.classList.remove('intro'); replayTimer=null;},1300);
  }
  function syncGlass() {
    const mode = reduceTransparency ? 'solid' : (!blurSupported ? 'fallback' : 'live');
    body.dataset.glassMode=mode;
    body.dataset.reference=String(reference);
    audit.glassMode=mode; audit.reference=reference;
    const data=panels[activePanel];
    $('#layout-number').textContent=reference?'01':activePanel.toUpperCase();
    $('#layout-name').textContent=reference?'Painel anterior':data.name;
    $('#layout-description').textContent=reference?'R02 / PAINEL OPACO':mode==='solid'?'TRANSPARÊNCIA REDUZIDA':mode==='fallback'?'MODO DE COMPATIBILIDADE / SEM BLUR':data.description;
    $('#reference-button').setAttribute('aria-pressed',String(reference));
    $('#reference-button').setAttribute('aria-label',reference?'Voltar ao painel translúcido':'Ver painel opaco anterior');
    $('#reference-label').textContent=reference?'Depois':'Antes';
    panelButtons.forEach(button=>button.setAttribute('aria-pressed',String(button.dataset.panelSelect===activePanel&&!reference)));
    const status=$('#glass-status');
    status.hidden=reference || mode==='live';
    status.textContent=mode==='solid'?'Transparência reduzida · painel sólido':mode==='fallback'?'Blur indisponível · contraste preservado':'';
    if($('#setting-transparency')) $('#setting-transparency').checked=reduceTransparency;
  }
  function setTransparency(value, override=true) {
    reduceTransparency=Boolean(value);
    if(override) transparencyOverride=true;
    syncGlass();
  }
  function applyPanel(id, announceChange=true) {
    if(!Object.prototype.hasOwnProperty.call(panels,id)) return false;
    if(dialog.open) closeDialog();
    cancelOperation();
    activePanel=id; reference=false;
    body.dataset.panel=id;
    audit.panel=id; audit.switches++;
    syncGlass();
    try { history.replaceState(null,'','#panel='+id); } catch (_) { /* sandbox/file may disallow history */ }
    if(announceChange) announce('Material '+id.toUpperCase()+', '+panels[id].name+'. A composição do Santuário permanece igual.');
    return true;
  }
  function toggleReference(){
    cancelOperation(); reference=!reference; syncGlass();
    announce(reference?'Painel opaco anterior, R02.':'Painel translúcido, '+panels[activePanel].name+'.');
  }
  function resizeCanvas() {
    const bounds=environment.getBoundingClientRect();
    width=Math.max(1,bounds.width); height=Math.max(1,bounds.height);
    dpr=Math.min(window.devicePixelRatio||1,1.5);
    canvas.width=Math.round(width*dpr); canvas.height=Math.round(height*dpr);
    if(context) context.setTransform(dpr,0,0,dpr,0,0);
  }
  function resetView() {
    pointer={x:0,y:0}; smooth={x:0,y:0}; previousTime=null;
    scene.style.transform='translate3d(0px,0px,0px)';
    panelScene.style.transform='translate3d(0px,0px,0px)';
    if(context) context.clearRect(0,0,width,height);
  }
  function stopLoop() {
    if(raf!==null) cancelAnimationFrame(raf);
    raf=null; previousTime=null;
  }
  function syncLoop() {
    stopLoop();
    const paused=!visible||!focused||dialog.open;
    body.dataset.paused=String(paused); audit.paused=paused;
    if(!reduced && !paused) raf=requestAnimationFrame(frame);
    else if(reduced) resetView();
  }
  function setReduced(value, override=true) {
    reduced=Boolean(value);
    if(override) userMotionOverride=true;
    body.dataset.motion=reduced?'reduced':'full';
    audit.reducedMotion=reduced;
    $('#motion-button').setAttribute('aria-pressed',String(reduced));
    $('#motion-button').setAttribute('aria-label',reduced?'Ativar movimento':'Reduzir movimento');
    $('#motion-symbol').textContent=reduced?'▷':'Ⅱ';
    $('#motion-label').textContent=reduced?'Animar':'Pausar';
    if($('#setting-motion')) $('#setting-motion').checked=reduced;
    syncLoop();
    if(reduced) body.classList.remove('intro');
  }
  function frame(timestamp) {
    raf=null;
    if(reduced || !visible || !focused || dialog.open) return;
    const started=performance.now();
    const raw=previousTime===null?0:(timestamp-previousTime)/1000;
    const dt=Math.min(.05,Math.max(0,raw));
    previousTime=timestamp; elapsed+=dt;
    if(raw>0 && audit.frameSamples.length<420) audit.frameSamples.push(raw*1000);
    // Exponential smoothing is measured in seconds, independent of refresh rate.
    const decay=1-Math.exp(-dt*4.2);
    smooth.x+=(pointer.x-smooth.x)*decay;
    smooth.y+=(pointer.y-smooth.y)*decay;
    const strength=6.5; // Fixed Layout 1 camera, independent of the selected glass.
    scene.style.transform=`translate3d(${(smooth.x*strength).toFixed(3)}px,${(smooth.y*4).toFixed(3)}px,0)`;
    panelScene.style.transform=`translate3d(${(smooth.x*4).toFixed(3)}px,${(smooth.y*2.5).toFixed(3)}px,0)`;
    if(context){
      context.clearRect(0,0,width,height);
      if(particles){
        context.fillStyle='#cfdbc2';
        for(const p of dust){
          const x=((p.x+elapsed*.003+Math.sin(elapsed*.1+p.phase)*.006)%1)*width;
          const y=((p.y-elapsed*p.speed)%1+1)%1*height;
          context.globalAlpha=.08+(Math.sin(elapsed*.45+p.phase)+1)*.15;
          context.beginPath(); context.arc(x,y,p.size,0,Math.PI*2); context.fill();
        }
        context.globalAlpha=1;
      }
    }
    if(audit.handlerSamples.length<420) audit.handlerSamples.push(performance.now()-started);
    raf=requestAnimationFrame(frame);
  }
  function cinema(value) {
    body.dataset.cinema=String(value);
    $('#restore-tools').hidden=!value;
    if(value) $('#restore-tools').focus({preventScroll:true});
    else $('#cinema-button').focus({preventScroll:true});
  }
  function compareMarkup(){
    return `<div class="compare-grid">${Object.keys(panels).map(id=>`<button type="button" class="compare-card ${activePanel===id?'selected':''}" data-compare="${id}" aria-label="Experimentar ${id.toUpperCase()}, ${panels[id].name}"><span class="material-sample" data-sample="${id}" aria-hidden="true"><span class="sample-glass"><b>FORBIDDEN</b><i>JOGAR</i><i>OPÇÕES</i></span><span class="sample-letter">${id.toUpperCase()}</span></span><span class="card-copy"><small>${panels[id].blur} PX / TINTA ${Math.round(panels[id].alpha*100)}%</small><strong>${panels[id].name}</strong><p>${panels[id].copy}</p></span></button>`).join('')}</div><p class="decision-note">Layout 1 / Santuário mantido em 27% no desktop. Os valores de tinta são a camada-base, não a opacidade combinada com os gradientes de leitura. A escolha troca apenas a prévia; não registra aprovação. Miniaturas são amostras do material, não capturas de tela. A cena e o anjo continuam em 2D.</p>`;
  }
  function optionsMarkup(){return `<div class="settings-grid"><label class="setting"><span>Reduzir transparência<small>Usa uma superfície sólida e desliga o blur do painel.</small></span><input id="setting-transparency" type="checkbox" ${reduceTransparency?'checked':''}></label><label class="setting"><span>Movimento reduzido<small>Desliga parallax, partículas animadas e transições decorativas.</small></span><input id="setting-motion" type="checkbox" ${reduced?'checked':''}></label><label class="setting"><span>Partículas de atmosfera<small>Uma camada discreta, independente dos botões.</small></span><input id="setting-particles" type="checkbox" ${particles?'checked':''}></label><label class="setting"><span>Atmosfera de musgo<small>Intensidade do tratamento verde local. Não recolore o núcleo.</small></span><input id="setting-green" aria-label="Intensidade da atmosfera de musgo" type="range" min="0" max="80" value="${Math.round((mistOverride===null?.30:mistOverride)*100)}"></label></div><button class="replay-button" id="replay-button" type="button">Rever a entrada do menu ↗</button>`;}
  const routeData={
    compare:{title:'A matéria do silêncio',intro:'A composição do Santuário foi escolhida. Agora, três tratamentos de transparência para o mesmo painel.',content:compareMarkup},
    play:{title:'Atravessar o limiar',intro:'Estudo da transição de entrada: o menu se recolhe, a cena avança e o próximo estado assume o foco.',content:()=>'<div class="demo-card"><div><h3>The End of Forbidden</h3><p>A conexão ao servidor pertence à implementação nativa.<br>Este HTML não abre o jogo e não inicia conexões.</p></div><span class="demo-label">PRÉVIA LOCAL</span></div>'},
    multiplayer:{title:'Além deste santuário',intro:'Destino demonstrativo do botão Multiplayer. O foco desta rodada é composição e comportamento do menu principal.',content:()=>'<div class="demo-card"><div><h3>Rede Akashic</h3><p>Nenhum servidor consultado. Endereço, disponibilidade e jogadores<br>não são simulados como dados reais.</p></div><span class="demo-label">NÃO CONSULTADO</span></div>'},
    options:{title:'Atmosfera & movimento',intro:'Estas opções alteram somente o estudo visual local. Não modificam configurações do Minecraft.',content:optionsMarkup},
    mods:{title:'Uma interface, não outro jogo',intro:'A lista real de mods será aberta pelo Forge. Esta rodada não lê os arquivos da sua instalação.',content:()=>'<dl class="facts"><div class="fact"><dt>IMPLEMENTAÇÃO DA PRÉVIA</dt><dd>HTML, CSS e Canvas 2D.</dd></div><div class="fact"><dt>INTEGRAÇÃO NATIVA</dt><dd>Não executada nesta rodada.</dd></div><div class="fact"><dt>CENA</dt><dd>Arte aprovada, recortada e tratada.</dd></div><div class="fact"><dt>ANJO 3D</dt><dd>Não implementado neste estudo.</dd></div></dl>'},
    exit:{title:'Permanecer ou partir?',intro:'No mod, esta ação solicitará confirmação antes de encerrar o jogo.',content:()=>'<p class="exit-note">A prévia não tenta fechar o seu navegador. Volte ao menu para continuar comparando os materiais ou feche esta aba para encerrar o estudo.</p>'}
  };
  function openDialog(route, trigger) {
    if(!Object.prototype.hasOwnProperty.call(routeData,route)||dialog.open) return;
    const info=routeData[route];
    lastTrigger=trigger||document.activeElement;
    $('#dialog-title').textContent=info.title;
    $('#dialog-intro').textContent=info.intro;
    $('#dialog-content').innerHTML=info.content();
    $('#dialog-kicker').textContent=route==='compare'?'DIREÇÃO DE ARTE / CANDIDATOS':'LABORATÓRIO VISUAL / R03';
    body.classList.add('panel-open');
    cancelOperation();
    dialog.showModal(); audit.phase='dialog'; audit.routeCount++;
    $('#close-dialog').focus();
    syncLoop();
    $$('[data-compare]').forEach(b=>b.addEventListener('click',()=>applyPanel(b.dataset.compare)));
    if(route==='options'){
      $('#setting-transparency').addEventListener('change',e=>setTransparency(e.target.checked));
      $('#setting-motion').addEventListener('change',e=>setReduced(e.target.checked));
      $('#setting-particles').addEventListener('change',e=>{particles=e.target.checked; audit.particles=particles;});
      $('#setting-green').addEventListener('input',e=>{mistOverride=Math.max(0,Math.min(.8,Number(e.target.value)/100)); body.style.setProperty('--moss-level',String(mistOverride));});
      $('#replay-button').addEventListener('click',()=>{closeDialog();replay();});
    }
  }
  function closeDialog() {if(dialog.open) dialog.close();}
  function startRoute(route, trigger) {
    if(operation!==null || dialog.open) return;
    if(!Object.prototype.hasOwnProperty.call(routeData,route)) return;
    if(reduced){openDialog(route,trigger);return;}
    body.classList.add('departing'); audit.phase='transitioning';
    trigger.setAttribute('aria-busy','true');
    const token=operationToken;
    operation=setTimeout(()=>{if(token===operationToken)openDialog(route,trigger);},320);
  }
  menuButtons.forEach((button,index)=>{
    button.style.setProperty('--order',index);
    button.addEventListener('click',()=>startRoute(button.dataset.action,button));
    button.addEventListener('keydown',event=>{
      let next;
      if(event.key==='ArrowDown') next=(index+1)%menuButtons.length;
      if(event.key==='ArrowUp') next=(index+menuButtons.length-1)%menuButtons.length;
      if(event.key==='Home') next=0;
      if(event.key==='End') next=menuButtons.length-1;
      if(next!==undefined){event.preventDefault();menuButtons[next].focus();}
    });
  });
  panelButtons.forEach(button=>button.addEventListener('click',()=>applyPanel(button.dataset.panelSelect)));
  $('#reference-button').addEventListener('click',toggleReference);
  $('#compare-button').addEventListener('click',event=>openDialog('compare',event.currentTarget));
  $('#motion-button').addEventListener('click',()=>setReduced(!reduced));
  $('#cinema-button').addEventListener('click',()=>cinema(true));
  $('#restore-tools').addEventListener('click',()=>cinema(false));
  $('#close-dialog').addEventListener('click',closeDialog);
  $('#dialog-back').addEventListener('click',closeDialog);
  dialog.addEventListener('close',()=>{
    body.classList.remove('panel-open'); audit.phase='idle';
    if(lastTrigger?.isConnected && !lastTrigger.closest('dialog')) lastTrigger.focus({preventScroll:true});
    lastTrigger=null; syncLoop();
  });
  document.addEventListener('keydown',event=>{
    if(event.key==='Escape' && !dialog.open && operation!==null){event.preventDefault();cancelOperation();announce('Transição cancelada.');return;}
    if(event.defaultPrevented || event.repeat || event.ctrlKey || event.metaKey || event.altKey || dialog.open || /INPUT|TEXTAREA|SELECT/.test(event.target.tagName))return;
    if(['1','2','3'].includes(event.key)){event.preventDefault();applyPanel({1:'a',2:'b',3:'c'}[event.key]);}
    else if(event.key.toLowerCase()==='b'){event.preventDefault();toggleReference();}
    else if(event.key.toLowerCase()==='m'){event.preventDefault();setReduced(!reduced);}
    else if(event.key.toLowerCase()==='v'){event.preventDefault();cinema(body.dataset.cinema!=='true');}
    else if(event.key.toLowerCase()==='c'){event.preventDefault();openDialog('compare',document.activeElement);}
    else if(event.key.toLowerCase()==='r'){event.preventDefault();replay();}
  });
  window.addEventListener('pointermove',event=>{
    if(reduced || dialog.open || event.pointerType==='touch')return;
    pointer.x=(event.clientX/window.innerWidth-.5)*2;
    pointer.y=(event.clientY/window.innerHeight-.5)*2;
  },{passive:true});
  document.documentElement.addEventListener('pointerleave',()=>{pointer={x:0,y:0};});
  window.addEventListener('resize',()=>{resizeCanvas(); resetView();},{passive:true});
  document.addEventListener('visibilitychange',()=>{
    visible=!document.hidden;
    if(!visible) cancelOperation();
    resetView(); syncLoop();
  });
  window.addEventListener('blur',()=>{focused=false;cancelOperation();resetView();syncLoop();});
  window.addEventListener('focus',()=>{focused=true;syncLoop();});
  motionQuery.addEventListener('change',e=>{if(!userMotionOverride)setReduced(e.matches,false);});
  transparencyQuery.addEventListener('change',e=>{if(!transparencyOverride)setTransparency(e.matches,false);});
  const panelArt=$('#panel-art');
  const checkPanelArt=()=>{audit.panelArtReady=panelArt.complete&&panelArt.naturalWidth>0;};
  panelArt.addEventListener('load',checkPanelArt); panelArt.addEventListener('error',checkPanelArt);
  if(panelArt.complete)checkPanelArt();
  const art=$('#cathedral');
  const artResult=()=>{
    audit.artReady=art.complete&&art.naturalWidth>0;
    $('#art-fallback').hidden=audit.artReady;
    body.dataset.ready='true';
  };
  art.addEventListener('load',artResult);
  art.addEventListener('error',artResult);
  if(art.complete) artResult();
  const selected=new URLSearchParams(location.hash.slice(1)).get('panel');
  applyPanel(selected&&Object.prototype.hasOwnProperty.call(panels,selected)?selected:'b',false);
  window.addEventListener('hashchange',()=>{const id=new URLSearchParams(location.hash.slice(1)).get('panel');if(id&&Object.prototype.hasOwnProperty.call(panels,id))applyPanel(id);});
  setReduced(reduced,false); resizeCanvas(); replay();
  if(typeof ResizeObserver!=='undefined') new ResizeObserver(resizeCanvas).observe(environment);
  window.addEventListener('pagehide',()=>{stopLoop(); cancelOperation(); if(replayTimer!==null)clearTimeout(replayTimer);});
  // Essential navigation remains visible even if an image fails; no fake loading screen is used.
})();
