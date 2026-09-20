from pathlib import Path
r=Path(__file__).resolve().parents[1]
p=r/'src/main/java/br/com/redeakashic/cinematic/client/ClientProxy.java';s=p.read_text(encoding='utf-8');old='Cinematic alpha registered. Native splash remains unchanged.';new='Cinematic menu registered. Splash overlay status is reported by bootstrap.';assert s.count(old)==1;s=s.replace(old,new);p.write_text(s,encoding='utf-8',newline='\n');print('LOG_DESCRIPTION_UPDATED')
