package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Tools {
	public static byte[] StringToBytes(String str)
	{
		
		return str.getBytes();
	}
	
	public static byte[] IntToBytes(int interger)
	{	
		byte[] result=null;
		try {
		ByteArrayOutputStream boutput = new ByteArrayOutputStream();
		DataOutputStream doutput = new DataOutputStream(boutput);
		doutput.writeInt(interger);
		result = boutput.toByteArray();
		doutput.close();
		boutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String BytesToString(byte[] b)
	{
		return new String(b);
	}

	public static int Bytes4ToInt(byte[] b)
	{
		int result=0;
		try {
		ByteArrayInputStream bintput = new ByteArrayInputStream(b);
		DataInputStream dintput = new DataInputStream(bintput);
		result = dintput.readInt();
		dintput.close();
		bintput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static int Bytes2ToInt(byte[] b)
	{
		byte[] bytes2buffer = b;
		byte[] bytes4buffer = new byte[4];
		for(int i=0;i<2;i++)
			bytes4buffer[i+2]=bytes2buffer[i];
		return Bytes4ToInt(bytes4buffer);
	}
	public static byte[] stringTo6Bytes(String str)
	{
		byte[] s = str.getBytes();
		byte[] b =new byte[6];
		for(int i=0;i<6;i++)
			if(i<s.length)
				b[i]=s[i];
		return b;
	}
	public static byte[] stringTo3Bytes(String str)
	{
		byte[] s = str.getBytes();
		byte[] b =new byte[3];
		for(int i=0;i<3;i++)
			if(i<s.length)
				b[i]=s[i];
		return b;
	}
	public static boolean isSame6Bytes(byte[] a,byte[] b)
	{
		byte[] a2 = new byte[6];
		byte[] b2 = new byte[6];
		int i=0;
		int j=0;
		while(i<6 && i<a.length)
		{
			
			a2[i]=a[i];
			i++;
		}
		
		while(j<6 && j<b.length)
		{
			
			b2[j]=b[j];
			j++;
		}
		
		
		for(int v=0;v<6;v++)
			if(a2[v]!=b2[v])
				return false;
		
				
		return true;
	}
	public static boolean isSame3Bytes(byte[] a,byte[] b)
	{
		
		byte[] a2 = new byte[3];
		byte[] b2 = new byte[3];
		int i=0;
		int j=0;
		while(i<3 && i<a.length)
		{
			
			a2[i]=a[i];
			i++;
		}
		
		while(j<3 && j<b.length)
		{
			
			b2[j]=b[j];
			j++;
		}
		
		for(int v=0;v<3;v++)
			if(a2[v]!=b2[v])
				return false;
		
				
		return true;
		
	}
}
