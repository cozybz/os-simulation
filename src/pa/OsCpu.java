package pa;

import java.util.Random;

import javax.swing.JPanel;

import pa.OsPcb.lack;
import pa.OsPcb.state;

public class OsCpu implements Runnable{
	public static state psw;//����״̬��
	public static int dr;//���ݻ���Ĵ���,�洢����ִ�е�ָ���е����ݣ����ڼ�������ݣ�
	public static int dr1[];;//�豸��ʱ���ݱ���������0Ҳ���У�,һ�����̿��ܶ�Ӧ����豸���Ҳ�ͬ��ռ��
	public static byte own_eq[];//�ý���ռ�е��豸����ͬʱռ�ж��   0��A  1��B  2��C
	public static byte[] ir;//����Ž�Ҫִ�е�ָ��
	public static int begin;//�ý������ڴ����ʼλ�ã�����cpuִ��ʱѰ�ң����м������ܹ�����
	public static int pc[];//����ڴ��иý���λ�õ���һ��ָ��λ��,pc[0]�����һ��ָ��ĵ�ַ��pc[1]���pc[0]��¼�Ŀ��ƿ���pc�ĵڼ���
	public static int runtime;//��ô�����Ľ����Ѿ����е�ʱ��
	public static OsPcb run;//����ִ�еĽ���
	
	//������ʾ
	private OsInterphase oi;
	private OsMemInt 	memdis=new OsMemInt(OsInterphase.panel_st);
	public static boolean open;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!������
	static
	{
		psw=OsPcb.state.running;
		pc=new int[2];
		pc[0]=-1;
		pc[1]=-1;
		dr=10000;//�γ����Ҫ��Ϊ1λ���Գ�ʼ������󶼲�����Ϊ10000
		dr1=new int[3];//�豸��ʱ���ݱ���������0Ҳ���У�,һ�����̿��ܶ�Ӧ����豸���Ҳ�ͬ��ռ��
		own_eq=new byte[3];
		ir=new byte[4];
		begin=-1;
		runtime=0;
		run=new OsPcb("idle",-1);//���滻������������װ��
		
	}
	public  void run()//cpu�߳�,һ��ִ��һ��
	{
		while (open)
		{ 
			System.out.println("cpu         "+run.getName());
			if(run.getName()!="idle")//���ǿ��н�����ִ��,�ɵ��Ƚ�����Ϣ��ʼ��
			{
				
//				memdis.image();
//				memdis.image.updateUI();
				
				if(psw==OsPcb.state.running)//���н���,����жϸı�cpu��psw
				{
					
					while(open)//������һֱִ�У����ж�����������ִ�е�ʱ��Ƭ����
					{
						System.out.println("ʱ��"+runtime);
						if(runtime==5)
						{
							System.out.println("ʱ��Ƭ����");
							psw=OsPcb.state.timeout;//ʱ��Ƭ����
							break;
						}
						
						String order=new  String(ir);//ָ���ָ����ʽ�ǲ���ģ������ռ���豸������
						System.out.println(order+"order");
//						System.out.println(new String(own_eq)+"own");
						
						
						if((ir[0]=='x'||ir[0]=='X')&&ir[1]=='='&&ir[3]==';')//�ж������ʽ�������ж�ir[2]��ֻ��һ���ַ�
						{
							if(ir[2]=='?')//ָ��ΪҪ���û�����
							{
								System.out.println("�޳�ֵ�ĸ�ֵ����");
								if(dr==10000)
								{
									psw=OsPcb.state.IOinterrupt;
									break;
								}
								else if(dr>-10&&dr<10)
								{
									updateOder();//����ָ��������Ϣ
									if(psw==OsPcb.state.PAGEinterrupt)//����ָ���ʱ����ȱҳ�ж�
										break;
								}
							}
							else  if(ir[2]>'0'&&ir[2]<='9')//��һλ������Ϊ�п���������һ���ַ�
							{
								dr=ir[2]-'0';
								System.out.println("�г�ֵ�ĸ�ֵ����");
								updateOder();//����ָ��������Ϣ
								if(psw==OsPcb.state.PAGEinterrupt)//����ָ���ʱ����ȱҳ�ж�
									break;
							}
							else//ָ����󣡣�������������������������������������������������������������������������������������������������������������
							{
								//������ʾ
								psw=OsPcb.state.ENDinterrupt;//���ִ���ָ��ֱ����ֹ����
							}
						}
						else if(order.equals("x++;")||order.equals("X++;"))
						{
							if(dr!=10000)//��������drΪ��λ�ж����п��ܼ���󳬹���10000һ��ﲻ��
							{
								dr++;
								updateOder();//����ָ��������Ϣ
								if(psw==OsPcb.state.PAGEinterrupt)//����ָ���ʱ����ȱҳ�ж�
									break;
							}
						}
						else if(order.equals("x--;")||order.equals("X--;"))
						{
							if(dr!=10000)//��������drΪ��λ�ж����п��ܼ���󳬹���10000һ��ﲻ��
							{
								dr--;
								updateOder();//����ָ��������Ϣ
								if(psw==OsPcb.state.PAGEinterrupt)//����ָ���ʱ����ȱҳ�ж�
									break;
							}
						}
						else if(ir[0]=='!'&&ir[3]==';')//�����豸ָ��Ĺ̶���ʽ,��ָ��ĸ�ʽֻ����Ϊ eg   "!A5;";������ĸ���ֲ���,��Ҫ�豸��ʱ��Ӧ�г��򶨶�������
						{
							int temp=ir[2]-'0';//ȷ����3���ַ�Ϊ��ȷָ����ַ�
							
							if(own_eq[0]==-1&&own_eq[1]==-1&&own_eq[2]==-1&&temp>-1&&temp<10)//�ý��̵�һ��ִ��û���豸
							{
							
								
								//��ָ����ȷ��ǰ���¸����жϳ�����δ���
								if((ir[1]=='A'||ir[1]=='a'))
								{
									run.setReason_b(OsPcb.lack.lackA);//��ȱA�ж�
									psw=OsPcb.state.Equinterrupt;
									break;
								}
								else if(ir[1]=='B'||ir[1]=='b')
								{
									run.setReason_b(OsPcb.lack.lackB);//��ȱB�ж�
									psw=OsPcb.state.Equinterrupt;
									break;
								}
								else if(ir[1]=='C'||ir[1]=='c')
								{
									run.setReason_b(OsPcb.lack.lackC);//��ȱC�ж�
									psw=OsPcb.state.Equinterrupt;
									break;
								}
								else//�����豸�����ڣ���������������������������������������������������������������������������������������������������������������
								{
									//������ʾ
									psw=OsPcb.state.ENDinterrupt;//���ִ���ָ��ֱ����ֹ����
								}
							}
							else if((own_eq[0]>-1)&&temp>-1&&temp<10)
							{
								Random r=new Random();//���������
								temp=r.nextInt(ir[2]-'0')+1;
								System.out.println(temp+"AAAA"+(ir[2]-'0'));
								dr1[0]=temp;
								updateOder();//����ָ��������Ϣ
								if(psw==OsPcb.state.PAGEinterrupt)//����ָ���ʱ����ȱҳ�ж�
									break;
							}
							else if((own_eq[1]>-1)&&temp>-1&&temp<10)
							{
								Random r=new Random();//���������
								temp=r.nextInt(ir[2]-'0')+1;
								System.out.println(temp+"BBBB"+(ir[2]-'0'));
								//������ǰ������Ѿ����뵽�豸 �� �˴����ÿ���ʱ�䲻���� ��ô���¸�ֵ�豸ʹ��ʱ��
								dr1[1]=temp;
								updateOder();//����ָ��������Ϣ
								if(psw==OsPcb.state.PAGEinterrupt)//����ָ���ʱ����ȱҳ�ж�
									break;
							}
							else if((own_eq[2]>-1)&&temp>-1&&temp<10)
							{
								Random r=new Random();//���������
								temp=r.nextInt(ir[2]-'0')+1;
								System.out.println(temp+"CCCC"+(ir[2]-'0'));
								//������ǰ������Ѿ����뵽�豸 �� �˴����ÿ���ʱ�䲻���� ��ô���¸�ֵ�豸ʹ��ʱ��
								dr1[2]=temp;
								updateOder();//����ָ��������Ϣ
								if(psw==OsPcb.state.PAGEinterrupt)//����ָ���ʱ����ȱҳ�ж�
									break;
							}
							else //ָ����󣡣�������������������������������������������������������������������������������
							{
								//������ʾ
								psw=OsPcb.state.ENDinterrupt;//���ִ���ָ��ֱ����ֹ����
							}
						}
						else if(order.equals("end.")||order.equals("END."))
						{
							psw=OsPcb.state.ENDinterrupt;
							break;
						}
						else//�Ƿ�ָ�������������������������������������������������������������������������������������������������������������
						{
							//���ؽ���
							psw=OsPcb.state.ENDinterrupt;//���ִ���ָ��ֱ����ֹ����
						}

						runtime++;//����ʱ���1����Ϊcpuһ������һ��
						if(own_eq[0]>-1)
						{
							dr1[0]--;
							System.out.println(dr1[0]+"Aʣ��ʱ��");
							if(dr1[0]==0)//�豸ռ����ϣ��ͷ�
							{
								System.out.println("�ͷ��豸A");
								OsEquipment.freeEq(run);
							}
						}
						if(own_eq[1]>-1)
						{
							dr1[1]--;
							System.out.println(dr1[1]+"Bʣ��ʱ��");
							if(dr1[1]==0)//�豸ռ����ϣ��ͷ�
							{
								System.out.println("�ͷ��豸B");
								OsEquipment.freeEq(run);
							}
						}
						if(own_eq[2]>-1)
						{
							dr1[2]--;
							System.out.println(dr1[2]+"Cʣ��ʱ��");
							if(dr1[2]==0)//�豸ռ����ϣ��ͷ�
							{
								System.out.println("�ͷ��豸C");
								OsEquipment.freeEq(run);
							}
						}
						
//						System.out.println("cpu������");
						try
						{

							Thread.sleep(1000);  //cpu   1��ִ��һ��
						}
						catch (InterruptedException ex)
						{
							ex.printStackTrace();  //���������Ϣ
						}
					}//end while(true)
				}
				//�жϲ���������ʱ
				if(psw==OsPcb.state.ENDinterrupt)//���жϣ������治��else��ϵ��������ʱ�����ж��ڱ��������޷�����
				{
					for(int i=0;i<4;i++)
					{
						ir[i]=' ';
					}
					dr=10000;
					OsSloveInt.sloveInt(OsPcb.state.ENDinterrupt,run);
				}
				else if(psw==OsPcb.state.PAGEinterrupt)//ȱҳ�ж�,������cpu
				{
					//ȱҳ�ж�Ҳ��������һ��ָ��
					runtime++;//����ʱ���1����Ϊcpuһ������һ��
					if(own_eq[0]>-1)
					{
						dr1[0]--;
						System.out.println(dr1[0]+"ʣ��ʱ��");
						if(dr1[0]==0)//�豸ռ����ϣ��ͷ�
						{
							System.out.println("�ͷ��豸A");
							OsEquipment.freeEq(run);
						}
					}
					if(own_eq[1]>-1)
					{
						dr1[1]--;
						
						if(dr1[1]==0)//�豸ռ����ϣ��ͷ�
						{
							System.out.println("�ͷ��豸B");
							OsEquipment.freeEq(run);
						}
					}
					if(own_eq[2]>-1)
					{
						dr1[2]--;
						if(dr1[2]==0)//�豸ռ����ϣ��ͷ�
						{
							System.out.println("�ͷ��豸C");
							OsEquipment.freeEq(run);
						}
					}
					System.out.println("ȱҳ����");
					
					OsPcbQueue.cpuToPcb(OsPcbQueue.running);//ȱҳ�ж�ҲҪ������Ϣ��ֵ
					OsMemory.pageDispatcher(run);
					psw=OsPcb.state.running;
					
				}
				else if(psw==OsPcb.state.IOinterrupt)//IO�ж�
				{
					OsSloveInt.sloveInt(OsPcb.state.IOinterrupt,run);
				}
				else if(psw==OsPcb.state.Equinterrupt)//�豸�ж�
				{
					System.out.println("�豸�ж�");
					
					OsSloveInt.sloveInt(OsPcb.state.Equinterrupt,run);
				}
				else if(psw==OsPcb.state.timeout)//ʱ��Ƭ����
				{
					System.out.println(run.getPc_num());
					OsEquipment.freeEq(run);//����������    ����ͱ��ֵı�Ҫ����
					OsPcbQueue.cpuToPcb(OsPcbQueue.running);
					OsPcbQueue.running=-1;
					OsPcbQueue.putInReady(run.getName());
					OsMemory.dispatcher();
					System.out.println(run.getPc_num());

				}
			}
			else//����ǿ��н���ʲô��������
			{
				System.out.println("���е���");
				OsMemory.dispatcher();//���е���
			}
			if(run.getName()=="idle")
			{
				try
				{

					Thread.sleep(1000);  //cpu   1��ִ��һ��
				}
				catch (InterruptedException ex)
				{
					ex.printStackTrace();  //���������Ϣ
				}
			}
			
		}//end while(open)	
	}
	public void updateOder()//����cpu���й�ָ�����Ϣ,��ʱ�п��ܷ���ȱҳ�ж�
	{
		
	
		pc[0]=OsPcbQueue.course_q[OsPcbQueue.running].pcb.getPc_num();//��ȡ��ִ�е�ָ�����
		run.pc_numUp();//��һ��ָ��
		pc[1]=OsPcbQueue.course_q[OsPcbQueue.running].pcb.getPc(pc[0]);//��ȡ��һ��ָ��ĵ�ַ
		
		if(pc[1]==-1)//��ʾ��һ��ָ����ڴ淢��ȱҳ�ж�
		{
			
			
			psw=OsPcb.state.PAGEinterrupt;//
			return ;
		}
		int row,line;
		row=pc[1]/16;//����һ��ָ�����begin��ҳλ��
		line=pc[1]%16;;//����һ��ָ�����begin��ҳ��λ��
		for(int i=0;i<4;i++)//��ȡ��һ��ָ�cpu��ָ��Ĵ���
		{
			ir[i]=OsMemory.memory[row][line+i];
		}
	}
}

