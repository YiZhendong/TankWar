import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * 网络相关的事情交给此类处理
 * Created by zhendong on 2016/7/17.
 * email:myyizhendong@gmail.com
 */
public class NetClient{
	TankClient tc;

	private int udpPort;

	String IP;

	DatagramSocket ds = null;

	//如果有多个线程访问，则需要考虑同步性问题
	public NetClient(TankClient tc){

		this.tc = tc;

	}


	public void connect(String IP, int PORT){
		this.IP = IP;
		try {
			ds = new DatagramSocket(udpPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		//1.根据指定的server地址和端口，建立socket连接
		Socket s = null;
		try {
			s = new Socket(IP,PORT);

			//2.根据socket实例获取InputStream,OutputStream进行数据读写
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(udpPort);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			int id = dis.readInt();
			tc.myTank.id = id;

			//根据奇数还是偶数设置好坏
			if(id%2 == 0) tc.myTank.good = false;
			else tc.myTank.good = true;

			System.out.println("Connected to server! and server give me a ID:" + id);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(s!=null)
				try{
					//3.操作结束，关闭socket
					s.close();
					s= null;
				} catch (IOException e){
					e.printStackTrace();
				}
		}
		TankNewMsg msg = new TankNewMsg(tc.myTank);
		send(msg);

		new Thread(new UDPRecvThread()).start();

	}

	//多态，大大增加了这个方法的方便性
	public void send(Msg msg){
		msg.send(ds,IP,TankServer.UDP_PORT);
	}

	private class UDPRecvThread implements Runnable{
		byte[] buf = new byte[1024];
		public void run (){
			while(ds != null){
				DatagramPacket dp = new DatagramPacket(buf,buf.length);
				try{
					ds.receive(dp);
					parse(dp);
					System.out.println("a packet received from server!");
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}

		private void parse(DatagramPacket dp){
			ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
			DataInputStream dis = new DataInputStream(bais);
			int msgType = 0;
			try {
				msgType = dis.readInt();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Msg msg = null;
			switch (msgType){
				case Msg.TANK_NEW_MSG:
					msg = new TankNewMsg(NetClient.this.tc);
					msg.parse(dis);
					break;
				case Msg.TANK_MOVE_MSG:
					msg = new TankMoveMsg(NetClient.this.tc);
					msg.parse(dis);
					break;
				case Msg.MISSIL_NEW_MSG:
					msg = new MissileNewMsg(NetClient.this.tc);
					msg.parse(dis);
					break;
				case Msg.TANK_DEAD_MSG:
					msg = new TankDeadMsg(NetClient.this.tc);
					msg.parse(dis);
					break;
				case Msg.MISSILE_DEAD_MSG:
					msg = new MissileDeadMsg(NetClient.this.tc);
					msg.parse(dis);
					break;
			}
		}
	}
	public int getUdpPort(){
		return udpPort;
	}

	public void setUdpPort(int udpPort){
		this.udpPort=udpPort;
	}

}
