package pa;

public class OsPcb {
	private String name;//标识符
	private String path;//该进程的文件的绝对路径
	private state psw;//程序状态字
	private lack reason_b;//a,b,c设备阻塞原因
//	private need need_eq;//所需设备
	private int dr;//数据缓冲寄存器,存储正在执行的指令中的数据
	private int dr1[];;//设备计时数据必须正数（0也不行）,一个进程可能对应多个设备，且不同步占用
	private int begin;//该进程在内存的起始位置，方便cpu执行时寻找，按行计数的总共个数
	private int pc[];//相对内存中该进程位置的下一个指令位置,存放相对begin的字节数
	private int pc_num;//存放阻塞时执行到第几条指令，主要用于cpu读
	private int execute_time;//该进程已经执行的时间
	private byte own_eq[];//该进程占用的设备,可同时占用多个,  0管A  1管B  2管C
	private int own_time[];//每个设备申请占用时间
	public static enum state{ready,running,block,ENDinterrupt,IOinterrupt,Equinterrupt,PAGEinterrupt,timeout,spare};
	//就绪状态,运行中,阻塞，结束中断，IO设备中断,设备中断  缺页中断 时间片中断   空闲（方便调度分类才添加的）
	public static enum lack{lackA,lackB,lackC};//由于三种设备阻塞
//	public static enum need{needA,needB,needC};//需要各种设备
	public static int timeslice=5;//时间片为5
	public static int page_number=3;//每个进程的虚拟页数为3
	OsPcb(String name,int begin)
	{
		this.name=name;
		this.begin=begin;
		psw=state.ready;
		reason_b=null;
//		need_eq=null;
		execute_time=0;
		pc_num=0;
		own_eq=new byte[3];
		own_time=new int[3];
		for(int i=0;i<3;i++)
		{
			own_eq[i]=-1;
		}
		dr=10000;
		dr1=new int [3];
		pc=new int[32];//每个存放相应的指令的地址
		pc[0]=0;//第一个始终从begin开始
		for(int i=1;i<32;i++)
		{
			if(i==1||i==2||i==3)
				pc[i]=4*i;//除了换页每页中每隔4个为一条指令
			else
				pc[i]=-1;//指示下一页地址，利用此值可判断缺页中断
		}
	}
	public void setName(String name)
	{
		this.name=name;
	}
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void setBegin(int begin)
	{
		this.begin=begin;
	}
	public int getBegin() {
		return begin;
	}
	public state getPsw() {
		return psw;
	}
	public void setPsw(state psw) {
		this.psw = psw;
	}
	public lack getReason_b() {
		return reason_b;
	}
	public void setReason_b(lack reason_b) {
		this.reason_b = reason_b;
	}
//	public need getNeed_eq() {
//		return need_eq;
//	}
//	public void setNeed_eq(need need_eq) {
//		this.need_eq = need_eq;
//	}
	
	public int getDr() {
		return dr;
	}
	public void setDr(int dr) {
		this.dr = dr;
	}
	
	public void getOwn_eq(byte eq[]) {
		
		for(int i=0;i<3;i++)
		{
			eq[i]=own_eq[i];
		}
	}
	public void setOwn_eq(byte eq[]) {
		for(int i=0;i<3;i++)
		{
			own_eq[i]=eq[i];
		}
	}
public void getOwn_time(int time[]) {
		
		for(int i=0;i<3;i++)
		{
			time[i]=own_time[i];
		}
	}
	public void setOwn_time(int time[]) {
		for(int i=0;i<3;i++)
		{
			own_time[i]=time[i];
		}
	}
	public int getPc(int i) {//返回下一条指令
		return pc[i];
	}
	public int[] getPc()//返回指令数组
	{
		return pc;
	}
	public void setPc(int p,int num)//设置P下一个指令的地址,num表示第几个指令
	{
		pc[num]=p;
	}
	public int getPc_num() {
		return pc_num;
	}
	public void pc_numUp() {
		pc_num++;
	}
	public void setPc_num(int pc_num) {
		this.pc_num = pc_num;
	}
	public void setExecute_time(int execute_time) {
		this.execute_time = execute_time;
	}
	public int getExecute_time() {
		return execute_time;
	}
}
