package whu.zq.phonebookimexport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyFileFilter implements FileFilter{
	
	private String fileSuffix = "";	
	private final static String strSecureFile = ".android_secure";
	
	public MyFileFilter(String fileSuffix){
		setFileSuffix(fileSuffix);
	}
	
	@Override
	public boolean accept(File pathname) {
		// 过滤系统安全目录
		String strPathName = pathname.getName();
		//安全路径
		if (strPathName.equals(strSecureFile)){
			return false;
		}
		//起始包含"."和"com."的路径
		if (strPathName.startsWith(".") || strPathName.startsWith("com.")){
			return false;
		}
		
		if (pathname.isDirectory()){
			return true;
		}else if (pathname.getName().endsWith(fileSuffix)){
			return true;
		}
			
		return false;
	}
	
	public String getFileSuffix() {
		return fileSuffix;
	}
	
	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}
	
	private boolean isFileNeedRoot(File file){
		try {
			Process process = Runtime.getRuntime().exec("ls -l"+file.getName());
			InputStream iStream = process.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(iStream));
			System.out.println(buffer.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
