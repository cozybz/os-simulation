package pa;

import javax.swing.JOptionPane;

public class OsIo {//��������û�������ͽ�������
	private static OsFileManager fileManager = null; 
	
	
	public static void inPut(OsPcb pcb)
	{
		String temp = JOptionPane.showInputDialog("io����");
		
		while(temp == null || temp.isEmpty() || temp.length() != 1 || Integer.parseInt(temp) < -9 || Integer.parseInt(temp) > 9 )
		{
			temp = JOptionPane.showInputDialog("�������,io����");
		}
		
		pcb.setDr(Integer.parseInt(temp));
		System.out.println(Integer.parseInt(temp));
		System.out.println(pcb.getName()+pcb.getDr());
		OsPcbQueue.getOutBlock(pcb.getName());
		OsPcbQueue.putInReady(pcb.getName());
		
		
//		JOptionPane op=
		//�����б�����Ϣ���������
		//�ı�����IO�Ľ��̵���Ϣ
		
	}
	public static void output(OsPcb pcb)
	{
		//����д�ļ����IO��Ϣ д����Ϣ����  ��ִ���ļ���  ������
		fileManager.saveLog(pcb.getPath(),pcb.getDr()+"");
		
	}
	
	public static OsFileManager getFileManager() {
		return fileManager;
	}
	public static void setFileManager(OsFileManager fileManager) {
		OsIo.fileManager = fileManager;
	}

}
