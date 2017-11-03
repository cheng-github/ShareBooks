package usst.edu.cn.sharebooks.model.donate;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GivenBookItem implements Serializable {

    @SerializedName("bookIntro")
    public String bookIntro;

    @SerializedName("author")
    public String author;

    @SerializedName("imageUrl")
    public String imageUrl;

    @SerializedName("id")
    public int id;

    @SerializedName("wishes")
    public String wishes;

    @SerializedName("press")
    public String press;

    @SerializedName("bookName")
    public String bookName;

    @SerializedName("version")
    public String version;

    @SerializedName("isJiaoCai")
    public int isJiaoCai;

    @SerializedName("donateUser_Id")
    public int donateUser_Id;

    @SerializedName("nickName")
    public String nickName;

    @SerializedName("sex")
    public String sex;
}
