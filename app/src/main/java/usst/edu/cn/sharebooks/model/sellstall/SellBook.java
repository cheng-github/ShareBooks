package usst.edu.cn.sharebooks.model.sellstall;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SellBook {
    @SerializedName("bookName")
    public String bookName;

    @SerializedName("author")
    public String author;

    @SerializedName("press")
    public String press;

    @SerializedName("version")
    public String version;

    @SerializedName("bookIntro")
    public String bookIntro;

    @SerializedName("imageUrl")
    public String imageUrl;

    @SerializedName("xueyuanCategory")
    public int xueyuanCategory;

    @SerializedName("bookState")
    public int bookState;

    @SerializedName("isJiaoCai")
    public int isJiaoCai;
}
