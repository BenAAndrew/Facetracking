package stream;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import classifier.ImageClassifier;
import main.Main;
import main.PacketHandler;
import properties.Properties;

public class ClassifyStream {
	private ImageClassifier imageClassifier;
	private PacketHandler packetHandler;
	private Stream stream;
	
	private float xSensitivity;
	private float ySensitivity;
	
	public ClassifyStream(Properties prop, ImageClassifier imageClassifier, PacketHandler packetHandler, Stream stream) {
		this.stream = stream;
		this.imageClassifier = imageClassifier;
		this.packetHandler = packetHandler;
		this.xSensitivity = prop.getXSensitivity();
		this.ySensitivity = prop.getYSensitivity();
	}
	
	public Rect getClosestFace(Rect[] faces) {
		Rect chosenFace = null;
		int largest = 0;
		for (int i = 0; i < faces.length; i++) {
			int size = faces[i].width * faces[i].height;
			if(size > largest) {
				largest = size;
				chosenFace = faces[i];
			}
		}
		return chosenFace;
	}
	
	public int getXDifference(Rect face) {
		return face.x+(face.width/2)-(stream.width/2);
	}
	
	public int getYDifference(Rect face) {
		return -(face.y+(face.height/2)-(stream.height/2));
	}
	
	public int[] applyRatios(int[] difference) {
		difference[0] = (int)(difference[0] * xSensitivity);
		difference[1] = (int)(difference[1] * xSensitivity);
		return difference;
	}
	
	public int[] getDifference(Rect face) {
		int[] difference = new int[]{getXDifference(face),getYDifference(face)};
		if(Main.verbose) {
			System.out.println("CALCULATED DIFFERENCE "+String.valueOf(difference[0])+","+String.valueOf(difference[1]));
		}
		difference = applyRatios(difference);
		if(Main.verbose) {
			System.out.println("SCALED VALUES "+String.valueOf(difference[0])+","+String.valueOf(difference[1]));
		}
		return difference;
	}
	
	public void classifyImage(Mat image) {
		Rect[] faces = imageClassifier.classify(image);
		if(faces.length > 0) {
			Rect face = getClosestFace(faces);
			int[] difference = getDifference(face);
			packetHandler.sendData(difference);
		} else if (Main.verbose) {
			System.out.println("NO FACES FOUND");
		}
	}
	
	public void run() {
		while(true) {
			Mat image = stream.getImage();
			classifyImage(image);
		}
	}
}
