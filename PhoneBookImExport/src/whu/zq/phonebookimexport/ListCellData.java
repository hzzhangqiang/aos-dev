package whu.zq.phonebookimexport;

import java.io.File;


public class ListCellData {

	private int typeId = 0;
	private String colorStr = "#000000";	// 默认字体颜色为黑色
	private File file = null;
	
	public ListCellData(int typeId, String color, File file){
		this.setTypeId(typeId);
		this.setColorStr(color);
		this.setFile(file);
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getColorStr() {
		return colorStr;
	}

	public void setColorStr(String colorStr) {
		if (colorStr==null || colorStr.equals("")){			
		}else{
			this.colorStr = colorStr;
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
}
