package pa;

public class OsSloveInt {//中断处理程序
	public static void sloveInt(OsPcb.state kind,OsPcb run)
	{
		if(kind==OsPcb.state.ENDinterrupt)//软中断
		{
			OsEquipment.freeEq(run);//进程结束 如果仍占用进程则释放
			OsCourse.destroy(run.getName());//撤销进程
			OsMemory.dispatcher();//进程调度
		}
		else if(kind==OsPcb.state.IOinterrupt)//IO中断
		{
			OsCourse.block(run.getName());//将该进程阻塞
			OsIo.inPut(run);//等待用户输入
			OsMemory.dispatcher();//进程调度
		}
		else if(kind==OsPcb.state.Equinterrupt)//设备中断
		{
//			System.out.println("阻塞");
			OsCourse.block(run.getName());//将该进程阻塞
//			System.out.println("申请");
			OsEquipment.apply_eq(run);//申请设备
//			System.out.println("调度");
			OsMemory.dispatcher();//进程调度
		}
	}

}
