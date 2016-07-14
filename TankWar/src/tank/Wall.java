package tank;

import java.awt.*;

/**
 * Created by zhendong on 2016/7/13.
 * email:myyizhendong@gmail.com
 */
public class Wall{
	private static int WIDTH = 10;
	private static int HEIGHT = 30;
	private  int x=10;
	private  int y=10;
	private boolean live = true;
	private TankClient tc;



	public Wall(int x,int y){
		this.x = x;
		this.y = y;
	}

	public Wall(int x,int y, TankClient tc){
		this(x,y);
		this.tc = tc;
	}


	public void draw(Graphics g){
		if(!live) {
			tc.walls.remove(this);
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.fillRect(x,y,WIDTH,HEIGHT);
		g.setColor(c);
	}

	/**
	 *
	 * @return 返回墙所在的矩形
	 */
	public Rectangle getRect(){
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}


	public boolean isLive(){
		return live;
	}

	public void setLive(boolean live){
		this.live = live;
	}

}
