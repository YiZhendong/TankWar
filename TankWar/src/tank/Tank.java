package tank;

import com.sun.org.apache.bcel.internal.generic.RETURN;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.List;

/**
 * Created by zhendong on 2016/7/12.
 * email:myyizhendong@gmail.com
 */
public class Tank{
	private int x,y;
	private static final int XSPEED = 5;
	private static final int YSPEED = 5;

	private static final int WIDTH = 50;
	private static final int HEIGHT = 50;

	//注意static的使用，如果使用了static，则一个变量会在全局都在使用！！！
	private Direction ptDir = Direction.D;
	private Direction dir = Direction.STOP;

	private boolean bL = false,bU = false,bR = false,bD = false;

	private int oldX;
	private int oldY;

	private boolean good;
	private boolean live = true;
	enum Direction {L, LU, U, RU, R, RD, D, LD, STOP}

	private BloodBar bb = new BloodBar();

	private int life = 80;

	private TankClient tc = null;

	//生成一个静态的随机数产生器
	private static Random r = new Random();

	private int step = r.nextInt(9) + 3;


	public int getLife(){
		return life;
	}

	public void setLife(int life){
		this.life=life;
	}

	public boolean isLive(){
		return live;
	}

	public void setLive(boolean live){
		this.live=live;
	}


	public boolean isGood(){
		return good;
	}

	/**
	 * 构造坦克
	 * @param x
	 * @param y
	 */
	public Tank(int x, int y,boolean good){
		this.x=x;
		this.y=y;
		this.good=good;
	}

	public Tank(int x,int y,boolean good,TankClient tc){
		this(x,y,good);
		this.tc = tc;
	}
	//新的构造方法
	public Tank(int x,int y,boolean good,TankClient tc,Tank.Direction dir){
		this(x,y,good);
		this.tc = tc;
		this.dir = dir;
	}

	public Tank(int x,int y,boolean good,TankClient tc,Tank.Direction dir,int life){
		this(x,y,good,tc,dir);
		this.life = life;
	}



	/**
	 * 画出坦克
	 * @param g graphics
	 */
	public void draw(Graphics g){
		if(!live) {
			if(!good){
				tc.tanks.remove(this);
			}
			return;
		}

		Color c = g.getColor();
		if(good) g.setColor(Color.RED);
		else g.setColor(Color.BLUE);

		g.fillOval(x,y,WIDTH,HEIGHT);
		g.setColor(c);
		if(good) bb.draw(g);
		switch (ptDir) {
			case L:
				g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y + Tank.HEIGHT / 2);
				break;
			case LU:
				g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y);
				break;
			case U:
				g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH / 2, y);
				break;
			case RU:
				g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH, y);
				break;
			case R:
				g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH , y + Tank.HEIGHT / 2);
				break;
			case RD:
				g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH, y + Tank.HEIGHT);
				break;
			case D:
				g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH / 2, y + Tank.HEIGHT);
				break;
			case LD:
				g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y + Tank.HEIGHT);
				break;
		}
		move();
	}


	/**
	 * 根据dir来移动
	 */
	public void move(){
		oldX = x;
		oldY = y;
		switch (dir){
			case L:
				x -= XSPEED;
				break;
			case LU:
				x -= XSPEED;
				y -= YSPEED;
				break;
			case U:
				y -= YSPEED;
				break;
			case RU:
				x += XSPEED;
				y -= YSPEED;
				break;
			case R:
				x += XSPEED;
				break;
			case RD:
				x += XSPEED;
				y += YSPEED;
				break;
			case D:
				y += YSPEED;
				break;
			case LD:
				x -= XSPEED;
				y += YSPEED;
				break;
			case STOP:
				break;
		}
		if(collidesWithWalls(tc.walls)){
			stay();
		}

		if(collidesWithTanks(tc.tanks)){
			stay();
		}

		if(this.dir != Direction.STOP){
			this.ptDir = this.dir;
		}
		//阻止坦克出界
		if(x < 0) x = 0;
		if(y < 30) y = 30;
		if(x + Tank.WIDTH > TankClient.GAMEWIDTH) x = TankClient.GAMEWIDTH - Tank.WIDTH;
		if(y + Tank.HEIGHT > TankClient.GAMEHEIGHT) y = TankClient.GAMEHEIGHT - Tank.HEIGHT;

		//改变一次方向，以及走的步数
		if(!good){
			Direction[] dirs = Direction.values();

			if(step == 0){
				step = r.nextInt(9) + 3;
				int rn = r.nextInt(dirs.length);
				dir = dirs[rn];
			}

			step--;
			if(r.nextInt(40)>38){
				this.fire();
			}
		}


	}

	private void stay(){
		this.x = oldX;
		this.y = oldY;
	}

	/**
	 *
	 * @param walls
	 * @return boolean 如果碰撞了，返回true，否则返回false；
	 */
	public boolean collidesWithWalls(List<Wall> walls){
		for(int i = 0;i < walls.size();i++){
			if(collidesWithWall(walls.get(i))){
				return true;
			}
		}
		return false;
	}

	private boolean collidesWithWall(Wall wall){
		if(this.live && this.getRect().intersects(wall.getRect()) && wall.isLive()){
			return true;
		}
		return false;
	}

	/**
	 * 判断坦克之间是否碰撞
	 * @param tanks
	 * @return
	 */
	public boolean collidesWithTanks(List<Tank> tanks){
		for(int i = 0;i < tanks.size();i++){
			if(collidesWithTank(tanks.get(i)) && !this.equals(tanks.get(i))){
				return true;
			}
		}
		return false;
	}

	private boolean collidesWithTank(Tank tank){
		if(this.live && this.getRect().intersects(tank.getRect()) && tank.isLive()){
			return true;
		}
		return false;
	}
	/**
	 * 响应键盘事件,设置方向的布尔值
	 * @param e KeyEvent e
	 */
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		switch(key){
			case KeyEvent.VK_LEFT:
				bL = true;
				break;
			case KeyEvent.VK_UP:
				bU = true;
				break;
			case KeyEvent.VK_RIGHT:
				bR = true;
				break;
			case KeyEvent.VK_DOWN:
				bD = true;
				break;
		}
		relocate();
	}

	private Missile fire(){
		if(!live) return null;
		int x = this.x + Tank.WIDTH/2 - Missile.WIDTH/2;
		int y = this.y + Tank.HEIGHT/2 - Missile.HEIGHT/2;
		Missile m = new Missile(x,y,ptDir,this.tc,this.good);
		tc.missiles.add(m);
		return m;
	}

	private Missile fire(Direction dir){
		if(!live) return null;
		int x = this.x + Tank.WIDTH/2 - Missile.WIDTH/2;
		int y = this.y + Tank.HEIGHT/2 - Missile.HEIGHT/2;
		Missile m = new Missile(x,y,dir,this.tc,this.good);
		tc.missiles.add(m);
		return m;
	}

	private void specialFire(){
		Direction[] dirs = Direction.values();
		for (int i= 0;i< 8;i++) {
			fire(dirs[i]);
		}
	}

	public void keyRealeased(KeyEvent e){
		int key = e.getKeyCode();
		switch(key){
			case KeyEvent.VK_A:
				specialFire();
				break;
			case KeyEvent.VK_CONTROL:
				fire();
				break;
			case KeyEvent.VK_LEFT:
				bL = false;
				break;
			case KeyEvent.VK_UP:
				bU = false;
				break;
			case KeyEvent.VK_RIGHT:
				bR = false;
				break;
			case KeyEvent.VK_DOWN:
				bD = false;
				break;
		}
		relocate();
	}



	/**
	 * 重新改变方向
	 */
	public void relocate(){
		if(bL && !bU && !bR && !bD) dir = Direction.L;
		else if(bL && bU && !bR && !bD) dir = Direction.LU;
		else if(!bL && bU && !bR && !bD) dir = Direction.U;
		else if(!bL && bU && bR && !bD) dir = Direction.RU;
		else if(!bL && !bU && bR && !bD) dir = Direction.R;
		else if(!bL && !bU && bR && bD) dir = Direction.RD;
		else if(!bL && !bU && !bR && bD) dir = Direction.D;
		else if(bL && !bU && !bR && bD) dir = Direction.LD;
		else if(!bL && !bU && !bR && !bD) dir = Direction.STOP;
	}

	/**
	 *
	 * @return 返回坦克所在的矩形
	 */
	public Rectangle getRect(){
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

	private class BloodBar{
		public void draw(Graphics g){
			Color c = g.getColor();
			g.setColor(Color.RED);
			g.drawRect(x,y-10,WIDTH,10);
			int w = WIDTH * life/80;
			g.fillRect(x,y-10,w,10);
			g.setColor(c);
		}

	}

	public boolean eat(Blood b){
		if(this.live && b.isLive() && this.getRect().intersects(b.getRect())){
			this.life = 100;
			b.setLive(false);
			return true;
		}
		return false;
	}

}
