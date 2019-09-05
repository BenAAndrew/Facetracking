package main;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import properties.Properties;

public class PacketHandler {
	private String ip;
	private int port;
	private DatagramSocket socket;
	private InetAddress address;
	
	private static int[] TEST_VALUES = {0,0};
	
	public PacketHandler(Properties prop) {
		this.ip = prop.getStreamIp();
		this.port = prop.getOutgoingPort();
		
		//initialise socket
		try {
			socket = new DatagramSocket();
			address = InetAddress.getByName(ip);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void sendUDPPacket(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		try {
			if(Main.verbose) {
				System.out.println("SENT "+data.toString());
			}
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] convertDataToBytes(int[] values) {
		//int = 4 bytes
		ByteBuffer byteBuffer = ByteBuffer.allocate(values.length * 4);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(values);
		return byteBuffer.array();
	}
	
	public void sendData(int[] data) {
		byte[] bytes = convertDataToBytes(data);
		sendUDPPacket(bytes);
	}
	
	public void testPacket() throws IOException {
		sendData(TEST_VALUES);
		System.out.println("Packet sent");
	}
}
