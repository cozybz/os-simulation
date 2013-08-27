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
	private JPanel data;//ʹ��������
	public static JPanel image;//�ڴ����
	private JPanel explain;//ͼ�����
	private  URL urly = getClass().getResource("ռ��.png");
	private URL urln = getClass().getResource("δռ��.png");
	private URL urlr = getClass().getResource("ռ��ִ��.png");
	private  ImageIcon iconr = new ImageIcon(urlr);//����ִ�еĽ���
	private  ImageIcon icony = new ImageIcon(urly); //ʹ��
	private ImageIcon iconn = new ImageIcon(urln); //δʹ��
	
	private int store[][]=new int [32][16];//ռ�ô�1��Ϊռ�ô�0��ִ�д�2
	private JLabel[][] mem_image = new JLabel[32][16];
	private JLabel[] mem_data = new JLabel[4];
	private int temp;
	private String tem;
	
	public static Thread refresh_mem;
	public OsMemInt(JPanel st) {
		st.setLayout(null);
		border_data = BorderFactory.createTitledBorder(border_data, "ʹ�����");
		border_image = BorderFactory.createTitledBorder(border_image, "�ڴ��");
		border_exp= BorderFactory.createTitledBorder(border_exp, "ͼ��");
		
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

		temp=OsMemory.content*16;//ʣ���ڴ�����
		tem=OsCpu.run.getName();//�������еĽ�����
		mem_data[0]= new JLabel("�ڴ��С��512B 32�� 16B/��");
		mem_data[1]= new JLabel("��ʹ�ã�"+(512-temp)+"���ڴ浥Ԫ");
		mem_data[2]= new JLabel("δʹ�ã�"+temp+"���ڴ浥Ԫ");
		mem_data[3]= new JLabel("�������еĽ��̣�"+tem);
		
		for(int i=0;i<4;i++)
			data.add(mem_data[i]);
		
		image();//��ͼ
		exp();//��ͼ��
		
		
		
		st.add(data);
		st.add(image);
		st.add(explain);
		st.setBounds(10, 30, 775, 532);
		
		
		
		
		new Thread(new Runnable() {							//�����ڲ��� ʵ�ּ���
			public void run() {
				while(true)
				{
					 temp=OsMemory.content*16;//ʣ���ڴ�����
					 tem=OsCpu.run.getName();//�������еĽ�����
					 
					if(OsCpu.run.getName()!="idle")//�������ִ�еĽ���
					{
						for(int i=0;i<32;i++)
						{
							for(int j=0;j<16;j++)
								store[i][j]=0;
						}
						for(int i=0;i<10;i++)//ʮ������
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
					else//���̵��ȿ϶�Ҫ����Ҫִ��һ�롰idle��,��ʱ�� store[][]==2
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
							
							for(int i=0;i<32;i++)//��ͼ
							{
								for(int j=0;j<16;j++)
								{
									if(store[i][j]==0)
									{
										mem_image[i][j].setIcon(iconn);
									}
									if(store[i][j]==1)
									{
										//System.out.println("��ͼ  0");
										mem_image[i][j].setIcon(icony);

									}
									if(store[i][j]==2)
									{
										//System.out.println("��ͼ  0");
										mem_image[i][j].setIcon(iconr);

									}
								}	
							}
							
							mem_data[0].setText("�ڴ��С��512B 32�� 16B/��");
							mem_data[1].setText("��ʹ�ã�"+(512-temp)+"���ڴ浥Ԫ");
							mem_data[2].setText("δʹ�ã�"+temp+"���ڴ浥Ԫ");
							mem_data[3].setText("�������еĽ��̣�"+tem);
							
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
		for(int i=0;i<32;i++)//��ʼ����δ��ռ��
		{
			for(int j=0;j<16;j++)
				store[i][j]=0;
		}
		for(int i=0;i<32;i++)//��ӱ�ռ�õ��ڴ浥Ԫ
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
		 JLabel label = new JLabel(":��ʾ���ڴ浥Ԫ�ѱ�ռ��",icony,JLabel.LEFT);
		 explain.add(label);
		 label = new JLabel(":��ʾ���ڴ浥Ԫδ��ռ��  ",iconn,JLabel.LEFT);
		 explain.add(label);
		 label = new JLabel(":��ʾ����ִ�н�����ռ�õ��ڴ浥Ԫ  ",iconr,JLabel.LEFT);
		 explain.add(label);
	}
	
}

	
	
	
	
	
	
	
	
	
	
	
