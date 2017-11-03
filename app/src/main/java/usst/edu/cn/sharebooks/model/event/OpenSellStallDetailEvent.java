package usst.edu.cn.sharebooks.model.event;



public class OpenSellStallDetailEvent {
    public int userId;

    public OpenSellStallDetailEvent() {
    }

    public OpenSellStallDetailEvent(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
