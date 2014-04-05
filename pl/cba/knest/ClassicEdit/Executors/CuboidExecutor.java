package pl.cba.knest.ClassicEdit.Executors;



import pl.cba.knest.ClassicEdit.ClassicEdit;
import pl.cba.knest.ClassicEdit.ExecutorException;
import pl.cba.knest.ClassicEdit.Selector;
import pl.cba.knest.ClassicEdit.Creations.CuboidCreation;
import pl.cba.knest.ClassicEdit.Selectors.TwoPointSelector;

public class CuboidExecutor extends TwoPointExecutor{
	public CuboidCreation getCreation(String nick){
		return new CuboidCreation(nick);
	}
	public void execute() throws ExecutorException{
		super.execute();
		CuboidCreation c = getCreation(p.getName());
		
		c.setFilling(f);
		
		c.setDashed(flags.contains("d"));

		Selector sel = new TwoPointSelector(p, c);
		
		p.sendMessage(sel.getMessage((byte) 0));
		ClassicEdit.getCuboidManager().setSelector(p, sel);
	}


}
