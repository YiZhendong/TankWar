package tank;

import java.awt.*;
import java.util.List;
/**
 * Created by zhendong on 2016/7/12.
 * email:myyizhendong@gmail.com
 */
public class Missile{

	private TankClient tc;
	private int x,y;
	private Tank.Direction dir;
	public static final int WIDTH = 10,HEIGHT = 10;
	public static final int XSPEED = 10,YSPEED = 10;
	private boolean live = true;
	private boolean good;

	public boolean isLive(){
		return live;
	}



	/**
	 * 子弹类的构造方法
	 * @param x 子弹的x坐标
	 * @param y 子弹的y坐标
	 * @param dir 坦克的方向
	 */
	public Missile(int x, int y, Tank.Direction dir){
		this.x=x;
		this.y=y;
		this.dir=dir;
	}
	public Missile(int x,int y, Tank.Direction dir,TankClient tc,boolean good){
		this(x,y,dir);
		this.tc = tc;
		this.good = good;
	}

	/**
	 * 画一个子弹
	 * @param g
	 */
	public void draw(Graphics g){
		if(!live) {
			tc.missiles.remove(this);
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.fillOval(x,y,WIDTH,HEIGHT);
		g.setColor(c);

		move();
	}

	/**
	 * 根据方向移动
	 */
	private void move(){
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
		}
		//如果子弹出界，则令 子弹死亡
		if( x < 0 || y < 0 || x > TankClient.GAMEWIDTH || y > TankClient.GAMEHEIGHT){
			live = false;
			tc.missiles.remove(this);
		}

	}

	/**
	 *
	 * @return 返回子弹所在的矩形
	 */
	public Rectangle getRect(){
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

	/**
	 *判断子弹是否打中坦克
	 * @param t 坦克t,判断子弹是否打中该坦克
	 * @return  true: 坦克被子弹打中；false：坦克没有被子弹打中
	 */
	public boolean hitTank(Tank t){
		if(this.live && this.getRect().intersects(t.getRect()) && t.isLive()&& this.good!=t.isGood()){
			this.live = false;
			t.setLife(t.getLife() -10);
			if(t.getLife()==0){
				t.setLive(false);
			}
			Explode e = new Explode(x,y,tc);
			tc.explodes.add(e);
			return true;
		}
		return false;
	}

	/**
	 * 判断子弹是否打中坦克序列
	 * @param tanks 坦克序列
	 * @return  boolean 类型
	 */
	public boolean hitTanks(List<Tank> tanks){
		for(int i = 0; i < tanks.size(); i++){
			if(hitTank(tanks.get(i))){
				return true;
			}
		}
		return false;
	}

	public boolean hitWalls(List<Wall> walls){
		for(int i = 0;i < walls.size();i++){
			if(hitWall(walls.get(i))){
				return true;
			}
		}
		return false;
	}

	private boolean hitWall(Wall wall){
		if(this.live && this.getRect().intersects(wall.getRect()) && wall.isLive()){
			this.live = false;
			wall.setLive(false);
			return true;
		}
		return false;
	}
}
