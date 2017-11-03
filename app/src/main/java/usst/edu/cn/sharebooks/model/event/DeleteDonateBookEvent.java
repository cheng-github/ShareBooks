package usst.edu.cn.sharebooks.model.event;



public class DeleteDonateBookEvent {
    public String bookName;
    public int userId;

    public DeleteDonateBookEvent() {
        super();
    }

    public DeleteDonateBookEvent(String bookName,int userId) {
        this.bookName = bookName;
        this.userId = userId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
