package tank;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**这个类的作用是坦克游戏的主窗口
 * Created by zhendong on 2016/7/11.
 * email:myyizhendong@gmail.com
 */

public class TankClient extends Frame{

	public static final int GAMEWIDTH = 800;
	public static final int GAMEHEIGHT = 600;

	//定义一辆坦克,注意this
	Tank myTank = new Tank(50,500,true,this,Direction.STOP,80);

	/**
	 * 设置一个虚拟图片
	 * 定义子弹，坦克，爆炸，墙的数量，以及一个血块
	 * 需要明确的引入util.List这个包
	 */
	List<Missile> missiles = new ArrayList<Missile>();
	List<Tank> tanks = new ArrayList<Tank>();
	List<Explode> explodes = new ArrayList<Explode>();
	List<Wall> walls = new ArrayList<Wall>();
	Blood b = new Blood();
	Image offScreenImage = null;

	/**
	 * 本方法显示坦克主窗口
	 * 分别循环产生10个敌方坦克，10堵墙，定义游戏的界面
	 */
	public void launchFrame(){

		int initTankCount = Integer.parseInt(PropertyMgr.getProperty("initTankCount"));
		for(int i=0 ;i<initTankCount ;i++){
			tanks.add(new Tank(50+60*(i+1),50,false,this,Direction.D,10));
		}

		for(int i=0; i<10 ; i++){
			walls.add(new Wall(70,35*(i+1),this));
		}
		this.setLocation(400,300);
		this.setSize(GAMEWIDTH,GAMEHEIGHT);
		this.setTitle("TankWar");
		setVisible(true);
		//添加匿名类，监听关闭窗口的事件
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		this.setBackground(Color.GREEN);
		this.setResizable(false);

		//添加键盘监听器
		this.addKeyListener(new KeyMonitor());

		//启动线程
		new Thread(new PaintThread()).start();
	}

	/**
	 * 坦克游戏主方法
	 * @param args
	 */
	public static void main (String args[]){
		TankClient tc = new TankClient();
		tc.launchFrame();
	}

	/**
	 * paint 方法不用调用，自己就会执行
	 */
	@Override
	public void paint(Graphics g){

		g.drawString("missiles count:" + missiles.size(), 10, 50);
		g.drawString("exolodes count:" + explodes.size(), 10, 80);
		g.drawString("tanks count:" + tanks.size(), 10, 110);
		g.drawString("walls count:" + walls.size(), 10, 140);
		g.drawString("mytank life:" + myTank.getLife(), 10, 170);

		if(tanks.size()<=0){
			for(int i=0 ;i<Integer.parseInt(PropertyMgr.getProperty("reProduceTankCount"));i++){
				tanks.add(new Tank(50+60*(i+1),50,false,this,Direction.D,10));
			}
		}

		for(int i=0; i<missiles.size();i++){
			Missile m = missiles.get(i);
			m.hitTanks(tanks);
			m.hitTank(myTank);
			m.hitWalls(walls);
			m.draw(g);
		}



		for (int i=0;i<explodes.size();i++){
			Explode e = explodes.get(i);
			e.draw(g);
		}

		for(int i = 0;i<tanks.size();i++){
			Tank t = tanks.get(i);
			t.collidesWithWalls(walls);
			t.collidesWithTanks(tanks);
			t.draw(g);
		}
		b.draw(g);
		myTank.draw(g);
		myTank.eat(b);

		for(int i = 0; i < walls.size(); i++){
			Wall w = walls.get(i);
			w.draw(g);
		}

	}



	/**
	 * 利用双缓冲消除闪烁
	 * @param g
	 */
	@Override
	//repaint首先会调用update方法，然后调用paint方法

	public void update(Graphics g){
		if(offScreenImage == null ){
			offScreenImage = this.createImage(GAMEWIDTH,GAMEHEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.BLACK);
		gOffScreen.fillRect(0,0,GAMEWIDTH,GAMEHEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage,0,0,null);
	}

	/**
	 * 新建一个内部类，线程类，控制坦克移动
	 *
	 */
	public class PaintThread implements Runnable{
		public void run(){
			while(true){
				//repaint是调用的外部包装类的方法
				repaint();
				try{
					Thread.sleep(50);
				}catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 键盘监听类,继承KeyAdapter
	 * 监听键盘按下和弹起的事件
	 *
	 */
	private class KeyMonitor extends KeyAdapter{

		@Override
		public void keyPressed(KeyEvent e){
			myTank.keyPressed(e);
		}

		@Override
		public void keyReleased(KeyEvent e){
			myTank.keyRealeased(e);
		}
	}
}
