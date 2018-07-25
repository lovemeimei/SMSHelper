package com.cmcc.smshelper;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;
import me.zhouzhuo.zzexcelcreator.MyCell;
import me.zhouzhuo.zzexcelcreator.ZzExcelCreator;
import me.zhouzhuo.zzexcelcreator.ZzFormatCreator;

public class MainActivity extends AppCompatActivity {

    private final int WHAT_READ = 0;
    private final int WHAT_READ_ERROR = 4;
    private final int WHAT_TEST = 1;
    private final int WHAT_FINISH = 2;
    private final int WHAT_Result_FINISH = 3;
    Context context;
    PowerManager powerManager = null;
    PowerManager.WakeLock wakeLock = null;
    private Rationale mRationale;

    PendingIntent sentPI = null;
    PendingIntent deliverPI =null;
    String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    String RECEIVER_SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";


    private int waitTime = 10 * 1000;
    private MyHander handler;
    //业务信息
    private List<ISInfo> isInfos = new ArrayList<>();

    MaterialDialog dialog = null;
    //false终止
    private boolean stopThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        Logger.addLogAdapter(new AndroidLogAdapter());
        init();


//        readAndSaveFile(null);


    }


    void init() {

        context = this;
        dialog = DialogUtil.instanc(this).getLoadingDialog("生成结果,请稍后", true);

        //被拒绝后，再次弹出的说明。我们应该展示为什么需要此权限的说明，以便用户判断是否需要授权给我们。在AndPermission中我们可以使用Rationale。
        mRationale = new Rationale() {
            @Override
            public void showRationale(Context context, List<String> permissions, RequestExecutor executor) {

            }
        };
        requestPermission(Permission.Group.SMS, Permission.Group.STORAGE);

        handler = new MyHander(this);
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        sentPI= PendingIntent.getBroadcast(context, 0, sentIntent,
                0);
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        deliverPI = PendingIntent.getBroadcast(context, 0,
                deliverIntent, 0);


    }

    class MyHander extends Handler {

        WeakReference<Activity> activities = null;

        public MyHander(Activity activity) {
            this.activities = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Activity act = activities.get();
            if (act == null) {
                return;
            }
            switch (msg.what) {
                case WHAT_READ:

                    dialog.dismiss();
                    Toast.makeText(context,"配置文件读取成功",Toast.LENGTH_SHORT).show();

                    break;
                case WHAT_READ_ERROR:
                    Toast.makeText(MainActivity.this, "导入配置文件出错了", Toast.LENGTH_SHORT).show();
                    break;
                case WHAT_TEST:
                    String name = msg.getData().getString("name");
                    dialog.setContent(name + "业务订购短信已发送");
                    break;
                case WHAT_FINISH:
                    dialog.dismiss();
                    dialog.setContent("短信已全部发送");
                    break;
                case WHAT_Result_FINISH:
                    dialog.dismiss();
                    toast("结果文件已生成：" + resultPATH + resultName);
                    break;
            }
        }
    }


    private void doTest() {

        if (isInfos == null || isInfos.size()==0) {
            toast("请先导入配置文件");
            return;
        }

        dialog.setContent("开始测试");
        dialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    int i=0;
                        for (ISInfo item :isInfos){

                        i++;
                        String jobName = item.getYwmc();
                        String num = item.getSxjrm();
                        String content = item.getDgzl();

//                        jobNameCode.put(jobName, num);

                        if (stopThread)
                            break;
                        sendSMS(num, content);

                        Message msg = Message.obtain();
                        msg.what = WHAT_TEST;
                        Bundle bundle = msg.getData();
                        bundle.putString("name", "("+i+"/"+isInfos.size()+")"+jobName);
                        handler.sendMessage(msg);
                        //发短信间隔
                        Thread.sleep(waitTime);

                    }
                    handler.sendEmptyMessage(WHAT_FINISH);
                    // 让出CPU，给其他线程执行
                    Thread.yield();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    /**
     * 读取file文件放到缓存
     */
    public void readAndSaveFile(View view) {

        stopThread = false;

        if (dialog != null) {
            dialog.setContent("加载数据，请稍后");
        } else {
            dialog = DialogUtil.instanc(this).getLoadingDialog("加载数据,请稍后", true);

        }
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Logger.d("取消任务");
                stopThread = true;
            }
        });
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                //读取excel配置文件
                File file = new File(FileUtil.getSDPath(null) + "config.xls");
                try {
                    Workbook workbook = Workbook.getWorkbook(file);
                    Sheet sheet = workbook.getSheet(0);

                    int sheetNum = workbook.getNumberOfSheets();
                    int sheetRows = sheet.getRows();
                    int sheetColumns = sheet.getColumns();

                    Logger.d("the num of sheets is " + sheetNum);
                    Logger.d("the name of sheet is  " + sheet.getName());
                    Logger.d("total rows is 行=" + sheetRows);
                    Logger.d("total cols is 列=" + sheetColumns);

                    ISInfoUtil.Instance(context).deleteAll();
                    for (int i = 1; i < sheetRows; i++) {

                        ISInfo item = new ISInfo();
                        if (TextUtils.isEmpty(sheet.getCell(0, i).getContents()))
                            continue;

                        item.setQyjc(sheet.getCell(0, i).getContents());
                        item.setQydm(sheet.getCell(1, i).getContents());
                        item.setFwdm(sheet.getCell(2, i).getContents());
                        item.setSplx(sheet.getCell(3, i).getContents());
                        item.setYwmc(sheet.getCell(4, i).getContents());
                        item.setYwdm(sheet.getCell(5, i).getContents());
                        item.setZf(sheet.getCell(6, i).getContents());
                        item.setDgzl(sheet.getCell(7, i).getContents());
                        item.setSxjrm(sheet.getCell(8, i).getContents());


                        ISInfoUtil.Instance(context).saveInfo(item);
                        isInfos.add(item);

                    }

                    handler.sendEmptyMessage(WHAT_READ);
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(WHAT_READ_ERROR);

                } catch (BiffException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(WHAT_READ_ERROR);

                }
            }
        }).start();


    }


    public void start(View v) {

//        readAndSaveFile();
        doTest();


    }

    public void getResut(View v) {
//        List<SmsInfo> ITEMS =  SmsInfoUtil.Instance(context).getAll();

        creatExcel();

    }

    public void clear(View v) {

        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("提示")
                .content("确定要清除数据吗？")
                .positiveText("是")
                .negativeText("否")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SmsInfoUtil.Instance(context).deleteAll();
                        ISInfoUtil.Instance(context).deleteAll();


                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .build();

        dialog.show();
    }

    String resultPATH = FileUtil.getSDPath(null);
    String resultName = "测试结果";
//    Map<String, String> jobNameCode = new HashMap<>();

    /**
     * 生成结果
     */
    private void creatExcel() {


        if (isInfos==null)
            isInfos = ISInfoUtil.Instance(context).getAll();
        if (dialog != null) {
            dialog.setContent("生成结果，请稍后");
        } else {
            dialog = DialogUtil.instanc(this).getLoadingDialog("生成结果,请稍后", true);

        }
        dialog.show();


        FileUtil.deleteDir(resultPATH + resultName + ".xls");
        try {
            ZzExcelCreator
                    .getInstance()
                    .createExcel(resultPATH, resultName)  //生成excel文件
                    .createSheet("短信测试")        //生成sheet工作表
                    .close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                //创建头文件
                writeExcel2(0, 0, "企业简称");
                writeExcel2(1, 0, "企业代码");
                writeExcel2(2, 0, "服务代码");
                writeExcel2(3, 0, "SP类型");
                writeExcel2(4, 0, "业务名称");
                writeExcel2(5, 0, "业务代码");
                writeExcel2(6, 0, "资费");
                writeExcel2(7, 0, "订购指令");
                writeExcel2(8, 0, "订购上行接入码");



                writeExcel2(9, 0, "二次确认内容");
                writeExcel2(10, 0, "订购成功提示语");
                writeExcel2(11, 0, "扣费提醒内容");
                writeExcel2(12, 0, "订购失败内容");
                writeExcel2(13, 0, "点播内容");
                writeExcel2(14, 0, "测试时间");

                int row = 1;
                List<MyCell> cells = new ArrayList<>();

                for (ISInfo itemInfo : isInfos) {

                    String name = itemInfo.getYwmc();

                    MyCell cell0 = new MyCell();
                    cell0.setValue(itemInfo.getQyjc());
                    cell0.setRow(row);
                    cell0.setCol(0);
                    cells.add(cell0);

                    MyCell cell1 = new MyCell();
                    cell1.setValue(itemInfo.getQydm());
                    cell1.setRow(row);
                    cell1.setCol(1);
                    cells.add(cell1);

                    MyCell cell2 = new MyCell();
                    cell2.setValue(itemInfo.getFwdm());
                    cell2.setRow(row);
                    cell2.setCol(2);
                    cells.add(cell2);


                    MyCell cell3 = new MyCell();
                    cell3.setValue(itemInfo.getSplx());
                    cell3.setRow(row);
                    cell3.setCol(3);
                    cells.add(cell3);


                    MyCell cell4 = new MyCell();
                    cell4.setValue(itemInfo.getYwmc());
                    cell4.setRow(row);
                    cell4.setCol(4);
                    cells.add(cell4);

                    MyCell cell5 = new MyCell();
                    cell5.setValue(itemInfo.getYwdm());
                    cell5.setRow(row);
                    cell5.setCol(5);
                    cells.add(cell5);

                    MyCell cell6 = new MyCell();
                    cell6.setValue(itemInfo.getZf());
                    cell6.setRow(row);
                    cell6.setCol(6);
                    cells.add(cell6);

                    MyCell cell7 = new MyCell();
                    cell7.setValue(itemInfo.getDgzl());
                    cell7.setRow(row);
                    cell7.setCol(7);
                    cells.add(cell7);

                    MyCell cell8 = new MyCell();
                    cell8.setValue(itemInfo.getSxjrm());
                    cell8.setRow(row);
                    cell8.setCol(8);
                    cells.add(cell8);

                    List<SmsInfo> items1 = SmsInfoUtil.Instance(context).getItemYes(name);
                    List<SmsInfo> items2 = SmsInfoUtil.Instance(context).getItemSuccess(name);
                    List<SmsInfo> items3 = SmsInfoUtil.Instance(context).getItemMoney(name);
                    List<SmsInfo> items4 = SmsInfoUtil.Instance(context).getItemFail(name);

                    String sendCode = itemInfo.getSxjrm();
                    if (sendCode.length() > 7) {
                        sendCode = sendCode.substring(0, 7);

                    }
                    List<SmsInfo> items5 = SmsInfoUtil.Instance(context).getItemDianbo(sendCode);

                    String value1 = "";
                    if (items1 != null && items1.size() > 0) {
                        value1 = items1.get(0).getContent();
                    }

                    MyCell cell9 = new MyCell();
                    cell9.setValue(value1);
                    cell9.setRow(row);
                    cell9.setCol(9);
                    cells.add(cell9);

                    String value2 = "";
                    if (items2 != null && items2.size() > 0) {
                        value2 = items2.get(0).getContent();
                    }
                    MyCell cell10 = new MyCell();
                    cell10.setValue(value2);
                    cell10.setRow(row);
                    cell10.setCol(10);
                    cells.add(cell10);

                    String value3 = "";
                    if (items3 != null && items3.size() > 0) {
                        value3 = items3.get(0).getContent();
                    }
                    MyCell cell11 = new MyCell();
                    cell11.setValue(value3);
                    cell11.setRow(row);
                    cell11.setCol(11);
                    cells.add(cell11);

                    String value4 = "";
                    if (items4 != null && items4.size() > 0) {
                        value4 = items4.get(0).getContent();
                    }
                    MyCell cell12 = new MyCell();
                    cell12.setValue(value4);
                    cell12.setRow(row);
                    cell12.setCol(12);
                    cells.add(cell12);

                    String value5 = "";
                    StringBuffer sb = new StringBuffer();
                    if (items5 != null && items5.size() > 0) {
                        for (SmsInfo item : items5) {
                            sb.append(item.getContent());
                            sb.append("\n");
                        }
                        value5 = sb.toString();
                    }
                    MyCell cell13 = new MyCell();
                    cell13.setValue(value5);
                    cell13.setRow(row);
                    cell13.setCol(13);
                    cells.add(cell13);

                    String value6 = "";
                    if (items1 != null && items1.size() > 0) {
                        value6 = items1.get(0).getTime();
                    }
                    MyCell cell14 = new MyCell();
                    cell14.setValue(value6);
                    cell14.setRow(row);
                    cell14.setCol(14);
                    cells.add(cell14);

                    row++;
                }
                writeExcel(cells);

                handler.sendEmptyMessage(WHAT_Result_FINISH);

            }
        }).start();

    }

    /**
     * 批量写入
     *
     * @param items
     */
    private void writeExcel(List<MyCell> items) {
        try {
            WritableCellFormat format = ZzFormatCreator
                    .getInstance()
                    .createCellFont(WritableFont.ARIAL)
                    .setAlignment(Alignment.CENTRE, VerticalAlignment.CENTRE)
                    .setFontSize(10)
                    .setFontColor(Colour.BLACK)
                    .getCellFormat();
            ZzExcelCreator
                    .getInstance()
                    .openExcel(new File(resultPATH + resultName + ".xls"))
                    .openSheet(0)
//                    .setColumnWidth(col, 25)
//                    .setRowHeight(row, 400)
                    .fillContentS(items, format)
                    .close();
        } catch (IOException | WriteException | BiffException e) {
            e.printStackTrace();
        }
    }

    private void writeExcel2(int col, int row, String value) {
        try {
            WritableCellFormat format = ZzFormatCreator
                    .getInstance()
                    .createCellFont(WritableFont.ARIAL)
                    .setAlignment(Alignment.CENTRE, VerticalAlignment.CENTRE)
                    .setFontSize(14)
                    .setFontColor(Colour.DARK_GREEN)
                    .getCellFormat();
            ZzExcelCreator
                    .getInstance()
                    .openExcel(new File(resultPATH + resultName + ".xls"))
                    .openSheet(0)
                    .setColumnWidth(col, 25)
                    .setRowHeight(row, 400)
                    .fillContent(col, row, value, format)
                    .close();
        } catch (IOException | WriteException | BiffException e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接调用短信接口发短信
     *
     * @param phoneNumber
     * @param message
     */
    public void sendSMS(String phoneNumber, String message) {
        //获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        //拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, sentPI, deliverPI);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

    @Override
    protected void onDestroy() {
//        unregisterReceiver(sendReceiver);
//        unregisterReceiver(responseReceiver);
//        unregisterReceiver(getSmsReceiver);
        super.onDestroy();
    }

    private void requestPermission(String[]... permissions) {
        AndPermission.with(this)
                .permission(permissions)
                .rationale(mRationale)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {

                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, permissions)) {
//                            mSetting.showSetting(permissions);
                        }
                    }
                })
                .start();
    }


    protected void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
