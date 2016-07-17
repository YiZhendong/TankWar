package tank;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by zhendong on 2016/7/17.
 * email:myyizhendong@gmail.com
 */
public class PropertyMgr{
	static Properties props = new Properties();
	static{
		try {
			props.load(PropertyMgr.class.getClassLoader().getResourceAsStream("properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private PropertyMgr(){};
	public static String getProperty(String key){
		return props.getProperty(key);
	}
}
