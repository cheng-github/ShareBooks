package usst.edu.cn.sharebooks.util;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public class RxUtil {
    private static <T> ObservableTransformer<T, T> schedulerTransformer(final Scheduler scheduler) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread(), true);
            }
        };
    }

    public  static <T> ObservableTransformer<T,T> io(){
        return schedulerTransformer(Schedulers.io());
    }
}
