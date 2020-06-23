package com.mabnets.lotidownload;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kosalgeek.android.json.JsonConverter;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;
import com.tonyodev.fetch2core.FetchObserver;
import com.tonyodev.fetch2core.Func;
import com.tonyodev.fetch2core.Reason;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mabnets.lotidownload.App.CHANNEL_1_ID;

/*implements FetchListener*/

public class MainActivity extends AppCompatActivity implements FetchListener {
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_WRITING_PERMISSION = 200;
    private static String fileName = null;
    private Fetch fetch;
    private RecyclerView rv;
    private ProgressDialog progressDialog;
    private Mycommand mycommand;
    final String Tag = this.getClass().getName();
    private NotificationManagerCompat notificationManager;
    private FloatingActionButton fb;
    private   ArrayList<diseases> a2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_WRITING_PERMISSION);
        rv = findViewById(R.id.rvd);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading");
        fb=findViewById(R.id.floatingActionButton);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,index.class));
            }
        });
        mycommand = new Mycommand(MainActivity.this);
        notificationManager=NotificationManagerCompat.from(MainActivity.this);

        rv.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        rv.setLayoutManager(manager);
        loadiseases();



    }


    public void mydownload(String url) {
        String f = getNameFromUrl(url);
        String fileName = Environment.getExternalStorageDirectory() + File.separator + "lotidownload/";
        //Create  folder if it does not exist
        File exportDir = new File(fileName);
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        String[] separated = f.split("\\.");
        String nm = separated[0];
        String ext = separated[1];

        File file = new File(fileName, nm + "." + ext);
        String fullpath = file.getAbsolutePath();

        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(3)
                .build();

        final Request request = new Request("http://kilicom.mabnets.com/photos/VIDEO_20191114_185728.mp4", fullpath);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);

        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        fetch.enqueue(request, new Func<Request>() {
            @Override
            public void call(@NotNull Request result) {
                Toast.makeText(MainActivity.this, "Call is made", Toast.LENGTH_SHORT).show();
            }
        }, new Func<Error>() {
            @Override
            public void call(@NotNull Error result) {
                Toast.makeText(MainActivity.this, "Error:" + result.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        fetch.addListener(this);
    }

    private String getNameFromUrl(final String url) {
        return Uri.parse(url).getLastPathSegment();
    }


    @Override
    public void onAdded(@NotNull Download download) {
        shownotification(download.getId());
    }

    Notification.Builder builder;
    NotificationManagerCompat nmc;

    private void shownotification(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            Bitmap myicon= BitmapFactory.decodeResource(getResources(),R.drawable.tips);
            Notification notification=new NotificationCompat.Builder(MainActivity.this,CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_perm_contact_calendar_black_24dp)
                    .setContentTitle("Downloading File")
                    .setProgress(100, 0, false)
                    .setAutoCancel(false)
                    .setLargeIcon(myicon)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();

            notificationManager.notify(id,notification);
        }
    }

    @Override
    public void onCancelled(@NotNull Download download) {

    }

    @Override
    public void onCompleted(@NotNull Download download) {
        Toast.makeText(getApplicationContext(), "Download complete", Toast.LENGTH_SHORT).show();

            Bitmap myicon= BitmapFactory.decodeResource(getResources(),R.drawable.tips);
            Notification notification=new NotificationCompat.Builder(MainActivity.this,CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_perm_contact_calendar_black_24dp)
                    .setContentText("Download complete")
                    .setProgress(0, 0, false)
                    .setAutoCancel(false)
                    .setLargeIcon(myicon)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
            notificationManager.notify(download.getId(),notification);

    }

    @Override
    public void onDeleted(@NotNull Download download) {

    }

    @Override
    public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

    }

    @Override
    public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {

    }

    @Override
    public void onPaused(@NotNull Download download) {

    }

    @Override
    public void onProgress(@NotNull Download download, long l, long l1) {
        int progress = download.getProgress();
        Toast.makeText(this, String.valueOf(progress), Toast.LENGTH_SHORT).show();

        Bitmap myicon= BitmapFactory.decodeResource(getResources(),R.drawable.tips);
        Notification notification=new NotificationCompat.Builder(MainActivity.this,CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_perm_contact_calendar_black_24dp)
                .setContentText("Downloading..:"+String.valueOf(progress)+"%")
                .setProgress(100, progress, false)
                .setAutoCancel(false)
                .setLargeIcon(myicon)
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        notificationManager.notify(download.getId(),notification);
      /*  builder.setProgress(0, progress, false);
        nmc.notify(download.getId(), builder.build());*/
    }

    @Override
    public void onQueued(@NotNull Download download, boolean b) {

    }

    @Override
    public void onRemoved(@NotNull Download download) {

    }

    @Override
    public void onResumed(@NotNull Download download) {

    }

    @Override
    public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {

    }

    @Override
    public void onWaitingNetwork(@NotNull Download download) {

    }

    private void loadiseases() {
        String url = "http://kilicom.mabnets.com/downloadtest.php";
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(Tag + "k", response);
                if (!response.isEmpty()) {
                    Log.d(Tag, response);
                    final ArrayList<diseases> slist = new JsonConverter<diseases>().toArrayList(response, diseases.class);
                    a2=slist;
                    final diseaseadapter adapter = new diseaseadapter(rv,MainActivity.this, slist);
                    rv.setAdapter(adapter);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error instanceof TimeoutError) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "error time out ", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setMessage("please check your internet connectivity");
                    alert.setNeutralButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
                    alert.show();
                } else if (error instanceof NoConnectionError) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "error no connection", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setMessage("please check your internet connectivity");
                    alert.setNeutralButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
                    alert.show();
                } else if (error instanceof NetworkError) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "error network error", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setMessage("please check your internet connectivity");
                    alert.setNeutralButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
                    alert.show();
                } else if (error instanceof AuthFailureError) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "errorin Authentication", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "error while parsing", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "error  in server", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ClientError) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "error with Client", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "error while loading", Toast.LENGTH_SHORT).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                /*params.put("phonen",phn);*/

                return params;
            }
        };
        mycommand.add(stringRequest);
        progressDialog.show();
        mycommand.execute();
        mycommand.remove(stringRequest);
    }

}
