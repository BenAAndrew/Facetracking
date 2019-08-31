package stream;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import classifier.ImageClassifier;
import main.Main;
import main.PacketHandler;

public class ClassifyStream {
	private ImageClassifier imageClassifier;
	private PacketHandler packetHandler;
	private Stream stream;
	
	public ClassifyStream(ImageClassifier imageClassifier, PacketHandler packetHandler, Stream stream) {
		this.stream = stream;
		this.imageClassifier = imageClassifier;
		this.packetHandler = packetHandler;
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
		//Add ratio
		return face.x+(face.width/2)-(stream.width/2);
	}
	
	public int getYDifference(Rect face) {
		//Add ratio
		return -(face.y+(face.height/2)-(stream.height/2));
	}
	
	public int[] getDifference(Rect face) {
		return new int[]{getXDifference(face),getYDifference(face)};
	}
	
	public void classifyImage(Mat image) {
		Rect[] faces = imageClassifier.classify(image);
		if(faces.length > 0) {
			Rect face = getClosestFace(faces);
			int[] difference = getDifference(face);
			if(Main.verbose) {
				System.out.println("CALCULATED DIFFERENCE "+String.valueOf(difference[0])+","+String.valueOf(difference[1]));
			}
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
