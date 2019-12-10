/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzy.demo.okgo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseDetailActivity;
import com.lzy.demo.ui.NumberProgressBar;
import com.lzy.demo.utils.FileUtils;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.io.File;
import java.text.NumberFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class SimpleDownloadActivity extends BaseDetailActivity
{

    private static final int REQUEST_PERMISSION_STORAGE = 0x01;

    @Bind(R.id.fileDownload)
    Button btnFileDownload;
    @Bind(R.id.downloadSize)
    TextView tvDownloadSize;
    @Bind(R.id.tvProgress)
    TextView tvProgress;
    @Bind(R.id.netSpeed)
    TextView tvNetSpeed;
    @Bind(R.id.pbProgress)
    NumberProgressBar pbProgress;
    private NumberFormat numberFormat;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_file_download);
        ButterKnife.bind(this);
        setTitle("简单文件下载");

        numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(2);

        checkSDCardPermission();
    }

    /**
     * 检查SD卡权限
     */
    protected void checkSDCardPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //获取权限
            } else
            {
                showToast("权限被禁止，无法下载文件！");
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }

    @OnClick(R.id.fileDownload)
    public void fileDownload(View view)
{
    String apkFileName = "";
    apkFileName = "OkGo.apk";
    OkGo.<File>get("http://47.94.147.36:80/bjrecycle/Public/Upload/2019-10-25/20191025105022_SmartClassify-R-4.35.4-HY_BJFenFenBao-20191025.1034.apk")//
            .tag(this)//
            .execute(new FileCallback()
            {

                @Override
                public void onStart(Request<File, ? extends Request> request)
                {
                    btnFileDownload.setText("正在下载中");
                }

                @Override
                public void onSuccess(Response<File> response)
                {
                    handleResponse(response);
                    btnFileDownload.setText("下载完成");
                    Log.e("下载完成", response.body().getName());
//                        FileUtils.openFile(SimpleDownloadActivity.this, response.body().getPath());
                    installApk(response.body().getName());
                }

                @Override
                public void onError(Response<File> response)
                {
                    handleError(response);
                    btnFileDownload.setText("下载出错");
                }

                @Override
                public void downloadProgress(Progress progress)
                {
                    System.out.println(progress);

                    String downloadLength = Formatter.formatFileSize(getApplicationContext(), progress.currentSize);//获取的文件大小
                    String totalLength = Formatter.formatFileSize(getApplicationContext(), progress.totalSize);//总文件大小
                    tvDownloadSize.setText(downloadLength + "/" + totalLength);//文件大小百分比
                    String speed = Formatter.formatFileSize(getApplicationContext(), progress.speed);//速度
                    tvNetSpeed.setText(String.format("%s/s", speed));//下载速度
                    tvProgress.setText(numberFormat.format(progress.fraction));//下载进度
                    pbProgress.setMax(10000);
                    pbProgress.setProgress((int) (progress.fraction * 10000));//下载条
                }
            });
}

    private void installApk(String name)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String type = "application/vnd.android.package-archive";
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            uri = FileProvider.getUriForFile(SimpleDownloadActivity.this, "com.lzy.demo.utils.MyFileProvider", getApkFile(name));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else
        {
            uri = Uri.fromFile(getApkFile(name));
        }
        intent.setDataAndType(uri, type);
        SimpleDownloadActivity.this.startActivity(intent);
    }

    private static File getApkFile(String apkName)
    {
        String apkDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "download" + File.separator;
        File newApkFile = new File(apkDir, apkName);       //File()的第一个参数为文件的父路径，第二个参数是文件名
        return newApkFile;
    }
}
