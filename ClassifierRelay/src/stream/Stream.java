package stream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import properties.Properties;

public class Stream {
	private URL stream;
	public int width, height;
	
	public Stream(Properties prop) {
		try {
			this.stream = new URL(prop.getStreamAddress());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		setWidthAndHeight();
	}
	
	public void setWidthAndHeight() {
		BufferedImage img = fetchImage();
		width = img.getWidth();
		height = img.getHeight();
	}
	
	public BufferedImage fetchImage() {
		BufferedImage img = null;
		try {
			img = ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	
	public Mat decodeImage(BufferedImage img) {
		Mat mat = new Mat(height, width, CvType.CV_8UC3);
		byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, pixels);
		return mat;
	}
	
	public Mat getImage() {
		BufferedImage img = fetchImage();
		return decodeImage(img);
	}
	
	public void captureImage() {
		try {
			Files.copy(stream.openStream(), new File("test.jpg").toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Capture saved!");
	}
}
