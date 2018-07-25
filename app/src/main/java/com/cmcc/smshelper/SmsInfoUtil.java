package com.cmcc.smshelper;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuan on 2018/2/22.
 */

public class SmsInfoUtil {

    private static SmsInfoUtil smsUtil;
    public static SmsInfoDao infoDao;

    public static SmsInfoUtil Instance(Context context){
        if (smsUtil ==null)
           smsUtil =new SmsInfoUtil(context);
        return smsUtil;
    }

    public SmsInfoUtil(Context context) {
        getInfoDao(context);
    }

    /**
     * infoDao
     */
    private static void getInfoDao(Context context) {
        // 创建数据
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "smshelper.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        infoDao = daoSession.getSmsInfoDao();
    }

    public  void addInfo(SmsInfo item){

        long id =infoDao.insert(item);
        if (id>0)
            System.out.println("新短信保存成功");
    }

    public List<SmsInfo> getItems(String... str){
        List<SmsInfo> items = new ArrayList<>();
        if (str.length==0){
            items = infoDao.loadAll();

        }else {
            items = infoDao.queryBuilder().where(SmsInfoDao.Properties.Type.eq(1) , SmsInfoDao.Properties.Content.like("%是%")).list();
        }
        return  items;
    }

    /**
     * 根据业务名称获取二次确认短信
     * @param jobName
     * @return
     */
    public List<SmsInfo> getItemYes(String jobName){
        List<SmsInfo> items = new ArrayList<>();

//        items = infoDao.queryBuilder().where(SmsInfoDao.Properties.Type.eq(1) , SmsInfoDao.Properties.ReceiverType.eq(1)).list();

        items = infoDao.queryBuilder().
                where(
                        SmsInfoDao.Properties.Type.eq(1) ,
                        SmsInfoDao.Properties.ReceiverType.eq(1),
                        SmsInfoDao.Properties.Content.like("%"+jobName+"%"))
                .list();
        return items;
    }
    /**
     * 根据业务名称获取扣费短信
     * @param jobName
     * @return
     */
    public List<SmsInfo> getItemMoney(String jobName){
        List<SmsInfo> items = new ArrayList<>();

        items = infoDao.queryBuilder().
                where(
                SmsInfoDao.Properties.Type.eq(1) ,
                SmsInfoDao.Properties.ReceiverType.eq(3),
                SmsInfoDao.Properties.Content.like("%"+jobName+"%"))
                .list();

        return items;
    }

    /**
     * 根据业务名称获取扣费短信
     * @param jobName
     * @return
     */
    public List<SmsInfo> getItemSuccess(String jobName){
        List<SmsInfo> items = new ArrayList<>();

        items = infoDao.queryBuilder().
                where(
                        SmsInfoDao.Properties.Type.eq(1) ,
                        SmsInfoDao.Properties.ReceiverType.eq(2),
                        SmsInfoDao.Properties.Content.like("%"+jobName+"%"))
                .list();

        return items;
    }


    /**
     * 根据业务名称获取扣费短信
     * @param jobName
     * @return
     */
    public List<SmsInfo> getItemFail(String jobName){
        List<SmsInfo> items = new ArrayList<>();

        items = infoDao.queryBuilder().
                where(
                        SmsInfoDao.Properties.Type.eq(1) ,
                        SmsInfoDao.Properties.ReceiverType.eq(4),
                        SmsInfoDao.Properties.Content.like("%"+jobName+"%"))
                .list();

        return items;
    }
    public void deleteAll(){
        infoDao.deleteAll();
    }

    /**
     * 根据业务端口获取点播短信
     * @param code
     * @return
     */
    public List<SmsInfo> getItemDianbo(String code){
        List<SmsInfo> items = new ArrayList<>();

        items = infoDao.queryBuilder().
                where(
                        SmsInfoDao.Properties.Type.eq(1) ,
                        SmsInfoDao.Properties.Sender.like(code+"%"))
                .list();

        return items;
    }


    public List<SmsInfo> getAll(){
        List<SmsInfo> items = new ArrayList<>();

        items = infoDao.queryBuilder().list();

        return items;
    }
}
