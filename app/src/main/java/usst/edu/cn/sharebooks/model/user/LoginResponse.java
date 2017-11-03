package usst.edu.cn.sharebooks.model.user;


import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    //0 表示登录失败  1表示登录成功
    @SerializedName("statusCode")
    public int status;
    @SerializedName("user")
    public User user;
}
