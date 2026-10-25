# Forbidden — Santuário R03

O Layout 1 escolhido pelo Akashic foi preservado. Esta revisão altera o material do painel esquerdo, não a direção da composição.

## Abrir
Use `dist/Forbidden-Santuario-R03.html` no navegador. O arquivo inclui CSS, JavaScript e imagens; não usa CDN, fontes baixadas ou conexão ao jogo.
O arquivo HTML também é entregue separadamente na conversa. A política do navegador deste ambiente bloqueia file://; consulte o parecer para distinguir isso da execução do HTML carregado no DOM.

## Três materiais
A / Cristal mineral: blur 10px, tinta-base 36%.
B / Vidro fumê: blur 16px, tinta-base 46%. Padrão de abertura, não aprovação final.
C / Véu de pedra: blur 24px, tinta-base 58%.
Os gradientes adicionais de leitura tornam a opacidade visual composta diferente desses valores-base.
A largura desktop permanece em 27%. Em retrato, mantém-se a adaptação do menu para largura integral.

## Controles
1, 2 e 3: escolhem o material A, B ou C. Não escolhem outro layout.
B: alterna antes/depois do painel opaco. C: abre as fichas de material.
V: oculta/restaura as ferramentas. M: pausa/retoma movimento. R: repete a introdução.
Escape: fecha painel demonstrativo ou cancela uma rota pendente.
Setas, Home e End: navegam pelos cinco botões. Enter: abre o painel correspondente.
Opções: movimento, transparência, partículas e atmosfera de musgo.
Todos os destinos são demonstrativos; nenhum servidor ou arquivo do Minecraft é consultado.

## Fonte e reconstrução
`python build.py` produz o HTML autocontido somente com a biblioteca padrão do Python.
Os arquivos de autoria estão em src/; as duas imagens preservadas da R02 estão em assets/.
Nenhum arquivo de fonte tipográfica é distribuído. As famílias tipográficas dependem das fontes locais do sistema.

## Reexecutar os testes
Instale as dependências de tests/requirements.txt em um ambiente virtual e tenha um Chromium/Chrome disponível.
`python tests/qa.py all --chromium /caminho/para/chromium`
O baseline da R02 está em baseline/Forbidden-Layouts-R02.html para conferir a regressão visual.
Os testes automatizados, medições e revisão visual são evidências distintas.

## Escopo e continuidade
Cena e anjo continuam sendo arte 2D, não um modelo 3D. O blur é implementado no HTML/CSS.
O startup, a alpha Java e a instalação jogável não foram alterados.
A composição escolhida é o Santuário. O material final ainda aguarda decisão do Akashic.
Leia docs/PARECER_R03.md e CHECKPOINT_R03.json antes de promover outra etapa.
