package tank;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhendong on 2016/7/11.
 * email:myyizhendong@gmail.com
 */
public class TankClient extends Frame{

	public static final int GAMEWIDTH = 800;
	public static final int GAMEHEIGHT = 600;

	//定义一辆坦克,注意this
	Tank myTank = new Tank(50,50,true,this);

	//定义子弹
	//需要明确的引入util.List这个包
	List<Missile> missiles = new ArrayList<Missile>();

	List<Tank> tanks = new ArrayList<Tank>();
	//定义爆炸
	List<Explode> explodes = new ArrayList<Explode>();

	public void launchFrame(){

		for(int i=0 ;i<10 ;i++){
			tanks.add(new Tank(50+60*(i+1),50,false,this,Tank.Direction.D));
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
	public static void main (String args[]){
		TankClient tc = new TankClient();
		tc.launchFrame();
	}

	//paint 方法不用调用，自己就会执行
	@Override
	public void paint(Graphics g){

		g.drawString("missiles count:" + missiles.size(), 10, 50);
		g.drawString("exolodes count:" + explodes.size(), 10, 80);
		g.drawString("tanks count:" + tanks.size(), 10, 110);

		for(int i=0; i<missiles.size();i++){
			Missile m = missiles.get(i);
			/*//判断是否击中敌人坦克,如果打中坦克，将坦克的生命值设置为false
			if(m.hitTank(enemyTank)){
				enemyTank.setLive(false);
			}*/
			m.hitTanks(tanks);
			m.hitTank(myTank);
			m.draw(g);
			/*//判断子弹是否活着
			if(!m.isLive()) missiles.remove(m);
			else m.draw(g);*/
		}


		for (int i=0;i<explodes.size();i++){
			Explode e = explodes.get(i);
			e.draw(g);
		}

		for(int i = 0;i<tanks.size();i++){
			Tank t = tanks.get(i);
			t.draw(g);
		}
		myTank.draw(g);


	}

	//设置一个虚拟图片
	Image offScreenImage = null;

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
		gOffScreen.setColor(Color.GREEN);
		gOffScreen.fillRect(0,0,GAMEWIDTH,GAMEHEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage,0,0,null);
	}

	//新建一个内部类，线程控制坦克移动
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

	//键盘监听类,继承KeyAdapter
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
