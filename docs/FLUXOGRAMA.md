# Fluxogramas — AkashicCinematicMenu

Todos os diagramas são Mermaid (renderizam direto no GitHub). Complementam [`CRONOLOGIA.md`](CRONOLOGIA.md).

## 1. Linhagem de versões (espelha os branches e tags do git)

```mermaid
flowchart LR
    subgraph HTML["Laboratório HTML (não é o mod)"]
        R02["R02.1<br/>3 layouts"] --> R03["R03 ✔ aprovada"] --> R04["R04 ✔ navegação"] --> S01["S01.2 ✔ O Despertar"]
    end
    subgraph MAIN["Linha principal (branch main)"]
        V1["0.1.0-alpha.1<br/>6d7f5b0a"] --> V2["0.1.0-alpha.2<br/>c8930796"]
        V2 --> N01["0.2.0-alpha.1 N01<br/>7e5c5b23"] --> N02["0.2.0-alpha.2 N02<br/>377e842e"] --> N03["0.2.0-alpha.3 N03<br/>154c9504"] --> N04["0.2.0-alpha.4 N04<br/>3adfd2cc"]
        N04 --> N06["0.3.0-alpha.3 N06<br/>5fdab2cf"] --> N07["0.4.0-alpha.3 N07<br/>14b366d1"]
        N07 --> N08R1["0.5.0-alpha.1 N08 BGM R1<br/>c9248a30"] --> N09["0.5.2-alpha.1 N09 ✔ música<br/>da5bb557"]
        N09 --> N10["0.6.0-alpha.3 N10<br/>a15e18a2"] --> N11["0.7.0-alpha.4 N11 ★ ÚLTIMA<br/>7dbf5177"]
    end
    subgraph EXP["Ramos preservados (não promover)"]
        N05["experiment/n05-blocked<br/>(sem build)"]
        N08P["variant/n08-partial<br/>0.5.0-alpha.1 · 9d1fa4e1"]
        N08B["variant/n08-bgm-6c79c577<br/>0.5.1-alpha.3 · 4f12227f"]
    end
    S01 -. "autoriza produção nativa" .-> N01
    R04 -. "rotas" .-> V2
    N04 -.-> N05
    N07 -.-> N08P
    N07 -.-> N08B
    N11 -. "downstream" .-> TP["ThePatcher beta.10<br/>fence GL (Angelica)"]
    N11 -. "downstream" .-> MOB["Launcher mobile 0.20.x<br/>reusa a arte, entrega ao mod"]
```

## 2. Fluxo de trabalho/governança (QUALITY_CONTRACT)

```mermaid
flowchart TD
    C["Critérios do pedido"] --> I["Implementação"]
    I --> RV["Revisão de código/arquitetura<br/>(autorrevisão ≠ auditoria independente)"]
    RV --> VI["Inspeção visual das capturas reais"]
    VI --> T["Testes: núcleo → gates → QA em CÓPIA do modpack"]
    T --> P{"Parecer técnico"}
    P -- "APROVADO PARA A ETAPA" --> U{"Aprovação VISUAL/SONORA<br/>do usuário (separada)"}
    P -- "REPROVADO" --> F["Corrigir/investigar<br/>preservar a falha como evidência"]
    P -- "BLOQUEADO POR FALTA DE EVIDÊNCIA" --> F
    F --> I
    U -- "sim" --> OK["Candidato aprovado<br/>(ainda ≠ liberação geral)"]
    U -- "pendente" --> W["Aguardar — não declarar aprovação"]
    OK --> D["Instalação no pack diário<br/>só por AÇÃO EXPLÍCITA do usuário (backup + rollback)"]
```

Estados que **nunca** se herdam entre versões: código escrito · compilado · testado · aprovado pelo usuário · homologado em runtime.

## 3. Fluxo de boot e substituição de telas (0.7.0-alpha.4)

```mermaid
flowchart TD
    JVM["JVM + LaunchWrapper + FML"] --> CP["CinematicLoadingPlugin (SortingIndex 1001)<br/>4 transformers ASM fail-open"]
    CP --> EW["EarlyWindowTransformer<br/>Minecraft: após OpenGlHelper.initializeTextures"]
    EW --> E1["EarlyWindowHooks.graphicsReady<br/>quadro 'Preparando o ambiente' (sem % inventado)"]
    CP --> SP["SplashTransformer<br/>cpw.mods.fml.client.SplashProgress$3.run"]
    SP --> S1["SplashHooks.frame → O Despertar<br/>progresso REAL do ProgressManager"]
    S1 --> S2["SplashHooks.present = Display.update()<br/>SplashHooks.finish libera texturas"]
    CP --> LT["LoadingThemeTransformer<br/>só fundo: Aether / LoadingScreenRenderer / GuiDownloadTerrain"]
    CP --> ST["SecondaryThemeTransformer<br/>GuiScreen.drawWorldBackground · GuiButton.drawButton · GuiSlot"]
    S2 --> PRE["FMLPreInit → ClientProxy.preInit<br/>MenuConfig · UiAudio · temas · MenuBgm · MenuHook"]
    PRE --> GO["GuiOpenEvent (LOWEST) → MenuHook.onOpen"]
    GO --> Q{"Classe exata?<br/>GuiMainMenu · DummyCore MenuVanilla/Old"}
    Q -- "não / SHIFT mantido / disabled" --> ORIG["Menu original preservado"]
    Q -- "sim" --> CM["CinematicMenu (Santuário)"]
    CM --> A1["Jogar → br2.xmxcloud.net:10511"]
    CM --> A2["Singleplayer → GuiSelectWorld"]
    CM --> A3["Opções → GuiOptions"]
    CM --> A4["Sair → encerra direto (N11)"]
    CM -- "F8 ou falha" --> FB["MenuHook.fallback<br/>trava a sessão e devolve o menu original"]
    CM -- "F7" --> RM["Movimento reduzido"]
```

## 4. Fluxo "Jogar" (conexão direta)

```mermaid
sequenceDiagram
    participant U as Usuário
    participant M as CinematicMenu
    participant F as MenuFlow
    participant FML as FMLClientHandler
    participant G as ConnectionCancellationGuard
    U->>M: clique em Jogar
    M->>F: request(PLAY) — só aceita em READY
    F-->>M: EXITING 0,22 s (0 em movimento reduzido)
    M->>M: consume() → HANDED_OFF (uma única navegação)
    M->>FML: setupServerList → connectToServer(ServerTarget)
    FML-->>U: GuiConnecting (tema próprio, rede nativa)
    U->>G: cancelar
    G->>FML: fecha o canal publicado tarde (defeito do run03/N03)
    FML-->>M: retorna ao menu
```

## 5. Árvore de áudio (BGM e SFX)

```mermaid
flowchart TD
    EV["Evento de UI"] --> UG["UiCueGate · MenuSoundGate"]
    UG --> SFX["6 SFX 'ritual' (sounds.json categoria master)<br/>foco×2, seleção, retorno, entrada, conexão"]
    FE["Frontend (BgmFrontendPolicy)<br/>menu raiz + telas auditadas · sem mundo"] --> BG["MenuBgm → SoundHandler<br/>categoria MUSIC · stream"]
    BG --> FD["MusicFade: entrada 2,5 s · saída 0,8 s · vol 0,40"]
    BG --> MUTE{"Música = 0 % ou MASTER = 0?"}
    MUTE -- "sim" --> SIL["Respeita o mute — NÃO contorna"]
    MUTE -- "não" --> PLAY["Toca a faixa; suprime música vanilla alheia só no frontend"]
    FILE{"menu.ogg presente no JAR?"} -- "não" --> MISS["Estado MISSING: 0 starts, música vanilla mantida"]
```

## 6. Ecossistema

```mermaid
flowchart LR
    MOB["Forbidden Series — launcher Android<br/>(home 'The End of Forbidden')"] -- "arte e identidade" --> ACM["AkashicCinematicMenu<br/>(este repo)"]
    ACM -- "mod no pack" --> TEOF["Modpack TEOF / Forbidden Series<br/>1.7.10 · Forge 10.13.4.1614"]
    TEOF -- "conecta" --> SRV["br2.xmxcloud.net:10511"]
    ACM -- "RawScope × Angelica" --> TP["AkashicThePatcher<br/>beta.10 Cinematic-Angelica"]
    ANG["Angelica / GTNHLib / Reese Sodium / RTG / DummyCore / BogoSorter"] -- "coexistência" --> ACM
    MOB -- "entrega a apresentação após atividade GL real" --> ACM
```
