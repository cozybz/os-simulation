package pa;

import java.util.Random;

import javax.swing.JPanel;

import pa.OsPcb.lack;
import pa.OsPcb.state;

public class OsCpu implements Runnable{
	public static state psw;//程序状态字
	public static int dr;//数据缓冲寄存器,存储正在执行的指令中的数据（用于计算的数据）
	public static int dr1[];;//设备计时数据必须正数（0也不行）,一个进程可能对应多个设备，且不同步占用
	public static byte own_eq[];//该进程占有的设备，可同时占有多个   0管A  1管B  2管C
	public static byte[] ir;//，存放将要执行的指令
	public static int begin;//该进程在内存的起始位置，方便cpu执行时寻找，按行计数的总共个数
	public static int pc[];//相对内存中该进程位置的下一个指令位置,pc[0]存放下一个指令的地址，pc[1]存放pc[0]记录的控制块中pc的第几个
	public static int runtime;//获得处理机的进程已经运行的时间
	public static OsPcb run;//正在执行的进程
	
	//界面显示
	private OsInterphase oi;
	private OsMemInt 	memdis=new OsMemInt(OsInterphase.panel_st);
	public static boolean open;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!开机键
	static
	{
		psw=OsPcb.state.running;
		pc=new int[2];
		pc[0]=-1;
		pc[1]=-1;
		dr=10000;//课程设计要求为1位所以初始化计算后都不可能为10000
		dr1=new int[3];//设备计时数据必须正数（0也不行）,一个进程可能对应多个设备，且不同步占用
		own_eq=new byte[3];
		ir=new byte[4];
		begin=-1;
		runtime=0;
		run=new OsPcb("idle",-1);//被替换后有垃圾处理装置
		
	}
	public  void run()//cpu线程,一秒执行一次
	{
		while (open)
		{ 
			System.out.println("cpu         "+run.getName());
			if(run.getName()!="idle")//不是空闲进程再执行,由调度进行信息初始化
			{
				
//				memdis.image();
//				memdis.image.updateUI();
				
				if(psw==OsPcb.state.running)//运行进程,如果中断改变cpu的psw
				{
					
					while(open)//无意外一直执行，有中断跳出，否则执行到时间片用完
					{
						System.out.println("时间"+runtime);
						if(runtime==5)
						{
							System.out.println("时间片用完");
							psw=OsPcb.state.timeout;//时间片用完
							break;
						}
						
						String order=new  String(ir);//指令的指定格式是不变的，变得是占有设备和数据
						System.out.println(order+"order");
//						System.out.println(new String(own_eq)+"own");
						
						
						if((ir[0]=='x'||ir[0]=='X')&&ir[1]=='='&&ir[3]==';')//判定命令格式，并且判定ir[2]中只是一个字符
						{
							if(ir[2]=='?')//指令为要求用户输入
							{
								System.out.println("无初值的赋值命令");
								if(dr==10000)
								{
									psw=OsPcb.state.IOinterrupt;
									break;
								}
								else if(dr>-10&&dr<10)
								{
									updateOder();//更新指令的相关信息
									if(psw==OsPcb.state.PAGEinterrupt)//更新指令的时候发生缺页中断
										break;
								}
							}
							else  if(ir[2]>'0'&&ir[2]<='9')//是一位数，因为有可能是其他一个字符
							{
								dr=ir[2]-'0';
								System.out.println("有初值的赋值命令");
								updateOder();//更新指令的相关信息
								if(psw==OsPcb.state.PAGEinterrupt)//更新指令的时候发生缺页中断
									break;
							}
							else//指令错误！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
							{
								//界面提示
								psw=OsPcb.state.ENDinterrupt;//发现错误指令直接终止程序
							}
						}
						else if(order.equals("x++;")||order.equals("X++;"))
						{
							if(dr!=10000)//不能依据dr为个位判定，有可能计算后超过了10000一般达不到
							{
								dr++;
								updateOder();//更新指令的相关信息
								if(psw==OsPcb.state.PAGEinterrupt)//更新指令的时候发生缺页中断
									break;
							}
						}
						else if(order.equals("x--;")||order.equals("X--;"))
						{
							if(dr!=10000)//不能依据dr为个位判定，有可能计算后超过了10000一般达不到
							{
								dr--;
								updateOder();//更新指令的相关信息
								if(psw==OsPcb.state.PAGEinterrupt)//更新指令的时候发生缺页中断
									break;
							}
						}
						else if(ir[0]=='!'&&ir[3]==';')//申请设备指令的固定格式,该指令的格式只可能为 eg   "!A5;";其中字母数字不定,需要设备和时间应有程序定而不是人
						{
							int temp=ir[2]-'0';//确定第3个字符为正确指令的字符
							
							if(own_eq[0]==-1&&own_eq[1]==-1&&own_eq[2]==-1&&temp>-1&&temp<10)//该进程第一次执行没有设备
							{
							
								
								//在指令正确的前提下告诉中断程序如何处理
								if((ir[1]=='A'||ir[1]=='a'))
								{
									run.setReason_b(OsPcb.lack.lackA);//因缺A中断
									psw=OsPcb.state.Equinterrupt;
									break;
								}
								else if(ir[1]=='B'||ir[1]=='b')
								{
									run.setReason_b(OsPcb.lack.lackB);//因缺B中断
									psw=OsPcb.state.Equinterrupt;
									break;
								}
								else if(ir[1]=='C'||ir[1]=='c')
								{
									run.setReason_b(OsPcb.lack.lackC);//因缺C中断
									psw=OsPcb.state.Equinterrupt;
									break;
								}
								else//申请设备不存在！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
								{
									//界面提示
									psw=OsPcb.state.ENDinterrupt;//发现错误指令直接终止程序
								}
							}
							else if((own_eq[0]>-1)&&temp>-1&&temp<10)
							{
								Random r=new Random();//随机生成数
								temp=r.nextInt(ir[2]-'0')+1;
								System.out.println(temp+"AAAA"+(ir[2]-'0'));
								dr1[0]=temp;
								updateOder();//更新指令的相关信息
								if(psw==OsPcb.state.PAGEinterrupt)//更新指令的时候发生缺页中断
									break;
							}
							else if((own_eq[1]>-1)&&temp>-1&&temp<10)
							{
								Random r=new Random();//随机生成数
								temp=r.nextInt(ir[2]-'0')+1;
								System.out.println(temp+"BBBB"+(ir[2]-'0'));
								//程序在前面可能已经申请到设备 ， 此处再用可能时间不够了 那么重新赋值设备使用时间
								dr1[1]=temp;
								updateOder();//更新指令的相关信息
								if(psw==OsPcb.state.PAGEinterrupt)//更新指令的时候发生缺页中断
									break;
							}
							else if((own_eq[2]>-1)&&temp>-1&&temp<10)
							{
								Random r=new Random();//随机生成数
								temp=r.nextInt(ir[2]-'0')+1;
								System.out.println(temp+"CCCC"+(ir[2]-'0'));
								//程序在前面可能已经申请到设备 ， 此处再用可能时间不够了 那么重新赋值设备使用时间
								dr1[2]=temp;
								updateOder();//更新指令的相关信息
								if(psw==OsPcb.state.PAGEinterrupt)//更新指令的时候发生缺页中断
									break;
							}
							else //指令错误！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
							{
								//界面提示
								psw=OsPcb.state.ENDinterrupt;//发现错误指令直接终止程序
							}
						}
						else if(order.equals("end.")||order.equals("END."))
						{
							psw=OsPcb.state.ENDinterrupt;
							break;
						}
						else//非法指令！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！反给界面
						{
							//返回界面
							psw=OsPcb.state.ENDinterrupt;//发现错误指令直接终止程序
						}

						runtime++;//运行时间加1，因为cpu一秒运行一次
						if(own_eq[0]>-1)
						{
							dr1[0]--;
							System.out.println(dr1[0]+"A剩余时间");
							if(dr1[0]==0)//设备占用完毕，释放
							{
								System.out.println("释放设备A");
								OsEquipment.freeEq(run);
							}
						}
						if(own_eq[1]>-1)
						{
							dr1[1]--;
							System.out.println(dr1[1]+"B剩余时间");
							if(dr1[1]==0)//设备占用完毕，释放
							{
								System.out.println("释放设备B");
								OsEquipment.freeEq(run);
							}
						}
						if(own_eq[2]>-1)
						{
							dr1[2]--;
							System.out.println(dr1[2]+"C剩余时间");
							if(dr1[2]==0)//设备占用完毕，释放
							{
								System.out.println("释放设备C");
								OsEquipment.freeEq(run);
							}
						}
						
//						System.out.println("cpu出来了");
						try
						{

							Thread.sleep(1000);  //cpu   1秒执行一次
						}
						catch (InterruptedException ex)
						{
							ex.printStackTrace();  //输出出错信息
						}
					}//end while(true)
				}
				//中断产生在运行时
				if(psw==OsPcb.state.ENDinterrupt)//软中断，与上面不是else关系否则运行时产生中断在本次运行无法处理
				{
					for(int i=0;i<4;i++)
					{
						ir[i]=' ';
					}
					dr=10000;
					OsSloveInt.sloveInt(OsPcb.state.ENDinterrupt,run);
				}
				else if(psw==OsPcb.state.PAGEinterrupt)//缺页中断,不放弃cpu
				{
					//缺页中断也算运行完一次指令
					runtime++;//运行时间加1，因为cpu一秒运行一次
					if(own_eq[0]>-1)
					{
						dr1[0]--;
						System.out.println(dr1[0]+"剩余时间");
						if(dr1[0]==0)//设备占用完毕，释放
						{
							System.out.println("释放设备A");
							OsEquipment.freeEq(run);
						}
					}
					if(own_eq[1]>-1)
					{
						dr1[1]--;
						
						if(dr1[1]==0)//设备占用完毕，释放
						{
							System.out.println("释放设备B");
							OsEquipment.freeEq(run);
						}
					}
					if(own_eq[2]>-1)
					{
						dr1[2]--;
						if(dr1[2]==0)//设备占用完毕，释放
						{
							System.out.println("释放设备C");
							OsEquipment.freeEq(run);
						}
					}
					System.out.println("缺页调度");
					
					OsPcbQueue.cpuToPcb(OsPcbQueue.running);//缺页中断也要进行信息赋值
					OsMemory.pageDispatcher(run);
					psw=OsPcb.state.running;
					
				}
				else if(psw==OsPcb.state.IOinterrupt)//IO中断
				{
					OsSloveInt.sloveInt(OsPcb.state.IOinterrupt,run);
				}
				else if(psw==OsPcb.state.Equinterrupt)//设备中断
				{
					System.out.println("设备中断");
					
					OsSloveInt.sloveInt(OsPcb.state.Equinterrupt,run);
				}
				else if(psw==OsPcb.state.timeout)//时间片用完
				{
					System.out.println(run.getPc_num());
					OsEquipment.freeEq(run);//避免死锁的    请求和保持的必要条件
					OsPcbQueue.cpuToPcb(OsPcbQueue.running);
					OsPcbQueue.running=-1;
					OsPcbQueue.putInReady(run.getName());
					OsMemory.dispatcher();
					System.out.println(run.getPc_num());

				}
			}
			else//如果是空闲进程什么都不用做
			{
				System.out.println("空闲调用");
				OsMemory.dispatcher();//空闲调用
			}
			if(run.getName()=="idle")
			{
				try
				{

					Thread.sleep(1000);  //cpu   1秒执行一次
				}
				catch (InterruptedException ex)
				{
					ex.printStackTrace();  //输出出错信息
				}
			}
			
		}//end while(open)	
	}
	public void updateOder()//更新cpu中有关指令的信息,此时有可能发生缺页中断
	{
		
	
		pc[0]=OsPcbQueue.course_q[OsPcbQueue.running].pcb.getPc_num();//读取该执行的指令个数
		run.pc_numUp();//下一条指令
		pc[1]=OsPcbQueue.course_q[OsPcbQueue.running].pcb.getPc(pc[0]);//读取下一条指令的地址
		
		if(pc[1]==-1)//表示下一条指令不在内存发生缺页中断
		{
			
			
			psw=OsPcb.state.PAGEinterrupt;//
			return ;
		}
		int row,line;
		row=pc[1]/16;//求下一条指令相对begin的页位置
		line=pc[1]%16;;//求下一条指令相对begin的页内位置
		for(int i=0;i<4;i++)//读取下一条指令到cpu的指令寄存器
		{
			ir[i]=OsMemory.memory[row][line+i];
		}
	}
}

