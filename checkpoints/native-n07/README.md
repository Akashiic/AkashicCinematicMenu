# Akashic Cinematic Menu — N07 / 0.4.0-alpha.3
Candidata de fidelidade, geometria responsiva e convergência com o launcher mobile.
Aprovação visual e sonora final permanece com o usuário. Consultar CHECKPOINT_N07.json para o parecer técnico consolidado.

## Arquivo do mod
`dist/AkashicCinematicMenu-0.4.0-alpha.3.jar`
SHA-256: `14b366d1ce85be558d11fb4bf0ed81f6c043de1fc39bf065856e8477716cf8ac`
7.238.260 bytes; 56 classes Java 8; APIs reais locais, sem stubs e sem driver de QA no JAR.
Não instalar junto com outra versão do CinematicMenu. O trabalho automatizado modificou somente a cópia de QA.

## Alterações
- Um único enquadramento cover do cenário; controles calculados por pixels do framebuffer, painel esquerdo27% ancorado emx0.
- Títulos, textos, molduras, botões e progresso separados da imagem; nove atlas por tamanho, sem arquivos de fonte redistribuídos.
- Paleta fria: obsidiana azulada, ardósia, marfim, ouro apagado e celeste; sem verde dominante na UI.
- Cena e texturas mantidas entre redimensionamentos, sem selecionar/destruir perfis fixos a cada troca de tamanho.
- Cinco SFX de UI, mute master e prevenção de rajadas; integração SoundHandler existente, não outro motor de áudio.
- Aviso conhecido InventoryTweaks/BogoSorter direcionado ao log; avisos desconhecidos preservados.
- Conexão e desconexão com cenário e painel da marca, preservando instâncias, botões, textos e rede nativos.
- Jogar permanece em br2.xmxcloud.net:10511; Singleplayer, Opções e Sair preservados.

## Limites explícitos
A arte-base de cenário tem1207x830. UI mais nítida não cria detalhes4K na imagem original.
Anjo2D; desfoque pré-processado com o mesmo mapeamento do cenário, não blur dinâmico de framebuffer.
A recuperação após pausa não prova renderização contínua durante parada total da JVM/driver ou uma janela fantasma do Windows.
Options e seleção de mundos não foram redesenhados nesta candidata. Login autenticado e jogabilidade não fazem parte dos testes dirigidos.
As imagens de erro de conexão usadas no teste preservam a identificação TESTE LOCAL; não são mensagens reais de indisponibilidade do servidor.

## Build e verificações reproduzíveis
`python tools/test_core.py`
`java -ea -cp build/core-tests br.com.redeakashic.cinematic.core.PixelLayoutTests`
`java -ea -cp build/core-tests br.com.redeakashic.cinematic.core.ConnectionWatchTests`
`java -ea -cp build/core-tests br.com.redeakashic.cinematic.core.UiFeaturesTests`
`python tools/build_native.py`
`python tools/audit_event_visibility.py`
`python tools/audit_fidelity_resources.py`
O build usa caches de dependências da máquina auditada; não os redistribui no pacote. Scripts históricos de migração não devem ser reexecutados.

## Evidência e continuidade
`docs/DESIGN_AND_DIAGNOSIS.md` explica causa do gap, fonte da arte, método tipográfico, paleta e limites de recuperação.
`evidence/fidelity-build-gates.json` é resultado de compilação, anterior à homologação de runtime.
`evidence/resource-fidelity-audit.json` verifica2016 posições de glyphs e os5OGGs preservados.
`../../qa-n02/evidence/n07final/` contém testes do cliente completo, capturas e a primeira tentativa de recuperação.
`../../qa-n02/evidence/n07recovery/` contém a repetição dirigida após corrigir a coleta de evidência GDI.
Os processos e hashes devem ser revalidados antes de retomar. Uma suíte interrompida não equivale a uma etapa aprovada.
Falhas anteriores e artefatos de versões rejeitadas são mantidos no workspace, não são a distribuição recomendada.

Resultado final consolidado em docs/PARECER_N07.md; versão testada em ../../qa-n02/evidence/n07release2. O histórico anterior permanece separado.
