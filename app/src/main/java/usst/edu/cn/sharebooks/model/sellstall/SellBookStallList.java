package usst.edu.cn.sharebooks.model.sellstall;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SellBookStallList {

    @SerializedName("bookStall")
    public List<SellBook> sellBookList = new ArrayList<>();

}
