# Referências técnicas — R03
Consultadas em 20/09/2026. Uso de documentação oficial, não prova de compatibilidade do mod.

## Camada translúcida
MDN: https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Properties/backdrop-filter
O efeito atua no conteúdo atrás da superfície. Um fundo parcialmente transparente permite enxergá-lo.
A opacidade do ancestral não foi reduzida: isso preserva o alcance do backdrop root e evita desbotar texto/botões.
O painel usa uma camada decorativa separada, sem aplicar `filter:blur()` à navegação.
Foram conferidos tanto valores de CSS quanto diferenças reais nos pixels com/sem blur.

## Preferência por reduzir transparência
MDN: https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/At-rules/@media/prefers-reduced-transparency
A opção de sistema é respeitada quando reportada pelo navegador. Também existe controle manual.
A consulta não é uma garantia de suporte em todo navegador. Uma superfície sólida de contraste oferece alternativa.
O teste de ausência de suporte injeta uma resposta negativa de CSS.supports no ambiente: não é uma execução em navegador antigo.

## Contraste
W3C: https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html
A referência de 4,5:1 foi usada para conferir os cinco rótulos de ação nos quadros amostrados.
A medição usa cores computadas do texto e pixels do fundo capturado, removendo temporariamente texto/sombra sem mudar geometria.
São amostras de quadros e caixas de texto, não uma certificação completa de conformidade WCAG.
As letras secundárias foram clareadas após a primeira inspeção de contraste.

## Limite de aplicação
O browser é laboratório visual. CSS backdrop-filter não é diretamente portável para o OpenGL do Minecraft.
Esta revisão não implementa nem valida blur no Forge/Angelica. Nenhum JAR ou startup foi alterado.
