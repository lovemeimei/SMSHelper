package com.cmcc.smshelper;

import android.content.Context;

import java.util.List;
import java.util.logging.Logger;

public class ISInfoUtil {


    private static ISInfoUtil smsUtil;

    public static ISInfoDao isInfoDao;

    public static ISInfoUtil Instance(Context context){
        if (smsUtil ==null)
            smsUtil =new ISInfoUtil(context);
        return smsUtil;
    }

    public ISInfoUtil(Context context) {
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
        isInfoDao = daoSession.getISInfoDao();
    }

    public  void saveInfo(ISInfo item){
       long id= isInfoDao.insert(item);
       if (id>0)
           com.orhanobut.logger.Logger.d("业务信息保存成功");
    }

    public void deleteAll(){
        isInfoDao.deleteAll();
    }

    public List<ISInfo> getAll(){

        return isInfoDao.queryBuilder().list();
    }

    public ISInfo getItem(String ywmc){
       List<ISInfo> items =  isInfoDao.queryBuilder().where(ISInfoDao.Properties.Ywdm.eq(ywmc)).list();
       if (items!=null && items.size()>0)
           return items.get(0);
       return null;
    }
}
