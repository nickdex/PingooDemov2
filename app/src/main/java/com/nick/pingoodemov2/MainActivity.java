package com.nick.pingoodemov2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.blunderer.materialdesignlibrary.activities.ViewPagerWithTabsActivity;
import com.blunderer.materialdesignlibrary.handlers.ActionBarDefaultHandler;
import com.blunderer.materialdesignlibrary.handlers.ActionBarHandler;
import com.blunderer.materialdesignlibrary.handlers.ViewPagerHandler;

public class MainActivity extends ViewPagerWithTabsActivity implements ContactFragment.OnContactFragmentInteractionListener, MusicFragment.OnMusicFragmentInteractionListener{


    private final int PERM_RQ = 20;
    private String[] permissions = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final String TAG = "Main_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Activity Created");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED)
            {
                requestPermissions(permissions, PERM_RQ);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        boolean check = false;
        if (grantResults.length != 0)
        {
            for (int grantResult : grantResults)
            {
                if (grantResult == PackageManager.PERMISSION_DENIED)
                {
                    check = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        requestPermissions(permissions, PERM_RQ);
                    }
                } else
                {
                    check = true;
                }
            }

            if (check)
            {
                Log.i(TAG, "Everything's cool, Onwards");
                startService(new Intent(this, CustomService.class));
            }
        }
    }

    @Override
    protected boolean expandTabs() {
        return true;
    }

    @Override
    protected boolean enableActionBarShadow() {
        return false;
    }

    @Override
    protected ActionBarHandler getActionBarHandler() {
        return new ActionBarDefaultHandler(this);
    }

    @Override
    public ViewPagerHandler getViewPagerHandler() {
        return new ViewPagerHandler(this)
                .addPage(R.string.music_fragment, MusicFragment.newInstance())
                .addPage(R.string.contact_fragmnet, ContactFragment.newInstance());
    }

    @Override
    public int defaultViewPagerPageSelectedPosition() {
        return 0;
    }

    @Override
    public void onContactFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMusicFragmentInteraction(Uri uri) {

    }
}
