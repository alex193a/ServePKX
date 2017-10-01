package com.alex193a.servepkx;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    AppCompatButton chooseFiles;
    FloatingActionButton sendFiles;
    TextInputEditText ip3ds;
    RecyclerView mRecyclerView;
    File[] pkxfiles;
    String host = "127.0.0.1";
    String REGEX_VALID_IP = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip3ds = findViewById(R.id.ip3ds);
        chooseFiles = findViewById(R.id.chooseBtn);
        sendFiles = findViewById(R.id.sendBtn);
        mRecyclerView = findViewById(R.id.pkmnIds);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        chooseFiles.setOnClickListener(new View.OnClickListener() {
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

        sendFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                host = ip3ds.getText().toString();

                if (checkIP()) {
                    new sendToClient().execute();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.no_ip_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean checkIP() { return !host.isEmpty() & !host.contains("127.0.0.1") & host.matches(REGEX_VALID_IP); }

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
