package com.lethimyhoa.k2341111e_mobile.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NetworkMonitor {
    public enum NetworkStatus {
        AVAILABLE, UNAVAILABLE
    }

    private final MutableLiveData<NetworkStatus> status = new MutableLiveData<>();
    private final ConnectivityManager connectivityManager;

    public NetworkMonitor(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        checkInitialStatus();
    }

    private void checkInitialStatus() {
        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            status.postValue(NetworkStatus.AVAILABLE);
        } else {
            status.postValue(NetworkStatus.UNAVAILABLE);
        }
    }

    public LiveData<NetworkStatus> getNetworkStatus() {
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                status.postValue(NetworkStatus.AVAILABLE);
            }

            @Override
            public void onLost(@NonNull Network network) {
                status.postValue(NetworkStatus.UNAVAILABLE);
            }
        });

        return status;
    }
}
