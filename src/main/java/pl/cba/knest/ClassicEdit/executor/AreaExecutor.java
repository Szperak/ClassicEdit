package pl.cba.knest.ClassicEdit.executor;


import java.util.Iterator;

import org.bukkit.ChatColor;

import pl.cba.knest.ClassicEdit.ExecutorException;
import pl.cba.knest.ClassicEdit.Mask;


public class AreaExecutor extends CreationExecutor {
	boolean dropmode = false;
	Mask mask = null;

	public void execute() throws ExecutorException{
		super.execute();
		dropmode = !player.hasPermission("ClassicEdit.creative");
	}
	@Override
	void flag(char c, Iterator<String> i) throws ExecutorException {
		switch(c){
		case 'm':
			if(i.hasNext()){
				mask = Mask.parseMask(i.next());
				i.remove();
			}else throw new ExecutorException(ChatColor.RED+"Not enough arguments for mask (-m <blocks>)");
		return;
		}
		super.flag(c, i);
	}
}
