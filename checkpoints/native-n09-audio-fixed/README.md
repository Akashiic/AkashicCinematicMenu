# Correção de entrega e exclusividade da BGM — 0.5.2-alpha.1
Candidata compilada e testada em unidade. Execução desta versão no Minecraft ainda pendente.

## Causa observada na instalação diária
JAR instalado:0.5.0-alpha.1, SHA c9248a3060139e5334facd6186e9c09f6826af650508e6c20d1438bc52975b0d, sem a gravação.
Não existe config/akashiccinematic/bgm/menu.ogg naquela instalação. O latest.log confirma falta do recurso akashiccinematic:sounds/bgm/menu.ogg.
options.txt registra Master1.0 e Música0.0. Respeitar esse controle significa que nossa BGM também permanece muda.
Não há evidência suficiente para atribuir a trilha ouvida ao Et Futurum. O Aether registra music.menu; identificação de evento em testes anteriores mostrou música vanilla classificada como RECORDS.
O log FML tinha aproximadamente2.85GB; a varredura ampla iniciada foi abortada e substituída por leitura limitada aos128KiB finais.

## Candidata preparada
Arquivo: dist/AkashicCinematicMenu-0.5.2-alpha.1.jar
SHA-256: da5bb5579545e9716590547fbf80efbfc05771f47dba38a62999cfa9e4c0460a
11.510.805bytes. Inclui a gravação fornecida já convertida para Ogg/Vorbis em streaming, dentro do JAR.
SHA do áudio:6e91e747a99148084b854012c48ce93974beb6275dc4add2951989beff0ae4a6. Derivado do MP3 fornecido SHA1e7a442de0759fb7a9b605c3f70d0fecdb7c3e1da428796e41df9a717d668408.
A proteção reconhece MUSIC de outros mods e identificadores conhecidos minecraft/music.* e aether_legacy/music.*, mesmo se rotulados RECORDS.
Não silencia RECORDS genericamente. Exclui a BGM própria e preserva SFX. Atua somente no frontend autorizado fora de um mundo.
Música0% continua sendo mute intencional: não alteramos volume para fingir funcionamento. A faixa exige volume Música acima de0%.
Não é necessário extrair um OGG separado para essa candidata. Não instalar duas versões do mod simultaneamente.

## Testes executados
Core:48cenários/17489asserções. Núcleo de música:1557verificações. Lifecycle:18cenários/124asserções. Nova classificação:32verificações.
Compilação contra APIs reais passou. ZIP íntegro; recurso OGG embutido possui hash esperado e evento stream=true na categoria MUSIC.
Sem TTF/OTF/WOFF e sem mod de QA no JAR. A arte e o restante do menu partem da versão0.5.0 instalada, sem incorporar os experimentos de transição não finalizados.
Não foi iniciado Minecraft adicional: havia clientes Java abertos e somente aproximadamente1.28GiB de memória física livre.
Não alteramos options.txt, o modpack diário, os processos do usuário ou os candidatos anteriores.
Parecer: compilação/testes dirigidos aprovados; runtime e coexistência sonora desta versão permanecem NÃO EXECUTADOS.
