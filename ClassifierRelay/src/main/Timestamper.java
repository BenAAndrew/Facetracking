package main;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import classifier.ImageClassifier;
import stream.ClassifyStream;
import stream.Stream;

public class Timestamper {
	private ImageClassifier imageClassifier;
	private PacketHandler packetHandler;
	private ClassifyStream classifyStream;
	private Stream stream;
	private Logger logger;
	
	private static String[] TIMESTAMP_STAGES = {"Start","Decode Image","Calculate face difference","Send packet"};
	
	public Timestamper(ImageClassifier imageClassifier, PacketHandler packetHandler, ClassifyStream classifyStream, Stream stream) {
		this.logger = Logger.getLogger(Timestamper.class.getName());
		this.imageClassifier = imageClassifier;
		this.packetHandler = packetHandler;
		this.classifyStream = classifyStream;
		this.stream = stream;
	}
	
	public long getTime() {
		return new Date().getTime();
	}
	
	public String getTimeString() {
		return String.valueOf(getTime());
	}
	
	public Mat timestampStream() {
		BufferedImage rawImage = stream.fetchImage();
		logger.log(Level.INFO, "Stream -> Received image at "+getTimeString());
		Mat image = stream.decodeImage(rawImage);
		logger.log(Level.INFO, "Stream ->Decoded image at "+getTimeString());
		return image;
	}
	
	public int[] timestampClassification(Mat image) {
		Rect[] faces = imageClassifier.classify(image);
		int[] difference = null;
		logger.log(Level.INFO, "Classifier -> Detected faces at "+getTimeString());
		if(faces.length > 0) {
			Rect face = classifyStream.getClosestFace(faces);
			logger.log(Level.INFO, "Classifier -> Found closest face at "+getTimeString());
			difference = classifyStream.getDifference(face);
			logger.log(Level.INFO, "Classifier -> Calculated difference at "+getTimeString());
		} else {
			logger.log(Level.SEVERE, "Classifier -> No faces found");
		}
		return difference;
	}
	
	public void timestampPacketHandler(int[] difference) {
		byte[] bytes = packetHandler.convertDataToBytes(difference);
		logger.log(Level.INFO, "Packet -> Converted to bytes at "+getTimeString());
		packetHandler.sendUDPPacket(bytes);
		logger.log(Level.INFO, "Packet -> Pakcet sent at at "+getTimeString());
	}
	
	public void summarise(long[] stages) {
		for(int i = 1; i < stages.length; i++) {
			long totalTime = stages[i] - stages[i-1];
			System.out.println("TOTAL TIME TO "+TIMESTAMP_STAGES[i]+" = "+String.valueOf(totalTime));
		}
	}
	
	public void timestamp() {
		logger.log(Level.INFO, "Started logging with fetching stream");
		long[] stages = new long[TIMESTAMP_STAGES.length];
		stages[0] = getTime();
		Mat image = timestampStream();
		stages[1] = getTime();
		int[] distance = timestampClassification(image);
		stages[2] = getTime();
		timestampPacketHandler(distance);
		stages[3] = getTime();
		summarise(stages);
	}
}
