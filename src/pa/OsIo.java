package pa;

import javax.swing.JOptionPane;

public class OsIo {//用于完成用户的输入和结果的输出
	private static OsFileManager fileManager = null; 
	
	
	public static void inPut(OsPcb pcb)
	{
		String temp = JOptionPane.showInputDialog("io输入");
		
		while(temp == null || temp.isEmpty() || temp.length() != 1 || Integer.parseInt(temp) < -9 || Integer.parseInt(temp) > 9 )
		{
			temp = JOptionPane.showInputDialog("输入错误,io输入");
		}
		
		pcb.setDr(Integer.parseInt(temp));
		System.out.println(Integer.parseInt(temp));
		System.out.println(pcb.getName()+pcb.getDr());
		OsPcbQueue.getOutBlock(pcb.getName());
		OsPcbQueue.putInReady(pcb.getName());
		
		
//		JOptionPane op=
		//并具有报错信息，输入错误
		//改变申请IO的进程的信息
		
	}
	public static void output(OsPcb pcb)
	{
		//调用写文件输出IO信息 写出信息包括  可执行文件名  运算结果
		fileManager.saveLog(pcb.getPath(),pcb.getDr()+"");
		
	}
	
	public static OsFileManager getFileManager() {
		return fileManager;
	}
	public static void setFileManager(OsFileManager fileManager) {
		OsIo.fileManager = fileManager;
	}

}
