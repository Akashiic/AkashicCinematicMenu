# Parecer N10 — 0.6.0-alpha.3

Candidata produzida em21/09/2026, com autorrevisão do assistente. A aprovação visual e sonora final permanece do usuário.

## Identidade
JAR: `AkashicCinematicMenu-0.6.0-alpha.3.jar`; 16498716bytes; 77classes Java8.
SHA-256: `a15e18a206e89aa7736d71cae22813ecb4be0e7cb0dffd75d4c10e0939785859`.
Base preservada: N09/0.5.2-alpha.1, com a BGM confirmada funcional pelo usuário. Gravação, evento de música e código do controlador permaneceram idênticos.

## Produção
Jogar recebeu gradiente carmesim, texto branco, brilho de borda, respiração lenta, uma faixa de luz periódica e pressão visual de220ms. Nenhuma animação dispara a conexão ou altera a área clicável.
O quadro principal agora usa três profundidades: céu/névoa, arquitetura e anjo/halo. A arquitetura dianteira usa uma máscara de ocultação no mesmo plano, para continuar na frente dos joelhos e do manto.
A camera compartilha um enquadramento cover, mas cada profundidade tem deslocamento próprio. O anjo percorre aproximadamente±13px horizontais em1920px de largura; arquitetura±4.5px e céu±1.4px, antes dos efeitos de recorte.
São imagens RGBA separadas; não é apenas um deslocamento da imagem inteira. As margens de24px e os limites de movimento foram verificados matematicamente.
Seis arquivos SFX originais cobrem foco(duas variantes), seleção, retorno, chegada ao menu e conexão. Há coro sintetizado sem palavras, metal ressonante, ar e cauda de ambiente. Não se afirma ter gravado canto gregoriano humano.
A reverberação é pré-calculada; a reprodução usa o motor do Minecraft. Há limite de vozes e liberação suave de90ms nas substituições, sem DSP de convolução em tempo real.

## Inspeção e correções
A primeira faixa de brilho tinha marcas entre segmentos. Foi reprovada e substituída por uma faixa contínua com interpolação de alfa.
O contorno do anjo foi refinado em uma faixa estreita; o halo continua com abertura transparente. Foi acrescentada ocultação frontal para evitar sobreposição incorreta da estátua sobre pilares.
A recomposição neutra final tem erro médio absoluto de0.0804por canal na escala0–255; não é uma alegação de identidade pixel a pixel.

## Verificação executada
48 cenários/17489 asserções do núcleo anterior, 4807 verificações novas de câmera/animação, 1785 de geometria, 140 da política de UI, 1557 de música/fade, 18 cenários/124 asserções de playback e32 da classificação de sons passaram.
O renderizador Java foi executado numa superfície pbuffer privada na Intel UHD Graphics/OpenGL4.6. Não abriu outra janela nem iniciou o modpack completo.
As276 verificações do probe incluem repetição de estado/erros GL, cinco resoluções(854x480,1024x768,1366x768,1920x1001,2560x1440), coordenadas de botões, extremos de câmera e pixels estáveis em movimento reduzido.
A ausência simulada de uma camada produziu imagem igual ao caminho estático e liberou os recursos parcialmente criados. Esse JAR incompleto é fixture e não faz parte da distribuição.
Os6OGGs foram decodificados integralmente pelo JOrbis das bibliotecas do pack, sem erro e com duração correta. Pico abaixo de-8dBFS e compatibilidade mono foram conferidos na análise dos arquivos.
As contagens incluem verificações repetidas; não equivalem a milhares de tipos independentes de defeito.

## Limitações que impedem liberação geral
Esta candidata não foi executada dentro do modpack completo nem se avaliou subjetivamente o novo áudio pelo SoundHandler. Havia clientes Java do usuário ativos e apenas1–2GiB de memória física livre; não foram encerrados para abrir outra instância.
O probe demonstra o renderizador e seus recursos, não compatibilidade total de transformação com Angelica/Forge e todos os demais mods.
Os tempos de desenho no pbuffer variaram com a carga do computador. Não são benchmark de FPS do jogo nem comprovação de60FPS.
A arte original permanece1207x830. Os planos são2.5D, com pequenas regiões escondidas restauradas para o movimento limitado; não um modelo3D nem uma cena reconstruída para câmera livre.
O blur é pré-processado por camada. Não foi acrescentado pós-processamento de framebuffer.
Não há promessa de apresentar novos quadros durante parada de toda a JVM/driver. O episódio de encerramento antecipado de versões históricas não foi resolvido por esta candidata.
A nova paleta do CTA e a sonoridade aguardam a aprovação do usuário. A N09 instalada não foi substituída por este trabalho.

## Referência metodológica
A reconstrução local das faixas ocultas emprega inpainting Telea do OpenCV. Esse método usa vizinhança de pixels; ele não recupera informação histórica real das áreas ocultas. Referência: https://docs.opencv.org/4.x/df/d3d/tutorial_py_inpainting.html

## Parecer
Compilação, análise de recursos, testes de lógica e renderizador nativo: passaram no escopo declarado. Homologação completa no pack e aprovação artística: pendentes.
