package pa;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.net.URL;


import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;




public class OsEqInter  {
	private Border border =null;
	private Border borderA =null;
	private Border borderB =null;
	private Border borderC =null;
	private JPanel deviceA ;
	private JPanel deviceB ;
	private JPanel deviceC ;
	private URL urly = getClass().getResource("使用.gif");
	private URL urln = getClass().getResource("未使用.gif");
	private URL urlw = getClass().getResource("等待设备的进程.gif");
	private ImageIcon iconw = new ImageIcon(urlw);//等待设备的进程
	private ImageIcon icony = new ImageIcon(urly); //使用
	private ImageIcon iconn = new ImageIcon(urln); //未使用
	private JLabel[] topa = new JLabel[3];
	private JLabel[] topb = new JLabel[2];
	private JLabel[] topc = new JLabel[1];
	private JLabel[] centera = new JLabel[10];
	private JLabel[] centerb = new JLabel[10];
	private JLabel[] centerc = new JLabel[10];
	public OsEqInter(JPanel eq) {
		eq.setLayout(null);
		border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		border = BorderFactory.createTitledBorder(border, "设备");
		borderA = BorderFactory.createTitledBorder(borderA, "设备A");
		borderB = BorderFactory.createTitledBorder(borderB, "设备B");
		borderC = BorderFactory.createTitledBorder(borderC, "设备C");
		
		deviceA = new JPanel();
		deviceB = new JPanel();
		deviceC = new JPanel();
		
		deviceA.setBorder(borderA);
		deviceA.setBounds(30, 25, 230, 600);
		JLabel label_topa = new JLabel("占用情况：    ");
		deviceA.add(label_topa);
		
		
		
		
		deviceB.setBorder(borderB);
		deviceB.setBounds(280, 25, 230, 600);
		JLabel label_topb = new JLabel("占用情况：       ");
		deviceB.add(label_topb);
		
		
		
		
		deviceC.setBorder(borderC);
		deviceC.setBounds(530, 25, 230, 600);
		JLabel label_topc = new JLabel("占用情况：          ");
		deviceC.add(label_topc);
		
		
		
		
		
		for(int i=0;i<3;i++)
		{
			topa[i] = new JLabel("无",iconn,JLabel.LEFT);
			topa[i].setVerticalTextPosition(JLabel.BOTTOM);
			topa[i].setHorizontalTextPosition(JLabel.CENTER);
			deviceA.add(topa[i]);
		}
		
		for(int i=0;i<2;i++)
		{
			topb[i] = new JLabel("无",iconn,JLabel.LEFT);
			topb[i].setVerticalTextPosition(JLabel.BOTTOM);
			topb[i].setHorizontalTextPosition(JLabel.CENTER);
			deviceB.add(topb[i]);
		}
		
		topc[0] = new JLabel("无",iconn,JLabel.LEFT);
		topc[0].setVerticalTextPosition(JLabel.BOTTOM);
		topc[0].setHorizontalTextPosition(JLabel.CENTER);
		deviceC.add(topc[0]);
		
		JLabel label_centera = new JLabel("等待队列：");
		deviceA.add(label_centera);
		
		JLabel label_centerb = new JLabel("等待队列：");
		deviceB.add(label_centerb);
		
		JLabel label_centerc = new JLabel("等待队列：");
		deviceC.add(label_centerc);
		
		for(int i=0;i<10;i++)
		{
			
			
			
			centera[i] = new JLabel();
			centerb[i] = new JLabel();
			centerc[i] = new JLabel();
			
			centera[i] = new JLabel("无",iconw,JLabel.LEFT);
			centera[i].setVerticalTextPosition(JLabel.BOTTOM);
			centera[i].setHorizontalTextPosition(JLabel.CENTER);
			deviceA.add(centera[i]);
			
			centerb[i] = new JLabel("无",iconw,JLabel.LEFT);
			centerb[i].setVerticalTextPosition(JLabel.BOTTOM);
			centerb[i].setHorizontalTextPosition(JLabel.CENTER);
			deviceB.add(centerb[i]);
			
			centerc[i] = new JLabel("无",iconw,JLabel.LEFT);
			centerc[i].setVerticalTextPosition(JLabel.BOTTOM);
			centerc[i].setHorizontalTextPosition(JLabel.CENTER);
			deviceC.add(centerc[i]);
			
			deviceA.add(centera[i]);
			deviceB.add(centerb[i]);
			deviceC.add(centerc[i]);
		}
		
		
		eq.add(deviceA);
		eq.add(deviceB);
		eq.add(deviceC);
		
		bottomIni(deviceA);
		bottomIni(deviceB);
		bottomIni(deviceC);
		
		eq.setBounds(10, 30, 775, 532);
	
		
		
		
		new Thread(new Runnable() {							//创建内部类 实现监听
			public void run() {
				while(true)
				{
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							
							for(int i=0;i<3;i++)
							{
								if(!OsEquipment.a_name[i].equals(" "))
								{
									topa[i].setText(OsEquipment.a_name[i]);
									topa[i].setIcon(icony);
								}
								else 
								{
									topa[i].setText("无");
									topa[i].setIcon(iconn);
								}
							}
							
							for(int i=0;i<2;i++)
							{
								if(!OsEquipment.b_name[i].equals(" "))
								{
									topb[i].setText(OsEquipment.b_name[i]);
									topb[i].setIcon(icony);
								}
								else 
								{
									topb[i].setText("无");
									topb[i].setIcon(iconn);
								}
							}
							
							if(!OsEquipment.c_name[0].equals(" "))
							{
								topc[0].setText(OsEquipment.c_name[0]);
								topc[0].setIcon(icony);
							}
							else
							{
								topc[0].setText("无");
								topc[0].setIcon(iconn);
							}
							
							for(int i=0;i<OsEquipment.wait_n[0][0];i++)
							{
								if(OsEquipment.wait_q[0][i]!=null)
									centera[i].setText(OsEquipment.wait_q[0][i].getName());
								else 
									centera[i].setText("无");
								centera[i].setIcon(iconw);
							}
							for(int i=0;i<OsEquipment.wait_n[1][0];i++)
							{
								if(OsEquipment.wait_q[1][i]!=null)
									centerb[i].setText(OsEquipment.wait_q[1][i].getName());
								else 
									centerb[i].setText("无");
								centerb[i].setIcon(iconw);
							}
							for(int i=0;i<OsEquipment.wait_n[2][0];i++)
							{
								if(OsEquipment.wait_q[2][i]!=null)
									centerc[i].setText(OsEquipment.wait_q[2][i].getName());
								else 
									centerc[i].setText("无");
								centerc[i].setIcon(iconw);
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
	
	
//	public void top(int kind,int num,String name[])//设备占用情况,kind 表示刷新哪个设备状况  num 表示占有该设备多少个  name 表示占领设备的进程名称
//	{
//		if(kind==1)//占用A设备
//		{
//			if(num==1)
//			{
//				JLabel label_top = new JLabel("占用情况：");
//				deviceA.add(label_top);
//				
//				JLabel label_top1 = new JLabel(name[0],iconn,JLabel.LEFT);
//				label_top1.setVerticalTextPosition(JLabel.BOTTOM);
//				label_top1.setHorizontalTextPosition(JLabel.CENTER);
//				deviceA.add(label_top1);
//				
//				JLabel label_top2 = new JLabel("无",iconn,JLabel.LEFT);
//				label_top2.setVerticalTextPosition(JLabel.BOTTOM);
//				label_top2.setHorizontalTextPosition(JLabel.CENTER);
//				deviceA.add(label_top2);
//				
//				JLabel label_top3 = new JLabel("无",iconn,JLabel.LEFT);
//				label_top3.setVerticalTextPosition(JLabel.BOTTOM);
//				label_top3.setHorizontalTextPosition(JLabel.CENTER);
//				deviceA.add(label_top3);
//			}
//			else if(num==2)
//			{
//				JLabel label_top = new JLabel("占用情况：");
//				deviceA.add(label_top);
//				
//				JLabel label_top1 = new JLabel(name[0],iconn,JLabel.LEFT);
//				label_top1.setVerticalTextPosition(JLabel.BOTTOM);
//				label_top1.setHorizontalTextPosition(JLabel.CENTER);
//				deviceA.add(label_top1);
//				
//				JLabel label_top2 = new JLabel(name[1],iconn,JLabel.LEFT);
//				label_top2.setVerticalTextPosition(JLabel.BOTTOM);
//				label_top2.setHorizontalTextPosition(JLabel.CENTER);
//				deviceA.add(label_top2);
//				
//				JLabel label_top3 = new JLabel("无",iconn,JLabel.LEFT);
//				label_top3.setVerticalTextPosition(JLabel.BOTTOM);
//				label_top3.setHorizontalTextPosition(JLabel.CENTER);
//				deviceA.add(label_top3);
//			}
//			else if(num==3)
//			{
//				JLabel label_top = new JLabel("占用情况：");
//				deviceA.add(label_top);
//				
//				JLabel label_top1 = new JLabel(name[0],iconn,JLabel.LEFT);
//				label_top1.setVerticalTextPosition(JLabel.BOTTOM);
//				label_top1.setHorizontalTextPosition(JLabel.CENTER);
//				deviceA.add(label_top1);
//				
//				JLabel label_top2 = new JLabel(name[1],iconn,JLabel.LEFT);
//				label_top2.setVerticalTextPosition(JLabel.BOTTOM);
//				label_top2.setHorizontalTextPosition(JLabel.CENTER);
//				deviceA.add(label_top2);
//				
//				JLabel label_top3 = new JLabel(name[2],iconn,JLabel.LEFT);
//				label_top3.setVerticalTextPosition(JLabel.BOTTOM);
//				label_top3.setHorizontalTextPosition(JLabel.CENTER);
//				deviceA.add(label_top3);
//			}
//			
//		}
//		else if(kind==2)//占用B设备
//		{}
//		else if(kind==3)//占用C设备
//		{}
//	}
//	
//	public void center(int kind,int num,OsPcb wait[][],int top,int tail)//进程等待情况,kind 表示刷新哪个设备状况  num 表示占有该设备多少个  name 表示占领设备的进程名称
//	{
//		JLabel label = new JLabel("等待队列：");
//		if(kind==1)
//		{
//			deviceA.add(label);
//			while((tail+1)==top)
//			{
//				label = new JLabel(wait[0][top].getName(),iconw,JLabel.LEFT);
//				label.setVerticalTextPosition(JLabel.BOTTOM);
//				label.setHorizontalTextPosition(JLabel.CENTER);
//				deviceA.add(label);
//				top=(top+1)%10;
//			}
//			for(int i=0;i<10-num;i++)
//			{
//					label = new JLabel("无",iconw,JLabel.LEFT);
//					label.setVerticalTextPosition(JLabel.BOTTOM);
//					label.setHorizontalTextPosition(JLabel.CENTER);
//					deviceA.add(label);
//			}
//		}
//		else if(kind==2)
//		{
//			deviceB.add(label);
//			while((tail+1)==top)
//			{
//				label = new JLabel(wait[1][top].getName(),iconw,JLabel.LEFT);
//				label.setVerticalTextPosition(JLabel.BOTTOM);
//				label.setHorizontalTextPosition(JLabel.CENTER);
//				deviceB.add(label);
//				top=(top+1)%10;
//			}
//			for(int i=0;i<10-num;i++)
//			{
//					label = new JLabel("无",iconw,JLabel.LEFT);
//					label.setVerticalTextPosition(JLabel.BOTTOM);
//					label.setHorizontalTextPosition(JLabel.CENTER);
//					deviceB.add(label);
//			}
//		}
//		else if(kind==3)
//		{
//			deviceC.add(label);
//			while((tail+1)==top)
//			{
//				label = new JLabel(wait[2][top].getName(),iconw,JLabel.LEFT);
//				label.setVerticalTextPosition(JLabel.BOTTOM);
//				label.setHorizontalTextPosition(JLabel.CENTER);
//				deviceC.add(label);
//				top=(top+1)%10;
//			}
//			for(int i=0;i<10-num;i++)
//			{
//					label = new JLabel("无",iconw,JLabel.LEFT);
//					label.setVerticalTextPosition(JLabel.BOTTOM);
//					label.setHorizontalTextPosition(JLabel.CENTER);
//					deviceC.add(label);
//			}
//		}
//	}
	public void bottomIni(JPanel device)//图像说明初始化
	{
		JLabel label = new JLabel("                                                                          ");
		device.add(label);
		label = new JLabel("                                                                          ");
		device.add(label);
		label = new JLabel("                                                                          ");
		device.add(label);
		label = new JLabel("                                                                          ");
		device.add(label);
		label = new JLabel("                                                                          ");
		device.add(label);
		label = new JLabel("                                                                          ");
		device.add(label);
		 label = new JLabel("      :表示设备已被占用       ",icony,JLabel.LEFT);
		 device.add(label);
		 label = new JLabel("      :表示设备未被占用      ",iconn,JLabel.LEFT);
		 device.add(label);
		 label = new JLabel(":表示等待该设备的进程的队列  ",iconw,JLabel.LEFT);
		 device.add(label);
	}
}

