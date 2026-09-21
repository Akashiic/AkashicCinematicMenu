# N07 — fidelidade responsiva e convergência cromática
Direção solicitada: preservar Santuário/O Despertar e aproximar o desktop do launcher mobile 0.15.0.
Não existe aprovação visual automática da nova paleta; usuário permanece aprovador final.

## Diagnóstico com evidência do código
N06 já corrige a atualização de geometria do LWJGL no splash. Não atribuir todo defeito ao Angelica.
O gap lateral da captura é compatível com outro defeito: um background cover recebia uma SEGUNDA composição opaca, centralizada em contain.
Em 1920x1001, o perfil 1366x768 usava scale=min(1920/1366,1001/768)=1.3033854; sobravam 69.7878 pixels em cada lateral.
Essa diferença é muito maior que um arredondamento de 1–3px. A imagem de trás aparecia nesse corredor.
N07 elimina a segunda composição raster. O cenário tem um único mapeamento cover, ancorado no alto para preservar o halo.
Painéis, molduras, botões e textos usam coordenadas de pixels do framebuffer; o painel esquerdo começa em x=0 e mantém 27%.

## Nitidez
A fonte de cenário disponível tem 1207x830, e não 4K. Não há alegação de detalhe inexistente ou upscaling mágico.
A implementação anterior ampliava texto/ornamentos embutidos em boot.png/panel.png e pequenos atlas de 12/36px.
N07 desenha a tipografia separadamente: atlas por tamanho, escolhido igual ou maior que o tamanho solicitado até a faixa testada.
Ornamentos, linhas e barras são geometria, não elementos ampliados de screenshot.
Ativos anteriores permanecem no histórico, mas a composição nova não os estica para desenhar UI.
Primeira alpha foi reprovada na inspeção compacta por metadados de 5–7px. Alpha.2 eleva o mínimo nominal para corpo9px/serifa12px antes do ajuste à largura.

## Paleta proposta a partir do mobile
Base #090D14; superfície #171C25; marfim #E8E4D8; ouro apagado #C4AA7C; texto secundário #ACB2BB; núcleo/frio #DCEBFA.
São tokens de implementação escolhidos a partir da referência, não supostos códigos-fonte do aplicativo.
Vegetação é dessaturada, pedra fica fria e neutra; o verde deixa de ser acento principal da interface.
O blur continua pré-processado na arte, agora com a MESMA projeção do fundo. Não é pós-processamento em framebuffer.

## Pausas e janelas sem resposta
A proposta anterior de simplesmente armazenar o último frame não garante atualização se a thread que apresenta quadros estiver parada.
O Windows pode criar uma cópia fantasma da janela não responsiva; uma textura armazenada não substitui o processamento de mensagens do sistema.
N07 mantém os recursos da cena entre tamanhos e recompõe usando as dimensões atuais quando a renderização retorna; não cria outra thread proprietária de OpenGL.
O teste usa atrasos limitados de800ms e6500ms na thread principal, sem mundo aberto. Não é um reset de driver nem uma pausa de toda a JVM.
A captura GDI durante a primeira pausa não conseguiu obter device context; o teste foi interrompido e a falha preservada.
A repetição dirigida verifica mensagens de início/fim, estado maximizado real, dimensões Display/Minecraft/FBO/UI e PNG completo após a recuperação.
Não há aprovação de ausência absoluta de flash/preto durante a pausa; a aceitação é da recomposição correta ao retomar.

## Trabalho anterior preservado e revalidado
Cinco cues SFX originais permanecem: focus/select/back/enter/connect. Os OGGs mantêm os hashes da síntese analisada.
Mute MASTER e reprodução reconhecida por SoundHandler foram exercitados no cliente completo. Isso não equivale a aprovação auditiva humana.
Apenas o warning conhecido InventoryTweaks/BogoSorter sai do fluxo modal e vai ao log. O conflito entre mods não foi corrigido por essa supressão.
Avisos desconhecidos permanecem modais, conforme teste explícito.
Connecting e Disconnected mantêm as instâncias nativas e recebem apresentação na paleta nova; mensagem original, retorno e cancelamento são preservados.
Options e seleção de mundos permanecem nativos. Não extrapolar para todos os menus do jogo.

## Distinção de versões
N07 alpha.1 demonstrou a composição nova, mas foi reprovada na legibilidade compacta e o teste de foreground não confirmou a janela correta.
N07 alpha.2 é a candidata desta revisão; resultados ficam vinculados ao SHA256, não só ao nome do arquivo.
A aprovação artística final das cores e SFX é do usuário. Compilação, inspeção, testes de runtime e parecer são registros distintos.
