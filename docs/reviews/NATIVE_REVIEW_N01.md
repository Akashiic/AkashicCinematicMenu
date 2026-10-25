# N01 — início da integração nativa — 20/09/2026
Contrato: QUALITY_CONTRACT.md. Autorrevisão do assistente, sem auditor independente.
O usuário aprovou o menu R04 e agora também a inicialização O Despertar S01.2, autorizando a produção nativa.
Não redesenhar: preservar Santuário/painel B e O Despertar. Anjo 2D continua sendo o ativo aprovado.

## Workspace isolado
candidates/native-n01 — versão 0.2.0-alpha.1.
JAR: candidates/native-n01/dist/AkashicCinematicMenu-0.2.0-alpha.1.jar.
SHA-256: 7e5c5b23932eff2011d464cfc4e5d6c352f20d750e6ee1a0a01108baf121731a
37 classes Java 8; 5764672 bytes. Código e PNG/JPEG/propriedades; sem navegador e sem fontes TTF/OTF.
52 ativos derivados dos HTMLs aprovados foram gerados no PC com navegador offline, apenas na etapa de construção.
Jogar mantém br2.xmxcloud.net:10511; Singleplayer; Opções; Sair. Nenhuma conexão iniciada nos testes.
Blur pré-renderizado; cenário separado para parallax; progresso e ações executados em Java.

## Evidência observada
Compilação contra APIs locais reais; núcleo 48 cenários / 17489 asserções sem falhas.
Probe OpenGL real no PC: Intel UHD Graphics, OpenGL 4.6.0, JDK8; 511 verificações sem falhas.
240 quadros com verificações repetidas de estado/erros e quatro perfis de arte/hitbox/liberação.
Tempos quentes com glFinish: menu mediana 2.0456 ms / p95 3.6606 ms; startup 2.2534 / 2.721 ms.
Não equivalem a FPS no modpack, não medem o custo de carregamento frio nem homologam Angelica.
Inspeção detectou e corrigiu truncamento de 100% em 4:3 e legibilidade do atlas pequeno. Captura corrigida conferida.

## Limite bloqueante
Ensaio do transformer: NÃO EXECUTADO, chamada bloqueada pela camada da ferramenta ao não determinar segurança; sem contorno.
O desenho nativo foi executado, mas Minecraft/Forge modificado não foi iniciado nesta revisão.
Hook de overlay foi implementado para preservar loop/mutex/pause/resume/Display.update do Forge; integração efetiva ainda não comprovada.
Inicialização real, coexistência com mods gráficos, falha/retorno, F8 e conexão: homologação isolada pendente.
Nenhum JAR instalado no pack. Root CHECKPOINT.json e alpha.1/alpha.2 anteriores foram preservados.
Parecer: renderer aprovado para continuar inspeção; N01 NÃO liberado para produção ou instalação na instância jogável.
