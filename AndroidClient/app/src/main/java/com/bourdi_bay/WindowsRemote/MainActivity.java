package com.bourdi_bay.WindowsRemote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bourdi_bay.WindowsRemote.Settings.SettingsActivity;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.ConnectionStateListener {

    private Menu mMenu;
    private eStateConnection mConnectionState = eStateConnection.OFFLINE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        switch (mConnectionState) {
            case CONNECTED:
                onStateConnected();
                break;
            case OFFLINE:
                onStateOffline();
                break;
            case ERROR:
                onStateError();
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            case R.id.action_settings:
                Intent act = new Intent(this, SettingsActivity.class);
                TaskStackBuilder.create(this).addNextIntentWithParentStack(act).startActivities();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStateConnected() {
        mConnectionState = eStateConnection.CONNECTED;
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(R.id.action_conn_state);
            item.setIcon(android.R.drawable.presence_online);
        }
    }

    @Override
    public void onStateOffline() {
        mConnectionState = eStateConnection.OFFLINE;
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(R.id.action_conn_state);
            item.setIcon(android.R.drawable.presence_offline);
        }
    }

    @Override
    public void onStateError() {
        mConnectionState = eStateConnection.ERROR;
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(R.id.action_conn_state);
            item.setIcon(android.R.drawable.presence_busy);
        }
    }

    enum eStateConnection {CONNECTED, OFFLINE, ERROR}
}
