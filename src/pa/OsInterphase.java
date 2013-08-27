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
	private JFrame important;//������
	Thread splashThread;  //�����������߳�
	JProgressBar progress; //������
	private  JTabbedPane tabs;
	public static   JPanel panel_co;//����ѡ����
	public static   JPanel panel_eq;//�豸ѡ����
	public static   JPanel panel_fi;//�ļ�ѡ����
	public static   JPanel panel_st;//�洢��ѡ����
	private  JLabel label_eq;
	private  Container contentPane;
	private static OsView osview =null;
	OsInterphase()
	{
		//����ʵ����
		important=new JFrame("Operate system");
		tabs = new JTabbedPane();
		panel_co=new JPanel();
		panel_eq=new JPanel();
		panel_fi=new JPanel();
		panel_st=new JPanel();
		
		tabs.add(panel_co,"OsCourse" );//������Ӧ��ѡ�
		tabs.add(panel_eq,"OsEquipment");//������Ӧ��ѡ�
		tabs.add(panel_fi,"OsFile" );//������Ӧ��ѡ�
		tabs.add(panel_st,"OsStorage" );//������Ӧ��ѡ�
		
		
		
		//�ȴ�����
		Container container=getContentPane(); //�õ�����
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));  //���ù��
	    URL url = getClass().getResource("����.jpg"); //ͼƬ��λ��
	    if(url != null){
	      container.add(new JLabel(new ImageIcon(url)),BorderLayout.CENTER);  //����ͼƬ
	    }
	    progress = new JProgressBar(1,100); //ʵ����������
	    progress.setStringPainted(true); //�������
	    progress.setString("ϵͳ������,���Ժ�......");  //������ʾ����
	    progress.setBackground(Color.white);  //���ñ���ɫ
	    container.add(progress,BorderLayout.SOUTH);  //���ӽ�������������

	    Dimension screen = getToolkit().getScreenSize();  //�õ���Ļ�ߴ�
	    pack(); //������Ӧ����ߴ�
	    setLocation((screen.width-getSize().width)/2,(screen.height-getSize().height)/2); //���ô���λ��
		
		

	}
	 public void start(){
		 	toFront();  //����ǰ����ʾ
		    splashThread=new Thread(this);  //ʵ�����߳�
		    splashThread.start();  //��ʼ�����߳�
		    
		  }

		  public void run(){
//			  setVisible(true); //��ʾ����
//		    try {
//		      for (int i=0;i<100;i++){
//		        Thread.sleep(100); //�߳�����
//		        progress.setValue(progress.getValue()+1); //���ý�����ֵ
//		      }
//		      
//		    }
//		    catch (Exception ex) {
//		      ex.printStackTrace();
//		    }
//		    dispose(); //�ͷŴ���
		    showFrame(); //����������
		  }
	  public void showFrame(){
		  contentPane = important.getContentPane();
			contentPane.add(tabs);
			important.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			important.setSize(800, 700);
			important.setVisible(true);
			important.setLocation(300,30); //���ô���λ��
			
			//OsEquipmentѡ�����
			OsEqInter eq=new OsEqInter(panel_eq);
			//OsFileѡ�����
			osview = new OsView();
			OsCourseInt.setOsview(osview);
			OsIo.setFileManager(osview.getFileManagerC());
			osview.show(panel_fi);
			//OsStorageѡ�����
			OsMemInt st=new OsMemInt(panel_st);
			//OsCourseѡ�����
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
		splash.start();  //������������
//		ofmC.getPage(0, "/run.exe", b);
		
//		for(int i=0;i<4;i++)
//			System.out.println(new String(b[i]));
		
		
	}
}








