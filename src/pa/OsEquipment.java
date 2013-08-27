package pa;

public class OsEquipment{
	public static int top[];//各种等待队列的头指针
	public static int tail[];//各种等待队列的尾指针
	public static int eqa;//当前占有该设备的进程数
	public static int eqb;//当前占有该设备的进程数
	public static int eqc;//当前占有该设备的进程数
	public static String a_name[];//占用当前设备的名称
	public static String b_name[];//占用当前设备的名称
	public static String c_name[];//占用当前设备的名称
	public static OsPcb wait_q[][];//存放等待进程在进程控制块队列的序号
	public static int wait_n[][];//存放等待某设备的进程个数
	static
	{
		top= new int [3];
		for(int i=0;i<3;i++)top[i]=0;
		tail=new int [3];
		for(int i=0;i<3;i++)tail[i]=0;
		eqa=0;
		eqb=0;
		eqc=0;
		a_name=new String[3];
		b_name=new String[2];
		c_name=new String[1];
		
		for(int i=0;i<3;i++)
			a_name[i] =new String(" ");
		
		for(int i=0;i<2;i++)
			b_name[i] =new String(" ");
		
		c_name[0]=new String(" ");

		
		wait_q=new OsPcb[3][10];//各类设备等待队列
		wait_n=new int[3][1];
		for(int i=0;i<3;i++)
		{
			wait_n[i][0]=0;
			top[i]=0;
			tail[i]=0;
			for(int j=0;j<10;j++)
			{
				wait_q[i][j]=null;
			}
		}
	}
	public static boolean apply_eq(OsPcb pcb)//申请占用
	{

		OsPcb.lack lack=pcb.getReason_b();
		byte eq[]=new byte[3];
		pcb.getOwn_eq(eq);//避免该进程已有设备被覆盖为0
		
		if(lack==OsPcb.lack.lackA)
		{
			
			if(eqa<3)
			{
				if(eq[0]<3)
					eq[0]++;
				a_name[eqa]=pcb.getName();
				eqa++;
				pcb.setOwn_eq(eq);
				for(int i=0;i<10;i++)//更新等待队列
					if(wait_q[0][i]==pcb)
						wait_q[0][i]=null;
				OsCourse.wake(pcb.getName());//设备申请到唤醒
				return true;
			}
			else
			{
				wait_q[0][tail[0]]=pcb;//放入等待队列
				tail[0]=(tail[0]+1)%10;//尾加一
				wait_n[0][0]++;//等待数量加一
			}
		}
		else if(lack==OsPcb.lack.lackB)
		{
			
			if(eqb<2)
			{
				if(eq[1]<2)
					eq[1]++;
				b_name[eqb]=pcb.getName();
				eqb++;
				pcb.setOwn_eq(eq);
				for(int i=0;i<10;i++)//更新等待队列
					if(wait_q[1][i]==pcb)
						wait_q[1][i]=null;
				OsCourse.wake(pcb.getName());//设备申请到唤醒
				return true;
			}
			else
			{
				wait_q[1][tail[1]]=pcb;//放入等待队列
				tail[1]=(tail[1]+1)%10;//尾加一
				wait_n[1][0]++;//等待数量加一
			}
		}
		else if(lack==OsPcb.lack.lackC)
		{
			if(eqc<1)
			{
				if(eq[2]<1)
					eq[2]++;
				c_name[0]=pcb.getName();
				eqc++;
				pcb.setOwn_eq(eq);
				for(int i=0;i<10;i++)//更新等待队列
					if(wait_q[2][i]==pcb)
						wait_q[2][i]=null;
				OsCourse.wake(pcb.getName());//设备申请到唤醒
				return true;
			}
			else
			{
				wait_q[2][tail[2]]=pcb;//放入等待队列
				tail[2]=(tail[2]+1)%10;//尾加一
				wait_n[2][0]++;//等待数量加一
			}
		}
		return false;
	}
	public static void freeEq(OsPcb pcb)//解除占用函数
	{
		byte own[]=new byte[3];
		pcb.getOwn_eq(own);
		if(own[0]>-1)
		{
			OsCpu.dr1[0]=0;//时间片用完同样释放设备
			own[0]--;//修改pcb中占用设备的数据
			for(int i=0;i<3;i++)
				if(a_name[i].equals(pcb.getName()))
					a_name[i]=" ";
			eqa--;//修改设备管理的信息
			pcb.setOwn_eq(own);
			OsCpu.own_eq[0]=own[0];//修改cpu
			if(wait_n[0][0]!=0)
				apply_eq(wait_q[0][top[0]]);//唤醒等待该设备的进程去占用该设备
		}
		else if(own[1]>-1)
		{
			OsCpu.dr1[1]=0;//时间片用完同样释放设备
			own[1]--;//修改pcb中占用设备的数据
			for(int i=0;i<2;i++)
				if(b_name[i].equals(pcb.getName()))
					b_name[i]=" ";
			eqb--;//修改设备管理的信息
//			display_eq.top(2, 3-eqb, b_name);//显示
			pcb.setOwn_eq(own);
			OsCpu.own_eq[1]=own[1];//修改cpu
			if(wait_n[1][0]!=0)
				apply_eq(wait_q[1][top[1]]);//唤醒等待该设备的进程去占用该设备
		}
		else if(own[2]>-1)
		{
			OsCpu.dr1[2]=0;//时间片用完同样释放设备
			own[2]--;//修改pcb中占用设备的数据
			c_name[0]=" ";
			eqc--;//修改设备管理的信息
//			display_eq.top(3, 3-eqb, b_name);//显示
			pcb.setOwn_eq(own);
			OsCpu.own_eq[2]=own[2];//修改cpu
			if(wait_n[2][0]!=0)
				apply_eq(wait_q[2][top[2]]);//唤醒等待该设备的进程去占用该设备
		}
	}
}
