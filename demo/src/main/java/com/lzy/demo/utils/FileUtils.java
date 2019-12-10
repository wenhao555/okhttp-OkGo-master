package com.lzy.demo.utils;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

/**
 * Created by liutao on 2017/9/1.
 */

public class FileUtils
{

    public static Intent openFile(Context context, String filePath)
    {

        File file = new File(filePath);

        if ((file == null) || !file.exists() || file.isDirectory())
            return null;

        /* 取得扩展名 */
        String end = file
                .getName()
                .substring(file.getName().lastIndexOf(".") + 1,
                        file.getName().length()).toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav"))
        {

            return getAudioFileIntent(filePath);
        } else if (end.equals("3gp") || end.equals("mp4"))
        {

            return getAudioFileIntent(filePath);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp"))
        {

            return getImageFileIntent(context, filePath);
        } else if (end.equals("apk"))
        {

            return getApkFileIntent(filePath);
        } else if (end.equals("ppt"))
        {

            return getPptFileIntent(filePath);
        } else if (end.equals("xls"))
        {

            return getExcelFileIntent(filePath);
        } else if (end.equals("doc") || end.equals("docx"))
        {

            return getWordFileIntent(filePath);

        } else if (end.equals("pdf"))
        {

            return getPdfFileIntent(filePath);
        } else if (end.equals("chm"))
        {

            return getChmFileIntent(filePath);
        } else if (end.equals("txt") || end.equals("rar"))
        {

            return getTextFileIntent(filePath, false);
        } else if (end.equals("zip") || end.equals("rar"))
        {

            return getZIPFileIntent(filePath);
        } else
        {
            return getAllIntent(context, end, filePath);
        }
    }


    // Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(Context context, String end, String param)
    {

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        {//小于安卓7.0
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(new File(param));
            intent.setDataAndType(uri, "*/*");
        } else
        {//大于安卓7.0
            Uri uri = FileProvider.getUriForFile(context, "com.youli.zbetuch_huangpu.provider", new File(param));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);    //这一步很重要。给目标应用一个临时的授权。
            if (end.equals("xlsx"))
            {
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else
            {
                intent.setDataAndType(uri, "*/*");
            }
        }
        return intent;
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(String param)
    {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    // Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(String param)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(String param)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    // Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param)
    {
        Uri uri = Uri.parse(param).buildUpon()
                .encodedAuthority("com.android.htmlfileprovider")
                .scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(Context context, String param)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        {//小于7.0查看图片
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(param));
            intent.setDataAndType(uri, "image/*");
        } else
        {//大于等于7.0查看图片
            Uri photoURI = FileProvider.getUriForFile(context, "com.youli.zbetuch_huangpu.provider", new File(param));
            intent.setDataAndType(photoURI, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);    //这一步很重要。给目标应用一个临时的授权。
        }
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(String param)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String param)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String param)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String param, boolean paramBoolean)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean)
        {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else
        {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    //Android获取一个用于打开ZIP压缩的文件的intent
    public static Intent getZIPFileIntent(String param)
    {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-gzip");
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    /**
     * 查询设备上所有非系统apk
     *
     * @param mContext
     * @return
     */
    public static String showAllApks(Context mContext)
    {
        String value = "";
        try
        {
            PackageManager packageManager = mContext.getPackageManager();
            List<PackageInfo> packageInfoList = packageManager
                    .getInstalledPackages(0);
            for (PackageInfo info : packageInfoList)
            {
                // 判断如果不是系统apk
                if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)
                {
                    // System.out.println(info.applicationInfo.packageName
                    // + "===>"
                    // + packageManager
                    // .getApplicationLabel(info.applicationInfo));
                    value += packageManager
                            .getApplicationLabel(info.applicationInfo) + ",";
                }

                // 获得应用的图标
                // packageManager.getApplicationIcon(applicationInfo)
            }
            if (value.trim().length() > 1)
            {
                value = value.substring(0, value.length() - 1);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }

    public static File getSaveFile(Context context)
    {
        return new File(context.getFilesDir(), "pic.jpg");
    }

}