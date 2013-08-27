package pa;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.net.URL;


import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;




public class OsMemInt{
	private Border border_data =null;
	private Border border_image =null;
	private Border border_exp =null;
	private JPanel data;//使用情况面板
	public static JPanel image;//内存面板
	private JPanel explain;//图例面板
	private  URL urly = getClass().getResource("占用.png");
	private URL urln = getClass().getResource("未占用.png");
	private URL urlr = getClass().getResource("占用执行.png");
	private  ImageIcon iconr = new ImageIcon(urlr);//正在执行的进程
	private  ImageIcon icony = new ImageIcon(urly); //使用
	private ImageIcon iconn = new ImageIcon(urln); //未使用
	
	private int store[][]=new int [32][16];//占用存1，为占用存0，执行存2
	private JLabel[][] mem_image = new JLabel[32][16];
	private JLabel[] mem_data = new JLabel[4];
	private int temp;
	private String tem;
	
	public static Thread refresh_mem;
	public OsMemInt(JPanel st) {
		st.setLayout(null);
		border_data = BorderFactory.createTitledBorder(border_data, "使用情况");
		border_image = BorderFactory.createTitledBorder(border_image, "内存块");
		border_exp= BorderFactory.createTitledBorder(border_exp, "图例");
		
		data = new JPanel();
		image = new JPanel();
		explain = new JPanel();
		
		data.setBorder(border_data);
		data.setBounds(610, 5, 170, 565);
		
		image.setBorder(border_image);
		image.setBounds(10, 5, 600, 565);
		image.setLayout(new GridLayout(32, 16));
		
		explain.setBorder(border_exp);
		explain.setBounds(10,570, 770, 60);
		
		for(int i=0;i<32;i++)
			for(int j=0;j<16;j++)
			{
				mem_image[i][j] = new JLabel(iconn);
				image.add(mem_image[i][j]);
			}

		temp=OsMemory.content*16;//剩余内存容量
		tem=OsCpu.run.getName();//正在运行的进程名
		mem_data[0]= new JLabel("内存大小：512B 32块 16B/块");
		mem_data[1]= new JLabel("已使用："+(512-temp)+"个内存单元");
		mem_data[2]= new JLabel("未使用："+temp+"个内存单元");
		mem_data[3]= new JLabel("正在运行的进程："+tem);
		
		for(int i=0;i<4;i++)
			data.add(mem_data[i]);
		
		image();//画图
		exp();//画图例
		
		
		
		st.add(data);
		st.add(image);
		st.add(explain);
		st.setBounds(10, 30, 775, 532);
		
		
		
		
		new Thread(new Runnable() {							//创建内部类 实现监听
			public void run() {
				while(true)
				{
					 temp=OsMemory.content*16;//剩余内存容量
					 tem=OsCpu.run.getName();//正在运行的进程名
					 
					if(OsCpu.run.getName()!="idle")//添加正在执行的进程
					{
						for(int i=0;i<32;i++)
						{
							for(int j=0;j<16;j++)
								store[i][j]=0;
						}
						for(int i=0;i<10;i++)//十个进程
						{
							for(int j=0;j<16;j++)
							{
								if(OsPcbQueue.course_q[i].pcb!=null)
								{
									if(OsCpu.run!=null&&!OsCpu.run.getName().equals("idle"))
									{
										store[OsCpu.run.getPc(0)/16][j]=2;
										store[OsCpu.run.getPc(4)/16][j]=2;
										store[OsCpu.run.getPc(8)/16][j]=2;
									}
									if (OsPcbQueue.course_q[i].pcb.getBegin()!=-1&&OsPcbQueue.course_q[i].pcb.getPsw()!=OsPcb.state.running&&OsPcbQueue.course_q[i].pcb.getPsw()!=OsPcb.state.ENDinterrupt)
									{
										store[OsPcbQueue.course_q[i].pcb.getPc(0)/16][j]=1;
										store[OsPcbQueue.course_q[i].pcb.getPc(4)/16][j]=1;
										store[OsPcbQueue.course_q[i].pcb.getPc(8)/16][j]=1;
									}
								}
							}
						}
					}
					else//进程调度肯定要至少要执行一秒“idle”,此时无 store[][]==2
					{
						for(int i=0;i<32;i++)
						{
							for(int j=0;j<16;j++)
							{
								if(OsMemory.store[i]==0)
									store[i][j]=0;
								else if (OsMemory.store[i]==1)
									store[i][j]=1;
							}
						}
					}
					
					
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							
							for(int i=0;i<32;i++)//画图
							{
								for(int j=0;j<16;j++)
								{
									if(store[i][j]==0)
									{
										mem_image[i][j].setIcon(iconn);
									}
									if(store[i][j]==1)
									{
										//System.out.println("画图  0");
										mem_image[i][j].setIcon(icony);

									}
									if(store[i][j]==2)
									{
										//System.out.println("画图  0");
										mem_image[i][j].setIcon(iconr);

									}
								}	
							}
							
							mem_data[0].setText("内存大小：512B 32块 16B/块");
							mem_data[1].setText("已使用："+(512-temp)+"个内存单元");
							mem_data[2].setText("未使用："+temp+"个内存单元");
							mem_data[3].setText("正在运行的进程："+tem);
							
						}
					});
					
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		
		
		
	}
	
	
	
	public  void image()
	{
		for(int i=0;i<32;i++)//初始化均未被占用
		{
			for(int j=0;j<16;j++)
				store[i][j]=0;
		}
		for(int i=0;i<32;i++)//添加被占用的内存单元
		{
			if(OsMemory.store[i]==1)
			{
				for(int j=0;j<16;j++)
				{
					store[i][j]=1;
				}
			}
		}
	}
	public void exp()
	{
		 JLabel label = new JLabel(":表示该内存单元已被占用",icony,JLabel.LEFT);
		 explain.add(label);
		 label = new JLabel(":表示该内存单元未被占用  ",iconn,JLabel.LEFT);
		 explain.add(label);
		 label = new JLabel(":表示正在执行进程所占用的内存单元  ",iconr,JLabel.LEFT);
		 explain.add(label);
	}
	
}

	
	
	
	
	
	
	
	
	
	
	
