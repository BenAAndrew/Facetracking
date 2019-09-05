package main;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;

import org.opencv.core.Core;

import classifier.ImageClassifier;
import properties.Properties;
import stream.ClassifyStream;
import stream.Stream;

public class Main {
	private static Properties prop = new Properties();
	private static Stream stream;
	private static ImageClassifier imageClassifier;
	private static PacketHandler packetHandler;
	private static ClassifyStream classifyStream;
	private static Timestamper timestamper;
	
	public static boolean verbose = false;	
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		// load the native OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		imageClassifier = new ImageClassifier(prop);
		packetHandler = new PacketHandler(prop);
		stream = new Stream(prop);
		classifyStream = new ClassifyStream(prop, imageClassifier, packetHandler, stream);
		timestamper = new Timestamper(imageClassifier, packetHandler, classifyStream, stream);
		
		System.out.println("LISTENING TO: "+prop.getStreamAddress());
		
		Scanner in = new Scanner(System.in);
		System.out.println("Launch mode (1:Normal, 2:Verbose, 3:Test Capture, 4:Test Packet, 5:Timestamp):");
		int opt = in.nextInt();
		switch(opt) {
			case 1: classifyStream.run(); break;
			case 2: verbose = true; classifyStream.run(); break;
			case 3: stream.captureImage(); break;
			case 4: packetHandler.testPacket(); break;
			case 5: timestamper.timestamp(); break;
		}
		in.close();
	}
}
