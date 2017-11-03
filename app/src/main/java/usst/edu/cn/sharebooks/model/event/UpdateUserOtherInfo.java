package usst.edu.cn.sharebooks.model.event;



public class UpdateUserOtherInfo {
    //to wirte ..... all information excepte image
    public String newStr;
    public int UpdateWay;

    //更新nickname
    public UpdateUserOtherInfo(int updateWay,String newStr) {
        UpdateWay = updateWay;
        this.newStr = newStr;
    }

    public int getUpdateWay() {
        return UpdateWay;
    }

    public void setUpdateWay(int updateWay) {
        UpdateWay = updateWay;
    }

    public String getNewStr() {
        return newStr;
    }

    public void setNewStr(String newStr) {
        this.newStr = newStr;
    }
}
