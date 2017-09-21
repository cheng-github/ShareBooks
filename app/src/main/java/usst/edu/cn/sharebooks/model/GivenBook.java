package usst.edu.cn.sharebooks.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GivenBook implements Serializable {

    @SerializedName("bookIntro")
    public String bookIntro;

    @SerializedName("author")
    public String author;

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


//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getBookName() {
//        return bookName;
//    }
//
//    public void setBookName(String bookName) {
//        this.bookName = bookName;
//    }
//
//    public String getAuthor() {
//        return author;
//    }
//
//    public void setAuthor(String author) {
//        this.author = author;
//    }
//
//    public String getPress() {
//        return press;
//    }
//
//    public void setPress(String press) {
//        this.press = press;
//    }
//
//    public String getVersion() {
//        return version;
//    }
//
//    public void setVersion(String version) {
//        this.version = version;
//    }
//
//    public String getBookIntro() {
//        return bookIntro;
//    }
//
//    public void setBookIntro(String bookIntro) {
//        this.bookIntro = bookIntro;
//    }
//
//    public String getWishes() {
//        return wishes;
//    }
//
//    public void setWishes(String wishes) {
//        this.wishes = wishes;
//    }
}
