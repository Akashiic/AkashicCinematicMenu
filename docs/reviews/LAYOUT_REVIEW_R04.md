# Santuário R04 — navegação e destino direto
Data: 2026-09-20. Referência: QUALITY_CONTRACT.md.
O usuário aprovou o visual entregue na R03: "Ficou muito bom, vamos deixar assim."
Manter Layout 1/Santuário, painel translúcido com blur e cenário; sem redesign.
B permanece como padrão já entregue. O usuário não identificou separadamente A, B ou C.

## Alterações solicitadas e implementadas
01 Jogar -> conexão direta para br2.xmxcloud.net:10511, sem lista de servidores.
02 Singleplayer -> seleção de mundos locais.
03 Opções -> opções.
04 Sair -> confirmação de saída na implementação nativa.
Removido o botão Mods e sua rota; Multiplayer foi substituído por Singleplayer.
No HTML as rotas são demonstrações explícitas, sem conexão, acesso a saves ou status inventado.

## HTML entregue na conversa
Forbidden-Santuario-R04.html: 702244 bytes.
SHA-256: 61be236468878e24128038fbf947695218787858d7feca9c39399aa6367651d5
272 verificações de regressão e 20 verificações de dimensões dos painéis passaram no Chromium do ambiente.
Arte, painel B, logo e rodapé preservados em comparação fixa fora da navegação alterada.
HTML/fonte/capturas ficam na conversa; não foram instalados no modpack nem copiados para este PC.

## Candidato nativo isolado, não homologado
candidates/navigation-r04/dist/AkashicCinematicMenu-0.1.0-alpha.2.jar
SHA-256: c8930796eb1e3b1ec9ef4799eec868db3a3b7ee1809033abdd5b9e6c2c54e7a2
48 cenários, 17489 asserções de núcleo sem falhas; compilação contra APIs reais, sem stubs.
Rotas verificadas no bytecode: setupServerList antes de FMLClientHandler.connectToServer.
Host/porta definidos em ServerTarget; não usar URL HTTP nem substituir a porta por 25565.
Não altera handshake, autenticação, lista de servidores salva, mundo ou instalação jogável.
A arte da alpha Java ainda é a cena procedural anterior, não o layout HTML aprovado.
Conexão real, retorno após desconexão e runtime no pack: NÃO EXECUTADOS nesta revisão.
Fonte da alpha original e seu JAR permanecem intactos; CHECKPOINT.json histórico preservado.
Fonte do candidato: 5062ce13b47a80fe426fee7b74455c9893abbe3690d0570563e2b095844fdc40.
Evidências do candidato em candidates/navigation-r04/evidence/.
Autorrevisão do assistente, sem revisor independente. Aprovação visual anterior não homologa o JAR.

## Próximo passo
Preservar a composição aprovada e validar a navegação nativa em instância isolada antes de instalar.
Não confundir a aprovação de R03 com uma aprovação de runtime ou da nova revisão ainda não vista.
