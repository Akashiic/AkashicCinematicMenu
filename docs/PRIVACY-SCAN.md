# Varredura de privacidade e direitos antes da publicação (24/10/2026)

Escopo: **todos** os blobs de **todas** as refs (932 blobs, ~147 MB), antes do primeiro push.

| Verificação | Resultado |
|---|---|
| Tokens GitHub / chaves privadas / `--accessToken` / `--uuid` / senhas | nenhuma ocorrência |
| Faixa de terceiros (`menu.ogg`) | **incluída por decisão do dono** (permissão obtida por outro contribuinte; ver OI-07). A varredura original (24/10) encontrou só 11 SFX sintetizados, pois a faixa estava excluída até então; nenhum outro OGG > 1 MB nem MP3 |
| E-mails | 3 e-mails públicos de autores de pacotes pip (terceiros) em `audio-codec-packages.json` → **substituídos por `[email-omitido]`**; 1 falso positivo (`lin@np.array`) |
| IPs | só números de versão (ex.: `21.0.11.10`); o servidor do projeto é por nome de host, mantido de propósito (`br2.xmxcloud.net:10511`, destino do botão Jogar) |
| Caminhos de usuário | apenas `C:\Users\User\…` (nome genérico do Windows); caminhos locais aparecem em logs/recibos e nos scripts de verificação |
| Identidade dos commits | nome `Akashiic` + e-mail *noreply* do GitHub; o e-mail pessoal do dono não foi usado |
| Modpack | nenhum JAR de terceiros, config, save, log de jogo ou conta foi copiado |

Limites: a varredura é por padrões; não substitui revisão humana. Logs de teste podem citar o apelido de jogo do dono (`Akashiic`), que já é público.
