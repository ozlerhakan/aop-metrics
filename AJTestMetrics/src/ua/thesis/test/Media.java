package ua.thesis.test;
public class Media {
	private String filePath;
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
class ImageFile extends Media {
	protected int width, height;
}
class JpegImage extends ImageFile {
}
class PngImage extends ImageFile {	
}
abstract class GraphicObject {
	abstract double getArea();
}
class Circle extends GraphicObject {
	double radius;
	@Override
	double getArea() {
		return radius * radius * Math.PI;
	}	
}
class Rectangle extends GraphicObject {
	double length, width;
	@Override
	double getArea() {
		return length * width;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public static void main(String[] args){
		Rectangle rec = new Rectangle();
		rec.setWidth(4);
		rec.setLength(5);
		rec.getArea();		
		Media media = new Media();
		media.setFilePath("images/figure.png");
		System.out.println(media.getFileName());		
		Circle circle = new Circle();
		circle.getArea();
	}
}
class Clipboard {
	private String content;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}