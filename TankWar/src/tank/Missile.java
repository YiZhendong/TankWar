package tank;

import java.awt.*;

/**
 * Created by zhendong on 2016/7/12.
 * email:myyizhendong@gmail.com
 */
public class Missile{
	private int x,y;
	private Tank.Direction dir;
	public static final int WIDTH = 10,HEIGHT = 10;
	public static final int XSPEED = 10,YSPEED = 10;
	private boolean live = true;


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

	/**
	 * 画一个子弹
	 * @param g
	 */
	public void draw(Graphics g){
		if(!live) return;
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
		if(this.getRect().intersects(t.getRect()) && t.isLive()){
			this.live = false;
			t.setLive(false);
			return true;
		}
		return false;
	}

}
