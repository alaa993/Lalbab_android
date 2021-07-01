package com.lalbab.app.UpdateVersion;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class UpdateHelper {
    public static String KEY_UPDATE_ENABLE = "isUpdate";
    public static String KEY_UPDATE_VERSION = "NewAppVersion";
    public static String KEY_UPDATE_URI = "newAppUrl";

    public  interface onUpdateCheckListener {
        void onUpdateCheckListener(String urlApp);
    }
    public static Builder with(Context context)
    {
        return new Builder(context);
    }
    private onUpdateCheckListener onUpdateCheckListener;
    private Context context;

    public UpdateHelper(Context context,onUpdateCheckListener onUpdateCheckListener)
    {
        this.onUpdateCheckListener=onUpdateCheckListener;
        this.context=context;
    }
    public void check()
    {
        FirebaseRemoteConfig remoteConfig =FirebaseRemoteConfig.getInstance();
        if(remoteConfig.getBoolean(KEY_UPDATE_ENABLE))
        {
            String currentVersion = remoteConfig.getString(KEY_UPDATE_VERSION);
            String appVersion =getAppVersion(context);
            String updateURL = remoteConfig.getString(KEY_UPDATE_URI);

            if(!TextUtils.equals(currentVersion,appVersion)&& onUpdateCheckListener != null)
            {
                onUpdateCheckListener.onUpdateCheckListener(updateURL);
            }
        }
    }

    private String getAppVersion(Context context) {
        String result = "";
        try {


            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            result=result.replaceAll("[a-zA-A]|-","");
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return result;
    }

    public static class Builder{

        private Context context;
        private  onUpdateCheckListener onUpdateCheckListener;

        public Builder (Context context) {
            this.context = context;
        }

        public Builder onUpdareCheck(onUpdateCheckListener onUpdateCheckListener){
            this.onUpdateCheckListener = onUpdateCheckListener;
            return this;
        }
        public UpdateHelper bulid(){
            return new UpdateHelper(context,onUpdateCheckListener);
        }
        public UpdateHelper check()
        {
            UpdateHelper updateHelper = bulid();
            updateHelper.check();
            return updateHelper;
        }
    }
}