# Regra cardinal de qualidade — Akashic Cinematic Menu
ID: ACM-QUALITY-2026-09-20-v1
Status: norma de trabalho solicitada pelo usuário nesta conversa.
Escopo: tela de inicialização e menu principal do Forbidden Series 1.7.10; HTML como laboratório visual.

## 1. Responsabilidade e prioridade
Qualidade técnica, fidelidade visual, estabilidade e evidência prevalecem sobre tempo de resposta.
Não reduzir o objetivo visual, omitir verificações ou promover uma etapa apenas para entregar mais rápido.
A liderança técnica deve revisar criticamente o próprio trabalho, rejeitar entregas insuficientes e registrar bloqueadores.
Não prometer ausência absoluta de bugs: registrar cobertura, limites, riscos e resultados efetivamente observados.
O usuário não deve ser o primeiro filtro de defeitos que uma inspeção local poderia detectar.

## 2. Estado e referência preservada
Referência visual aprovada nesta conversa: concept da catedral brutalista com navegação à esquerda e anjo à direita.
Arquivo da concept nesta conversa: catedral_proibida_menu_do_fim.png; não pressupor cópia no PC.
Referência arquitetônica fornecida: 04_passagem_industrial_pontes.png; não pressupor cópia no PC.
Navegação desktop: aproximadamente 25–30% da largura, sem texto cortado nem botões sobrepostos.
Cenário: arquitetura monumental, cânions verticais, pontes, cascatas e atmosfera úmida com profundidade.
Anjo: estátua misteriosa sem rosto à direita, asas longas, halo circular e núcleo branco/celeste.
Revisão cromática solicitada: acrescentar verde eco-brutalista; preservar concreto escuro e identidade da composição.
Último HTML colado na conversa, com estátua simplificada em Three.js: REPROVADO VISUALMENTE pelo usuário.
Essa reprovação não revoga automaticamente os testes do núcleo Java nem comprova defeito no JAR anterior.
CHECKPOINT.json da alpha é histórico técnico; este contrato não equivale à homologação de runtime.

## 3. Entrega não é aprovação
Código escrito, código compilado, navegador renderizado, visual aprovado e runtime homologado são estados distintos.
Uma versão nova é candidata até passar pelos critérios aplicáveis; não herda testes de outro arquivo ou versão.
Placeholders podem apoiar testes internos, mas não podem ser apresentados como arte final equivalente à referência.

## 4. Fluxo obrigatório e parecer
Critérios -> implementação -> revisão de código/arquitetura -> inspeção visual -> testes -> parecer.
Falha ou evidência insuficiente -> corrigir/investigar -> reexecutar os testes afetados e a regressão pertinente.
Implementação e aprovação são passagens separadas, com checklist explícito.
Quando o mesmo assistente fizer ambas, identificar a revisão como autorrevisão, não como auditoria independente.
Não inventar equipe, segundo aprovador, testes executados, medições ou aprovação do usuário.
Parecer técnico por candidato: APROVADO PARA A ETAPA, REPROVADO ou BLOQUEADO POR FALTA DE EVIDÊNCIA.
Cada critério deve registrar PASSOU, FALHOU ou NÃO EXECUTADO, com referência à evidência.
O parecer deve incluir versão/hash, ambiente, escopo validado, defeitos, limitações e próximo requisito.
Aprovação técnica não substitui aprovação visual final do usuário, e vice-versa.

## 5. Critérios para o próximo HTML
Comparar capturas do navegador com a concept aprovada, incluindo proporção, silhueta, profundidade e iluminação.
Não aprovar apenas porque há WebGL, uma figura com asas, efeitos verdes ou animação.
A estátua precisa cumprir o objetivo artístico; geometria primitiva demonstrativa não é substituto automaticamente aceito.
Revisar vegetação, musgo e materiais para comunicar eco-brutalismo, não somente aplicar uma tonalidade verde.
Inspecionar composição parada antes de adicionar efeitos para disfarçar uma composição inadequada.
Testar navegação, foco visível, teclado, cliques repetidos, cancelamento, redimensionamento e movimento reduzido.
Verificar erros de console, carregamento dos assets, perda de foco e falha de WebGL/contexto quando aplicável.
Inspecionar pelo menos 1920x1080, 1366x768 e 854x480; registrar adaptações para outras proporções.
Medir desempenho no ambiente testado e separar custo gráfico, estabilidade temporal e carregamento.
Sem execução no navegador e inspeção das capturas, não aprovar o HTML como visualmente validado.
Entregar arquivos utilizáveis e relatório; código colado sem execução não é evidência de funcionamento.
Não apresentar botões simulados como integração ao Minecraft nem inventar status online ou progresso real.
Dependências externas, versões, licenças, modo de execução e necessidade de internet devem estar explícitos.
Resultados de browser não homologam o mod Java; runtime no pack continua sendo uma etapa própria.

## 6. Continuidade e segurança operacional
Ler este contrato e o último checkpoint antes de retomar; preservar a referência aprovada e os resultados anteriores.
Não alterar a instância jogável para compensar uma falha do protótipo; manter homologação isolada e rollback verificável.
Não expandir o escopo para inventário, HUD, launcher Android ou outras GUIs sem solicitação.
Em falha de transporte: não repetir cegamente; reduzir operação e revalidar o último estado confirmado.
Não considerar escrita, build, instalação, exclusão ou commit concluído sem verificar o resultado.
Não iniciar trabalho em segundo plano nem alegar monitoramento sem uma execução efetiva e identificável.

## 7. Registro desta revisão
O usuário rejeitou o último HTML; o layout conceitual anterior permanece como referência de composição.
Esta revisão registra governança e reprovação; não gera nova versão do HTML nem nova homologação do JAR.
Próxima entrega visual deve reconstruir o HTML conforme a referência e passar por inspeção antes de ser oferecida como candidata aprovada tecnicamente.
