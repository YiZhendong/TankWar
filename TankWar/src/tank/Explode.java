package tank;

import java.awt.*;
import java.awt.image.*;
import java.awt.Toolkit.*;
import java.util.Observer;

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
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static boolean init = false;
	private static Image[] imgs = {
		tk.getImage(Explode.class.getClassLoader().getResource("images/0.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/1.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/2.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/3.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/4.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/5.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/6.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/7.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/8.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/9.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/10.gif"))
	};
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

		//提前初始化
		if(!init){
			for (int i=0;i<imgs.length;i++) {
				g.drawImage(imgs[i],-100,-100,null);
			}
			init = true;
		}


		if(!live) {
			tc.explodes.remove(this);
			return;
		}

		if(step == imgs.length){
			live = false;
			step = 0;
			return;
		}

		g.drawImage(imgs[step],x,y,null);
		step++;
	}
}
