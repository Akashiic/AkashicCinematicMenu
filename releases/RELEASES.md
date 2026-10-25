# Releases

Todos são versões **alpha**. Os 11 anteriores estão marcados como *pré-lançamento*; `v0.7.0-alpha.4` (último estado) não está porque o GitHub só permite *Latest* em lançamentos não-pré — continua sendo alpha.
Hashes: `releases/SHA256SUMS` (confira com `python tools/repo/verify_release.py <pasta-dos-jars>`).

## A faixa de música
Os JARs 0.5.2-alpha.1, 0.6.0-alpha.3 e 0.7.0-alpha.4 **contêm a faixa original** (`assets/akashiccinematic/sounds/bgm/menu.ogg`, SHA-256 `6e91e747a99148084b854012c48ce93974beb6275dc4add2951989beff0ae4a6`). Ela é de terceiros e é mantida com a permissão obtida por outro contribuinte, por decisão do dono (ver `docs/OPEN-ISSUES.md` OI-07). Os JARs são os **arquivos oficiais, byte a byte** (hashes iguais aos de `docs/LINEAGE.md`).

> Histórico: por algumas horas, em 24/10/2026, esses três Releases foram publicados como `-nobgm`; foram substituídos pelos oficiais. `tools/repo/strip_bgm.py` continua disponível para quem queira redistribuir sem a faixa.

## Outros JARs (0.1.0 … 0.5.0-alpha.1)
Arquivos originais, byte a byte (nenhum contém a faixa). Variantes N08 paralelas (0.5.0-alpha.1 parcial, 0.5.1-alpha.1..3), alphas intermediárias (0.3.0-alpha.1/2, 0.4.0-alpha.1/2, 0.6.0-alpha.1/2, 0.7.0-alpha.1..3) e os pacotes de engenharia (.zip, até 40 MB) **não** foram publicados; seus hashes estão em `docs/LINEAGE.md`.

## Aviso
Software alpha, **sem garantia**, sem aprovação visual do dono nas versões ≥ 0.4.0, sem licença definida (OI-08). Instalar apenas em cópia de teste do modpack.
