package pa;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.*;
import javax.swing.event.*;

import view.OsView;

public class OsInterphase extends JWindow implements Runnable{
	private JFrame important;//主界面
	Thread splashThread;  //进度条更新线程
	JProgressBar progress; //进度条
	private  JTabbedPane tabs;
	public static   JPanel panel_co;//进程选项卡面板
	public static   JPanel panel_eq;//设备选项卡面板
	public static   JPanel panel_fi;//文件选项卡面板
	public static   JPanel panel_st;//存储器选项卡面板
	private  JLabel label_eq;
	private  Container contentPane;
	private static OsView osview =null;
	OsInterphase()
	{
		//变量实例化
		important=new JFrame("Operate system");
		tabs = new JTabbedPane();
		panel_co=new JPanel();
		panel_eq=new JPanel();
		panel_fi=new JPanel();
		panel_st=new JPanel();
		
		tabs.add(panel_co,"OsCourse" );//创建相应的选项卡
		tabs.add(panel_eq,"OsEquipment");//创建相应的选项卡
		tabs.add(panel_fi,"OsFile" );//创建相应的选项卡
		tabs.add(panel_st,"OsStorage" );//创建相应的选项卡
		
		
		
		//等待界面
		Container container=getContentPane(); //得到容器
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));  //设置光标
	    URL url = getClass().getResource("启动.jpg"); //图片的位置
	    if(url != null){
	      container.add(new JLabel(new ImageIcon(url)),BorderLayout.CENTER);  //增加图片
	    }
	    progress = new JProgressBar(1,100); //实例化进度条
	    progress.setStringPainted(true); //描绘文字
	    progress.setString("系统加载中,请稍候......");  //设置显示文字
	    progress.setBackground(Color.white);  //设置背景色
	    container.add(progress,BorderLayout.SOUTH);  //增加进度条到容器上

	    Dimension screen = getToolkit().getScreenSize();  //得到屏幕尺寸
	    pack(); //窗口适应组件尺寸
	    setLocation((screen.width-getSize().width)/2,(screen.height-getSize().height)/2); //设置窗口位置
		
		

	}
	 public void start(){
		 	toFront();  //窗口前端显示
		    splashThread=new Thread(this);  //实例化线程
		    splashThread.start();  //开始运行线程
		    
		  }

		  public void run(){
//			  setVisible(true); //显示窗口
//		    try {
//		      for (int i=0;i<100;i++){
//		        Thread.sleep(100); //线程休眠
//		        progress.setValue(progress.getValue()+1); //设置进度条值
//		      }
//		      
//		    }
//		    catch (Exception ex) {
//		      ex.printStackTrace();
//		    }
//		    dispose(); //释放窗口
		    showFrame(); //运行主程序
		  }
	  public void showFrame(){
		  contentPane = important.getContentPane();
			contentPane.add(tabs);
			important.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			important.setSize(800, 700);
			important.setVisible(true);
			important.setLocation(300,30); //设置窗口位置
			
			//OsEquipment选项卡布置
			OsEqInter eq=new OsEqInter(panel_eq);
			//OsFile选项卡布置
			osview = new OsView();
			OsCourseInt.setOsview(osview);
			OsIo.setFileManager(osview.getFileManagerC());
			osview.show(panel_fi);
			//OsStorage选项卡布置
			OsMemInt st=new OsMemInt(panel_st);
			//OsCourse选项卡布置
			OsCourseInt cou=new OsCourseInt(panel_co);
		  }
	public static void main(String args[])
	{
//		 OsDiskManager odmC = new OsDiskManager("c");
//		
//		 OsFileManager ofmC = new OsFileManager(odmC);
//		
//		ofmC.createFile(null, null, "run".getBytes(), "exe".getBytes(), (byte)0);
//		
//		FCB file = ofmC.openFileByPath("/run.exe");
//		
//		ofmC.reSaveFile(null, file, "x=5;x--;x++;!A5;x--;x--;x--;x--;x--;x--;x--;x++;x++;x++;x++;x++;!b3;!c9;!a6;!a7;x--;x--;x--;x--;x--;x++;x++;end.".getBytes());
//		ofmC.createProcess("/run.exe");
//		String content =new String( ofmC.readFile(file));
//		
//		System.out.println(content);
		
		
		byte b[][] = new byte[4][4];
		OsInterphase splash= new OsInterphase();
		splash.start();  //运行启动界面
//		ofmC.getPage(0, "/run.exe", b);
		
//		for(int i=0;i<4;i++)
//			System.out.println(new String(b[i]));
		
		
	}
}








