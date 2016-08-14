package whu.zq.phonebookimexport;


public class XslContactData {
	String name = "";
	String mobilePhoneNum = "";
	String email = "";
	String other = "";
	
	public void clear(){
		name = "";
		mobilePhoneNum = "";
		email = "";
		other = "";
	}
	
	public void setDataById(int id,String content){
		switch (id) {
		case 0:
			name = content;
			break;
		case 1:
			mobilePhoneNum = content;
			break;
		case 2:
			email = content;
			break;
		default:
			other = content;
			break;
		}
	}
	
	private boolean isNameEmpty(){
		return name.isEmpty();
	}
	
	private boolean isPhoneEmpty(){
		return mobilePhoneNum.isEmpty();
	}
	
	private boolean isEmailEmpty(){
		return email.isEmpty();
	}
	
	public boolean isEmpty(){
		return isNameEmpty() && isPhoneEmpty() && isEmailEmpty();
	}
	

}
