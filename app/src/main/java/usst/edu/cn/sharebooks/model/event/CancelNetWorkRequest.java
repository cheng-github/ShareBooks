package usst.edu.cn.sharebooks.model.event;


import io.reactivex.disposables.Disposable;

public class CancelNetWorkRequest {
    private Disposable disposable;

    public Disposable getDisposable() {
        return disposable;
    }

    public void setDisposable(Disposable disposable) {
        this.disposable = disposable;
    }

    public CancelNetWorkRequest(Disposable disposable) {
        this.disposable = disposable;
    }

    public CancelNetWorkRequest(){

    }
}
