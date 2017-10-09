package com.example.apollinariia.litup.setup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.apollinariia.litup.AppActivity;
import com.example.apollinariia.litup.MainActivity;
import com.example.apollinariia.litup.R;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;
import com.example.apollinariia.litup.setup.DeviceSetupActivityFragment.FragmentSettings;

import bolts.Continuation;
import bolts.Task;

import static android.content.DialogInterface.*;

public class DeviceSetupActivity extends AppCompatActivity implements ServiceConnection, FragmentSettings {

    public final static String EXTRA_BT_DEVICE = "com.mbientlab.metawear.starter.DeviceSetupActivity.EXTRA_BT_DEVICE";

    public static class ReconnectDialogFragment extends DialogFragment implements ServiceConnection {
        private static final String KEY_BLUETOOTH_DEVICE = "com.mbientlab.metawear.starter.DeviceSetupActivity.ReconnectDialogFragment.KEY_BLUETOOTH_DEVICE";

        private ProgressDialog reconnectDialog = null;
        private BluetoothDevice btDevice = null;
        private MetaWearBoard currentMwBoard = null;

        public static ReconnectDialogFragment newInstance(BluetoothDevice btDevice) {
            Bundle args = new Bundle();
            args.putParcelable(KEY_BLUETOOTH_DEVICE, btDevice);

            ReconnectDialogFragment newFragment = new ReconnectDialogFragment();
            newFragment.setArguments(args);

            return newFragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            btDevice = getArguments().getParcelable(KEY_BLUETOOTH_DEVICE);
            getActivity().getApplicationContext().bindService(new Intent(getActivity(), BtleService.class), this, BIND_AUTO_CREATE);

            reconnectDialog = new ProgressDialog(getActivity());
            reconnectDialog.setTitle(getString(R.string.title_reconnect_attempt));
            reconnectDialog.setMessage(getString(R.string.message_wait));
            reconnectDialog.setCancelable(false);
            reconnectDialog.setCanceledOnTouchOutside(false);
            reconnectDialog.setIndeterminate(true);
            reconnectDialog.setButton(BUTTON_NEGATIVE, getString(android.R.string.cancel), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    currentMwBoard.disconnectAsync();
                    getActivity().finish();
                }
            });

            return reconnectDialog;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            currentMwBoard = ((BtleService.LocalBinder) service).getMetaWearBoard(btDevice);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    private BluetoothDevice btDevice;
    private MetaWearBoard metawear;

    private final String RECONNECT_DIALOG_TAG = "reconnect_dialog_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setup);

        btDevice = getIntent().getParcelableExtra(EXTRA_BT_DEVICE);
        getApplicationContext().bindService(new Intent(this, BtleService.class), this, BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_disconnect:
                metawear.disconnectAsync();
                finish();
                return true;
            case R.id.action_continue:
                Intent intent = new Intent(DeviceSetupActivity.this, AppActivity.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        metawear.disconnectAsync();
        super.onBackPressed();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        metawear = ((BtleService.LocalBinder) service).getMetaWearBoard(btDevice);
        metawear.onUnexpectedDisconnect(new MetaWearBoard.UnexpectedDisconnectHandler() {
            @Override
            public void disconnected(int status) {
                ReconnectDialogFragment dialogFragment = ReconnectDialogFragment.newInstance(btDevice);
                dialogFragment.show(getSupportFragmentManager(), RECONNECT_DIALOG_TAG);

                metawear.connectAsync().continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(Task<Void> task) throws Exception {
                        return task.isCancelled() || !task.isFaulted() ? task : MainActivity.reconnect(metawear);
                    }
                }).continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(Task<Void> task) throws Exception {
                        if (!task.isCancelled()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((DialogFragment) getSupportFragmentManager().findFragmentByTag(RECONNECT_DIALOG_TAG)).dismiss();
                                    ((DeviceSetupActivityFragment) getSupportFragmentManager().findFragmentById(R.id.device_setup_fragment)).reconnected();
                                }
                            });
                        } else {
                            finish();
                        }

                        return null;
                    }
                });
            }
        });
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {}

    @Override
    public BluetoothDevice getBtDevice() {
        return btDevice;
    }
}
