# N12 — Edição de Natal (0.7.0-alpha.5)

Troca completa da estética do menu: a catedral com o anjo dá lugar à arte de Natal do dono (a garota com a árvore e o lança-mísseis fugindo pelos corredores amarelos, com dois perseguidores). **Implementado e testado no núcleo; JAR não gerado; não testado no jogo; aprovação visual do dono PENDENTE.**

## Fonte e autoria

* `source/natal-source.jpg` — 2000×1116, SHA-256 `48daedf34cbe69cdbdec7e670c733f36f387ae56fcec6ae93b74c479421bc716`.
* Autoria declarada pelo dono em 17/11/2026 ("Sim, criei essa imagem"). [DOC]
* `src/main/resources/assets/akashiccinematic/native/backdrop.jpg` é **byte a byte** essa fonte (sem recompressão).
* `source/gui-alpha4/` — cópia das texturas da alpha.4, entrada do recolorido de `tools/build_natal_gui.py`.

## O que muda na tela

| Superfície | Antes (alpha.4) | Agora (N12) |
|---|---|---|
| Fundo / paralaxe | 4 camadas (céu, arquitetura, anjo, oclusor) de 1255×878 | 3 camadas de 2080×1196: **room** (corredor, opaco), **chasers** (perseguidores, plano médio), **hero** (garota + árvore + lança-mísseis, 1º plano). Vidro em meia resolução |
| Enquadramento | crop central, âncora vertical 1,5 % | âncora **X 72 %** (em 4:3 preserva a estrela e a árvore; a esquerda fica sob o painel) e **Y 8 %** (preserva a boina em 21:9) |
| Paleta | obsidiana/ardósia/marfim/ouro frio | café `170D09`, ardósia quente `2E1C14`, creme `FFF3DE`, ouro de estrela `E9BF63`, bege `D4BE9E`, **vermelho de laço `C8283C`**, **verde pinheiro `3B7A47`**, luz `FFD98A` |
| Ornamentos | losangos | **estrela de 5 pontas** com halo; **guirlanda de luzes** que pisca (só com movimento permitido); **neve** leve no lugar da poeira |
| Botão Jogar | carmesim com borda rosada | vermelho de Natal com borda dourada |
| Demais botões | ardósia + ouro | café + ouro; foco com friso vermelho |
| Telas secundárias | widgets grafite | widgets café/ouro, hover com friso vermelho; lista em tom quente; guirlanda no painel |
| Conexão | painel grafite | painel café com friso vermelho/ouro |
| Texto miúdo | base de letras sumindo (E→F, L→I) | glifos com 1 px de margem: correção no `UiTypography` |

## Textos (PT-BR)

Mantidos: **THE END OF / FORBIDDEN**, REDE AKASHIC, FORBIDDEN SERIES / 1.7.10, JOGAR / SINGLEPLAYER / OPÇÕES / SAIR, mensagens reais do Forge, "Progresso da etapa ativa / Forge", FORGE 1.7.10.

| Lugar | Antes | Agora |
|---|---|---|
| Menu: lema | Há lugares que / o tempo não alcança. | Até nos corredores sem fim, / o Natal encontra caminho. |
| Menu: rótulo dos botões | O LIMIAR | EDIÇÃO DE NATAL |
| Menu: rodapé | THE END OF FORBIDDEN | BOAS FESTAS · NATAL 2026 |
| Menu: citação | O silêncio também observa. | Corra. O Natal não espera. |
| Loading: subtítulo | Antes do primeiro passo, o mundo desperta. | Acendam as luzes: o Natal chegou aos corredores. |
| Loading: caixa | SEQUÊNCIA DE INICIALIZAÇÃO | PREPARANDO A NOITE FELIZ |
| Loading: rodapé | O DESPERTAR / INICIALIZAÇÃO DO SANTUÁRIO | NOITE FELIZ / EDIÇÃO DE NATAL · REDE AKASHIC |
| Loading: citação | A pedra guarda. / A vida retorna. | Leve a árvore. / Não olhe para trás. |
| Loading sem total | O santuário desperta. | As luzes estão acendendo. |
| Transição / conexão | A TRAVESSIA | A TRAVESSIA DE NATAL |
| Telas secundárias | THE END OF FORBIDDEN | THE END OF FORBIDDEN · EDIÇÃO DE NATAL |

## Como regenerar

```bash
pip install opencv-python-headless numpy pillow "rembg[cpu]"   # rembg só para --segment
python tools/build_natal_layers.py --segment   # opcional: refaz as máscaras (BiRefNet-general-lite)
python tools/build_natal_layers.py             # camadas + fundo estático + evidências (determinístico)
python tools/build_natal_gui.py                # texturas das telas secundárias
python tools/preview/run_natal_preview.py      # amostras em build/preview-natal (sem Minecraft/GPU)
```

As máscaras ficam versionadas em `masks/`, então a fase de build não precisa de rede nem de modelo de IA.

## Amostras

`evidence/natal/preview/` — renderizadas pelo **próprio código** do pacote `render` (NativeVisuals, BackdropRenderer, HeroButton, SecondaryFrameRenderer, UiTypography) sobre um rasterizador OpenGL em software (`tools/preview/stubs`). Elas comprovam layout, textos e paleta, mas **não** são captura do jogo: `RawScope`, Angelica, drivers e o splash real do Forge não são exercitados.

## Limitações conhecidas

* No menu, o perseguidor de cabelo branco fica sob o vidro do painel de botões (decisão do dono: botões no mesmo lugar).
* O interior reconstruído do corredor (sob os personagens) é borrado de propósito: só aparece na faixa de até ~12 px revelada pelo movimento.
* Os perfis antigos `native/{classic,compact,hd,wide}/` e os atlas `native/serif.png`/`body.png` **não são carregados** pelo código desde a N07 e continuam com a arte antiga; não foram tocados.
