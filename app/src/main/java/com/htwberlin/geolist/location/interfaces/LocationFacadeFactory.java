package com.htwberlin.geolist.location.interfaces;

import android.content.Context;

import com.htwberlin.geolist.data.interfaces.DataStorageImpl;
import com.htwberlin.geolist.data.interfaces.LocationRepositoryImpl;

public class LocationFacadeFactory {

    private static LocationFacadeImpl instance;

    public static LocationFacadeImpl getInstance(Context context){
        if(instance == null){
            final LocationRepositoryImpl locationRepository = (LocationRepositoryImpl) new DataStorageImpl(context).getLocationRepo();
            instance = new LocationFacadeImpl(locationRepository);
        }
        return instance;
    }
}
