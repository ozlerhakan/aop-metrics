package ua.thesis.test;
import java.io.File;
import java.util.ArrayList;
public aspect UtilAspect {	
	interface Log{
		void logPath(String msg);
	}	
	String Media.filename;
	String Media.getFileName(){
		return filename;
	}
	private ArrayList<String> Clipboard.listofContent = 
									new ArrayList<String>();
	void addList(Clipboard clip,String content){
		clip.listofContent.add(content);
	}	
	//divide the selected content
	void splitContent(Clipboard clip,String content){
		String[] list = content.split("\n");
		for (int i = 0; i < list.length; i++) {
			addList(clip, list[i]);
		}
	}	 
	pointcut printArea() : call(double GraphicObject+.getArea());
	after() returning(double area) : printArea() {
		 System.out.println("Execution of the "
				 	+ thisJoinPoint.getSignature().getName()+" method\n"
		 			+ "Area is "+area);
	}	
	pointcut showContent(Clipboard clip,String content) : 
		execution(public void Clipboard.setContent(String)) && 
		args(content) && 
		this(clip);
	before(Clipboard clip,String content) : showContent(clip,content) {
		 splitContent(clip,content);
	}	
	after(Log log,String path) : call(* Media.setFilePath(..)) && args(path) && target(log){
		log.logPath(path);
	}
	after(String filePath,Media media): set(private String Media.filePath) && 
			if(media.getFilePath() != null) &&
			args(filePath) && 
			this(media){
		media.filename = (new File(filePath).getName());
	}	
	pointcut Area(Circle circle) : execution(double getArea()) && this(circle);
	double around(Circle circle): Area(circle){
		if(circle.radius <= 0)
			return -1;
		else
			return proceed(circle);
	}
}