package pa;

public class OsEquipment{
	public static int top[];//���ֵȴ����е�ͷָ��
	public static int tail[];//���ֵȴ����е�βָ��
	public static int eqa;//��ǰռ�и��豸�Ľ�����
	public static int eqb;//��ǰռ�и��豸�Ľ�����
	public static int eqc;//��ǰռ�и��豸�Ľ�����
	public static String a_name[];//ռ�õ�ǰ�豸������
	public static String b_name[];//ռ�õ�ǰ�豸������
	public static String c_name[];//ռ�õ�ǰ�豸������
	public static OsPcb wait_q[][];//��ŵȴ������ڽ��̿��ƿ���е����
	public static int wait_n[][];//��ŵȴ�ĳ�豸�Ľ��̸���
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

		
		wait_q=new OsPcb[3][10];//�����豸�ȴ�����
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
	public static boolean apply_eq(OsPcb pcb)//����ռ��
	{

		OsPcb.lack lack=pcb.getReason_b();
		byte eq[]=new byte[3];
		pcb.getOwn_eq(eq);//����ý��������豸������Ϊ0
		
		if(lack==OsPcb.lack.lackA)
		{
			
			if(eqa<3)
			{
				if(eq[0]<3)
					eq[0]++;
				a_name[eqa]=pcb.getName();
				eqa++;
				pcb.setOwn_eq(eq);
				for(int i=0;i<10;i++)//���µȴ�����
					if(wait_q[0][i]==pcb)
						wait_q[0][i]=null;
				OsCourse.wake(pcb.getName());//�豸���뵽����
				return true;
			}
			else
			{
				wait_q[0][tail[0]]=pcb;//����ȴ�����
				tail[0]=(tail[0]+1)%10;//β��һ
				wait_n[0][0]++;//�ȴ�������һ
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
				for(int i=0;i<10;i++)//���µȴ�����
					if(wait_q[1][i]==pcb)
						wait_q[1][i]=null;
				OsCourse.wake(pcb.getName());//�豸���뵽����
				return true;
			}
			else
			{
				wait_q[1][tail[1]]=pcb;//����ȴ�����
				tail[1]=(tail[1]+1)%10;//β��һ
				wait_n[1][0]++;//�ȴ�������һ
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
				for(int i=0;i<10;i++)//���µȴ�����
					if(wait_q[2][i]==pcb)
						wait_q[2][i]=null;
				OsCourse.wake(pcb.getName());//�豸���뵽����
				return true;
			}
			else
			{
				wait_q[2][tail[2]]=pcb;//����ȴ�����
				tail[2]=(tail[2]+1)%10;//β��һ
				wait_n[2][0]++;//�ȴ�������һ
			}
		}
		return false;
	}
	public static void freeEq(OsPcb pcb)//���ռ�ú���
	{
		byte own[]=new byte[3];
		pcb.getOwn_eq(own);
		if(own[0]>-1)
		{
			OsCpu.dr1[0]=0;//ʱ��Ƭ����ͬ���ͷ��豸
			own[0]--;//�޸�pcb��ռ���豸������
			for(int i=0;i<3;i++)
				if(a_name[i].equals(pcb.getName()))
					a_name[i]=" ";
			eqa--;//�޸��豸�������Ϣ
			pcb.setOwn_eq(own);
			OsCpu.own_eq[0]=own[0];//�޸�cpu
			if(wait_n[0][0]!=0)
				apply_eq(wait_q[0][top[0]]);//���ѵȴ����豸�Ľ���ȥռ�ø��豸
		}
		else if(own[1]>-1)
		{
			OsCpu.dr1[1]=0;//ʱ��Ƭ����ͬ���ͷ��豸
			own[1]--;//�޸�pcb��ռ���豸������
			for(int i=0;i<2;i++)
				if(b_name[i].equals(pcb.getName()))
					b_name[i]=" ";
			eqb--;//�޸��豸�������Ϣ
//			display_eq.top(2, 3-eqb, b_name);//��ʾ
			pcb.setOwn_eq(own);
			OsCpu.own_eq[1]=own[1];//�޸�cpu
			if(wait_n[1][0]!=0)
				apply_eq(wait_q[1][top[1]]);//���ѵȴ����豸�Ľ���ȥռ�ø��豸
		}
		else if(own[2]>-1)
		{
			OsCpu.dr1[2]=0;//ʱ��Ƭ����ͬ���ͷ��豸
			own[2]--;//�޸�pcb��ռ���豸������
			c_name[0]=" ";
			eqc--;//�޸��豸�������Ϣ
//			display_eq.top(3, 3-eqb, b_name);//��ʾ
			pcb.setOwn_eq(own);
			OsCpu.own_eq[2]=own[2];//�޸�cpu
			if(wait_n[2][0]!=0)
				apply_eq(wait_q[2][top[2]]);//���ѵȴ����豸�Ľ���ȥռ�ø��豸
		}
	}
}
