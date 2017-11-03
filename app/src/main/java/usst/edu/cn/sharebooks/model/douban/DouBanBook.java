package usst.edu.cn.sharebooks.model.douban;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DouBanBook implements Serializable{

    public Rating rating;   //书籍评分

    @SerializedName("author")
    public String[] author; // 作者名称  如果装对象的数组使用 List  那么字符串数组应该使用String[] 我猜的

    @SerializedName("pubdate")
    public String version; //出版日期

    @SerializedName("publisher")
    public String press; //出版社

    @SerializedName("image")
    public String imageUrl;//图片地址

    @SerializedName("title")
    public String bookName;    //图书名称

    public String author_intro;   //作者介绍

    @SerializedName("summary")
    public String bookIntro; //书籍简介

    public String price; //书籍价格

    public String[] translator;//如果是外国的书 应该会有翻译的人的名字
}
