package pa;

public class OsPcb {
	private String name;//��ʶ��
	private String path;//�ý��̵��ļ��ľ���·��
	private state psw;//����״̬��
	private lack reason_b;//a,b,c�豸����ԭ��
//	private need need_eq;//�����豸
	private int dr;//���ݻ���Ĵ���,�洢����ִ�е�ָ���е�����
	private int dr1[];;//�豸��ʱ���ݱ���������0Ҳ���У�,һ�����̿��ܶ�Ӧ����豸���Ҳ�ͬ��ռ��
	private int begin;//�ý������ڴ����ʼλ�ã�����cpuִ��ʱѰ�ң����м������ܹ�����
	private int pc[];//����ڴ��иý���λ�õ���һ��ָ��λ��,������begin���ֽ���
	private int pc_num;//�������ʱִ�е��ڼ���ָ���Ҫ����cpu��
	private int execute_time;//�ý����Ѿ�ִ�е�ʱ��
	private byte own_eq[];//�ý���ռ�õ��豸,��ͬʱռ�ö��,  0��A  1��B  2��C
	private int own_time[];//ÿ���豸����ռ��ʱ��
	public static enum state{ready,running,block,ENDinterrupt,IOinterrupt,Equinterrupt,PAGEinterrupt,timeout,spare};
	//����״̬,������,�����������жϣ�IO�豸�ж�,�豸�ж�  ȱҳ�ж� ʱ��Ƭ�ж�   ���У�������ȷ������ӵģ�
	public static enum lack{lackA,lackB,lackC};//���������豸����
//	public static enum need{needA,needB,needC};//��Ҫ�����豸
	public static int timeslice=5;//ʱ��ƬΪ5
	public static int page_number=3;//ÿ�����̵�����ҳ��Ϊ3
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
		pc=new int[32];//ÿ�������Ӧ��ָ��ĵ�ַ
		pc[0]=0;//��һ��ʼ�մ�begin��ʼ
		for(int i=1;i<32;i++)
		{
			if(i==1||i==2||i==3)
				pc[i]=4*i;//���˻�ҳÿҳ��ÿ��4��Ϊһ��ָ��
			else
				pc[i]=-1;//ָʾ��һҳ��ַ�����ô�ֵ���ж�ȱҳ�ж�
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
	public int getPc(int i) {//������һ��ָ��
		return pc[i];
	}
	public int[] getPc()//����ָ������
	{
		return pc;
	}
	public void setPc(int p,int num)//����P��һ��ָ��ĵ�ַ,num��ʾ�ڼ���ָ��
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
