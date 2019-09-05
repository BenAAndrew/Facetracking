package properties;

public class Properties {
	private PropertyLoader prop;
	
	public Properties() {
		prop = new PropertyLoader();
	}
	
	public String getStreamIp() {
		return prop.getProperty("stream_ip");
	}
	
	public String getStreamAddress() {
		return "http://"+getStreamIp()+":"+
				prop.getProperty("stream_port")+prop.getProperty("stream_extension");
	}
	
	public int getOutgoingPort() {
		return Integer.valueOf(prop.getProperty("outgoing_port"));
	}
	
	public String getClassifier() {
		return prop.getProperty("classfier");
	}
	
	public float getXSensitivity() {
		return Float.valueOf(prop.getProperty("x_sensitivity"));
	}
	
	public float getYSensitivity() {
		return Float.valueOf(prop.getProperty("y_sensitivity"));
	}
}