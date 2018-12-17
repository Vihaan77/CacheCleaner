package com.cacheClean.datacleaner;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CleanerService.OnActionListener{

    long packageSize = 0;
    AppDetails cAppDetails;
    public ArrayList<App> res;
    private RecyclerView recyclerView;
    private AdapterCache mAdapter;
    String[] app_labels;
    ArrayList<App> applist,detailAppList;
    private static final int REQUEST_STORAGE = 0;

    @Override
    protected void onDestroy() {

        getApplication().unbindService(mServiceConnection);
        super.onDestroy();
    }

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private CleanerService mCleanerService;
    private TextView mEmptyView;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog mProgressDialog;
    private View mProgressBar;
    private TextView mProgressBarText;
    private LinearLayoutManager mLayoutManager;
    private Menu mOptionsMenu;

    private boolean mAlreadyScanned = false;
    private boolean mAlreadyCleaned = false;
    private String mSearchQuery;

    private String mSortByKey;
    private String mCleanOnAppStartupKey;
    private String mExitAfterCleanKey;



    public static final int FETCH_PACKAGE_SIZE_COMPLETED = 100;
    public static final int ALL_PACAGE_SIZE_COMPLETED = 200;
    Button cachebtn,pagebtn;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCleanerService = ((CleanerService.CleanerServiceBinder) service).getService();
            mCleanerService.setOnActionListener(MainActivity.this);

         //   updateStorageUsage();

            if (!mCleanerService.isCleaning() && !mCleanerService.isScanning()) {
                if (mSharedPreferences.getBoolean(mCleanOnAppStartupKey, false) &&
                        !mAlreadyCleaned) {
                    mAlreadyCleaned = true;

                    cleanCache();

                } else if (!mAlreadyScanned) {
                    mCleanerService.scanCache();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCleanerService.setOnActionListener(null);
            mCleanerService = null;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    // contacts-related task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(this,"You need to accept the permission",Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        cachebtn = (Button) findViewById(R.id.cache_btn);
        pagebtn = (Button) findViewById(R.id.appcach_btne);
        DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);        mCleanOnAppStartupKey = getString(R.string.clean_on_app_startup_key);
        mExitAfterCleanKey = getString(R.string.exit_after_clean_key);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        StorageInformation storageInformation =  new  StorageInformation(MainActivity.this);
        detailAppList = new ArrayList<App>();
        applist = new ArrayList<App>();
        getPackages();

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CLEAR_APP_CACHE},
                1);

      /*  if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CLEAR_APP_CACHE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CLEAR_APP_CACHE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CLEAR_APP_CACHE},
                        1);

                // 1 is an int constant. The callback method gets the  result of the request.
            }

            return;
        }*/

        ComponentName admin = new ComponentName(this, AdminReceiver.class);
        Intent intent1 = new Intent(
                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).putExtra(
                DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);

        pagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        cachebtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                   //  trimCache(MainActivity.this);


                if (mCleanerService != null && !mCleanerService.isScanning() &&
                        !mCleanerService.isCleaning() && mCleanerService.getCacheSize() > 0) {
                    mAlreadyCleaned = false;

                    cleanCache();
                }
            }
        });
        getApplication().bindService(new Intent(this, CleanerService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);



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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void cleanCache() {
        if (!CleanerService.canCleanExternalCache(this)) {
            if (shouldShowRequestPermissionRationale(PERMISSIONS_STORAGE[0])) {
                showStorageRationale();
            } else {
                requestPermissions(PERMISSIONS_STORAGE, REQUEST_STORAGE);
            }
        } else {
            mCleanerService.cleanCache();
        }
    }
    private void showStorageRationale() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(R.string.rationale_title);
        dialog.setMessage(getString(R.string.rationale_storage));
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog.show();
    }


    public ArrayList<App> getPackages() {
        ArrayList<App> apps = getInstalledApps(false);
        final int max = apps.size();
        for (int i = 0; i < max; i++) {
            apps.get(i);
        }

        return apps;


    }
    private ArrayList<App> getInstalledApps(boolean getSysPackages) {

        List<PackageInfo> packs = getPackageManager()
                .getInstalledPackages(0);
        try {
            app_labels = new String[packs.size()];
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        for (int i = 0; i < packs.size(); i++) {
           App newInfo = new App();
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }
            newInfo.name = p.applicationInfo.loadLabel(
                    getPackageManager()).toString();
            newInfo.packageName = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
            applist.add(newInfo);
        }
        getAppPackages();
        mAdapter = new AdapterCache(MainActivity.this,MainActivity.this,applist,detailAppList);

        recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        return applist;
    }

    public void getAppPackages(){
        for (int m = 0; m < applist.size(); m++) {
            PackageManager pm = getPackageManager();
            Method getPackageSizeInfo;
            try {
                getPackageSizeInfo = pm.getClass().getMethod(
                        "getPackageSizeInfo", String.class,
                        IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(pm, applist.get(m).packageName,
                        new cachePackState());
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }



    }

    @Override
    public void onScanStarted(Context context) {

    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max) {

    }

    @Override
    public void onScanCompleted(Context context, List<AppsListItem> apps) {

    }

    @Override
    public void onCleanStarted(Context context) {

    }

    @Override
    public void onCleanCompleted(Context context, boolean succeeded) {

    }

  /*  public void  getpackageSize() {
        cAppDetails = new AppDetails(this);
        res = cAppDetails.getPackages();


        mAdapter = new AdapterCache(this,this,res);

        recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);


        if (res == null)
            return;
        for (int m = 0; m < res.size(); m++) {
            // Create object to access Package Manager
            PackageManager pm = getPackageManager();
            Method getPackageSizeInfo;
            try {
                getPackageSizeInfo = pm.getClass().getMethod(
                        "getPackageSizeInfo", String.class,
                        IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(pm, res.get(m).pname,
                        new cachePackState()); //Call the inner class

            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }*/

   /* public void initCache(){

        for (int m = 0; m < res.size(); m++) {
            PackageManager pm = getPackageManager();
            Method getPackageSizeInfo;
            try {
                getPackageSizeInfo = pm.getClass().getMethod(
                        "getPackageSizeInfo", String.class,
                        IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(pm, res.get(m).pname,
                        new cachePackState());
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }*/


    /*
     * Inner class which will get the data size for application
     * */
    private class cachePackState extends IPackageStatsObserver.Stub {
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {


            App app = new App();
            app.setPackageName(pStats.packageName);
            app.cacheSize = pStats.cacheSize;
            app.setDataSize(pStats.dataSize);
            app.setCacheSize(pStats.cacheSize);
            // app object has cache and data size of app with package name
            Log.d("Package Size", pStats.packageName + "");
            Log.e("Cache Size", pStats.packageName + "  " + pStats.cacheSize + "");
            Log.w("Data Size", pStats.dataSize + "");
            packageSize = packageSize + pStats.cacheSize;
            Log.v("Total Cache Size", " " + pStats.cacheSize);
            detailAppList.add(app);


        }
    }
}
