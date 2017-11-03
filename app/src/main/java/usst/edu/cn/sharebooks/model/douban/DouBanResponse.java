package usst.edu.cn.sharebooks.model.douban;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DouBanResponse {
    public int count = 10;

    @SerializedName("books")
    public List<DouBanBook> books = new ArrayList<>();
}
