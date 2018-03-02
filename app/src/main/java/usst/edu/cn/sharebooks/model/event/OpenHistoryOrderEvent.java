package usst.edu.cn.sharebooks.model.event;


import usst.edu.cn.sharebooks.model.historyorders.HtyUserInfo;

public class OpenHistoryOrderEvent {
    private HtyUserInfo userInfo;

    public HtyUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(HtyUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public OpenHistoryOrderEvent(HtyUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public OpenHistoryOrderEvent() {
    }
}
