# Parecer de autorrevisão — Santuário R03.1

**Candidato:** ACM-SANTUARIO-R03.1  
**Data:** 20/09/2026  
**Revisor:** assistente, em autorrevisão. Não houve auditor independente.  
**Direção de composição:** Layout 1 / Santuário, escolhido pelo Akashic na conversa.  
**Material do painel:** pendente de aprovação final do Akashic. B é padrão de visualização, não decisão em nome do usuário.  
**Parecer:** apto à avaliação visual do painel no ambiente testado; não homologado para o Minecraft nem para produção.

## Identidade
`Forbidden-Santuario-R03.html`: 702,189 bytes.  
SHA-256: `0646661bbea90e692e85ae9533b43da546126e1dcafca7240e5999bce6a714ac`.  
Rebuild independente produziu o mesmo hash.
Baseline R02 preservado: `32fdf021084d1b826a03cfb1a37d57d4a3710a00748ec0643202baa69666fd7e`.

## Alteração entregue
Três materiais para a MESMA composição: A, blur 10px/tinta-base 36%; B, 16px/46%; C, 24px/58%.
Tinta-base é a camada RGBA principal. Há gradientes adicionais de leitura; não se promete uma transparência uniforme nesses percentuais.
Largura desktop de 27%, título, botões e enquadramento do cenário preservados.
A textura industrial que já fazia parte da R02 agora é um plano de cenário atrás do painel. Não se gerou nova imagem.
O blur afeta uma superfície separada: texto e botões não são filhos de um elemento com `filter:blur()`.
O botão Antes/Depois recupera a superfície anterior e retorna ao material selecionado sem deslocar navegação.
As teclas 1/2/3 escolhem materiais, não os três layouts anteriores.
Preferência de reduzir transparência, controle manual e alternativa opaca quando CSS.supports relata indisponibilidade foram implementados.

## Inspeção e correções
A primeira inspeção identificou perda de contraste nos textos secundários do material A; sua luminância foi aumentada sem alterar fonte/tamanho/posição.
O primeiro comparador de Antes detectou influência da nova imagem sob a camada 96% opaca; o novo plano foi ocultado no modo de referência para reproduzir o painel anterior.
Uma captura inicial ocorreu sem a imagem de fundo decodificada. A imagem crítica passou a usar decodificação síncrona; o comparador também aguarda decode explícito nos dois documentos.
Um erro no teste de espera usava uma expressão string incompatível com a política CSP. O teste foi corrigido para função, sem liberar unsafe-eval no produto.
Não se herdaram os testes R02 como validação deste arquivo. A suíte completa foi reexecutada após as correções.

## Execução final
**243 verificações aprovadas; 0 falhas na execução final.**
O número inclui repetições por material e resolução; não representa tantos defeitos independentes nem comprova ausência de bugs.
21 combinações geométricas: A/B/C em 1920×1080, 1366×768, 854×480, 2560×1080, 1024×768, 390×844 e 320×568.
No retrato há adaptação para painel inferior de largura integral; telas baixas podem rolar verticalmente.

| Critério | Evidência |
|---|---|
| Botões, textos, rodapé, proporção e ausência de overflow horizontal | Passou nas 21 combinações |
| Geometria frente à R02, 1366×768 | Sidebar, marca e cinco botões idênticos |
| Comparação raster da região de cenário direita | Diferença média 0.0 no quadro fixo testado |
| Painel Antes frente ao R02 | Diferença média 0.0 no quadro fixo testado |
| Blur real no framebuffer do navegador | Remover o efeito altera os pixels nas três variantes; não só metadados CSS |
| Navegação, teclado, modal, foco e Escape | Passou |
| Cancelamento e clique repetido | Passou |
| Movimento reduzido e perda de foco | Passou |
| Preferência de reduzir transparência | Passou em emulação de mídia nativa Chromium |
| Ausência de suporte ao blur | Ramo de fallback passou sob injeção controlada de CSS.supports=false; não é teste de navegador antigo |
| Falha da imagem principal ou do fundo lateral | Navegação preservada |
| Consoles e rede | Sem exceções inesperadas e sem requisições HTTP/HTTPS |
| Escolha de material | Não registra aprovação do usuário |

## Contraste amostrado
Mínimo entre os cinco rótulos principais, nos 12 quadros de A/B/C em 1920×1080, 1366×768, 854×480 e 390×844: **7.45:1**.
O mínimo entre os textos amostrados, excluindo o logotipo grande: **4.67:1**.
Método: cor CSS computada sobre pixels de captura do fundo, ocultando temporariamente texto e sombra, sem modificar layout; mínimo de cada caixa de texto.
Não é auditoria WCAG completa, nem mede cada frame animado, sistema operacional, monitor ou combinação de configurações.

## Tempos observados (milissegundos)
Ambiente: Chromium 144.0.7559.96, Linux headless, viewport 1366×768, Playwright. Três amostras independentes curtas.

| Material | Mediana do callback JS | Mediana entre frames | P95 entre frames | Máximo entre frames |
|---|---:|---:|---:|---:|
| A | 0.1 | 16.7 | 33.4 | 166.7 |
| B | 0.1 | 16.7 | 33.5 | 166.6 |
| C | 0.1 | 16.7 | 50 | 149.9 |

O callback não inclui todo o custo do compositor/GPU. Há frames longos nas amostras: não se declara 60 FPS estáveis, superioridade de um preset, nem desempenho validado no PC do usuário.
O orçamento de desempenho e a portabilidade do blur para a implementação nativa seguem pendentes.

## Limites e continuidade
O HTML integral foi executado no navegador com a rede offline. file:// foi bloqueado pela política do navegador deste ambiente; a abertura por duplo clique no PC do usuário não foi homologada.
Firefox e Safari não foram executados. Não foi feita publicação em site nem chamada a servidor.
O anjo permanece na arte 2D aprovada. Não há novo modelo 3D, integração Forge, alteração de startup, JAR ou instância jogável.
A alpha Java anterior mantém seu checkpoint; este documento não promove sua homologação.
O próximo parecer visual é do Akashic sobre A/B/C. A seleção no HTML não é uma gravação de aceite.
