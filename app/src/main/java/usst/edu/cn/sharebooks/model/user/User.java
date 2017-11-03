package usst.edu.cn.sharebooks.model.user;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class User extends DataSupport implements Serializable{
    @SerializedName("id")
    private int userId;//之前我这里是大写的i所以收不到
    private String userName;
    private String passWord;
    private int contri_count;
    private int get_count;
    private String nickName;
    private String sex;
    private String sellStallDescri;
    private String xueYuan;
    private String contactWay;
    private String imageUrl;

    public User() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContactWay() {
        return contactWay;
    }

    public void setContactWay(String contactWay) {
        this.contactWay = contactWay;
    }

    public String getXueYuan() {
        return xueYuan;
    }

    public void setXueYuan(String xueYuan) {
        this.xueYuan = xueYuan;
    }

    public String getSellStallDescri() {
        return sellStallDescri;
    }

    public void setSellStallDescri(String sellStallDescri) {
        this.sellStallDescri = sellStallDescri;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getId() {
        return userId;
    }

    public void setId(int id) {
        userId = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public int getContri_count() {
        return contri_count;
    }

    public void setContri_count(int contri_count) {
        this.contri_count = contri_count;
    }

    public int getGet_count() {
        return get_count;
    }

    public void setGet_count(int get_count) {
        this.get_count = get_count;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
