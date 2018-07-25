package com.cmcc.smshelper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class FileUtil {

    private FileUtil() {
    }

    public static boolean copyAndDeleteSourceFile(File fromFile, File toFile) {
        return copyFile(fromFile, toFile, true, true);
    }

    public static boolean deleteFile(String path) {
        if (path == null || path.trim().equals(""))
            return true;

        File f = new File(path);

        if (!f.exists())
            return true;

        return f.delete();
    }

    public static boolean deleteDir(String path) {

        if (path == null || path.trim().equals(""))
            return true;

        File dir = new File(path);

        if (dir.isDirectory()) {
            File[] children = dir.listFiles();

            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(children[i].getAbsolutePath());
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public static boolean copyFile(File fromFile, File toFile, Boolean rewrite,
                                   Boolean copySuccIfDeleteFromFile) {
        if (!fromFile.exists() || !fromFile.isFile() || !fromFile.canRead()) {
            return false;
        }

        if (toFile.exists() && rewrite) {
            toFile.delete();
        }

        FileInputStream from = null;
        FileOutputStream to = null;

        boolean isSuccess = true;

        try {

            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);

            byte[] content = new byte[1024];

            int reads = 0;

            while ((reads = from.read(content)) > 0) {
                to.write(content, 0, reads);
            }

            to.flush();

        } catch (Exception ex) {
            isSuccess = false;
        } finally {
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (copySuccIfDeleteFromFile && isSuccess) {
                fromFile.delete();
            }
        }

        return isSuccess;
    }

    /**
     * aFinal缓存目录：<br>
     * 不能放到cache文件夹下,否则在小米上清除缓存存在问题!
     */
    public static String getSDAFinalPath() {
        return getSDPath("aFinal");
    }

    /**
     * 拍照保存图片
     */
    public static String getSDImagePath() {
        return getSDPath("image");
    }

    /**
     * 录音保存目录
     */
    public static String getSDRecordPath() {
        return getSDPath("cache/record");
    }

    /**
     * 缓存目录
     */
    public static String getSDCachePath() {
        return getSDPath("cache");
    }

    /**
     * 缓存目录
     */
    public static String getSDCacheCropPath() {
        return getSDPath("cache/crop");
    }

    /**
     * 分页查看大图临时文件夹
     */
    public static String getSDBigImagePath() {
        return getSDPath("cache/bigImage");
    }

    /**
     * 分页查看大图临时文件夹
     */
    public static String getSDBigImagePathAfinal() {
        return getSDPath("cache/bigImage/afinal");
    }

    /**
     * 视频文件夹
     */
    public static String getSDVideoPath() {
        return getSDPath("cache/video");
    }

    /**
     * 下载APK文件夹
     */
    public static String getSDDownloadPath() {
        return getSDPath("cache/download");
    }

    public static boolean isExist(String path) {

        if (path == null || path.trim().equals(""))
            return false;

        File f = new File(path);

        if (!f.exists())
            return false;

        return true;
    }

    public static String getSDPath(String subDir) {

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {

            String path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();

            if (!path.endsWith("/"))
                path += "/";

            path += (SystemConst.PROJECT_DIR + "/");

            if (subDir != null && subDir.trim().length() > 0)
                path += (subDir + "/");

            File f = new File(path);

            if (!f.exists()) {
                if (f.mkdirs())
                    return path;
                else
                    return null;
            } else {
                if (f.isFile()) {
                    if (f.delete()) {
                        if (f.mkdir())
                            return path;
                        else
                            return null;
                    } else
                        return null;
                } else
                    return path;
            }
        }
        return null;
    }

    /**
     * 获取文件夹大小
     *
     * @param file 文件夹
     * @return 文件夹大小，单位字节
     */
    public static long getFileSize(File file) {
        long size = 0;
        File flist[] = file.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    /**
     * 获取文件大小
     *
     * @param file 文件
     * @return 文件大小，单位字节
     */
    public static long getFileSize(String file) {
        long size = 0;
        FileInputStream fis;
        try {
            fis = new FileInputStream(new File(file));
            size = fis.available();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static void openFile(Context context, String filePath) {
        try {

            if (TextUtils.isEmpty(filePath)) {
                return;
            }

            File file = new File(filePath);

            if (!file.exists()) {
                return;
            }

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 设置intent的Action属性
            intent.setAction(Intent.ACTION_VIEW);
            // 获取文件file的MIME类型To
            String type = getMIMEType(file);
            // 设置intent的data和Type属性。
            intent.setDataAndType(Uri.fromFile(file), type);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "sorry附件不能打开,请下载相关软件！", Toast.LENGTH_SHORT).show();
        }
    }

    private static String getMIMEType(File file) {

        String type = "*/*";

        if (file == null)
            return type;

        if (TextUtils.isEmpty(file.getName())) {
            return type;
        }

        String fName = file.getName();
        // 获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }

        String ext = fName.substring(dotIndex, fName.length()).toLowerCase();
        // 在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIMEMapTable.length; i++) {
            if (ext.equals(MIMEMapTable[i][0]))
                type = MIMEMapTable[i][1];
        }
        return type;
    }

    public static void saveObjects(Object objs, String fileName)
            throws Exception {
        FileOutputStream os = new FileOutputStream(FileUtil.getSDCachePath()
                + fileName);
        ObjectOutputStream oos = new ObjectOutputStream(os);

        oos.writeObject(objs);

    }

    @SuppressWarnings("resource")
    public static Object readObjects(String fileName) {
        Object object = null;
        try {
            FileInputStream is = new FileInputStream(FileUtil.getSDCachePath()
                    + fileName);
            ObjectInputStream ois = new ObjectInputStream(is);
            object = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    private static final String[][] MIMEMapTable = {
            // {文件扩展名,MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"}, {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"}, {".rtf", "application/rtf"},
            {".sh", "text/plain"}, {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"}, {".txt", "text/plain"},
            {".wav", "audio/x-wav"}, {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"}, {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"}, {"", "*/*"}};

    /**
     * 获取文件名
     *
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        String fileName = "";
        if ("".equals(path)) {

            return "";
        }

        int index = path.lastIndexOf("/");
        if (index == -1) {

            return "";
        } else {
            fileName = path.substring(index + 1);
        }

        return fileName;
    }

    /**
     * 读取表情配置文件
     *
     * @param context
     * @return
     */
    public static List<String> getEmojiFile(Context context) {
        try {
            List<String> list = new ArrayList<String>();
            InputStream in = context.getResources().getAssets().open("emoji");
            BufferedReader br = new BufferedReader(new InputStreamReader(in,
                    "GBK"));
            String str = null;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }

            System.out.println(list);
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Properties loadConfig(String file) {
        Properties properties = new Properties();
        try {
            FileInputStream s = new FileInputStream(file);
            properties.load(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    public void saveConfig(String file, Properties properties) {
        try {
            FileOutputStream s = new FileOutputStream(file, false);
            properties.store(s, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String Inputstr2Str_byteArr(InputStream in, String encode)
            throws UnsupportedEncodingException, IOException {
        StringBuffer sb = new StringBuffer();
        byte[] b = new byte[1024];
        int len = 0;

        if (encode == null || encode.equals("")) {
            // 默认以utf-8形式
            encode = "utf-8";
        }
        while ((len = in.read(b)) != -1) {
            sb.append(new String(b, 0, len, encode));
        }
        return sb.toString();

    }

}
