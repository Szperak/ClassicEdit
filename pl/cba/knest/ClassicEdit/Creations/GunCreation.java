package pl.cba.knest.ClassicEdit.Creations;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.util.Vector;


public class GunCreation extends InfiniteCreation{
	private List<Bolt> bolts = new ArrayList<Bolt>();
	private class Bolt{
		private Location l;
		private Vector v;
		private int ticks = 0;
		private Queue<Block> last = new LinkedList<Block>();
		private final int len = 7;
		private int end = 200;
		public Bolt(Location l, Vector v){
			this.l = l;
			this.v = v;
			
		}
		
		public boolean tick(){
			World w = l.getWorld();
			Block b = w.getBlockAt(l);
			
			if(b == null) return false;
			if(b.getType()!=Material.AIR) end = ticks+len;
			if(end>ticks+len){
				l.add(v);
				if(!last.contains(b)){
					fake(b, f.getMaterial(), f.getData());
					last.add(b);
				}
			}
			if(ticks>2 && last.size()>len) fake(last.poll(), Material.AIR, (byte) 0);
			if(ticks>end+len) return false;
			ticks++;
			return true;
		}
		@SuppressWarnings("deprecation")
		private void fake(Block b, Material m, byte data){
			if(b == null) return;
			for(Player p : Bukkit.getOnlinePlayers()){
				p.sendBlockChange(b.getLocation(),m,data);
			}
		}
		public void kill(){
			for(Block b : last){
				fake(b, Material.AIR, (byte) 0);
			}
		}
	}
	public GunCreation(String nick) {
		super(nick);
	}
	@Override
	public void click(Location l){
		Vector v = l.getDirection();
		//v.multiply(1.4);
		l.add(v);
		Bolt bo = new Bolt(l, v);
		bolts.add(bo);
	}
	@Override
	public void run(){
		ArrayList<Bolt> toremove = new ArrayList<Bolt>();
		for(Bolt b : bolts){
			if(!b.tick() || !b.tick()){
				toremove.add(b);
				b.kill();
			}
		}
		bolts.removeAll(toremove);
		
	}

	@Override
	public String getName(){
		return "gun";
	}

	@Override
	public boolean start(){
		//msgPlayer(ChatColor.GREEN+"Gun active");
		return true;
	}



	@Override
	public void onBlockPhysics(BlockPhysicsEvent e){
		
	}


	
}
