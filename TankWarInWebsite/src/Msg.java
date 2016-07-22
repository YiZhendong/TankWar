import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by zhendong on 2016/7/18.
 * email:myyizhendong@gmail.com
 */
public interface Msg{
	public static final int TANK_NEW_MSG = 1;
	public static final int TANK_MOVE_MSG= 2;
	public static final int MISSIL_NEW_MSG = 3;
	public static final int TANK_DEAD_MSG = 4;
	public static final int MISSILE_DEAD_MSG =5;
	public void send(DatagramSocket ds, String IP, int udpPort);
	public void parse(DataInputStream dis);
}
