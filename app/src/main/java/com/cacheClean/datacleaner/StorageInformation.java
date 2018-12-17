package com.cacheClean.datacleaner;

import android.content.Context;

import java.util.ArrayList;

/**
 * This class will perform data operation
 */
public class StorageInformation {

    long packageSize = 0;
    AppDetails cAppDetails;
    public ArrayList<AppDetails.PackageInfoStruct> res;
    Context context;

    public StorageInformation(Context context){
        this.context=context;
    }


}
