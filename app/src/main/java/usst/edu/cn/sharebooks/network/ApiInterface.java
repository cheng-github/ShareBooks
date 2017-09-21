package usst.edu.cn.sharebooks.network;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import usst.edu.cn.sharebooks.model.AllAvailableBook;

public interface ApiInterface {
    String HOST = "http://192.168.1.103:8080/ShareBookProject_1.0/";
    //这里这个Host不能写localhost,而必须写电脑的Ipv4的地址
    //这样  我们终于访问上server了

    @GET("GivenBookServlet")
    Observable<AllAvailableBook> mGivenBookApi(@Query("bookName") String bookName, @Query("author") String author);

}
