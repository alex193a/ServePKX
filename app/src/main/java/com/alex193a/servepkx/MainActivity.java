package com.alex193a.servepkx;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    AppCompatButton chooseFiles, sendFiles;
    EditText ip3ds;
    File[] pkxfiles;
    String host = "127.0.0.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip3ds = findViewById(R.id.ip3ds);
        chooseFiles = findViewById(R.id.chooseBtn);
        sendFiles = findViewById(R.id.sendBtn);

        chooseFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File file = new File(Environment.getExternalStorageDirectory(), "ServePKX");
                pkxfiles = file.listFiles();

            }
        });

        sendFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                host = ip3ds.getText().toString();

                if (!host.isEmpty() & !host.contains("127.0.0.1")) {
                    new sendToClient().execute();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.no_ip_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class sendToClient extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Client client = new Client(host);

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
