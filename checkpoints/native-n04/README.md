# Akashic Cinematic Menu — N04 / 0.2.0-alpha.4
Candidato nativo validado em testes dirigidos; não é liberação geral.
SHA-256 do JAR: 3adfd2cc102eed60666d5594afc0a375e2a1fc7047ed409c7afc81c5969c190e.

## Correção aplicada
A N03 só desenhava após o início do splash do Forge. A N04 acrescenta um quadro próprio depois da inicialização gráfica do OpenGlHelper, antes de beginMinecraftLoading.
O primeiro quadro usa a composição aprovada O Despertar e a mensagem Preparando o ambiente, com indicador sem total.
Quando o Forge publica progresso, o renderer existente passa a desenhar a etapa real; sem percentuais fictícios.
Mantém menu Santuário, 52 recursos visuais e proteção contra conexão criada após cancelamento da N03.
Anjo continua 2D; blur permanece pré-renderizado. Nenhum navegador/CDN em runtime.

## Evidência já confirmada
Runs06/07: dois boots completos, 35 verificações de regressão, 11 de startup/captura e seis ciclos de minimizar/restaurar pelo HWND correto.
Com observação desde a criação da janela: N03 7.479s para primeira arte customizada; N04 2.432s na execução comparada.
Amostragem 250ms, uma execução selecionada por versão; NÃO é benchmark estatístico nem ganho garantido.
Foco direto: inatividade observada entre 101 e 406ms; restauração entre 303 e 409ms nos seis ciclos.
Telemetria do jogo ~300ms; esses valores não medem latência de quadro. Nenhum código de foco do menu foi alterado.
A medição anterior com helper não estabelecia o mesmo foreground real; atrasos anteriores permanecem preservados nos relatórios.

## Limites e segurança
Ainda há aproximadamente 2.4s iniciais sem arte na execução medida, antes do hook gráfico. Não declarar primeiro pixel instantâneo.
Nenhum candidato instalado na pasta de mods de uso diário; somente qa-n02/instance.
O experimento native-n05 é parcial, sem build e bloqueado na ferramenta; NÃO deve ser usado nem incluído no pacote.
CHECKPOINT_N04.json registra as rodadas concluídas. Certificação adicional em evidence/ tem escopo explícito.
Build reproduzível, 43 classes Java 8; sem stubs, classes de QA ou fontes TTF/OTF/WOFF no artefato.

## Compilar e testar
python tools/test_core.py
java -ea -cp build/core-tests br.com.redeakashic.cinematic.core.ConnectionWatchTests
python tools/build_native.py
Não reexecutar scripts históricos de migração das versões anteriores copiados em tools.
