package com.alex193a.servepkx;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;

import java.io.File;
import java.util.ArrayList;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements FileChooserDialog.FileCallback{

    AppCompatButton loadFiles, chooseFiles;
    FloatingActionButton sendFiles;
    TextInputEditText ip3ds;
    RecyclerView mRecyclerView;
    File[] pkxfiles;
    String host = "";
    String REGEX_VALID_IP = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip3ds = findViewById(R.id.ip3ds);
        loadFiles = findViewById(R.id.loadDefaultBtn);
        chooseFiles = findViewById(R.id.chooseBtn);
        sendFiles = findViewById(R.id.sendBtn);
        mRecyclerView = findViewById(R.id.pkmnIds);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            } else {
                loadFiles.setEnabled(true);
                chooseFiles.setEnabled(true);
            }
        }

        loadFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File file = new File(Environment.getExternalStorageDirectory(), "ServePKX");
                pkxfiles = file.listFiles();
                ArrayList<String> pkmnFilesName = new ArrayList<>();
                for (File pkxfile : pkxfiles) {
                    pkmnFilesName.add(pkxfile.getName());
                }

                mRecyclerView.setAdapter(new SimpleRVAdapter(pkmnFilesName.toArray(new String[pkmnFilesName.size()])));
            }
        });

        chooseFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new FileChooserDialog.Builder(MainActivity.this)
                        .initialPath(Environment.getExternalStorageDirectory().getPath() + "/Download")
                        .extensionsFilter(".pk6", ".pk7")
                        .show(MainActivity.this);

            }
        });

        sendFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                host = ip3ds.getText().toString();

                if (checkIP()) {
                    new MaterialDialog.Builder(MainActivity.this)
                            .title(getString(R.string.attention_dialog_title))
                            .content(getString(R.string.confirm_send, pkxfiles.length))
                            .negativeText(getString(R.string.no_upper))
                            .positiveText(getString(R.string.yes_upper))
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                    materialDialog.dismiss();
                                }
                            })
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                    new sendToClient().execute();
                                }
                            }).show();

                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.no_ip_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean checkIP() {
        return host.matches(REGEX_VALID_IP);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 193);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 193: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    loadFiles.setEnabled(true);
                    chooseFiles.setEnabled(true);

                } else {

                    if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {

                        new MaterialDialog.Builder(this)
                                .title(getString(R.string.attention_dialog_title))
                                .content(getString(R.string.permissions_error))
                                .negativeText(getString(R.string.no_upper))
                                .positiveText(getString(R.string.yes_upper))
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        finish();
                                    }
                                })
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 193);
                                    }
                                })
                                .show();
                    } else {
                        new MaterialDialog.Builder(this)
                                .title(getString(R.string.attention_dialog_title))
                                .content(getString(R.string.permissions_error_2))
                                .negativeText(getString(R.string.no_upper))
                                .positiveText(getString(R.string.yes_upper))
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        finish();
                                    }
                                })
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }
                }
                return;
            }

        }

    }

    @Override
    public void onFileSelection(@NonNull FileChooserDialog fileChooserDialog, @NonNull File file) {
        mRecyclerView.invalidate();
        mRecyclerView.setAdapter(new SimpleRVAdapter(new String[] {file.getName()}));
    }

    @Override
    public void onFileChooserDismissed(@NonNull FileChooserDialog fileChooserDialog) {

    }

    private class sendToClient extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Client client = new Client(host.trim());

            for (File pkxfile : pkxfiles) {
                client.sendPKM(pkxfile);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(MainActivity.this, getString(R.string.files_sent_toast), Toast.LENGTH_SHORT).show();

        }
    }

}
