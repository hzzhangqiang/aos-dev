package whu.zq.encrypt;

/**
 * Created by Kubbi on 2015/11/1.
 */
public class CodeDataStruct {
    private String title="";
    private String password="";

    public CodeDataStruct(String title, String password){
        this.title = title;
        this.password = password;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
