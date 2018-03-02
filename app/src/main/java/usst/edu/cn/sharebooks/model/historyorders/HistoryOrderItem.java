package usst.edu.cn.sharebooks.model.historyorders;

public class HistoryOrderItem {
    public boolean is_obtain;// 用这个值去判断对于查询的条件进行区别是获得书籍还是贡献书籍
    public int id;
    public String bookName;
    public String author;
    public String press;
    public String version;
    public String bookImageUrl;
    public int isJiaoCai;
    public int orderUserId;
    public int orderedUserId;
    public String orderDate;
    public String finishTime;
    public int orderType;
    public HtyUserInfo userInfo;
}
