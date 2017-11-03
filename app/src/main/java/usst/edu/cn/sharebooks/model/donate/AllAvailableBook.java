package usst.edu.cn.sharebooks.model.donate;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AllAvailableBook {

    @SerializedName("shareBook")
    @Expose
    public List<GivenBookItem> shareBook = new ArrayList<>();//注意这里需要给这个分配内存的
}
