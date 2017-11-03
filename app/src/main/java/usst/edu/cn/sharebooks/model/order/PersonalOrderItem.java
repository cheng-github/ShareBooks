package usst.edu.cn.sharebooks.model.order;


import java.io.Serializable;

public class PersonalOrderItem implements Serializable{

    public String isOreder; //表示是否是预定他人的书籍  可选值为:已预订 被预定

    public String bookName;

    public String author;

    public String press;

    public String bookIntro;

    public String imagerUrl;

    public int isJiaoCai;

    public String orderDate;

    public int orderedUserId;

    public int orderType;

    public String version;

    public int get_count;

    public int contri_count;

    public String nickName;

    public String sex;

    public String sellStallDes;

    public String xueYuan;

    public String contactWay;

    public String touXiangImageUrl;

    public int orderUserId;
}
