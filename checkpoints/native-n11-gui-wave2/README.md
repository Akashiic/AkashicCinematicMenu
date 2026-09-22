# N11 — GUI Wave 2 / 0.7.0-alpha.4

Candidata full-pack testada para ampliar a linguagem visual do Forbidden além do menu principal.
A aprovação visual final continua com o usuário; não é liberação geral automática.

## JAR
dist/AkashicCinematicMenu-0.7.0-alpha.4.jar
SHA-256: 7dbf5177c4e26edea3c0d5caceafac6d4ec013eee6d543d2dbe80066d4c0fb4a
16.508.324 bytes; 81 classes Java 8.

## Cobertura
Options, Select World, Create World, Language, Resource Packs, Controls, Video, Sound,
Chat, Snooper, Flat World, Presets, Rename World, GuiYesNo/ConfirmOpenLink.
RTG WorldTypeMessageGUI e Reese Sodium Video também entram na família auditada.
GUIs desconhecidas de outros mods permanecem com o renderer original.

## Comportamento
Background, painel, lista e widgets recebem a identidade visual.
Ações vanilla/modded, sliders, scroll, seleção, resource packs e gerenciamento de mundo não são reimplementados.
O botão Sair do menu principal encerra diretamente o Minecraft.
Confirmações destrutivas — como apagar mundo — continuam nativas e agora podem receber apenas a pele visual.

## Runtime
A alpha.4 executou no modpack completo em QA e encerrou com exit=0.
Options, Singleplayer, Create World/RTG, Language, Resource Packs, Controls, Reese Video,
Sound, Chat, Snooper, Flat, Presets e uma confirmação vanilla foram capturados em 1920×1001.
Nenhum mundo foi aberto/criado e nenhuma conexão com servidor foi iniciada.

A BGM permaneceu numa única sessão pelos submenus testados.
A gravação e o controlador principal de BGM permanecem idênticos à N10;
somente a allow-list de telas de frontend foi ampliada.

O primeiro gate de QUIT foi executado com a janela inativa e foi corretamente cancelado pela política do menu.
A repetição com foreground real encerrou o processo com exit=0 e sem GuiYesNo.

## Evidência
CHECKPOINT_N11.json — estado consolidado.
docs/PARECER_N11.md — escopo, correções de teste e limites.
docs/Revisao-N11.html — galeria das capturas reais do modpack.
evidence/final-audit.json — recibo técnico completo.
evidence/runtime-review/ — imagens da execução.
evidence/wave2-alpha4-gates.json — gates independentes.
