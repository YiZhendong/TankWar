package tank;

import java.awt.*;

/**
 * Created by zhendong on 2016/7/14.
 * email:myyizhendong@gmail.com
 */
public class Blood{
	/**
	 * x,y,w,h：int 分别代表血块的x，y坐标，以及宽度和高度
	 * live：boolean 血块是否还活着
	 * step：int 步数
	 * pos：int[][] 血块的活动坐标
	 */
	int x,y,w,h;

	private boolean live = true;
	TankClient tc;

	int step = 0;

	/*
	 * 指明血块的轨迹
	 */
	private int[][] pos = {
			{350,300},{360,300},{375,275},{400,200},{360,270},{365,290},{340,280}
	};

	/**
	 * 构造方法
	 */
	public Blood(){
		x = pos[0][0];
		y = pos[0][1];
		w = h = 15;
	}

	/**
	 * setter和getter方法
	 * @return
	 */
	public boolean isLive(){
		return live;
	}

	public void setLive(boolean live){
		this.live = live;
	}

	/**
	 * 画出血块
	 * @param g
	 */
	public void draw(Graphics g){
		if(!live) return;
		Color c = g.getColor();
		g.setColor(Color.MAGENTA);
		g.fillRect(x,y,w,h);
		g.setColor(c);

		move();
	}

	/**
	 * 血块的移动
	 */
	private void move(){
		step++;
		if(step == pos.length){
			step = 0;
		}
		x = pos[step][0];
		y = pos[step][1];

	}

	/**
	 * 返回血块所在的矩形
	 * @return
	 */
	public Rectangle getRect(){
		return new Rectangle(x,y,w,h);
	}
}
