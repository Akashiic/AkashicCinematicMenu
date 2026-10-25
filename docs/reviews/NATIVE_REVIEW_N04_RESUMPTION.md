# Retomada N04 — integridade, instalação e estado atual
Data: 20/09/2026. Autorrevisão; não constitui nova homologação de runtime.
Versão ativa mantida: 0.2.0-alpha.4. Não promover native-n05.
JAR SHA-256: 3adfd2cc102eed60666d5594afc0a375e2a1fc7047ed409c7afc81c5969c190e.

## Trabalho desta retomada
Conferidos CURRENT_STATUS, checkpoint de recuperação, recibo de restauração e código do hook inicial.
Revalidados SHA-256 do JAR ativo, JAR restaurado em QA e JAR empacotado no ZIP de engenharia.
Conferidos 52 ativos esperados; ausência de classes de QA, código earlygl/N05 e arquivos de fonte no candidato.
O ZIP certificado tem SHA-256 d15f6dd938cc9a902f4870f3e766974af29c5324c00b1ad8dba469672b57db34.
Regressão recompilada em diretório separado: 48 cenários / 17489 asserções sem falhas; 15 verificações de conexão sem falhas.
Nenhum Minecraft iniciado nesta retomada. Não repetir o orquestrador bloqueado nem o experimento N05 por outro caminho.

## Explicação da tela inicial
Na N03, só o splash do Forge era coberto. N04 mostra a preparação após OpenGlHelper.initializeTextures e antes do progresso Forge.
Capturas reais do run07 foram novamente inspecionadas: frame-009.jpg mostra Preparando o ambiente; frame-028.jpg mostra Constructing Mods.
A comparação pontual existente é 7.479s (N03) versus 2.432s (N04) até a primeira arte customizada observada.
A amostragem era de 250ms mais custo de captura. Não é melhoria estatística de FPS nem redução equivalente no boot total.
O trecho inicial de ~2.4s anterior ao hook permanece descoberto. Primeiro pixel sem preto não aprovado.

## Instalação e diferenças atuais
QA contém exatamente o JAR N04, menu habilitado e splash habilitado.
A instalação de uso diário continua sem CinematicMenu; splash.enabled=true sozinho não instala a nossa interface.
A auditoria estrita foi interrompida ao encontrar diferenças em relação ao manifesto antigo; o resultado não foi ignorado.
Uma segunda auditoria classificada encontrou 20 hashes diferentes em 898 entradas: 18 configurações e 2 arquivos mcdata.
Os valores das 18 configurações inspecionadas coincidem com a cópia QA após remover comentários/espaços; falsepatternlib também teve ordem de propriedades escalar alterada.
Os 170 JARs de mods/coremods registrados no manifesto permanecem iguais; há um JAR adicional não presente na homologação.
Adição: mods/akashicindex-tablet-music-online-r2.jar, SHA-256 55f2012682d5c74b72cd4b43f44a63ddfe8405e4c1903b17ffe2c5a727b3dd7c.
A comparação é uma leitura do estado observado, não um snapshot atômico: a contagem de diferenças cresceu de 15 para 20 entre leituras.
Nenhuma dessas configurações foi sobrescrita para forçar igualdade. A causa externa das regravações não foi atribuída sem evidência.
A homologação existente continua válida apenas para a cópia e versões testadas; não transferir automaticamente ao pack diário com o JAR adicional.

## Evidências novas
qa-n02/evidence/resumption-installation-audit-1789931739114908500.json
qa-n02/evidence/resume-core-3i2dyqz1/result.json e logs de compilação/regressão/conexão.
Ferramentas reutilizáveis: verify_active_installations.py (estrita), verify_active_installations_classified.py (classificada) e retest_active_core_readonly.py.
As ferramentas não lançam o jogo, não mexem em contas nem alteram mods/configurações nas instalações.
Asserções de igualdade bruta não devem ser removidas de provas antigas; a auditoria classificada apresenta explicitamente as diferenças.

## Estado e próximo gate
N04 permanece o candidato de referência para boot/menu; dados de N05 não foram compilados ou incorporados.
Abertura após restauração continua não testada devido ao bloqueio anterior do orquestrador. Não alegar que houve novo boot agora.
Testes normais anteriores: 46 verificações e 6 ciclos de foco direto; fallback sob imagens ausentes: 12 verificações.
A melhoria do método de foco não foi alteração de produto: menu mantém o mesmo código de foco da N03.
General release=false; first pixel coverage=false; exact daily pack runtime approved=false.
Restam o intervalo anterior à camada gráfica e o teste controlado da composição atual do pack.
