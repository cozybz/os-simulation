package pa;

public class OsMemory {

	public static byte memory[][];//内存块
	public static int store[];//位示图按块来，可能发生块内碎片
	public static byte ir[];//指令寄存器,存放当前需要执行的一条指令
	public static int  dr;//存放当前需要执行的指令中的数据
	public static int pc[];//程序计数器存放下一条指令的地址,pc[0]存放下一个指令的地址，pc[1]存放pc[0]记录的控制块中pc的第几个
	public static  int content;//内存剩余容量,以页为单位，每页16b
	static
	{
		memory=new byte[32][16];//内存有36块，每块16B
		store=new int [32];
		content=32;
		for(int i=0;i<32;i++)
			store[i]=0;//初始‘0’为空闲块
		pc=new int[2];
		ir=new byte[4];
	}
	public static int apply(int num, int size)//试探是否有足够可用空间，是否有足够的页数,num表示在要加入的进程控制块的队列序号,size为页数
	{
		int begin=-1;//记录物理首地址
		int p;//记录下一个指令在哪
		int order_num=size*4;//有多少条指令
		if(size>content)//根据总容量剩余判断是否可分配
			return begin;
		else//总容量剩余够了，查找分配地址
		{
			content-=size;
			int i=0;//i为零时变量
			int j=0;//j为零时变量
			while(size!=0)//查找合适物理地址,循环查找,   分配页
			{
				if(store[i]==0)
				{
					if(begin==-1)//判断是第一次找到空闲块
						begin=i;
					//记录每一条指令地址，找到一页循环4次 因为每页4个指令
					int k=4;
					while(k!=0)//分配每一条
					{
						p=begin*16+j*4;//以字节为单位,begin*16表示多少个字节
						OsPcbQueue.course_q[num].pcb.setPc(p,j);//此处虚拟页式存储仅放入3个页
						j++;
						k--;
					}
					store[i]=1;
					size--;
				}
				i=(i+1)%32;//为了循环查找
			}
			for(int c=0;c<32;c++)
				System.out.println(OsPcbQueue.course_q[num].pcb.getName()+store[i]);
			int q[]=OsPcbQueue.course_q[num].pcb.getPc();
			System.out.println(OsPcbQueue.course_q[num].pcb.getName());
			for(int c=0;c<32;c++)
			{
				System.out.println(q[c]);
			}
		}
		return begin;
	}
	public static void assign(int num,byte order[][],int begin)//分配,虚拟页式分配,num表示在要加入的进程控制块的队列序号
	{
		int row,line;//因为begin是总个数要分第几行地几列,row为行数，line为列数
		row=begin;//因为是趋零取整
		line=0;//每次都是新块
		int next=1;//下一条指令
		for(int i=0;i<12;i++)
		{
			for(int j=0;j<4;j++)
			{
				memory[row][line]=order[i][j];
				line++;
				if(line==16)
				{
					System.out.println(new String(memory[row])+OsPcbQueue.course_q[num].pcb.getName()+" 添加指令 "+row);
					row=OsPcbQueue.course_q[num].pcb.getPc(next)/16;
					
					if(row>=32)//循环
					{
						row=row%32;
					}
					line=0;//换行列归零；
				}
			}
			next++;//下一条指令的地址
		}
	}
	public static int callback(int begin,int pc[])//回收，直接回收页即可，把标志位置零即可，下次使用覆盖就行
	{
		int temp;
		for(int i=0;i<48&&pc[i]!=-1;i++)
		{
			temp=pc[i]/16;
			store[temp]=0;//置位为空闲块
		}
		content+=3;//每个进程固定占有3个页
		return 0;
	}

	public static void dispatcher()//进程的调度,采用时间偏轮转调度算法,kind确定何时引起的调度
	{//调度时把进程控制块赋给cpu，调度只管哪个可以用，不管撤销阻塞，无进程可调时给空闲进程
		
		if(OsPcbQueue.ready_q!=-1)//就绪队列中有可被调度进程
		{
			int temp;
			temp=OsPcbQueue.getReady();//获得下一个执行的就绪队列的序号
//			System.out.println(temp+"dispa");
			OsPcbQueue.course_q[temp].pcb.setPsw(OsPcb.state.running);//改变进程状态为运行
			OsPcbQueue.pcbToCpu(temp);//将该进程的所有信息赋给cpu
			OsPcbQueue.running=temp;
			OsCpu.run=OsPcbQueue.course_q[temp].pcb;//将进程控制块赋给cpu
			System.out.println(OsCpu.run.getName()+"进程调度");
//			
		}
		else//无可被调度进程,给空闲进程
		{
			OsCpu.run=new OsPcb("idle",-1);
			OsPcbQueue.running=-1;
		}
	}
	public static void pageDispatcher(OsPcb run)//缺页调度，应该常驻内存所以不把具体操作放在中断处理程序中，此处使用先进先出
	{
		byte nextpage[][]=new byte[4][4];
		int row,line;
		OsDiskManager odm = new OsDiskManager("c");
		OsFileManager osm  = new OsFileManager(odm);
		osm.getPage(run.getPc_num(),run.getPath(),nextpage);
		
		if(run.getPc_num()%3==0)//调用页发生中断时,pc_num从12开始
		{
			
			row=run.getBegin();
			line=0;
			for(int i=0;i<4;i++)
			{
				for(int j=0;j<4;j++)
				{
					if(j==0)
						run.setPc(16*row+line, run.getPc_num()+i);//修改pcb中的指令信息
					if(i==0&&nextpage[i][j]!=-10)
						OsCpu.ir[j]=nextpage[i][j];//同时更新cpu指令
					if(nextpage[i][j]!=-10)
						OsMemory.memory[row][line]=nextpage[i][j];//更新页
					line++;
				}
			}
			run.pc_numUp();//因为上面更新了一个指令
//			int q[]=run.getPc();
//			for(int c=0;c<32;c++)
//			{
//				System.out.println(q[c]);
//			}
//			System.out.println(new String (memory[row])+row+"pageeeeeeeeeeeeeeeeeeeeeeeeeee");
		}
		else if(run.getPc_num()%3==1)
		{
			row=run.getPc(4)/16;
			line=0;
			for(int i=0;i<4;i++)
			{
				for(int j=0;j<4;j++)
				{
					if(j==0)
						run.setPc(16*row+line, run.getPc_num()+i);//修改pcb中的指令信息
					if(i==0&&nextpage[i][j]!=-10)
						OsCpu.ir[j]=nextpage[i][j];//同时更新cpu指令
					if(nextpage[i][j]!=-10)
						OsMemory.memory[row][line]=nextpage[i][j];
					line++;
				}
			}
			run.pc_numUp();//因为上面更新了一个指令
//			int q[]=run.getPc();
//			for(int c=0;c<32;c++)
//			{
//				System.out.println(q[c]);
//			}
//			System.out.println(new String (memory[row])+row+"pageeeeeeeeeeeeeeeeeeeeeeeeeee");
		}
		
		else if(run.getPc_num()%3==2)
		{
			row=run.getPc(8)/16;
			line=0;
			for(int i=0;i<4;i++)
			{
				for(int j=0;j<4;j++)
				{
					if(j==0)
						run.setPc(16*row+line, run.getPc_num()+i);//修改pcb中的指令信息
					if(i==0&&nextpage[i][j]!=-10)
						OsCpu.ir[j]=nextpage[i][j];//同时更新cpu指令
					if(nextpage[i][j]!=-10)
						OsMemory.memory[row][line]=nextpage[i][j];
					line++;
				}
			}
			run.pc_numUp();//因为上面更新了一个指令
//			int q[]=run.getPc();
//			for(int c=0;c<32;c++)
//			{
//				System.out.println(q[c]);
//			}
//			System.out.println(new String (memory[row])+row+"pageeeeeeeeeeeeeeeeeeeeeeeeeee");
		}
	}

}
