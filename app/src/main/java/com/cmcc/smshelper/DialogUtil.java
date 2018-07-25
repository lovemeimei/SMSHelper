package com.cmcc.smshelper;

import android.content.ComponentName;
import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by xuan on 2018/2/12.
 */

public class DialogUtil {

    private Context context;

    private static DialogUtil dialogUtil;

    public DialogUtil(Context context) {
        this.context = context;
    }

    public static DialogUtil instanc(Context context){
        if (dialogUtil==null)
            dialogUtil = new DialogUtil(context);
        return dialogUtil;
    }


    public MaterialDialog getLoadingDialog(String msg,boolean cancelable){
        MaterialDialog dialog = new MaterialDialog.Builder(context).
                content(msg).
                progress(true,100)
                .cancelable(cancelable)
                .canceledOnTouchOutside(false)
                .build();
        return dialog;
    }

}
