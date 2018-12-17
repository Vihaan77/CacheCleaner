package com.cacheClean.datacleaner;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

 public class AdapterCache extends RecyclerView.Adapter<AdapterCache.MyViewHolder> implements  ActivityCompat.OnRequestPermissionsResultCallback {

    ArrayList<App> res;
    ArrayList<App> res2;
    long packageSize = 1;
    Context context;
    String packageName;
    String appName;
    Activity activity;
    private static final long CACHE_APP = Long.MAX_VALUE;
    private CachePackageDataObserver mClearCacheObserver;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    clearCache(packageName);
                    // permission was granted, yay! Do the

                    // contacts-related task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(context,"You need to accept the permission",Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView pkgName,size,appname;
        Button appcacheBtn,pagebtn;
        ImageView appicon;
        public MyViewHolder(View view) {
            super(view);
            pkgName = (TextView) view.findViewById(R.id.name);
            size = (TextView) view.findViewById(R.id.size);
            appname =(TextView) view.findViewById(R.id.appname);
            appcacheBtn =(Button) view.findViewById(R.id.appcache);
            pagebtn = (Button) view.findViewById(R.id.appcach_btne);
            appicon =(ImageView) view.findViewById(R.id.appicon);

        }

    }

    public AdapterCache(Activity activity,Context context, ArrayList<App> res,ArrayList<App> res2) {
        this.res = res;
        this.res2 = res2;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cache_data, parent, false);



        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        if (res2.size() > 0 && res.size()>0 ){
            App objectApp = res.get(position);
            App objectApp2 = res2.get(position);
            holder.appname.setText(objectApp.getName());
            holder.pkgName.setText(objectApp.getPackageName());
            final long SIZE_KB = 1024L;
            final long SIZE_MB = SIZE_KB * SIZE_KB;
            long cacheInKB=  objectApp2.getCacheSize()/SIZE_KB;
            long cacheInMB=  objectApp2.getCacheSize()/SIZE_MB;
            if (cacheInMB>=1){
                holder.size.setText(cacheInMB +" MB");
            }else {
                holder.size.setText(cacheInKB +" KB");
            }


            String cacheSize = String.valueOf(objectApp2.getCacheSize());
            //holder.size.setText(cacheSize);
            holder.appicon.setImageDrawable(objectApp.getIcon());

            holder.appcacheBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    trimCache(context);



                }
            });

        }

         }



    @Override
    public int getItemCount() {
        return res.size();
    }

     public static void trimCache(Context context) {
         try {
             File dir = context.getCacheDir();
             if (dir != null && dir.isDirectory()) {
                 deleteDir(dir);
             }
         } catch (Exception e) {
             // TODO: handle exception
         }
     }

     public static boolean deleteDir(File dir) {
         if (dir != null && dir.isDirectory()) {
             String[] children = dir.list();
             for (int i = 0; i < children.length; i++) {
                 boolean success = deleteDir(new File(dir, children[i]));
                 if (!success) {
                     return false;
                 }
             }
         }

         // The directory is now empty so delete it
         return dir.delete();
     }

    void clearCache()
    {


       /* PackageManager  pm = context.getPackageManager();
// Get all methods on the PackageManager
        Method[] methods = pm.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals("freeStorage")) {
                // Found the method I want to use
                try {
                    long desiredFreeStorage =Long.MAX_VALUE; // Request for 8GB of free space
                    m.invoke(pm, desiredFreeStorage , null);
                } catch (Exception e) {
                    // Method invocation failed. Could be a permission problem
                }
                break;
            }
        }
        Log.e("reached","===================CACHE CALLED================");

       *//* if (mClearCacheObserver == null)
        {
            mClearCacheObserver=new CachePackageDataObserver();
        }

        PackageManager mPM=context.getPackageManager();

        @SuppressWarnings("rawtypes")
        final Class[] classes= { Long.TYPE, IPackageDataObserver.class };

        Long localLong=Long.valueOf(CACHE_APP);

        try
        {
            Method localMethod=
                    mPM.getClass().getMethod("freeStorageAndNotify", classes);

            *//**//*
             * Start of inner try-catch block
             *//**//*
            try
            {
                localMethod.invoke(mPM, localLong, mClearCacheObserver);
            }
            catch (IllegalArgumentException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            *//**//*
             * End of inner try-catch block
             *//**//*
        }
        catch (NoSuchMethodException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }*/
    }//End of clearCache() method





    private class cachePackState extends IPackageStatsObserver.Stub {
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {



            Log.e("Package Name", pStats.packageName + "");

            Log.e("Cache Size", pStats.cacheSize + "");
            Log.e("Data Size", pStats.dataSize + "");


PackageManager pm = context.getPackageManager();

            packageName = pStats.packageName;

            packageSize =  pStats.cacheSize;
            try {
                ApplicationInfo ai = pm.getApplicationInfo(packageName,PackageManager.GET_META_DATA);
                appName =""+ pm.getApplicationLabel(ai);

            }catch (Exception e){
                Log.e("Eception ",e.getMessage());
            }
            Log.e("APK Size",pStats.codeSize+"");
            Log.e("APP Name",appName+"");
        }
    }




    private final void clearCache(String packageName) {
        CachePackageDataObserver mClearCacheObserver=null;
        if (mClearCacheObserver == null) {
            mClearCacheObserver = new CachePackageDataObserver();
        }

        PackageManager mPM = context.getPackageManager();


        try {
            Class<?> myClass = Class
                    .forName("android.content.pm.PackageManager");
            Class<?>[] dataCacheTypes = new Class[] { String.class,
                    IPackageDataObserver.class };

            Method localMethod = myClass.getMethod(
                    "deleteApplicationCacheFiles", dataCacheTypes);



            try {

                localMethod.invoke(mPM, new Object[] { packageName,
                        mClearCacheObserver });

            } catch (IllegalArgumentException e) {
                                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                             // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                          // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e1) {
                        // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
                     // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private class CachePackageDataObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(String packageName, boolean succeeded) {

        }
    }
}
