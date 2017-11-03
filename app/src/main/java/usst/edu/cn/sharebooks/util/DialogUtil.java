package usst.edu.cn.sharebooks.util;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.component.RxBus;
import usst.edu.cn.sharebooks.model.event.UpdateUserOtherInfo;


public class DialogUtil {
    private static Dialog mDialog;

    /**
     * 不使用MaterialDialog
     * @param context
     * @param message
     */
    public static void showTriangleDialogForLoading(Context context, String message){
        View view  = LayoutInflater.from(context).inflate(R.layout.loadinganim_trianglelayout,null);
        TextView content = (TextView) view.findViewById(R.id.tv_message);
        AVLoadingIndicatorView avLoadingIndicatorView = (AVLoadingIndicatorView)view.findViewById(R.id.av_loading);
        content.setText(message);
        mDialog = new Dialog(context,R.style.loading_dialog_style);
    //    mDialog.setCancelable(false); //返回键不消失
        mDialog.setContentView(view,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mDialog.show();
        avLoadingIndicatorView.smoothToShow();
    }

    public static void showRoundDialogForLoading(Context context, String message){
        View view  = LayoutInflater.from(context).inflate(R.layout.loadinganim_roundlayout,null);
        TextView content = (TextView) view.findViewById(R.id.tv_message);
        AVLoadingIndicatorView avLoadingIndicatorView = (AVLoadingIndicatorView)view.findViewById(R.id.av_loading);
        content.setText(message);
        mDialog = new Dialog(context,R.style.loading_dialog_style);
        //    mDialog.setCancelable(false); //返回键不消失
        mDialog.setContentView(view,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mDialog.show();
        avLoadingIndicatorView.smoothToShow();
    }

    public static void showDotsDialogForLoading(Context context,String message){
        View view  = LayoutInflater.from(context).inflate(R.layout.loadinganim_dotslayout,null);
        TextView content = (TextView) view.findViewById(R.id.tv_message);
        AVLoadingIndicatorView avLoadingIndicatorView = (AVLoadingIndicatorView)view.findViewById(R.id.av_loading);
        content.setText(message);
        mDialog = new Dialog(context,R.style.loading_dialog_style);
        //    mDialog.setCancelable(false); //返回键不消失
        mDialog.setContentView(view,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mDialog.show();
        avLoadingIndicatorView.smoothToShow();
    }



    public static void showUpdateUserInfoDialog(final Context context, String message){
        View view  = LayoutInflater.from(context).inflate(R.layout.updatenicjname_dialog,null);
        final EditText editNickName = (EditText)view.findViewById(R.id.et_nickname);
        Button ensure = (Button)view.findViewById(R.id.bt_ensure);
        ImageView cancel = (ImageView)view.findViewById(R.id.iv_cancle);
        TextView textView = (TextView)view.findViewById(R.id.tv_title);
        if (message.equals("昵称")){
            textView.setText("修改昵称");
            editNickName.setHint("请输入昵称");
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            });
            ensure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editNickName.length()>0 && editNickName.length()<=12){
                        String nickName = editNickName.getText().toString();
                        UpdateUserOtherInfo updateUserOtherInfo = new UpdateUserOtherInfo(2,nickName);
                        mDialog.dismiss();
                        mDialog = null;
                        RxBus.getInstance().post(updateUserOtherInfo);
                    }
                    else {
                        Toast.makeText(context,"昵称不能为空以及超过十二位",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else if (message.equals("学院")){
            textView.setText("学院名称");
            editNickName.setHint("请输入学院名称");
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            });
            ensure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editNickName.length()>0 && editNickName.length()<=20){
                        String xueyuanName = editNickName.getText().toString();
                        UpdateUserOtherInfo updateUserOtherInfo = new UpdateUserOtherInfo(4,xueyuanName);
                        mDialog.dismiss();
                        mDialog = null;
                        RxBus.getInstance().post(updateUserOtherInfo);
                    }
                    else {
                        Toast.makeText(context,"学院名称不能为空以及超过二十位",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else if (message.equals("联系")){
            textView.setText("添加联系方式");
            editNickName.setHint("QQ微信或其它联系方式");
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            });
            ensure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editNickName.length()>0 && editNickName.length()<=50){
                        String contactWay = editNickName.getText().toString();
                        UpdateUserOtherInfo updateUserOtherInfo = new UpdateUserOtherInfo(5,contactWay);
                        mDialog.dismiss();
                        mDialog = null;
                        RxBus.getInstance().post(updateUserOtherInfo);
                    }
                    else {
                        Toast.makeText(context,"联系方式不能超过五十位",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        mDialog = new Dialog(context,R.style.loading_dialog_style);
        mDialog.setContentView(view,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mDialog.show();
    }

    public static void showEditStallDesDialog(final Context context,String oldStr){
        View view  = LayoutInflater.from(context).inflate(R.layout.editstalldes_layout,null);
        Button ensure = (Button)view.findViewById(R.id.bt_ensure);
        ImageView cancel = (ImageView)view.findViewById(R.id.iv_cancle);
        final EditText stalldes = (EditText)view.findViewById(R.id.et_stalldes);
        if (oldStr.length() == 0){
            stalldes.setText(oldStr);
            stalldes.setSelection(oldStr.length());
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                mDialog = null;
            }
        });
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stalldes.length()>0 && stalldes.length()<=200){
                    String stallDes = stalldes.getText().toString();
                    UpdateUserOtherInfo updateUserOtherInfo = new UpdateUserOtherInfo(6,stallDes);
                    mDialog.dismiss();
                    mDialog = null;
                    RxBus.getInstance().post(updateUserOtherInfo);
                }
                else {
                    Toast.makeText(context,"书摊描述不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mDialog = new Dialog(context,R.style.loading_dialog_style);
        mDialog.setContentView(view,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mDialog.show();
    }


    public static void hideDialogForLoading(Context context){
        if (mDialog != null){
            mDialog.dismiss();
            mDialog = null;
        }
    }


}
