package pa;
/*
 * 关于操作Pcb的语句均在此
 */
import java.io.BufferedReader;
import java.io.FileReader;

public class OsCourse {

	public static int create(String name,byte order[][],String path)//进程的创建,0失败1成功
	{
		int begin;
		if(OsPcbQueue.remain_cou==0)
		{
			return 0;//进程数已满,无空白进程控制块
		}
		else//仍有空闲进程控制块可用
		{
			int temp;//新申请的控制块在就绪队列的序号
			temp=OsPcbQueue.getFree();//得到空白控制块,因为前面已经判定就绪队列是否还有位置
			OsPcbQueue.course_q[temp].pcb=new OsPcb(name,-1);//为了在下面apply中获得指令地址提前申请进程
			if((begin=OsMemory.apply(temp,OsPcb.page_number))==-1)//申请内存空间失败
			{
				return 0;
			}
			else
			{
				OsPcbQueue.ini_pcb(temp,begin,path);//初始化进程控制块
				OsPcbQueue.putInReady(temp);//将新建进程放入就绪队列
				OsMemory.assign(temp,order,begin);//得到内存空间	放入初始指令集		
			}
		}

		return 1;//申请进程成功
	}
	public static void destroy(String name)//进程撤销
	{
		for(int i=0;i<10;i++)
		{
			if(OsPcbQueue.pcbName(i)==name)//找到要被撤销的进程在控制块队列的位置
			{
				if(OsPcbQueue.course_q[i].pcb.getPsw()==OsPcb.state.running)//如果该进程正在运行
				{
					OsPcbQueue.cpuToPcb(i);//更改正在执行的进程的所有信息 使其不执行
				}
				else
					OsPcbQueue.course_q[i].pcb.setPsw(OsPcb.state.ENDinterrupt);//如果为非运行进程只更改psw
				OsMemory.callback(OsPcbQueue.course_q[i].pcb.getBegin(), OsPcbQueue.course_q[i].pcb.getPc());//内存收回
				OsPcbQueue.setFree(i);//归还空白进程控制块
				OsIo.output(OsPcbQueue.course_q[i].pcb);//将数据写入out文件
				break;
			}
		}

	}
	public static void block(String name)//进程阻塞
	{
		for(int i=0;i<10;i++)
		{
			if(OsPcbQueue.pcbName(i)==name)//找到要被撤销的进程在控制块队列的位置
			{
				OsPcbQueue.cpuToPcb(i);//更改正在执行的进程的所有信息 使其不执行
				OsPcbQueue.putInblock(i);//将该进程放入阻塞队列
				break;
			}
		}
	}
	public static void wake(String name)//进程唤醒,由设备完成
	{
		for(int i=0;i<10;i++)
		{
			if(OsPcbQueue.pcbName(i)==name)//找到要唤醒的进程在控制块队列的位置
			{

				OsPcbQueue.getOutBlock(i);//阻塞进程移出
				OsPcbQueue.putInReady(i);//将阻塞进程放进就绪队列末尾
				break;
			}
		}
	}

}
