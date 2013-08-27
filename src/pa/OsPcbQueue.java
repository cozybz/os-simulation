package pa;
/*
 * ���д洢���ܵ���
 */
public class OsPcbQueue {
	public static Pcb course_q[];//���̿��ƿ���� ��֯��ʽΪ������ʽ  ��ʼ���հ׽��̿��ʱ���ٿ��ٿռ�
	public static int running;//�������еĽ���ָ��
	public static int ready_q;//��������ͷָ��
	public static int block_q;//��������ͷָ��
	public static int free_q;//���ж���ͷָ��
	public static int remain_cou;//���ƿɴ����Ľ�����Ϊ10��
	
//	public static enum kind_tran{spare,end,time,interrupt};//������ȵ����  �������п��н��̣����жϣ�ʱ�����	 �����ж�
	static
	{
		remain_cou=10;
		running=-1;
		ready_q=-1;
		block_q=-1;
		free_q=0;
		course_q=new Pcb[10];
		for(int i=0;i<remain_cou;i++)//��ʼֻ�п��п�����
		{
			course_q[i]=new Pcb();
			if(i!=remain_cou-1)
				course_q[i].next=i+1;			
		}
	}
	public static void getOutBlock(int num)
	{
		int temp=block_q;
		if(num>-1&&num<10)
		{
			while(true)
			{
				if(block_q==num)
				{
					block_q=course_q[temp].next;
					course_q[temp].next=-1;
					break;
				}
				else if(temp==num)
				{
					course_q[temp].next=course_q[num].next;//������������ɾ��Ҫ �����ѵĵĽ���
					break;
				}
				temp=course_q[temp].next;//��Ϊָ����в���˫������Ա����ҵ�ǰһ��
			}
		}
	}
	public static void getOutBlock(String name)
	{
		int temp=course_q[block_q].next;
		int tempbef=block_q;
		while(true)
		{
			if(course_q[block_q].pcb.getName().equals(name))
			{
				int i=block_q;
				block_q=course_q[block_q].next;
				course_q[i].next=-1;
				break;
			}
			else if(course_q[temp].pcb.getName().equals(name))
			{
				course_q[tempbef].next=course_q[temp].next;//������������ɾ��Ҫ �����ѵĵĽ���
				break;	
			}
			temp=course_q[temp].next;//��Ϊָ����в���˫������Ա����ҵ�ǰһ��
			tempbef=course_q[tempbef].next;
		}
	}
	public static void putInblock(int num)
	{
		int temp=block_q;
		if(block_q==-1)
		{
			course_q[num].next=block_q;
			block_q=num;
		}
		else
		{
			while(true)
			{
				temp=course_q[temp].next;//�ߵ�ָ���β
				if(course_q[temp].next==-1)
				{
					course_q[temp].next=num;
					course_q[num].next=-1;//���һ��ָ��-1
					break;
				}
			}
		}
//		course_q[num].pcb.setPsw(OsPcb.state.block);
	}
	public static void setFree(int num)//�黹�հ׿��ƿ�
	{
		int temp;
		if(num>-1&&num<10)
		{
			temp=free_q;
			free_q=num;
			course_q[num].next=temp;
		}
		else
			System.out.println("�黹���̿��ƿ����");
	}
	public static int getFree()//��ÿհ׿��ƿ�
	{
		int temp;//��¼�������
		if(remain_cou==0)
			return -1;
		else
		{
			temp=free_q;//��¼���ÿհ׿��ƿ�
			free_q=course_q[free_q].next;
			remain_cou--;//���п�������һ
			course_q[temp].next=-1;//��Ϊԭ���ǿ��п�����
			return temp;
		}
	}
	public static void putInReady(String name)//name��ʾ��Ϊname�Ŀ��ƿ�����������
	{
		int temp=ready_q;
		int num=-1;//��¼name ���̵����
		for(int i=0;i<10;i++)
		{
			if(OsPcbQueue.pcbName(i)==name)//�ҵ�Ҫ�������Ľ����ڿ��ƿ���е�λ��
			{
				num=i;
				break;
			}
		}

		if(ready_q==-1)
		{
			course_q[num].next=ready_q;
			ready_q=num;
		}
		else
		{
			while(true)
			{
				
				if(course_q[temp].next==-1)
				{
					course_q[temp].next=num;
					course_q[num].next=-1;
					break;
				}
				temp=course_q[temp].next;//�ߵ�ָ���β
			}
		}
//		course_q[num].pcb.setPsw(OsPcb.state.ready);
	}
	public static void putInReady(int num)//num��ʾ�ڼ������ƿ�����������
	{
		int temp=ready_q;
		if(ready_q==-1)
		{
			course_q[num].next=ready_q;
			ready_q=num;
			
		}
		else
		{
			while(true)
			{
				
				if(course_q[temp].next==-1)
				{
					course_q[temp].next=num;
					course_q[num].next=-1;
					break;
				}
				temp=course_q[temp].next;//�ߵ�ָ���β
			}
		}
//		course_q[num].pcb.setPsw(OsPcb.state.ready);
	}
	public static int getReady()//��þ������еĵ�һ������
	{
		int temp;//��¼�������
		if(remain_cou==0)
			return -1;
		else
		{
			temp=ready_q;//��¼���ÿհ׿��ƿ�
			ready_q=course_q[ready_q].next;
			return temp;
		}
	}
	public static void ini_pcb(int num,int begin,String path)//��ʼ�����̿��ƿ�
	{
		course_q[num].pcb.setBegin(begin);
		course_q[num].pcb.setPath(path);//�洢����·��
	}
	public static boolean isRunning()//����Ƿ��н�������ִ��
	{
		if(OsPcbQueue.running==-1)
		{
			return false;
		}
		else
			return true;
	}
	public static boolean timeOut()//���ʱ��Ƭ�Ƿ�����
	{
		if(OsPcbQueue.course_q[OsPcbQueue.running].pcb.getExecute_time()>=OsPcb.timeslice)
		{
			return true;
		}
		else
			return false;
	}
	public static String pcbName(int num)
	{
		return course_q[num].pcb.getName();
	}
	public static void pcbToCpu(int num)//����Ҫ���еĽ�����Ϣ���浽cpu��,
	{
		OsCpu.begin=course_q[num].pcb.getBegin();
		OsCpu.dr=course_q[num].pcb.getDr();
		course_q[num].pcb.getOwn_eq(OsCpu.own_eq);
		OsCpu.psw=course_q[num].pcb.getPsw();
		OsCpu.pc[0]=course_q[num].pcb.getPc_num();//�п����ǵ�һ�γ�ʼ�� �п����ǻָ�ִ��
		if(course_q[num].pcb.getPsw()!=OsPcb.state.timeout)
			course_q[num].pcb.pc_numUp();
		OsCpu.pc[1]=course_q[num].pcb.getPc(OsCpu.pc[0]);
//		System.out.println(OsCpu.pc[0]+"num");
//		System.out.println(OsCpu.pc[1]+"order");
		int row,line;
		row=course_q[num].pcb.getPc(OsCpu.pc[0])/16;
		line=course_q[num].pcb.getPc(OsCpu.pc[0])%16;
//		System.out.println(row+"row");
//		System.out.println(line+"line");
//		System.out.println(course_q[num].pcb.getBegin()+"begin");
		for(int i=0;i<4;i++)//��ȡ��һ��ָ�cpu��ָ��Ĵ���
		{
			
			OsCpu.ir[i]=OsMemory.memory[row][line+i];
			
		}
		OsCpu.runtime=0;
	}
	public static void cpuToPcb(int num)//��cpu�е���Ϣ���浽��Ҫֹͣ�Ľ�����
	{
		course_q[num].pcb.setBegin(OsCpu.begin);
		course_q[num].pcb.setDr(OsCpu.dr);
		course_q[num].pcb.setPsw(OsCpu.psw);
		course_q[num].pcb.setPc_num(OsCpu.pc[0]);//�������е��ڼ���ָ��,����Ҫ����ָ���ַ��pcb�ж���
		course_q[num].pcb.setDr(OsCpu.dr);
		course_q[num].pcb.setOwn_eq(OsCpu.own_eq);
		course_q[num].pcb.setExecute_time(OsCpu.runtime);
	}
	public static String getAllName()
	{
		String name=" ";
		int temp_b=block_q;
		int temp_r=ready_q;
		while(temp_b!=-1||temp_r!=-1)
		{
			if(temp_b!=-1)
			{
				name+=course_q[temp_b].pcb.getName();
				temp_b=course_q[temp_b].next;
			}
			if(temp_r!=-1)
			{
				name+=course_q[temp_r].pcb.getName();
				temp_r=course_q[temp_r].next;
			}
		}
		return name;
	}
}
class Pcb
{
	OsPcb pcb;
	int next;
	Pcb()
	{
		pcb=null;
		next=-1;
	}
}










