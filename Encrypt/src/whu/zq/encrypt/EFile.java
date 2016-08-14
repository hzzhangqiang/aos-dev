package whu.zq.encrypt;

import java.io.File;

public class EFile {
	
	private File file=null;
	
	public EFile(File  f) {
		file = f;
	}
	
	public File getFile() {
		return file;
	}
	
	@Override
	public String toString() {
		return getFile()!=null?getFile().getName():"";
	}
}
