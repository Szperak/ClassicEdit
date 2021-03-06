package pl.cba.knest.ClassicEdit.creation;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import pl.cba.knest.ClassicEdit.ClassicEdit;



public class MazeCreation extends PlaceableCreation{
	
	Random r = new Random();
	int mazex = 1;
	int mazez = 1;
	
	int mazew = 1;
	int mazeh = 1;
	
	byte[][] cells;
	
	Queue<Block> q = new LinkedList<Block>();
	
	
	private static final int UP = 1;
	private static final int RIGHT = 2;
	private static final int DOWN = 3;
	private static final int LEFT = 4;
	private static final int IS = 16;



	@Override
	public boolean canPlace(int x, int y, int z){
		return true;
	}
	
	@Override
	public boolean init(){
		if(!super.init()) return false;
		if(Math.max(width, height)>ClassicEdit.getLimit(session.getPlayer(), dropmode)){
			msgPlayer(ChatColor.RED+"Too many blocks to place");
			stop();
			return false;
		}
		mazew = (width/2)+1;
		mazeh = (length/2)+1;
		cells = new byte[mazew][mazeh];		
		mazex = r.nextInt(mazew);
		mazez = r.nextInt(mazeh);
		setIs(mazex,mazez);
		int x = minx+mazex*2;
		int z = minz+mazez*2;
		for(int y = 0; y<height;y++){
			q.add(w.getBlockAt(x,miny+y,z));
		}
		next();
		return true;
	}
	
	private void queueBlocks(int mazex, int mazez,int dir){
		int dirx = dirX(dir);
		int dirz = dirZ(dir);
		int x = minx+mazex*2;
		int z = minz+mazez*2;
		for(int y = 0; y<height;y++){
			q.add(w.getBlockAt(x+dirx,miny+y,z+dirz));
			q.add(w.getBlockAt(x+dirx*2,miny+y,z+dirz*2));
		}
	}
	
	private int swap(int dir){
		if(dir == UP) return DOWN;
		if(dir == RIGHT) return LEFT;
		if(dir == DOWN) return UP;
		if(dir == LEFT) return RIGHT;
		return 0;
	}
	private byte current(){
		return cells[mazex][mazez];
	}
	public boolean search(){
		int dir = randDir(mazex, mazez);
		
		if(dir != 0){
			queueBlocks(mazex,mazez,dir);
			move(dir);
			setIs(mazex,mazez);
			appendBits(mazex,mazez,swap(dir));
			
			
		}else{
			if(!move(current())){
				return false;
			}else{
				search();
			}
		}
		return true;
	}
	
	
	private byte dirX(int dir){
		if(dir == RIGHT) return 1;
		if(dir == LEFT) return -1;
		return 0;
	}
	private byte dirZ(int dir){
		if(dir == UP) return 1;
		if(dir == DOWN) return -1;
		return 0;
	}
	private boolean move(int dir){
		dir &= 7;
		if(dir == UP) mazez += 1;
		else if(dir == RIGHT) mazex += 1;
		else if(dir == DOWN) mazez -= 1;
		else if(dir == LEFT) mazex -= 1;
		else return false;
		return true;
	}
	private int randDir(int mazex, int mazez){
		ArrayList<Integer> poss = new ArrayList<Integer>();
		if(!is(mazex,mazez,UP)) poss.add(UP);
		if(!is(mazex,mazez,DOWN)) poss.add(DOWN);
		if(!is(mazex,mazez,LEFT)) poss.add(LEFT);
		if(!is(mazex,mazez,RIGHT)) poss.add(RIGHT);
		if(poss.size()==0) return 0;
		int rand = r.nextInt(poss.size());
		return poss.get(rand);
	}
	private boolean is(int mazex, int mazez){
		if(mazex<0 || mazez<0 || mazex>=mazew || mazez>=mazeh) return true;
		return (cells[mazex][mazez] & IS) == IS;
	}
	private boolean is(int mazex, int mazez, int dir){
		return is(mazex+dirX(dir),mazez+dirZ(dir));
	}
	private void setIs(int mazex, int mazez){
		appendBits(mazex,mazez, IS);
	}
	private void appendBits(int mazex, int mazez, int bits){
		cells[mazex][mazez] |= bits;
	}
	@Override
	public boolean next(){
		if(q.size()<1){
			try{
				if(!search()){
					return false;
				}
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		
		Block b = q.poll();
		if(b==null){
			return false;
		}
		currentx = b.getX();
		currenty = b.getY();
		currentz = b.getZ();
		
		return true;
	}
	@Override
	public String getName() {
		return "maze";
	}





}
