package pl.cba.knest.ClassicEdit.Creations;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import pl.cba.knest.ClassicEdit.ClassicEdit;

public abstract class TwoPointCreation extends FilledCreation{
	
	public TwoPointCreation(String nick) {
		super(nick);
	}
	boolean started = false;
	
	Location l1;
	Location l2;
	World w;

	
	
	
	boolean dropmode = false;
	
	

	int maxx;
	int maxy;
	int maxz;
	int minx;
	int miny;
	int minz;
	
	int x;
	int y;
	int z;
	
	int width;
	int length;
	int height;
	
	int pertick = 1;
	
	boolean loop = false;
	
	boolean up = true;
	public boolean canPlace(int x, int y, int z){
		return true;
	}
	
	
	public boolean isLoop(){
		return loop;
	}
	public void setLoop(boolean loop){
		this.loop = loop;
	}
	@Override
	public void run() {
		Player p = null;
		AtomicInteger amount = new AtomicInteger(0);
		
		int items = 0;
		if(dropmode){
			p = Bukkit.getPlayer(nick);
			if(p==null){
				ClassicEdit.getCuboidManager().pauseCreation(this); return;
			}
			if(f.getMaterial()!=Material.AIR){
				amount.set(getAmount(f.getMaterial(), f.getData(), p.getInventory()));
				items = amount.get();
			}
		}
		ppt = 0;
		for(int i = 0; i<1024; i++){
			
			if(canPlace(x,y,z)){
				if(!place(amount, p)){
					break;
				}
			}
			if(!next()){
				sum += placed;
				if(loop){
					if(placed>0){
						placed = 0;
						init();
						msgPlayer(ChatColor.YELLOW+"Checking... next lap");
					}else{
						pause();
						placed = 0;
						init();
						msgPlayer(ChatColor.YELLOW+"No more blocks to place, paused.");
						break;
					}
				}else{
					stop();
					end();
					break;
				}
			}
			if(ppt>=pertick) break;
		}
		if(dropmode) setAmount(f.getMaterial(), f.getData(), p.getInventory(), items-amount.get());
	}

	@Override
	public String getName() {
		return "2-point structure";
	}


	public void setDropmode(boolean dropmode){
		if(!started) this.dropmode = dropmode;
	}

	public void setPoints(Location l1, Location l2){
		if(started) return;
		this.l1 = l1;
		this.w = l1.getWorld();
		this.l2 = l2;
	}

	
	
	
	@SuppressWarnings("deprecation")
	public boolean place(AtomicInteger amount, Player p){
		Block b = w.getBlockAt(x,y,z);
		//msgPlayer("trying to place at "+x+" "+y+" "+z);
		if(b.getType()==f.getMaterial() && b.getData()==f.getData()) return true;
		boolean place = true;
		
		if(dropmode){
			if(b.getType()==Material.BEDROCK || b.getType()==Material.ENDER_PORTAL || b.getType()==Material.ENDER_PORTAL_FRAME){
				return true;
			}else{
				if(b.getType()!=Material.AIR){
					BlockBreakEvent be = new BlockBreakEvent(b, p);
					ClassicEdit.callEventWithoutNCP(be);
					if(!be.isCancelled()){
						for(ItemStack drop : b.getDrops()){
							HashMap<Integer, ItemStack> out = p.getInventory().addItem(drop);
							for(ItemStack is : out.values()){
								w.dropItemNaturally(b.getLocation(), is);
							}
						}
						b.setType(Material.AIR);
					}else{
						if(getFilling().getMaterial()==Material.AIR){
							place = false;
							if(b.getType()==Material.AIR){
								ppt++;
								placed++;
							}else{
								p.sendMessage(ChatColor.YELLOW+"Event cancelled");
								p.sendMessage(ChatColor.YELLOW+"Type /p to unpause or /p stop");
								pause(); 
								return false;
							}
						}
					}
				}
			}
			if(getFilling().getMaterial()!=Material.AIR){
				ItemStack is = new ItemStack(getFilling().getMaterial(), amount.get()>64?64:amount.get(), getFilling().getData());
				
				BlockPlaceEvent bp = new BlockPlaceEvent(b, b.getState(), b.getRelative(BlockFace.DOWN), is, p, true);
				ClassicEdit.callEventWithoutNCP(bp);
				if(!bp.isCancelled()){
					if(amount.decrementAndGet() < 0){
						p.sendMessage(ChatColor.RED+"You do not have required materials ("+f+")");
						p.sendMessage(ChatColor.YELLOW+"Supply them and type /p or /p stop");
						pause(); 
						return false;
					}
				}else{
					place = false;
				}
			}
		}
		
		if(place){
			b.setType(f.getMaterial());
			b.setData(f.getData());
			placed++;
			ppt++;
		}

		return true;
	}
	
	
	
	
	
	@Override
	public void init(){
		
		maxx = Math.max(l1.getBlockX(), l2.getBlockX());
		maxy = Math.max(l1.getBlockY(), l2.getBlockY());
		maxz = Math.max(l1.getBlockZ(), l2.getBlockZ());
		minx = Math.min(l1.getBlockX(), l2.getBlockX());
		miny = Math.min(l1.getBlockY(), l2.getBlockY());
		minz = Math.min(l1.getBlockZ(), l2.getBlockZ());
		width = maxx-minx+1;
		height = maxy-miny+1;
		length = maxz-minz+1;

		up = f.getMaterial()!=Material.AIR;

		pertick = dropmode?ClassicEdit.droppertick:ClassicEdit.pertick;
		started = true;
	}

	int getAmount(Material m, short d, PlayerInventory inv){
		int ile = 0;
		for(ItemStack is : inv.getContents()){
			if(is==null) continue;
			if(isVariant(is, m, d)){
				ile += is.getAmount();
			}
		}
		return ile;
	}
	boolean isVariant(ItemStack is, Material m, short d){
		if(!is.getType().equals(m)) return false;
		if(!m.isBlock()) return true;
		switch(m){
		case RAILS:
		case HOPPER:
		case FURNACE:
		case PUMPKIN:
		case WOOD_STAIRS:
		case BIRCH_WOOD_STAIRS:
		case SPRUCE_WOOD_STAIRS:
		case JUNGLE_WOOD_STAIRS:
		case ACACIA_STAIRS:
		case DARK_OAK_STAIRS:
		case STEP:
		case WOOD_STEP:
		case SANDSTONE_STAIRS:
		case BRICK_STAIRS:
		case COBBLESTONE_STAIRS:
			return true;
		case LOG:
		case LOG_2:
			return (is.getDurability() & 3)==(d & 3);
		default:
			return d==is.getDurability();
		}
	}
	void setAmount(Material m, short d, PlayerInventory inv, int ile){
		
		for(int i = 0; i<inv.getSize(); i++){
			ItemStack is = inv.getItem(i);
			if(is==null) continue;
			if(isVariant(is, m, d)){
				if(is.getAmount()<=ile){
					ile -= is.getAmount();;
					inv.setItem(i, null);
				}else{
					is.setAmount(is.getAmount()-ile);
					return;
				}
			}
		}
		
	}

	public abstract boolean next();
	public void end(){
		Player p = Bukkit.getPlayer(nick);
		if(p!=null){
			p.sendMessage(ChatColor.YELLOW+"Created "+sum+" block"+(sum==1?"":"s")+" of "+getFilling().toString());
		}
	}

}
