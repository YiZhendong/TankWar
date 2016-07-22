import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhendong on 2016/7/17.
 * email:myyizhendong@gmail.com
 */
public class TankServer{
	private static int ID = 100;
	public static final int TCP_PORT=8888;
	public static final int UDP_PORT=7777;
	List<Client> clients = new ArrayList<Client>();
	public void start(){

		new Thread(new UDPThread()).start();
		//1.服务端建立
		ServerSocket ss = null;

		//注意，在选择端口时，必须小心。每一个端口提供一种特定的服务，只有给出正确的端口，
		// 才 能获得相应的服务。0~1023的端口号为系统所保留，
		// 例如http服务的端口号为80,
		// telnet服务的端口号为21,
		// ftp服务的端口号为23,
		// 所以我们在选择端口号时，最好选择一个大于1023的数以防止发生冲突。
		try {
			ss = new ServerSocket(TCP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//repeat
		while(true){
			Socket s = null;
			try {
				//2.accept()方法
				s = ss.accept();

				//3.输入输出
				DataInputStream dis = new DataInputStream(s.getInputStream());
				String IP = s.getInetAddress().getHostAddress();

				int udpPort = dis.readInt();
				Client c = new Client(IP,udpPort);
				clients.add(c);

				//在服务器写入id
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeInt(ID++);

				System.out.println("A client connect!Addr:-"+s.getInetAddress()+",port:"+s.getPort()+ "----UDP Port:" + udpPort);
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				//4.close()
				if(s!=null){
					try {
						s.close();
						s = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
	public static void main(String args[]){
		new TankServer().start();
	}
	private class Client{
		String IP;
		int udpPort;
		public Client(String IP,int udpPort){
			this.IP = IP;
			this.udpPort = udpPort;
		}
	}

	private class UDPThread implements Runnable{
		byte[] buf = new byte[1024];
		public  void run(){
			//1.构建DatagramSocket实例，指定本地端口。
			DatagramSocket ds = null;
			try {
				ds = new DatagramSocket(UDP_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			System.out.println("UDP thread started at port : "+UDP_PORT);
			while(ds != null){

				//2.构建需要收发的DatagramPacket报文
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
					//每接受一个包，将这个包转发到其他的客户端
					for(int i = 0;i < clients.size();i++){
						Client c = clients.get(i);
						dp.setSocketAddress(new InetSocketAddress(c.IP,c.udpPort));
						ds.send(dp);
					}
					System.out.println("a packet received");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
