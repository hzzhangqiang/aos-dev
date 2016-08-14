package whu.zq.phonebookimexport;

import java.io.File;
import java.util.Comparator;

public class FileSortComparatorUp implements Comparator<File>{

	@Override
	public int compare(File lhs, File rhs) {
		if (lhs==null || rhs==null){
			if (lhs==null){
				return -1;
			}else{
				return 1;
			}
		}else{
			if (lhs.isDirectory() == true && rhs.isDirectory() == true){		//�ȱȽ�Ŀ¼
				return lhs.getName().compareToIgnoreCase(rhs.getName());
			}else{
				if (lhs.isDirectory() && !rhs.isDirectory()){
					return -1;
				}else if(!lhs.isDirectory() && rhs.isDirectory()){
					return 1;
				}else{
					return lhs.getName().compareToIgnoreCase(rhs.getName());	// ���Ƚ��ļ�
				}
			}
		}
	}
	
}