package com.example.viktoriasemina.asynch_task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> subList;
    ProgressBar progressBar;
    TextView tvProgress;
    Context context;
    ArrayList<String> newlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subList = new ArrayList<>();

        subList.add("COMPP744");
        subList.add("COMP715");
        subList.add("COMP743");
        subList.add("CCOMP742");
        subList.add("COMP745");

        context = this;
    }

    class myTasks extends AsyncTask<Void, String, Void> {

        ArrayAdapter<String> adapter;
        int counter=0;

        @Override
        protected void onPreExecute() {
            adapter = (ArrayAdapter)listView.getAdapter();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                progressBar.setMin(0);
            }
            progressBar.setMax(10000);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(context, "Subjects have been loaded", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            adapter.add(values[0]);

            int progress = (int)(((double)counter/newlist.size())*10000);
            int progress2 = (int)(((double)counter/newlist.size())*100);
            progressBar.setProgress(progress);

            tvProgress.setText(String.valueOf(progress2) + "%");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (String sub:newlist) {
                try {
                    Thread.sleep(1000);
                    counter++;
                } catch (InterruptedException e){
                    e.printStackTrace();

                }
                publishProgress(sub);
            }
            return null;
        }
    }

    public static void write(Context context, Object nameOfClass) {
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "serialization");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = "List2.txt";
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(directory
                    + File.separator + filename));
            out.writeObject(nameOfClass);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> read(Context context) {

        ObjectInputStream input = null;
        ArrayList<String> ReturnClass = null;
        String filename = "List2.txt";
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "serialization");
        try {

            input = new ObjectInputStream(new FileInputStream(directory
                    + File.separator + filename));
            ReturnClass = (ArrayList<String>) input.readObject();
            input.close();

        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ReturnClass;
    }

    public void saveData(View view) {
        write(context, subList);
        Toast.makeText(context, "Subjects have been saved", Toast.LENGTH_LONG).show();
    }

    public void loadData(View view) {

        newlist = read(context);
        listView = findViewById(R.id.listViewSubject);
        progressBar = findViewById(R.id.progressSub);
        progressBar.setProgress(0);
        progressBar.setIndeterminate(false);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
        new myTasks().execute();
        tvProgress = findViewById(R.id.tvProgress);
    }
}
