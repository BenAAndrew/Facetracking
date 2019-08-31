package classifier;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import properties.Properties;

import org.opencv.core.Rect;

public class ImageClassifier {
	private CascadeClassifier faceCascade = new CascadeClassifier();
	
	private final float MIN_FACE_SIZE = 0.2f;
	private final double SCALE_FACTOR = 1.1;
	private final int MIN_NEIGHBOURS = 2;
	private final int SCREEN_ENCODING = Imgproc.COLOR_BGR2GRAY;
	private final int OBJECT_DETECTION_SETTING =  Objdetect.CASCADE_SCALE_IMAGE;
	
	private final Size MAX_SIZE = new Size();
	private Size MIN_SIZE = null;
	
	public ImageClassifier(Properties prop) {
		faceCascade.load(prop.getClassifier());
	}
	
	public void setMinimumFaceSize(Mat frame) {
		double height = Math.round(frame.rows() * MIN_FACE_SIZE);
		double width = Math.round(frame.cols() * MIN_FACE_SIZE);
		if (height > 0 && width > 0) {
			this.MIN_SIZE = new Size(width, height);
		}
	}
	
	public Rect[] classify(Mat image) {
		MatOfRect faces = new MatOfRect();
		Mat grayFrame = new Mat();
		
		// convert the frame in gray scale
		Imgproc.cvtColor(image, grayFrame, SCREEN_ENCODING);
		// equalize the frame histogram to improve the result
		Imgproc.equalizeHist(grayFrame, grayFrame);
		
		// compute minimum face size
		if (this.MIN_SIZE == null){
			setMinimumFaceSize(grayFrame);
		}
		
		// detect faces
		this.faceCascade.detectMultiScale(grayFrame, faces, SCALE_FACTOR, MIN_NEIGHBOURS,
											0 | OBJECT_DETECTION_SETTING, MIN_SIZE, MAX_SIZE);
		
		return faces.toArray();
	}
}
