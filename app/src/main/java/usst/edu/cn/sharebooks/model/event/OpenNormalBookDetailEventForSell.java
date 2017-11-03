package usst.edu.cn.sharebooks.model.event;


import android.graphics.Bitmap;
import android.widget.ImageView;

import usst.edu.cn.sharebooks.model.event.normalbookmodel.NormalBookData;

public class OpenNormalBookDetailEventForSell {
    public NormalBookData normalBookData;
    public Bitmap bitmap;
    public ImageView imageView;

    public OpenNormalBookDetailEventForSell() {
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public NormalBookData getNormalBookData() {
        return normalBookData;
    }

    public void setNormalBookData(NormalBookData normalBookData) {
        this.normalBookData = normalBookData;
    }
}
