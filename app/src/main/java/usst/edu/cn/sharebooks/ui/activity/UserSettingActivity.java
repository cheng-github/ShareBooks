package usst.edu.cn.sharebooks.ui.activity;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import usst.edu.cn.sharebooks.BuildConfig;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.component.GlideApp;
import usst.edu.cn.sharebooks.component.RxBus;
import usst.edu.cn.sharebooks.model.event.UpdateUserInfoEvent;
import usst.edu.cn.sharebooks.model.event.UpdateUserOtherInfo;
import usst.edu.cn.sharebooks.model.user.UpdateUserInfoResponse;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.ApiInterface;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;
import usst.edu.cn.sharebooks.util.DialogUtil;
import usst.edu.cn.sharebooks.util.FileUtil;
import usst.edu.cn.sharebooks.util.ImageCompressUtil;
import usst.edu.cn.sharebooks.util.RxUtil;

public class UserSettingActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
    public final static int ACTIVITY_RESULT_CAMERA = 1;//选择 拍照 的返回码
    public final static int ACTIVITY_RESULT_ALBUM = 2;//选择 相册 的返回码
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    public final static int RC_CAMERA_AND_WRITE = 0x11;

    private User mUser;
    private RelativeLayout mNickNameSetting;
    private RelativeLayout mSexSetting;
    private RelativeLayout mStallDesSetting;
    private RelativeLayout mXueyuanSetting;
    private RelativeLayout mContactSetting;
    private RelativeLayout mTouxiangSetting;
    private TextView mNickName;
    private TextView mSex;
    private TextView mXueyuan;
    private TextView mId;
    private Toolbar mToolBar;
    private ImageView mTouxiang;
    public final String CROPPED_IMAGE_NAME = "touXiang";
    private MaterialDialog mMaterialDialog;
    private AlertDialog mAlertDialog;
    private TextView mContactStr;
    public Uri cameraUri;
    File picFile;
    private UpdateUserInfoResponse updateUserInfoResponse;

    @Override
    protected void onStart() {
        super.onStart();
//        RxBus.getInstance().tObservable(UpdateUserOtherInfo.class)
//                .doOnNext(new Consumer<UpdateUserOtherInfo>() {
//                    @Override
//                    public void accept(@io.reactivex.annotations.NonNull UpdateUserOtherInfo updateUserOtherInfo) throws Exception {
//                        if (updateUserOtherInfo.UpdateWay == 2 && updateUserOtherInfo.getNewStr().length()>12){ //2代表修改昵称
//                            Toast.makeText(UserSettingActivity.this,"昵称长度不能超过12位",Toast.LENGTH_SHORT).show();
//                        }else{
//                            uploadOtherInfo(updateUserOtherInfo);
//                        }
//                    }
//                })
//                .compose(RxUtil.<UpdateUserOtherInfo>io())
//                .compose(bindUntilEvent(ActivityEvent.DESTROY))
//                .subscribe();
        //rxbus需要放在onCreate()里面
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        RxBus.getInstance().tObservable(UpdateUserOtherInfo.class)
                .doOnNext(new Consumer<UpdateUserOtherInfo>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull UpdateUserOtherInfo updateUserOtherInfo) throws Exception {
                        if (updateUserOtherInfo.UpdateWay == 2 && updateUserOtherInfo.getNewStr().length()>12){ //2代表修改昵称
                            Toast.makeText(UserSettingActivity.this,"昵称长度不能超过12位",Toast.LENGTH_SHORT).show();
                        }else{
                            uploadOtherInfo(updateUserOtherInfo);
                        }
                    }
                })
                .compose(RxUtil.<UpdateUserOtherInfo>io())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        initDataFromLastActivity();
        initView();
    }

    private void initDataFromLastActivity(){
        mUser = (User) getIntent().getSerializableExtra("User");
    }

    private void initView(){
        setupToolBar();
        mNickName = (TextView)findViewById(R.id.tv_nickName);
        mNickName.setText(mUser.getNickName());
        mId = (TextView)findViewById(R.id.tv_userId);
        mId.setText(mUser.getUserName());//是UserName不是id
        mContactStr = (TextView)findViewById(R.id.tv_contactway);
        mContactStr.setText(mUser.getContactWay());
        mSex = (TextView)findViewById(R.id.tv_sex);
        if (mUser.getSex().equals("?")){
            mSex.setText("未设置");
        }else if (mUser.getSex().equals("男")){
            mSex.setText("男");
        }else if (mUser.getSex().equals("女")){
            mSex.setText("女");
        }
        mXueyuan = (TextView)findViewById(R.id.tv_xueyuan);
        if (mUser.getXueYuan().equals("?")){
            mXueyuan.setText("未设置");
        }else {
            mXueyuan.setText(mUser.getXueYuan());
        }
        mTouxiangSetting = (RelativeLayout)findViewById(R.id.rl_touxiang);
        mTouxiangSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingTouxiangDialog();
            }
        });
        mTouxiang = (ImageView)findViewById(R.id.tv_touxiang);
        if (mUser.getImageUrl().equals("?")){
            Glide.with(this).load(R.drawable.touxiang).into(mTouxiang);
        }else {
            String imageUrl = ApiInterface.UserImageUrl+mUser.getImageUrl();
            GlideApp.with(this).load(imageUrl).into(mTouxiang);//记得设置不缓存
        }
        mNickNameSetting = (RelativeLayout)findViewById(R.id.rl_nickname);
        mNickNameSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showUpdateUserInfoDialog(UserSettingActivity.this,"昵称");
            }
        });
        mSexSetting = (RelativeLayout)findViewById(R.id.rl_sex);
        mSexSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettringSexPopupWindow();
            }
        });
        mXueyuanSetting = (RelativeLayout)findViewById(R.id.rl_xueyuancate);
        mXueyuanSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showUpdateUserInfoDialog(UserSettingActivity.this,"学院");
            }
        });
        mContactSetting = (RelativeLayout)findViewById(R.id.rl_contactway);
        mContactSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showUpdateUserInfoDialog(UserSettingActivity.this,"联系");
            }
        });
        mStallDesSetting = (RelativeLayout)findViewById(R.id.rl_stalldes);
        mStallDesSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to finish a dialog to  add some information a little much
                DialogUtil.showEditStallDesDialog(UserSettingActivity.this,mUser.getSellStallDescri());
            }
        });
    }

    private void setupToolBar(){
        mToolBar = (Toolbar)findViewById(R.id.tb_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//设置自定义标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolBar.setNavigationIcon(R.drawable.arrow_back_userinfo);//设置cancel图标
    }

    private void showSettingTouxiangDialog(){
        mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setMessage("选择头像")
                .setPositiveButton("相册", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        pickFromGallery();
                    }
                })
                .setNegativeButton("拍照", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        methodRequiresOnePermission();
                        //不可以直接调用相机
                   //     byCamera();
                    }
                })
                .setCanceledOnTouchOutside(true)
                .show();
    }

    public void pickFromGallery(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermission
                    (Manifest.permission.READ_EXTERNAL_STORAGE,
                            getString(R.string.permission_read_storage_rationale),
                            REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        }else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)),ACTIVITY_RESULT_ALBUM);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //得到权限  调用相机
        byCamera();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //没有权限啥也不做
    }

    /**
     * 请求write external storager 权限
     */
    @AfterPermissionGranted(RC_CAMERA_AND_WRITE)
    private void methodRequiresOnePermission(){
        String[] perms = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this,perms)){
            // Already have permission, do the thing
            // ...
            byCamera();
        }else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.camera_and_writedoc),
                    RC_CAMERA_AND_WRITE, perms);
        }
    }

    //调用这段代码前应该先确认有写入文件的权限  真是尼玛的坑
    public void byCamera(){
        try {
            File uploadFileDir = new File(Environment.getExternalStorageDirectory(),"icon");
            if (!uploadFileDir.exists()){
                uploadFileDir.mkdirs();
            }
            // 创建图片，以当前系统时间命名
            picFile = new File(uploadFileDir, SystemClock.currentThreadTimeMillis()+".jpg");
            if (!picFile.exists()){
               picFile.createNewFile();
            }
        //    cameraUri = Uri.fromFile(picFile);
            cameraUri = FileProvider.getUriForFile(UserSettingActivity.this, BuildConfig.APPLICATION_ID+".provider",picFile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,cameraUri);
            startActivityForResult(cameraIntent,ACTIVITY_RESULT_CAMERA);
            Log.i("TestCamera","-->tempUri : " + cameraUri.toString()
                    + "-->path:" + cameraUri.getPath());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void showSettringSexPopupWindow(){
        View parent =((ViewGroup)this.findViewById(android.R.id.content)).getChildAt(0); //原来还可以这么写...
        View popView = View.inflate(this,R.layout.settingsexpopup_layout,null);
        Button female = (Button)popView.findViewById(R.id.bt_female);
        Button male = (Button)popView.findViewById(R.id.bt_male);
        Button cancel = (Button)popView.findViewById(R.id.bt_cancel);
        final PopupWindow popWindow = new PopupWindow(popView, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
        popWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popWindow.setAnimationStyle(R.style.AnimBottom);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);// 设置允许在外点击消失
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadOtherInfo(new UpdateUserOtherInfo(3,"女"));
                popWindow.dismiss();
            }
        });
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadOtherInfo(new UpdateUserOtherInfo(3,"男"));
                popWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
      // ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ACTIVITY_RESULT_CAMERA:
                try {
                    if (resultCode == RESULT_OK ) {
                        if (cameraUri != null){
                            //应该先对文件进行压缩
                            cameraUri = ImageCompressUtil.getSmallBitmap(this,picFile,200,200); //压缩图片 不然图片太大的话  会出现无法显示的问题
                            //而且服务器接受到的文件 显示0字节....
                            //应该是在应用里不允许保存这么大的文件吧...
                            startCropActivity(cameraUri);//使用第三方库裁剪图片
                        }
                    }
                    else {
                        // 因为在无任何操作返回时，系统依然会创建一个文件，这里就是删除那个产生的文件
                        if (picFile != null) {
                            picFile.delete();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ACTIVITY_RESULT_ALBUM:
                if (resultCode == RESULT_OK) {
                    if (data.getData() != null){
                        startCropActivity(data.getData());//使用第三方库裁剪图片  这里不能使用 tempUri 了
                    }
                }
                break;
            case UCrop.REQUEST_CROP:
                //这里处理裁剪后的图片
                if (resultCode == RESULT_OK){
                    //原来这个bug只要设置 ok才执行下一步操作就好了!!!!  虽然不知道为什么...但是还是要mark一下
                    handleCropResult(data);
                }else if (resultCode == RESULT_CANCELED){
                    Toast.makeText(UserSettingActivity.this,"RESULT_CANCELED",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void handleCropResult(@NonNull Intent result) {
        Uri resultUri = UCrop.getOutput(result);//接下来就是处理这个数据了
        if (resultUri != null){
            Log.i("TestCamera","我是执行了这里的并且:resultUri.toString():"+resultUri.toString()+
                    "\nresultUri.getPath()"+resultUri.getPath());
            uploadImage(resultUri);
//                            Bitmap bitmap = BitmapFactory.decodeStream(this
//                                    .getContentResolver().openInputStream(resultUri));
//                            mTouxiang.setImageBitmap(bitmap);//暂且先这么设置
            //有一种无法获取到图片的问题  就是不做任何操作...这种情况  我们暂且先不要管它
            //上面的是图片没有处理的情况  所以会出现OOM
        }else {
            Toast.makeText(UserSettingActivity.this,"无法获取裁剪后的图片",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * to do 实现修改之后界面的刷新......
     * @param user
     */
    private void updateUserInfo(User user){
        this.mUser = user;
        RxBus.getInstance().post(new UpdateUserInfoEvent(this.mUser)); //统一通知其它的地方更新数据
        //刷新  昵称 图片 学院 性别  至于书摊描述 还有联系方式  就需要打开一个新的dialog或者popupwindow
        mNickName.setText(mUser.getNickName());
        mContactStr.setText(mUser.getContactWay());
        if (mUser.getImageUrl().equals("?")){
            GlideApp.with(this).load(R.drawable.touxiang).into(mTouxiang);
        }else {
            String imageUrl = ApiInterface.UserImageUrl+mUser.getImageUrl();
            GlideApp.with(this).load(imageUrl).into(mTouxiang);
        }
        if (mUser.getXueYuan().equals("?")){
            mXueyuan.setText("未设置");
        }else {
            mXueyuan.setText(mUser.getXueYuan());
        }
        if (mUser.getSex().equals("?")){
            mSex.setText("未设置");
        }else if (mUser.getSex().equals("男")){
            mSex.setText("男");
        }else if (mUser.getSex().equals("女")){
            mSex.setText("女");
        }
    }

    private void uploadOtherInfo(UpdateUserOtherInfo updateUserOtherInfo){
        //进行与服务器的数据修改操作
        RetrofitSingleton.getInstance().updateUserOtherInfo(updateUserOtherInfo.getUpdateWay(),mUser.getId(),updateUserOtherInfo.getNewStr())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                        DialogUtil.showRoundDialogForLoading(UserSettingActivity.this,"同步中...");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        Toast.makeText(UserSettingActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnNext(new Consumer<UpdateUserInfoResponse>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull UpdateUserInfoResponse updateUserInfoResponse) throws Exception {
                        Toast.makeText(UserSettingActivity.this,updateUserInfoResponse.message,Toast.LENGTH_SHORT).show();
                        if (updateUserInfoResponse.message.equals("修改成功")){
                            updateUserInfo(updateUserInfoResponse.user);
                        }
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        DialogUtil.hideDialogForLoading(UserSettingActivity.this);
                    }
                })
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
    }

    private void uploadImage(Uri resultUri){
        File imageFile = new File(FileUtil.getPath(this,resultUri));
        Log.i("TestCamera","uploadImage"+FileUtil.getPath(this,resultUri));
        int userId = mUser.getId();
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"),imageFile);
        Log.i("TestCamera",this.mUser.getImageUrl());
        RetrofitSingleton.getInstance().updateImageAction(userId,this.mUser.getImageUrl(),requestFile)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                        DialogUtil.showRoundDialogForLoading(UserSettingActivity.this,"同步中...");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        Log.i("TestCamera","上传图片出错");
                        Toast.makeText(UserSettingActivity.this,"上传图片出错",Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnNext(new Consumer<UpdateUserInfoResponse>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull UpdateUserInfoResponse updateImageResponse) throws Exception {
                        updateUserInfoResponse = updateImageResponse;
                        Toast.makeText(UserSettingActivity.this,updateUserInfoResponse.message,Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        DialogUtil.hideDialogForLoading(UserSettingActivity.this);
                        if (updateUserInfoResponse.message.equals("修改成功")){
                            updateUserInfo(updateUserInfoResponse.user);
                        }//只有成功刷新才进行下一步操作  否则不进行下一步操作
                        //没有刷新 很奇怪  是不是glide 有缓存作用
                    }
                })
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
    }

    private void startCropActivity(@NonNull Uri uri) {
        //设置目的文件以及需要处理的图片文件
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(FileUtil.getFileDir(this), mUser.getId()+".jpg")));//将文件名字改为 id.jpg
        //设置状态栏toolBar颜色
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(this,R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
        options.setToolbarWidgetColor(ContextCompat.getColor(this,R.color.color_white));
        options.setActiveWidgetColor(ContextCompat.getColor(this,R.color.cropWigetColor));
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        uCrop.withOptions(options);
        uCrop.start(UserSettingActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickFromGallery();
                }
                break;
            default:
            {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);//使用第三库简化请求权限的过程
            }

        }
    }

    /**
     * Requests given permission.
     * If the permission has been denied previously, a Dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    protected void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            showAlertDialog(getString(R.string.s_permission_title_rationale), rationale,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(UserSettingActivity.this,
                                    new String[]{permission}, requestCode);
                        }
                    }, getString(R.string.s_labelok), null, getString(R.string.s_labelcancel));
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        mAlertDialog = builder.show();
    }
}
