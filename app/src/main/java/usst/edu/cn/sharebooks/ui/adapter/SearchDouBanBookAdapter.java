package usst.edu.cn.sharebooks.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.component.GlideApp;
import usst.edu.cn.sharebooks.model.douban.DouBanBook;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.ApiInterface;
import usst.edu.cn.sharebooks.ui.activity.SearchBookDetailActivity;


public class SearchDouBanBookAdapter extends RecyclerView.Adapter {
    private Context context;
    private User mUser;
    private BaseActivity mBaseActivity;
    private List<DouBanBook> list = new ArrayList<>();
    private int ADD_TYPE;

    public SearchDouBanBookAdapter(BaseActivity baseActivity,User user){
        this.mBaseActivity = baseActivity;
        this.mUser = user;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SearchDouBanBookViewHolder(LayoutInflater.from(context).inflate(R.layout.searchbookitem_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SearchDouBanBookViewHolder)holder).bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list != null?list.size():0;
    }

    public void setAddType(int type){
        ADD_TYPE = type;
    }

    public class  SearchDouBanBookViewHolder extends RecyclerView.ViewHolder{
        private ImageView mBookImage;
        private TextView mBookName;
        private TextView mAuthor;
        private TextView mPressVerison;
        private TextView mBrifeContent;
//        private SimpleTarget target = new SimpleTarget() {
//            @Override
//            public void onResourceReady(Object resource, Transition transition) {
//                mBookImage.setImageBitmap((Bitmap) resource);
//            }
//        };

        public SearchDouBanBookViewHolder(View itemView) {
            super(itemView);
            mBookImage = (ImageView) itemView.findViewById(R.id.iv_bookimage);
            mBookName = (TextView)itemView.findViewById(R.id.tv_bookName);
            mAuthor = (TextView)itemView.findViewById(R.id.tv_author);
            mPressVerison = (TextView)itemView.findViewById(R.id.tv_pressversion);
            mBrifeContent = (TextView)itemView.findViewById(R.id.tv_bookIntro);
        }

        private void bind(final DouBanBook douBanBook){
            String versionPress = douBanBook.press+"/"+douBanBook.version;
            String imageUrl = douBanBook.imageUrl;
            StringBuilder allAuthorName = new StringBuilder();
            for (String author:douBanBook.author){
            //    Log.i("TestDouBanData",author);
                allAuthorName.append(author+"/");
            }
           // Log.i("TestDouBanData","调用了外部并且length="+douBanBook.author.length);
            if (allAuthorName.length() != 0)
            allAuthorName.deleteCharAt(allAuthorName.length()-1);
            //这里居然会出现空指针  说index = -1 !?
            //调试之后居然发现  还有  author为0的情况....
            //大于等于1代表有翻译的人
            if (douBanBook.translator.length == 1){
                allAuthorName.append("   "+douBanBook.translator[0]+" 译");
            }else if (douBanBook.translator.length >1){
                allAuthorName.append("   ");
                for (String trans:douBanBook.translator){
                    allAuthorName.append(trans+"/");
                }
                allAuthorName.deleteCharAt(allAuthorName.length()-1);
                allAuthorName.append(" 译");
            }
            //上面的写法有问题  修改之后应该没问题了 将 这里图片转换为bitmap
         //    Glide.with(context).load(imageUrl).into(mBookImage); 经过测试这样加载的图片会很占内存
//             RequestBuilder requestBuilder = Glide.with(context).load(imageUrl);
//             requestBuilder.into(mBookImage);
             GlideApp.with(context)
                     .asBitmap()
                     .load(imageUrl)
                     .override(200,200)
                     .into(new SimpleTarget<Bitmap>() {
                         @Override
                         public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                             mBookImage.setImageBitmap(resource);
                             //终于得到这个bitmap了 打印一下这个大小  不知道为什么这样会占比较小的内存
                             Log.i("TestNormal","resource.getRowBytes()"+resource.getRowBytes()+"");
                             Log.i("TestNormal","bitmap.getByteCount() kb"+resource.getByteCount()/1024);
                         }
                     });
            mBookName.setText(douBanBook.bookName);
            mAuthor.setText(allAuthorName.toString());
            mPressVerison.setText(versionPress);
            mBrifeContent.setText(douBanBook.bookIntro);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mBaseActivity,SearchBookDetailActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("User",mUser);
                    args.putSerializable("DouBanInfo",douBanBook);
                    args.putInt("BookType",2); //
                    args.putInt("AddType",ADD_TYPE);
                    //将图片放在args里传到 SearchBookDetailActivity
                    Bitmap bitmap = null;
                    BitmapDrawable bitmapDrawable =((BitmapDrawable) mBookImage.getDrawable());
                    if (bitmapDrawable != null){
                        bitmap =  bitmapDrawable.getBitmap();
                    }
                    args.putParcelable("book_image",bitmap);
                    Log.i("TestNormal","bitmap.getRowBytes()"+bitmap.getRowBytes()+"");
                    Log.i("TestNormal","bitmap.getByteCount() kb"+bitmap.getByteCount()/1024);
               //     args.putString("ImageUrl",douBanBook.imageUrl);
                   intent.putExtras(args);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                .makeSceneTransitionAnimation(mBaseActivity,mBookImage,"book_image");
                        mBaseActivity.startActivity(intent,optionsCompat.toBundle());
                    }else {
                        mBaseActivity.startActivity(intent);
                    }
                }
            });
        }
    }

    public void setList(List<DouBanBook> list){
        this.list = list;
    }
}
