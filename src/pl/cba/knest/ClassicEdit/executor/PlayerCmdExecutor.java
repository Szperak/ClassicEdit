package pl.cba.knest.ClassicEdit.executor;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;




import pl.cba.knest.ClassicEdit.ClassicEdit;
import pl.cba.knest.ClassicEdit.ExecutorException;
import pl.cba.knest.ClassicEdit.Session;
import pl.cba.knest.ClassicEdit.creation.Creation;

public class PlayerCmdExecutor extends Executor {

	Player player = null;
	
	public void msgPlayer(String msg){
		msg(player, msg);
	}
	@Override
	public void execute() throws ExecutorException {
		super.execute();
		player = getIfPlayer(s);
		if(params.contains("next") || params.contains("n")){
			Creation c = ClassicEdit.getCreationManager().getCreation(player);
			if(c == null) throw new ExecutorException(ChatColor.YELLOW+"You don't have any cuboid running");
			c.stop();
			throw new ExecutorException(ChatColor.YELLOW+"Finished "+c.getName());
		}
		if(params.contains("stop") || params.contains("s")){
			Session s = ClassicEdit.getCreationManager().getSession(player);
			//if(c == null) throw new ExecutorException(ChatColor.YELLOW+"You don't have any cuboid running");
			s.stop();
			throw new ExecutorException(ChatColor.YELLOW+"Session stopped");
		}
		if(params.contains("help") || params.contains("h")){
			help();
			throw new ExecutorException();
		}
	}
	public void help() {
		msgPlayer("No help");
	}

	private Player getIfPlayer(CommandSender s) throws ExecutorException{
		if(s instanceof Player) return (Player) s;
		throw new ExecutorException(ChatColor.RED+"Only player may call this");
	}
}
