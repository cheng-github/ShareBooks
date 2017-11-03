package usst.edu.cn.sharebooks.model.event;


import usst.edu.cn.sharebooks.model.user.User;

public class UpdateUserInfoEvent {
    public User user;

    public UpdateUserInfoEvent() {
    }

    public UpdateUserInfoEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
