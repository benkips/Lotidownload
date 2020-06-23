package com.mabnets.lotidownload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.tonyodev.fetch2.AbstractFetchListener;
import com.tonyodev.fetch2.DefaultFetchNotificationManager;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Status;
import com.tonyodev.fetch2core.Downloader;
import com.tonyodev.fetch2core.Func;
import com.tonyodev.fetch2okhttp.OkHttpDownloader;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kotlin.Pair;

public class index extends AppCompatActivity implements ActionListener {

    private static final long UNKNOWN_REMAINING_TIME = -1;
    private static final long UNKNOWN_DOWNLOADED_BYTES_PER_SECOND = 0;
    private static final int GROUP_ID = "listGroup".hashCode();
    static final String FETCH_NAMESPACE = "DownloadListActivity";

    private FileAdapter fileAdapter;
    private Fetch fetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        setUpViews();
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(3)
                .build();

        fetch = Fetch.Impl.getInstance(fetchConfiguration);
//        enqueueDownloads();
    }

    private void setUpViews() {
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fileAdapter = new FileAdapter(this);
        recyclerView.setAdapter(fileAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetch.getDownloads(new Func<List<Download>>() {
            @Override
            public void call(@NotNull List<Download> result) {
                final ArrayList<Download> list = new ArrayList<>(result);
              /*  Collections.sort(list, new Comparator<Download>() {
                    @Override
                    public int compare(Download first, Download second) {
                        return Long.compare(first.getCreated(), second.getCreated());

                    }
                });*/
                for (Download download : list) {
                    fileAdapter.addDownload(download);
                }
            }
        }).addListener(fetchListener);


    }

    @Override
    protected void onPause() {
        super.onPause();
        fetch.removeListener(fetchListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fetch.close();
    }

    private final FetchListener fetchListener = new AbstractFetchListener() {
        @Override
        public void onAdded(@NotNull Download download) {
            fileAdapter.addDownload(download);
        }

        @Override
        public void onQueued(@NotNull Download download, boolean waitingOnNetwork) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onCompleted(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {
            super.onError(download, error, throwable);
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onProgress(@NotNull Download download, long etaInMilliseconds, long downloadedBytesPerSecond) {
            fileAdapter.update(download, etaInMilliseconds, downloadedBytesPerSecond);
        }

        @Override
        public void onPaused(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onResumed(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onCancelled(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onRemoved(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onDeleted(@NotNull Download download) {
            fileAdapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }
    };


    private void enqueueDownloads() {
        fetch.getDownloadsWithStatus(Status.COMPLETED,new Func<List<Download>>() {
            @Override
            public void call(@NotNull List<Download> result) {
                String sixe=String.valueOf(result.size());
                Toast.makeText(index.this, sixe, Toast.LENGTH_SHORT).show();
                final ArrayList<Download> list = new ArrayList<>(result);
                for (Download download : list) {
                    fileAdapter.addDownload(download);
                }

            }
        }).addListener(fetchListener);

    }

    @Override
    public void onPauseDownload(int id) {
        fetch.pause(id);
    }

    @Override
    public void onResumeDownload(int id) {
        fetch.resume(id);
    }

    @Override
    public void onRemoveDownload(int id) {
        fetch.remove(id);
    }

    @Override
    public void onRetryDownload(int id) {
        fetch.retry(id);
    }


}
