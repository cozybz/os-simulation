package pa;
/*
 * ���ڲ���Pcb�������ڴ�
 */
import java.io.BufferedReader;
import java.io.FileReader;

public class OsCourse {

	public static int create(String name,byte order[][],String path)//���̵Ĵ���,0ʧ��1�ɹ�
	{
		int begin;
		if(OsPcbQueue.remain_cou==0)
		{
			return 0;//����������,�޿հ׽��̿��ƿ�
		}
		else//���п��н��̿��ƿ����
		{
			int temp;//������Ŀ��ƿ��ھ������е����
			temp=OsPcbQueue.getFree();//�õ��հ׿��ƿ�,��Ϊǰ���Ѿ��ж����������Ƿ���λ��
			OsPcbQueue.course_q[temp].pcb=new OsPcb(name,-1);//Ϊ��������apply�л��ָ���ַ��ǰ�������
			if((begin=OsMemory.apply(temp,OsPcb.page_number))==-1)//�����ڴ�ռ�ʧ��
			{
				return 0;
			}
			else
			{
				OsPcbQueue.ini_pcb(temp,begin,path);//��ʼ�����̿��ƿ�
				OsPcbQueue.putInReady(temp);//���½����̷����������
				OsMemory.assign(temp,order,begin);//�õ��ڴ�ռ�	�����ʼָ�		
			}
		}

		return 1;//������̳ɹ�
	}
	public static void destroy(String name)//���̳���
	{
		for(int i=0;i<10;i++)
		{
			if(OsPcbQueue.pcbName(i)==name)//�ҵ�Ҫ�������Ľ����ڿ��ƿ���е�λ��
			{
				if(OsPcbQueue.course_q[i].pcb.getPsw()==OsPcb.state.running)//����ý�����������
				{
					OsPcbQueue.cpuToPcb(i);//��������ִ�еĽ��̵�������Ϣ ʹ�䲻ִ��
				}
				else
					OsPcbQueue.course_q[i].pcb.setPsw(OsPcb.state.ENDinterrupt);//���Ϊ�����н���ֻ����psw
				OsMemory.callback(OsPcbQueue.course_q[i].pcb.getBegin(), OsPcbQueue.course_q[i].pcb.getPc());//�ڴ��ջ�
				OsPcbQueue.setFree(i);//�黹�հ׽��̿��ƿ�
				OsIo.output(OsPcbQueue.course_q[i].pcb);//������д��out�ļ�
				break;
			}
		}

	}
	public static void block(String name)//��������
	{
		for(int i=0;i<10;i++)
		{
			if(OsPcbQueue.pcbName(i)==name)//�ҵ�Ҫ�������Ľ����ڿ��ƿ���е�λ��
			{
				OsPcbQueue.cpuToPcb(i);//��������ִ�еĽ��̵�������Ϣ ʹ�䲻ִ��
				OsPcbQueue.putInblock(i);//���ý��̷�����������
				break;
			}
		}
	}
	public static void wake(String name)//���̻���,���豸���
	{
		for(int i=0;i<10;i++)
		{
			if(OsPcbQueue.pcbName(i)==name)//�ҵ�Ҫ���ѵĽ����ڿ��ƿ���е�λ��
			{

				OsPcbQueue.getOutBlock(i);//���������Ƴ�
				OsPcbQueue.putInReady(i);//���������̷Ž���������ĩβ
				break;
			}
		}
	}

}
