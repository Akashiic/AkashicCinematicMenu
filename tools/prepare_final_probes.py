from pathlib import Path
R=Path(__file__).resolve().parents[1];p=R/'tools/CeremonialGraphicsProbe.java';s=p.read_text()
s=s.replace('    private static int checks;','    private static int checks;private static final ByteBuffer scratch=BufferUtils.createByteBuffer(2560*1440*4);')
s=s.replace('ByteBuffer b=BufferUtils.createByteBuffer(w*h*4);','ByteBuffer b=scratch;b.clear();b.limit(w*h*4);')
anchor='            check(BackdropRenderer.depthDraws>100,';assert s.count(anchor)==1
extra='''            List<Double> flat=new ArrayList<Double>(),depth=new ArrayList<Double>();
            for(int i=0;i<80;i++){v.depthEnabled=(i%2==0);long start=System.nanoTime();menu(v,1366,768,i*.04,.4,.4,.3,-1,true);GL11.glFinish();if(i>5)(v.depthEnabled?depth:flat).add((System.nanoTime()-start)/1e6);}
            Collections.sort(flat);Collections.sort(depth);System.out.println("PAIRED_MEDIAN_MS static="+flat.get(flat.size()/2)+" layered="+depth.get(depth.size()/2));v.depthEnabled=true;
            for(int i=0;i<42;i++){double t=i/42.0;menu(v,960,540,t*6.8,Math.sin(t*2*Math.PI),Math.cos(t*2*Math.PI)*.7,.4,-1,true);save(out,String.format(java.util.Locale.ROOT,"motion-%03d",i),960,540);}
'''
s=s.replace(anchor,extra+anchor);p.write_text(s,newline='\n')
p=R/'tools/run_graphics_probe.py';s=p.read_text();s=s.replace("evidence=R/'evidence/native-probe'","evidence=R/'evidence'/('native-probe-'+b['version'])")
s=s.replace("(R/'evidence/native-probe.log')","(R/'evidence'/('native-probe-'+b['version']+'.log'))")
s=s.replace("(R/'evidence/probe-process.json')","(R/'evidence'/('probe-process-'+b['version']+'.json'))")
s=s.replace("for f in evidence.glob('*.png'):","for f in [p for p in evidence.glob('*.png') if not p.name.startswith('motion-')]:")
s+='\nframes=[Image.open(p).convert("RGB") for p in sorted(evidence.glob("motion-*.png"))]\nframes[0].save(evidence/"N10-Movimento-Nativo.webp",save_all=True,append_images=frames[1:],duration=162,loop=0,quality=88)\n'
p.write_text(s,encoding='utf-8',newline='\n');print('FINAL_PROBES_VERSION_BOUND_AND_CAPTURE_BUFFER_REUSED')
