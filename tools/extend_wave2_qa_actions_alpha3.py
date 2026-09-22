from pathlib import Path
R=Path(__file__).resolve().parents[1]
p=R/'tools/Wave2QaActions.java'
s=p.read_text(encoding='utf-8')
anchor='        if(name.equals("flatworld")){'
assert s.count(anchor)==1
extra='''        if(name.equals("rename"))return new GuiRenameWorld(r,"qa-nonexistent-world");
'''
s=s.replace(anchor,extra+anchor)
anchor='        if(a[1].equals("wave2cancel")){'
assert s.count(anchor)==1
extra='''        if(a[1].equals("wave2button")){
            if(current==null||!current.getClass().getName().equals("rtg.client.gui.WorldTypeMessageGUI"))throw new IllegalStateException("RTG wrapper only");
            int id=Integer.parseInt(a[2]);if(id!=0)throw new IllegalArgumentException("Only RTG OK allowed");
            Method m=GuiScreen.class.getDeclaredMethod("func_146284_a",GuiButton.class);m.setAccessible(true);
            GuiButton target=null;for(Object x:(java.util.List<?>)field(current,"field_146292_n")){GuiButton b=(GuiButton)x;if(b.field_146127_k==id)target=b;}
            if(target==null||!target.field_146124_l||!target.field_146125_m)throw new IllegalStateException("RTG OK missing");
            m.invoke(current,target);return true;
        }
'''
s=s.replace(anchor,extra+anchor)
p.write_text(s,encoding='utf-8',newline='\n')
print('WAVE2_QA_ALPHA3_ACTIONS_EXTENDED')
