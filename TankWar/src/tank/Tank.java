package tank;

import com.sun.org.apache.bcel.internal.generic.RETURN;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;

/**
 * 坦克类，用来存放和生成关于坦克的相关方法与变量
 * Created by zhendong on 2016/7/12.
 * email:myyizhendong@gmail.com
 */
public class Tank{
	/**
	 * int x,y坦克的x坐标，y坐标
	 * int XSPEED,YSPEED:坦克x方向和y方向的速度
	 * int WIDTH，HEIGHT：坦克所在长方形的宽度和高度
	 * Direction ptDir:炮筒的方向
	 * Direciont dir:坦克的方向
	 * boolean bL,bU,bR,bD:方向是否为真
	 * int oldX,oldY:移动之前的坐标，用来处理碰撞之后返回原位置
	 * boolean good:是否是我方坦克
	 * boolean live:是否还活着
	 * enum Direction:8个方向
	 * BloodBar bb:血槽内部类
	 * int life:血量，默认是80
	 * TankClient tc: TankClient 类
	 * Random r:产生随机数
	 */
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


	private BloodBar bb = new BloodBar();

	private int life = 80;

	private TankClient tc = null;

	//生成一个静态的随机数产生器
	private static Random r = new Random();

	private int step = r.nextInt(9) + 3;

	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] TankImages = null;
	private static Map<String , Image> imgs = new HashMap<>();
	static {
		TankImages = new Image[]
		{
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankL.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankLU.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankU.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankRU.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankR.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankRD.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankD.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankLD.gif"))
		};
		imgs.put("L",TankImages[0]);
		imgs.put("LU",TankImages[1]);
		imgs.put("U",TankImages[2]);
		imgs.put("RU",TankImages[3]);
		imgs.put("R",TankImages[4]);
		imgs.put("RD",TankImages[5]);
		imgs.put("D",TankImages[6]);
		imgs.put("LD",TankImages[7]);
	}

	/**
	 * getter与setter方法
	 * @return
	 */

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
	 * 坦克的构造方法
	 * @param x，int 坦克所在矩形的x坐标
	 * @param y，int 坦克所在矩形的y坐标
	 */
	public Tank(int x, int y,boolean good){
		this.x=x;
		this.y=y;
		this.good=good;
	}

	/**
	 * 坦克的构造方法
	 * @param x ，同上
	 * @param y ，同上
	 * @param good ，boolean 坦克是否为我方坦克
	 * @param tc ,TankClient
	 */
	public Tank(int x,int y,boolean good,TankClient tc){
		this(x,y,good);
		this.tc = tc;
	}

	/**
	 * 坦克的构造方法
	 * @param x ，同上
	 * @param y ，同上
	 * @param good ，同上
	 * @param tc ，同上
	 * @param dir ，Direction 坦克方向
	 */
	public Tank(int x,int y,boolean good,TankClient tc,Direction dir){
		this(x,y,good);
		this.tc = tc;
		this.dir = dir;
	}


	/**
	 * 坦克的构造方法
	 * @param x ，同上
	 * @param y ，同上
	 * @param good ，同上
	 * @param tc ，同上
	 * @param dir ，Direction 坦克方向
	 * @param life , int 生命值
	 */

	public Tank(int x,int y,boolean good,TankClient tc,Direction dir,int life){
		this(x,y,good,tc,dir);
		this.life = life;
	}



	/**
	 * 画出坦克
	 * 如果坦克不再存活，从tanks中移除
	 * 否则，画出该坦克
	 * @param g graphics
	 */
	public void draw(Graphics g){
		if(!live) {
			if(!good){
				tc.tanks.remove(this);
			}
			return;
		}


		if(good) bb.draw(g);
		switch (ptDir) {
			case L:
				g.drawImage(imgs.get("L"),x,y,null);
				break;
			case LU:
				g.drawImage(imgs.get("LU"),x,y,null);
				break;
			case U:
				g.drawImage(imgs.get("U"),x,y,null);
				break;
			case RU:
				g.drawImage(imgs.get("RU"),x,y,null);
				break;
			case R:
				g.drawImage(imgs.get("R"),x,y,null);
				break;
			case RD:
				g.drawImage(imgs.get("RD"),x,y,null);
				break;
			case D:
				g.drawImage(imgs.get("D"),x,y,null);				break;
			case LD:
				g.drawImage(imgs.get("LD"),x,y,null);
				break;
		}
		move();
	}


	/**
	 * 根据dir来移动
	 * 事先存放oldX,oldY,当发生碰撞时返回旧坐标
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

		/*
		如果和墙碰撞，呆在原地
		 */
		if(collidesWithWalls(tc.walls)){
			stay();
		}

		/*
		如果和坦克碰撞，呆在原地
		 */
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

	/**
	 * 呆在原地
	 */
	private void stay(){
		this.x = oldX;
		this.y = oldY;
	}

	/**
	 * 撞墙序列
	 * @param walls 被撞的墙的序列
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

	/**
	 * 撞墙
	 * @param wall 被撞的墙
	 * @return boolean 如果碰撞了，返回true，否则返回false；
	 */
	private boolean collidesWithWall(Wall wall){
		if(this.live && this.getRect().intersects(wall.getRect()) && wall.isLive()){
			return true;
		}
		return false;
	}

	/**
	 * 判断坦克序列之间是否碰撞
	 * @param tanks 被撞的坦克序列
	 * @return boolean 如果碰撞了，返回true，否则返回false；
	 */
	public boolean collidesWithTanks(List<Tank> tanks){
		for(int i = 0;i < tanks.size();i++){
			if(collidesWithTank(tanks.get(i)) && !this.equals(tanks.get(i))){
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断坦克之间是否碰撞
	 * @param tank 被撞的坦克
	 * @return boolean 如果碰撞了，返回true，否则返回false；
	 */
	private boolean collidesWithTank(Tank tank){
		if(this.live && this.getRect().intersects(tank.getRect()) && tank.isLive()){
			return true;
		}
		return false;
	}
	/**
	 * 响应键盘事件,设置方向的布尔值
	 * @param e KeyEvent e，键盘事件
	 *          left:控制坦克向左
	 *          right:控制坦克向右
	 *          up:控制坦克向上
	 *          down:控制坦克向下
	 *          F2:自己坦克死后可以按f2键复活
	 *
	 */
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		switch(key){
			case KeyEvent.VK_F2:
				if(this.live == false){
					this.setLife(100);
					this.dir = Direction.STOP;
					this.setLive(true);
				}
				break;
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

	/**
	 * fire方法，如果坦克还存活，可以生成子弹
	 * @return missile 返回一个子弹
	 */
	private Missile fire(){
		if(!live) return null;
		int x = this.x + Tank.WIDTH/2 - Missile.WIDTH/2;
		int y = this.y + Tank.HEIGHT/2 - Missile.HEIGHT/2;
		Missile m = new Missile(x,y,ptDir,this.tc,this.good);
		tc.missiles.add(m);
		return m;
	}

	/**
	 * fire方法，如果坦克还存活，可以生成dir方向的子弹
	 * @param dir 子弹的方向
	 * @return missile 返回一个dir方向的子弹
	 */
	private Missile fire(Direction dir){
		if(!live) return null;
		int x = this.x + Tank.WIDTH/2 - Missile.WIDTH/2;
		int y = this.y + Tank.HEIGHT/2 - Missile.HEIGHT/2;
		Missile m = new Missile(x,y,dir,this.tc,this.good);
		tc.missiles.add(m);
		return m;
	}

	/**
	 * 特殊fire方法，可以朝8个方向同时发射子弹
	 */
	private void specialFire(){
		Direction[] dirs = Direction.values();
		for (int i= 0;i< 8;i++) {
			fire(dirs[i]);
		}
	}

	/**
	 *
	 * KeyEvent e，键盘事件
	 *          left:控制坦克向左
	 *          right:控制坦克向右
	 *          up:控制坦克向上
	 *          down:控制坦克向下
	 *          F2:自己坦克死后可以按f2键复活
	 * @param e 键盘释放事件
	 */
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
	 * 返回坦克所在的矩形
	 * @return Rectangle，返回的矩形
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

	/**
	 * 判断坦克是否可以吃掉血块
	 * @param b ，Blood 血块
	 * @return boolean, 如果和血块碰撞，则吃了血块，返回true
	 *          否则，返回false
	 */
	public boolean eat(Blood b){
		if(this.live && b.isLive() && this.getRect().intersects(b.getRect())){
			this.life = 100;
			b.setLive(false);
			return true;
		}
		return false;
	}

}
