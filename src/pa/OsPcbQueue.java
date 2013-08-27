package pa;
/*
 * 仅有存储功能的类
 */
public class OsPcbQueue {
	public static Pcb course_q[];//进程控制块队列 组织形式为索引方式  初始化空白进程块的时候再开辟空间
	public static int running;//正在运行的进程指针
	public static int ready_q;//就绪队列头指针
	public static int block_q;//阻塞队列头指针
	public static int free_q;//空闲队列头指针
	public static int remain_cou;//控制可创建的进程数为10个
	
//	public static enum kind_tran{spare,end,time,interrupt};//引起调度的情况  正在运行空闲进程，软中断，时间完成	 其他中断
	static
	{
		remain_cou=10;
		running=-1;
		ready_q=-1;
		block_q=-1;
		free_q=0;
		course_q=new Pcb[10];
		for(int i=0;i<remain_cou;i++)//开始只有空闲块链表
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
					course_q[temp].next=course_q[num].next;//从阻塞队列中删除要 被唤醒的的进程
					break;
				}
				temp=course_q[temp].next;//因为指针队列不是双向的所以必须找到前一项
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
				course_q[tempbef].next=course_q[temp].next;//从阻塞队列中删除要 被唤醒的的进程
				break;	
			}
			temp=course_q[temp].next;//因为指针队列不是双向的所以必须找到前一项
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
				temp=course_q[temp].next;//走到指针队尾
				if(course_q[temp].next==-1)
				{
					course_q[temp].next=num;
					course_q[num].next=-1;//最后一项指向-1
					break;
				}
			}
		}
//		course_q[num].pcb.setPsw(OsPcb.state.block);
	}
	public static void setFree(int num)//归还空白控制块
	{
		int temp;
		if(num>-1&&num<10)
		{
			temp=free_q;
			free_q=num;
			course_q[num].next=temp;
		}
		else
			System.out.println("归还进程控制块出错！");
	}
	public static int getFree()//获得空白控制块
	{
		int temp;//记录返回序号
		if(remain_cou==0)
			return -1;
		else
		{
			temp=free_q;//记录可用空白控制块
			free_q=course_q[free_q].next;
			remain_cou--;//空闲块数量减一
			course_q[temp].next=-1;//因为原来是空闲块链表
			return temp;
		}
	}
	public static void putInReady(String name)//name表示名为name的控制块放入就绪队列
	{
		int temp=ready_q;
		int num=-1;//记录name 进程的序号
		for(int i=0;i<10;i++)
		{
			if(OsPcbQueue.pcbName(i)==name)//找到要被撤销的进程在控制块队列的位置
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
				temp=course_q[temp].next;//走到指针队尾
			}
		}
//		course_q[num].pcb.setPsw(OsPcb.state.ready);
	}
	public static void putInReady(int num)//num表示第几个控制块放入就绪队列
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
				temp=course_q[temp].next;//走到指针队尾
			}
		}
//		course_q[num].pcb.setPsw(OsPcb.state.ready);
	}
	public static int getReady()//获得就绪队列的第一个进程
	{
		int temp;//记录返回序号
		if(remain_cou==0)
			return -1;
		else
		{
			temp=ready_q;//记录可用空白控制块
			ready_q=course_q[ready_q].next;
			return temp;
		}
	}
	public static void ini_pcb(int num,int begin,String path)//初始化进程控制块
	{
		course_q[num].pcb.setBegin(begin);
		course_q[num].pcb.setPath(path);//存储绝对路径
	}
	public static boolean isRunning()//检查是否有进程正在执行
	{
		if(OsPcbQueue.running==-1)
		{
			return false;
		}
		else
			return true;
	}
	public static boolean timeOut()//检查时间片是否用完
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
	public static void pcbToCpu(int num)//将将要运行的进程信息保存到cpu中,
	{
		OsCpu.begin=course_q[num].pcb.getBegin();
		OsCpu.dr=course_q[num].pcb.getDr();
		course_q[num].pcb.getOwn_eq(OsCpu.own_eq);
		OsCpu.psw=course_q[num].pcb.getPsw();
		OsCpu.pc[0]=course_q[num].pcb.getPc_num();//有可能是第一次初始化 有可能是恢复执行
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
		for(int i=0;i<4;i++)//读取下一条指令到cpu的指令寄存器
		{
			
			OsCpu.ir[i]=OsMemory.memory[row][line+i];
			
		}
		OsCpu.runtime=0;
	}
	public static void cpuToPcb(int num)//将cpu中的信息保存到将要停止的进程中
	{
		course_q[num].pcb.setBegin(OsCpu.begin);
		course_q[num].pcb.setDr(OsCpu.dr);
		course_q[num].pcb.setPsw(OsCpu.psw);
		course_q[num].pcb.setPc_num(OsCpu.pc[0]);//保存运行到第几个指令,不需要保存指令地址，pcb中都有
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










