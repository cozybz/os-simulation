package pa;

public class OsMemory {

	public static byte memory[][];//�ڴ��
	public static int store[];//λʾͼ�����������ܷ���������Ƭ
	public static byte ir[];//ָ��Ĵ���,��ŵ�ǰ��Ҫִ�е�һ��ָ��
	public static int  dr;//��ŵ�ǰ��Ҫִ�е�ָ���е�����
	public static int pc[];//��������������һ��ָ��ĵ�ַ,pc[0]�����һ��ָ��ĵ�ַ��pc[1]���pc[0]��¼�Ŀ��ƿ���pc�ĵڼ���
	public static  int content;//�ڴ�ʣ������,��ҳΪ��λ��ÿҳ16b
	static
	{
		memory=new byte[32][16];//�ڴ���36�飬ÿ��16B
		store=new int [32];
		content=32;
		for(int i=0;i<32;i++)
			store[i]=0;//��ʼ��0��Ϊ���п�
		pc=new int[2];
		ir=new byte[4];
	}
	public static int apply(int num, int size)//��̽�Ƿ����㹻���ÿռ䣬�Ƿ����㹻��ҳ��,num��ʾ��Ҫ����Ľ��̿��ƿ�Ķ������,sizeΪҳ��
	{
		int begin=-1;//��¼�����׵�ַ
		int p;//��¼��һ��ָ������
		int order_num=size*4;//�ж�����ָ��
		if(size>content)//����������ʣ���ж��Ƿ�ɷ���
			return begin;
		else//������ʣ�๻�ˣ����ҷ����ַ
		{
			content-=size;
			int i=0;//iΪ��ʱ����
			int j=0;//jΪ��ʱ����
			while(size!=0)//���Һ��������ַ,ѭ������,   ����ҳ
			{
				if(store[i]==0)
				{
					if(begin==-1)//�ж��ǵ�һ���ҵ����п�
						begin=i;
					//��¼ÿһ��ָ���ַ���ҵ�һҳѭ��4�� ��Ϊÿҳ4��ָ��
					int k=4;
					while(k!=0)//����ÿһ��
					{
						p=begin*16+j*4;//���ֽ�Ϊ��λ,begin*16��ʾ���ٸ��ֽ�
						OsPcbQueue.course_q[num].pcb.setPc(p,j);//�˴�����ҳʽ�洢������3��ҳ
						j++;
						k--;
					}
					store[i]=1;
					size--;
				}
				i=(i+1)%32;//Ϊ��ѭ������
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
	public static void assign(int num,byte order[][],int begin)//����,����ҳʽ����,num��ʾ��Ҫ����Ľ��̿��ƿ�Ķ������
	{
		int row,line;//��Ϊbegin���ܸ���Ҫ�ֵڼ��еؼ���,rowΪ������lineΪ����
		row=begin;//��Ϊ������ȡ��
		line=0;//ÿ�ζ����¿�
		int next=1;//��һ��ָ��
		for(int i=0;i<12;i++)
		{
			for(int j=0;j<4;j++)
			{
				memory[row][line]=order[i][j];
				line++;
				if(line==16)
				{
					System.out.println(new String(memory[row])+OsPcbQueue.course_q[num].pcb.getName()+" ���ָ�� "+row);
					row=OsPcbQueue.course_q[num].pcb.getPc(next)/16;
					
					if(row>=32)//ѭ��
					{
						row=row%32;
					}
					line=0;//�����й��㣻
				}
			}
			next++;//��һ��ָ��ĵ�ַ
		}
	}
	public static int callback(int begin,int pc[])//���գ�ֱ�ӻ���ҳ���ɣ��ѱ�־λ���㼴�ɣ��´�ʹ�ø��Ǿ���
	{
		int temp;
		for(int i=0;i<48&&pc[i]!=-1;i++)
		{
			temp=pc[i]/16;
			store[temp]=0;//��λΪ���п�
		}
		content+=3;//ÿ�����̶̹�ռ��3��ҳ
		return 0;
	}

	public static void dispatcher()//���̵ĵ���,����ʱ��ƫ��ת�����㷨,kindȷ����ʱ����ĵ���
	{//����ʱ�ѽ��̿��ƿ鸳��cpu������ֻ���ĸ������ã����ܳ����������޽��̿ɵ�ʱ�����н���
		
		if(OsPcbQueue.ready_q!=-1)//�����������пɱ����Ƚ���
		{
			int temp;
			temp=OsPcbQueue.getReady();//�����һ��ִ�еľ������е����
//			System.out.println(temp+"dispa");
			OsPcbQueue.course_q[temp].pcb.setPsw(OsPcb.state.running);//�ı����״̬Ϊ����
			OsPcbQueue.pcbToCpu(temp);//���ý��̵�������Ϣ����cpu
			OsPcbQueue.running=temp;
			OsCpu.run=OsPcbQueue.course_q[temp].pcb;//�����̿��ƿ鸳��cpu
			System.out.println(OsCpu.run.getName()+"���̵���");
//			
		}
		else//�޿ɱ����Ƚ���,�����н���
		{
			OsCpu.run=new OsPcb("idle",-1);
			OsPcbQueue.running=-1;
		}
	}
	public static void pageDispatcher(OsPcb run)//ȱҳ���ȣ�Ӧ�ó�פ�ڴ����Բ��Ѿ�����������жϴ�������У��˴�ʹ���Ƚ��ȳ�
	{
		byte nextpage[][]=new byte[4][4];
		int row,line;
		OsDiskManager odm = new OsDiskManager("c");
		OsFileManager osm  = new OsFileManager(odm);
		osm.getPage(run.getPc_num(),run.getPath(),nextpage);
		
		if(run.getPc_num()%3==0)//����ҳ�����ж�ʱ,pc_num��12��ʼ
		{
			
			row=run.getBegin();
			line=0;
			for(int i=0;i<4;i++)
			{
				for(int j=0;j<4;j++)
				{
					if(j==0)
						run.setPc(16*row+line, run.getPc_num()+i);//�޸�pcb�е�ָ����Ϣ
					if(i==0&&nextpage[i][j]!=-10)
						OsCpu.ir[j]=nextpage[i][j];//ͬʱ����cpuָ��
					if(nextpage[i][j]!=-10)
						OsMemory.memory[row][line]=nextpage[i][j];//����ҳ
					line++;
				}
			}
			run.pc_numUp();//��Ϊ���������һ��ָ��
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
						run.setPc(16*row+line, run.getPc_num()+i);//�޸�pcb�е�ָ����Ϣ
					if(i==0&&nextpage[i][j]!=-10)
						OsCpu.ir[j]=nextpage[i][j];//ͬʱ����cpuָ��
					if(nextpage[i][j]!=-10)
						OsMemory.memory[row][line]=nextpage[i][j];
					line++;
				}
			}
			run.pc_numUp();//��Ϊ���������һ��ָ��
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
						run.setPc(16*row+line, run.getPc_num()+i);//�޸�pcb�е�ָ����Ϣ
					if(i==0&&nextpage[i][j]!=-10)
						OsCpu.ir[j]=nextpage[i][j];//ͬʱ����cpuָ��
					if(nextpage[i][j]!=-10)
						OsMemory.memory[row][line]=nextpage[i][j];
					line++;
				}
			}
			run.pc_numUp();//��Ϊ���������һ��ָ��
//			int q[]=run.getPc();
//			for(int c=0;c<32;c++)
//			{
//				System.out.println(q[c]);
//			}
//			System.out.println(new String (memory[row])+row+"pageeeeeeeeeeeeeeeeeeeeeeeeeee");
		}
	}

}
