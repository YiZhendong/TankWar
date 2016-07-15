package tank;

import java.awt.*;

/**
 * 爆炸类
 * Created by zhendong on 2016/7/13.
 * email:myyizhendong@gmail.com
 */
public class Explode{
	/**
	 * live：爆炸是否还活着
	 * x，y爆炸的坐标
	 * TankClient ：tc工具类
	 * diameter[]：爆炸的维度
	 * step：步数
	 */
	private boolean live = true;
	private int x,y;
	private TankClient tc;
	private static int diameter[] = {4,7,12,18,26,32,49,30,14,6};
	int step = 0;

	public Explode(int x, int y, TankClient tc){
		this.x=x;
		this.y=y;
		this.tc=tc;
	}

	/**
	 * 显示爆炸
	 * @param g
	 */
	public void draw(Graphics g){
		if(!live) {
			tc.explodes.remove(this);
			return;
		}

		if(step == diameter.length){
			live = false;
			step = 0;
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.orange);
		g.fillOval(x,y,diameter[step],diameter[step]);
		g.setColor(c);
		step++;
	}
}
