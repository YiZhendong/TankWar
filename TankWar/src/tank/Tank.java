package tank;

import java.awt.*;
import java.awt.event.KeyEvent;

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

	private boolean good;
	private boolean live = true;
	enum Direction {L, LU, U, RU, R, RD, D, LD, STOP}


	private TankClient tc = null;


	public boolean isLive(){
		return live;
	}

	public void setLive(boolean live){
		this.live=live;
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

	//新的构造方法
	public Tank(int x,int y,boolean good,TankClient tc){
		this(x,y,good);
		this.tc = tc;
	}
	/**
	 * 画出坦克
	 * @param g graphics
	 */
	public void draw(Graphics g){
		if(!live) return;
		Color c = g.getColor();
		if(good) g.setColor(Color.RED);
		else g.setColor(Color.BLUE);

		g.fillOval(x,y,WIDTH,HEIGHT);
		g.setColor(c);

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

		if(this.dir != Direction.STOP){
			this.ptDir = this.dir;
		}
		//阻止坦克出界
		if(x < 0) x = 0;
		if(y < 30) y = 30;
		if(x + Tank.WIDTH > TankClient.GAMEWIDTH) x = TankClient.GAMEWIDTH - Tank.WIDTH;
		if(y + Tank.HEIGHT > TankClient.GAMEHEIGHT) y = TankClient.GAMEHEIGHT - Tank.HEIGHT;
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
		int x = this.x + Tank.WIDTH/2 - Missile.WIDTH/2;
		int y = this.y + Tank.HEIGHT/2 - Missile.HEIGHT/2;
		Missile m = new Missile(x,y,ptDir);
		tc.missiles.add(m);
		return m;
	}

	public void keyRealeased(KeyEvent e){
		int key = e.getKeyCode();
		switch(key){
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

}
