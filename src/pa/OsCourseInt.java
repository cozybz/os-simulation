package pa;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import view.OsView;




public class OsCourseInt implements ActionListener,Runnable{
	private JLabel time1=null ;
	public static JLabel time2=null ;//用于cpu看进程的时间片
	static int hour,minute,second;
	private Border border_clo1 =null;
	private Border border_clo2 =null;
	private Border border_run =null;
	private Border border_rea =null;
	private Border border_blo =null;
	private Border border_oc =null;
	private  JPanel clock1_pro;//系统时钟面板
	private  JPanel clock2_pro;//相对时钟面板
	private JPanel run_cou;//正在运行面板
	private JPanel ready;//就绪队列面板
	private JPanel block;//阻塞队列面板
	private JPanel open_clo;//开关机面板
//	private timeJTextArea time;//用来显示时间
	private URL url = getClass().getResource("等待设备的进程.gif");
	private ImageIcon icon= new ImageIcon(url);//阻塞
	private JButton open;//开机键
	private JButton close;//关机键
	private JScrollPane info;
	private JTextArea in_oc;//开关机键信息显示
	private JLabel[] run = new JLabel[3];
	private JLabel[] ready_label = new JLabel[10];
	private JLabel[] block_label = new JLabel[10];
	private static OsView osview =null;
	
	public OsCourseInt(JPanel co) {
		co.setLayout(null);
		time1=new JLabel();
		time2=new JLabel();
		hour=0;
		minute=0;
		second=0;
		border_clo1 = BorderFactory.createTitledBorder(border_clo1, "系统时钟");
		border_clo2= BorderFactory.createTitledBorder(border_clo2, "相对时钟");
		border_run = BorderFactory.createTitledBorder(border_run, "正在运行");
		border_rea= BorderFactory.createTitledBorder(border_rea, "就绪队列");
		border_blo= BorderFactory.createTitledBorder(border_blo, "阻塞队列");
		border_oc= BorderFactory.createTitledBorder(border_oc, "开关");
		open=new JButton("开机");
		close=new JButton("关机");
		in_oc=new JTextArea(3,6);
		info=new JScrollPane(in_oc);
//		time=new timeJTextArea(3,15);
		
		in_oc.setEditable(false);
		
		open.addActionListener(this);//添加事件
		close.addActionListener(this);
		
		
		
		clock1_pro = new JPanel();
		clock2_pro= new JPanel();
		run_cou = new JPanel();
		ready = new JPanel();
		block = new JPanel();
		open_clo = new JPanel();//开关机面板
		
		clock1_pro.setBorder(border_clo1);
		clock1_pro.setBounds(10, 10, 200,50);
		
		clock2_pro.setBorder(border_clo2);
		clock2_pro.setBounds(10, 60, 200, 50);
		
		run_cou.setBorder(border_run);
		run_cou.setBounds(10,120, 200, 350);
		
		ready.setBorder(border_rea);
		ready.setBounds(230,10, 520, 300);
		
		block.setBorder(border_blo);
		block.setBounds(230,320, 520, 300);
		
		open_clo.setBorder(border_oc);
		open_clo.setLayout(new BorderLayout());
		open_clo.add(open, BorderLayout.NORTH);
		open_clo.add(close, BorderLayout.CENTER);
		open_clo.add(info, BorderLayout.SOUTH);
		open_clo.setBounds(10,480, 200, 140);
		
		for(int i=0;i<3;i++)
			run[i]= new JLabel();
		
		for(int i=0;i<10;i++)
			ready_label[i] = new JLabel("无",icon,JLabel.LEFT);
		
		for(int i=0;i<10;i++)
			block_label[i] = new JLabel("无",icon,JLabel.LEFT);
		
		run_ini();//运行块初始化
		ready_ini();//就绪块初始化
		block_ini();//阻塞块初始化
		
		co.add(clock1_pro);
		co.add(clock2_pro);
		co.add(run_cou);
		co.add(ready);
		co.add(block);
		co.add(open_clo);
		
		co.setBounds(10, 30, 775, 532);
		
		
		new Thread(new Runnable() {							//创建内部类 实现监听
			public void run() {
				while(true)
				{
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							
						run[0].setText(OsCpu.run.getName());
						run[1].setText(new String(OsCpu.ir));
						run[2].setText(OsCpu.dr+"");
						
						int temp=OsPcbQueue.ready_q;
						
						for(int i=0;i<10;i++)
						{
							if(temp!=-1)
							{
								ready_label[i].setText(OsPcbQueue.course_q[temp].pcb.getName());
								ready_label[i].setIcon(icon);
								temp=OsPcbQueue.course_q[temp].next;
								
							}
							else
							{
								ready_label[i].setText("无");
								ready_label[i].setIcon(icon);
							}
						}
						
						temp=OsPcbQueue.block_q;
						
						for(int i=0;i<10;i++)
						{
							if(temp!=-1)
							{
								block_label[i].setText(OsPcbQueue.course_q[temp].pcb.getName());
								block_label[i].setIcon(icon);
								temp=OsPcbQueue.course_q[temp].next;
								
							}
							else
							{
								block_label[i].setText("无");
								block_label[i].setIcon(icon);
							}
						}
							
							
							
						}
					});
					
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		
		
		
		
	}
	public void run_ini()
	{
		JLabel label = new JLabel("正在运行进程名称：");
		run_cou.add(label);
		run[0].setText("无");
		run_cou.add(run[0]);
		label = new JLabel("当前运行指令：");
		run_cou.add(label);
		run[1].setText("无");
		run_cou.add(run[1]);
		label = new JLabel("中间数据结果：");
		run_cou.add(label);
		run[2].setText("无");
		run_cou.add(run[2]);
		
	}
//	public void run_d(String name,String order,int result)//cpu调用
//	{
//		JLabel label = new JLabel("正在运行进程名称："+name);
//		run_cou.add(label);
//		label = new JLabel("当前运行指令"+order);
//		run_cou.add(label);
//		label = new JLabel("中间数据结果："+result);
//		run_cou.add(label);
//		
//	}
	public void ready_ini()
	{
		JLabel label = new JLabel("就绪队列进程名称：");
		ready.add(label);
		for(int i=0;i<10;i++)
		{
				ready_label[i].setVerticalTextPosition(JLabel.BOTTOM);
				ready_label[i].setHorizontalTextPosition(JLabel.CENTER);
				ready.add(ready_label[i]);
		}
	}
//	public void ready_qd(String name[])//由别处调用
//	{
//		JLabel label = new JLabel("就绪队列进程名称：");
//		ready.add(label);
//		for(int i=0;i<10;i++)
//		{
//				label = new JLabel(name[i],icon,JLabel.LEFT);
//				label.setVerticalTextPosition(JLabel.BOTTOM);
//				label.setHorizontalTextPosition(JLabel.CENTER);
//				ready.add(label);
//		}
//	}
	public void block_ini()
	{
		JLabel label = new JLabel("阻塞队列进程名称：");
		block.add(label);
		for(int i=0;i<10;i++)
		{
				block_label[i].setVerticalTextPosition(JLabel.BOTTOM);
				block_label[i].setHorizontalTextPosition(JLabel.CENTER);
				block.add(block_label[i]);
		}
	}
//	public void block_qd(String name[])//由别处调用
//	{
//		JLabel label = new JLabel("阻塞队列进程名称：");
//		block.add(label);
//		for(int i=0;i<10;i++)
//		{
//				label = new JLabel("无",icon,JLabel.LEFT);
//				label.setVerticalTextPosition(JLabel.BOTTOM);
//				label.setHorizontalTextPosition(JLabel.CENTER);
//				block.add(label);
//		}
//	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==open)
		{
			
			in_oc.append("操作系统已经启动！\n");
			OsCpu.open=true;
			//系统时钟
			Thread th=new Thread(this);
			th.start();
			//cpu开始运行
			OsCpu cpu=new OsCpu();
			Thread th_cpu=new Thread(cpu);
			th_cpu.start();
			
			osview.open();
			
		}
		else if(e.getSource()==close)
		{
			in_oc.append("操作系统已经关闭！\n");
			OsCpu.open=false;
			hour=0;
			minute=0;
			second=0;
			System.exit(0);
			
		}
	}
	public void run() {
		int hour=0,minute=0,second=0;
		String timeInfo=" ";
		while(OsCpu.open)
		{
			
			second++;
			if(second==60)
			{
				second=0;
				minute++;
				if(minute==60)
				{
					minute=0;
					hour++;
					if(hour==25)
					{
						hour=0;
					}
				}
			}
			if (hour<=9) 
				timeInfo+="0"+hour+":"; //格式化输出
			else 
				timeInfo+=hour+":";
			if (minute<=9)
				timeInfo+="0"+minute+":";
			else
				timeInfo+=minute+":";
			if (second<=9)
				timeInfo+="0"+second;
			else
				timeInfo+=second;
			clock1_pro.add(time1);
			time1.setText(getCurrentTime());
			clock2_pro.add(time2);
			time2.setText(getRealitiveTime());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public String getCurrentTime() {
		String currentTime;
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		currentTime = sdf.format(d);
		return currentTime;
		}
	public String getRealitiveTime()
	{
		
		String timeInfo=" ";
		second++;
		if(second==60)
		{
			second=0;
			minute++;
			if(minute==60)
			{
				minute=0;
				hour++;
				if(hour==25)
				{
					hour=0;
				}
			}
		}
		if (hour<=9) 
			timeInfo+="0"+hour+":"; //格式化输出
		else 
			timeInfo+=hour+":";
		if (minute<=9)
			timeInfo+="0"+minute+":";
		else
			timeInfo+=minute+":";
		if (second<=9)
			timeInfo+="0"+second;
		else
			timeInfo+=second;
		return timeInfo;
	}
	
	public static OsView getOsview() {
		return osview;
	}
	public static void setOsview(OsView osvie) {
		osview = osvie;
	}
}


	
	
	
	
	
	
	
	
	
	
	
	
	
	