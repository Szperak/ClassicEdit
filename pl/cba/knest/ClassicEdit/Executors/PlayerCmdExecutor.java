package pl.cba.knest.ClassicEdit.Executors;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import pl.cba.knest.ClassicEdit.ClassicEdit;
import pl.cba.knest.ClassicEdit.Creation;
import pl.cba.knest.ClassicEdit.ExecutorException;

public class PlayerCmdExecutor extends Executor{

	Player p;
	@Override
	public void execute() throws ExecutorException {
		super.execute();
		p = getIfPlayer(s);
		if(params.contains("stop") || params.contains("s")){
			Creation c = ClassicEdit.getCuboidManager().getCreation(p);
			c.stop();
			ClassicEdit.getCuboidManager().removeSelector(p);
			throw new ExecutorException(ChatColor.YELLOW+"Stopped "+c.getName());
		}
	}
	private Player getIfPlayer(CommandSender s) throws ExecutorException{
		if(s instanceof Player) return (Player) s;
		throw new ExecutorException(ChatColor.RED+"Only player may call this");
	}
}
