package pa;

public class OsSloveInt {//�жϴ������
	public static void sloveInt(OsPcb.state kind,OsPcb run)
	{
		if(kind==OsPcb.state.ENDinterrupt)//���ж�
		{
			OsEquipment.freeEq(run);//���̽��� �����ռ�ý������ͷ�
			OsCourse.destroy(run.getName());//��������
			OsMemory.dispatcher();//���̵���
		}
		else if(kind==OsPcb.state.IOinterrupt)//IO�ж�
		{
			OsCourse.block(run.getName());//���ý�������
			OsIo.inPut(run);//�ȴ��û�����
			OsMemory.dispatcher();//���̵���
		}
		else if(kind==OsPcb.state.Equinterrupt)//�豸�ж�
		{
//			System.out.println("����");
			OsCourse.block(run.getName());//���ý�������
//			System.out.println("����");
			OsEquipment.apply_eq(run);//�����豸
//			System.out.println("����");
			OsMemory.dispatcher();//���̵���
		}
	}

}
