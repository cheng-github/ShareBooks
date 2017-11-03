package usst.edu.cn.sharebooks.model.event;


public class DeleteSellBookEvent {
    public String bookName;

    public DeleteSellBookEvent(String bookName) {
        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }

}
