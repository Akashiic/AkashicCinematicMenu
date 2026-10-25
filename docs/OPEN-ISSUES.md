# Pendências, riscos e questões em aberto (a partir da 0.7.0-alpha.4)

Formato: **ID · gravidade · estado**. "Evidência" aponta para o que prova o item; "Próximo passo" é sugestão, não decisão.

| ID | Item | Grav. | Estado |
|---|---|---|---|
| OI-01 | `RawScope` × Angelica (blocos brancos) | Alta | Aberto — correção no mod inexistente |
| OI-02 | Intervalo inicial (~2,4 s) sem arte | Média | Aberto |
| OI-03 | `exit=1` inexplicado em `n07release` | Média | Aberto — causa nunca estabelecida |
| OI-04 | Latência de minimizar/foco (8,5 s no N03) | Média | Aberto — sem mudança de produto |
| OI-05 | Aprovação visual/sonora do dono (N10, N11) | Alta | Pendente do dono |
| OI-06 | N10 nunca rodou no modpack completo | Média | Coberta indiretamente pela N11 (herda), mas sem aceite próprio |
| OI-07 | Música: direitos da faixa e caminho "sem faixa" nas versões 0.5.2+ | Alta (jurídico) | Decisão do dono |
| OI-08 | Licença do repositório | Média | Não definida |
| OI-09 | Build depende de caminhos da máquina do autor | Média | Aberto |
| OI-10 | Lacuna: conversa do ChatGPT (R02→N11) fora do acervo | Info | Documentada |
| OI-11 | HTMLs R04 e S01 só existiam na conversa | Info | Hashes preservados, arquivos ausentes |
| OI-12 | `CURRENT_STATUS.json` defasado | Baixa | Corrigir ao próximo checkpoint |
| OI-13 | Nunca testado: mundo aberto, servidor real, login autenticado, Android | Média | Aberto |
| OI-14 | Pack diário divergente do manifesto homologado | Info | Registrado, não corrigido |
| OI-15 | Angelica 2.2.37: splash cinemático sumiu e o cliente crashou | Alta | Causa externa (NecroTempus); corrigida na ThePatcher 1.1.0-beta.39 — aceite no pack pendente |
| OI-16 | Sem foco, o fade do boot para o menu fica parado no último quadro do splash | Baixa | Aberto — decisão de design do dono |

---

### OI-01 — `RawScope` × Angelica (blocos brancos)
* **Evidência** (ThePatcher beta.10, 23/09): o `RawScope` foi carregado às 17:14:50 pelo cliente real e o LaunchWrapper registrou **2.559 B antes / 2.646 B depois** do `AngelicaRedirectorTransformer` ⇒ o Angelica 2.2.18 **está** reescrevendo as chamadas GL do menu. O `RawScope` usa `glPushAttrib(GL_ALL_ATTRIB_BITS)`, desabilita texturas em todas as unidades e confia em `glPopAttrib()`; o GLSM do Angelica mapeia essas chamadas. Hipótese: `GL_TEXTURE_2D` fica dessincronizado ⇒ geometria válida renderiza branca.
* **Mitigação existente** (externa): *state fence* da ThePatcher (`-Dakashic.cinematicAngelicaStateFence.disable=true` o desliga). Candidato técnico PASS; **aceite em GPU real pendente**.
* **Por que não é trivial:** a N01 falhava no contexto core do Angelica quando o pacote `render` estava **excluído** dos transformers; a N02 removeu a exclusão. Logo, "excluir de volta" não é solução gratuita.
* **Próximo passo:** reproduzir os blocos brancos com a N11 pura (sem a ThePatcher) e com o fence; capturar `glGetBoolean(GL_TEXTURE_2D)` por unidade antes/depois do `RawScope`; avaliar restaurar explicitamente o estado usado (em vez de `PushAttrib` global) por meio de chamadas compatíveis com o GLSM; adicionar *probe* que rode o `RawScope` sob o redirecionador do Angelica.

### OI-02 — Intervalo inicial sem arte
N04 reduziu a 1ª arte visível de 7,479 s para 2,432 s (medição pontual, amostragem 250 ms). Resta ~2,4 s **antes** de `OpenGlHelper.initializeTextures`. A tentativa N05 (pipeline programável, antes do OpenGlHelper) foi interrompida por bloqueio da ferramenta e **não** deve ser promovida. "Primeiro pixel sem preto" **não** está aprovado.

### OI-03 — `exit=1` em `n07release`
A alpha.3 da N07 encerrou cedo, sem `hs_err` nem stacktrace fatal, antes do menu. A reexecução `n07release2` (sem manipular a janela durante o boot) passou 34+10+20 verificações, mas **isso não explica** o encerramento anterior. Nunca foi atribuído ao usuário nem ao código.

### OI-04 — Foco/minimização
N03: >12 s numa tentativa e 8,501 s na repetição para observar `active=false`. N04/N07 mediram recuperação em ≤ 409 ms agindo direto no HWND, mas **o código de foco do menu não mudou** — a diferença é de método de teste. Origem (mod, driver gráfico, SO ou automação) não isolada.

### OI-05 — Aprovação do dono
Pendentes: paleta/enquadramento (N07), SFX cerimoniais, CTA carmesim, parallax (N10), Wave 2 (N11). Nenhum agente deve marcar como aprovado.

### OI-07 — Música de terceiros (mantida)
* A faixa é *Fluxxwave (Slowed + Reverb)*; o registro antigo do projeto pedia não baixar/ripar nem substituir. **Decisão do dono em 24/10/2026: a faixa fica** no repositório (`src/main/resources/assets/akashiccinematic/sounds/bgm/menu.ogg`, SHA-256 `6e91e747…`) e nos JARs oficiais ≥ 0.5.2, porque **outro contribuinte obteve a permissão**.
* Pendente (documentação, não bloqueia): registrar em `docs/` **quem** obteve a permissão, **de quem**, o **escopo** (redistribuição pública? licença?) e anexar a prova. Este repositório é público: se o escopo da permissão não cobrir redistribuição pública, o dono deve ajustar.
* Observação histórica: durante a montagem deste repositório a faixa foi inicialmente excluída e os Releases foram publicados `-nobgm`; isso foi revertido por esta decisão.
* Não verificado: JAR 0.5.2+ **sem** a faixa em runtime.

### OI-08 — Licença
Sem arquivo `LICENSE`: por padrão, todos os direitos reservados, e terceiros **não** podem legalmente reutilizar o código. O dono deve escolher (ex.: MIT/Apache-2.0 para o código; arte e áudio podem ter licença própria). O assistente que montou o repositório **não** escolheu por ele.

### OI-09 — Build
Dependências SRG (RFG) e `launchwrapper` vêm de pastas locais (`CubicChunks-Crucible`, `.gradle`, `ThePatcher/lib`). Falta um build público (Gradle/RFG) que baixe tudo por coordenadas Maven e rode os gates em CI. Hoje o CI público só roda o **núcleo puro**. **O workflow está em `docs/ci/core-tests.yml`, ainda inativo**: o push inicial usou um token sem o escopo `workflow`, e o GitHub recusou criar `.github/workflows/`. Para ativar: `gh auth refresh -s workflow` e mover o arquivo para `.github/workflows/core-tests.yml`.

### OI-13 — Cobertura nunca exercitada
Mundo aberto/criado/apagado, servidor `br2.xmxcloud.net:10511` em conexão real e retorno após desconexão real, login autenticado, jogabilidade prolongada, *stress*, outros drivers/GPUs, Android (o launcher mobile entrega a apresentação ao mod, mas isso é outro repositório/ciclo).

### OI-14 — Pack diário
Observado: JAR extra `mods/akashicindex-tablet-music-online-r2.jar` (`55f26820…`), 20 de 898 entradas do manifesto com hash diferente (18 configs + 2 mcdata) e a pasta diária contendo, em momentos distintos, N07 alpha.3, 0.5.0-alpha.1, 0.5.2-alpha.1 e 0.6.0-alpha.3. Autoria/momento das cópias não foi atribuído.

### OI-15 — Angelica 2.2.37 (25/10/2026)
* **Sintoma no pack:** ao atualizar o Angelica para a 2.2.37, o splash cinemático não apareceu e o cliente crashou com `NoClassDefFoundError: com/gtnewhorizons/angelica/client/font/BatchingFontRenderer`.
* **Causa:** o Angelica 2.2.24+ trocou `ResourceLocation` por id de textura GL na API de fontes; o mixin obrigatório do NecroTempus 2.0.0/2.1.0 deixou de encaixar, o `BatchingFontRenderer` não carrega e a thread de splash do Forge morre no construtor do `SplashFontRenderer`, antes do primeiro `SplashHooks.frame()`.
* **Este mod:** testado num cliente real *headless* A/B (Angelica 2.2.18 × 2.2.37): splash e menu iguais, nenhum erro GL, `splash.properties` intacto. **Nenhuma mudança de código necessária.** Evidência: `evidence/angelica-2.2.37-20261025/`.
* **Correção:** ThePatcher **1.1.0-beta.39** (a beta.38 é inerte no cliente real por causa do Mixingasm). Correção-fonte para o NecroTempus preparada no repositório da ThePatcher.
* **`splash.properties`:** o Forge grava `enabled=false` quando a thread de splash morre e o carregamento termina; se isso aconteceu no pack, volte para `enabled=true`.
* **Próximo passo:** aceite no pack completo (Windows/Intel) com beta.39.

### OI-16 — Fade do boot congela sem foco
Com a janela sem foco quando o carregamento termina (alt-tab, ou um ambiente sem gerenciador de janelas), o `CinematicMenu` abre, mas `FrameClock.tick(Display.isActive())` não avança, `visitAge` fica em 0 e o quadro de fade do boot (último estado do splash, "Loading 100%", cronômetro parado) continua na tela até a janela receber foco. Observado igual com Angelica 2.2.18 e 2.2.37 (`evidence/angelica-2.2.37-20261025/`). O relógio pausar sem foco é proposital ("no background catch-up"); a dúvida é só se o **fade de entrada** deveria usar tempo real. Decisão visual do dono; não alterado.

