package usst.edu.cn.sharebooks.component;


import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {
    private final Subject<Object> mBus;
    private static RxBus sInstance;

    private RxBus(){
        mBus = PublishSubject.create();
    }

    public static synchronized RxBus getInstance(){ //最好使用同步的方法
        if (sInstance == null){
            sInstance = new RxBus();
        }
        return sInstance;
    }

    public void post(Object o){
        mBus.onNext(o);
    }

    public <T>Observable<T> tObservable(Class<T> eventType){
        return mBus.ofType(eventType);
    }
}
