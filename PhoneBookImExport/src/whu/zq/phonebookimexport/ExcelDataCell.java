package whu.zq.phonebookimexport;

public class ExcelDataCell {
		
	private int row;
	private int col;
	private String content;
		
	public int getCol() {
		return col;
	}
	public String getContent() {
		return content;
	}
	public int getRow() {
		return row;
	}
	
	public void setCol(int col) {
		this.col = col;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setRow(int row) {
		this.row = row;
	}
}
