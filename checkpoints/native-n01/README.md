# Akashic Cinematic Menu — N01 / 0.2.0-alpha.1
Candidato de engenharia. NÃO instalar na instância jogável antes de homologação isolada.

## O que existe
Menu Santuário R04 e inicialização O Despertar S01.2 portados para renderização Java/OpenGL.
A arte e as posições vêm dos HTMLs aprovados. Não há navegador, servidor web ou CDN em runtime.
Jogar: br2.xmxcloud.net:10511; Singleplayer; Opções; Sair com confirmação.
O anjo permanece 2D. O painel usa blur pré-renderizado, não blur dinâmico por framebuffer.
Camada de cenário separada com parallax discreto, hover, foco e transições nativas.
Progresso do splash amostra a etapa ativa de ProgressManager. Não inventa percentuais globais ou ETA.
O splice preserva o loop, mutex, pausa, retomada e troca de buffers do Forge; ainda não homologado no pack.

## Validação executada
Compilação com APIs reais existentes, bytecode Java 8, sem stubs nem bibliotecas empacotadas.
48 cenários do núcleo / 17489 asserções: passaram.
Probe gráfico executado no PC com Java 8 e Intel UHD Graphics: 511 verificações passaram.
O probe não é Minecraft e usa dados de ensaio explícitos para o carregamento.
Capturas reais em evidence/native-probe; quatro perfis: 1366x768, 854x480, 1024x768 e 2048x864.
O contador de texturas próprias voltou a zero; isso não é uma medição completa de VRAM.
Corrigidos alinhamento do atlas, legenda de percentual e truncamento de 100% em 4:3.

## Bloqueios antes da liberação
Teste do splice ASM sobre bytecode não executado: a camada de execução bloqueou a chamada por não determinar o status de segurança. Não houve tentativa de contornar o bloqueio.
Boot real com Forge/Angelica/DummyCore, pausa, falha, retorno, recarga e conexão ao servidor: NÃO EXECUTADOS.
Teste gráfico isolado não demonstra compatibilidade do stack inteiro. Android não homologado.
Os tempos do probe são custo de desenho com glFinish no contexto isolado, não FPS do modpack.

## Executar o build / teste visual
python tools/build_native.py
python tools/test_core.py
python tools/run_probe.py

O build usa o JDK e caches auditados desta máquina; --classpath-json permite indicar dependências reais em outro ambiente.
O probe abre uma janela local curta e a fecha; não carrega Minecraft nem estabelece conexão.
Exportar novamente arte exige o HTML S01 aprovado e o Chrome/Playwright no ambiente de construção, não no jogador.
Os atlas são PNGs; nenhum arquivo TTF/OTF foi incluído. Não redistribuir ferramentas/dependências de terceiros por cópia dos caches.

## Desligamento para homologação futura
-Dakashic.cinematic.disabled=true desabilita menu e hooks de splash.
-Dakashic.cinematic.disableSplash=true mantém somente o splash original.
-Dakashic.cinematic.reduceMotion=true desliga a animação do indicador indeterminado no splash.
No menu: F7 alterna movimento reduzido; F8 tenta retornar ao menu anterior e desativa o hook na sessão.
Essas rotas de recuperação ainda precisam ser verificadas dentro do pack, não somente no código.

## Referências e aprovação
S01 aprovado pelo usuário nesta retomada; R04 já aprovado. Não redesenhar nem substituir por cenário procedural.
Aprovação visual não aprova o novo mod nativo. N01 é apto à inspeção do renderer; bloqueado para distribuição.
CHECKPOINT.json original, alpha.1 e navigation-r04 foram preservados. Nenhum candidato foi instalado no pack.
Ler CHECKPOINT_N01.json e evidence/final-audit.json para identidade, limites e resultados desta compilação.
