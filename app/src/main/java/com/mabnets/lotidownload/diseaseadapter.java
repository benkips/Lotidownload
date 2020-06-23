package com.mabnets.lotidownload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class diseaseadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<diseases> dlist;



    public diseaseadapter(RecyclerView recyclerView, Context context, ArrayList<diseases> dlist) {
        this.context = context;
        this.dlist = dlist;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dfiles, parent, false);
                return new disviewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        disviewHolder Holder = (disviewHolder) holder;
        final diseases ds = (diseases) dlist.get(position);
        Holder.filz.setText(ds.photo);
        Holder.dnld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).mydownload("http://kilicom.mabnets.com/photos/" + ds.photo);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (dlist != null) {
            return dlist.size();
        }
        return 0;
    }

    public static class disviewHolder extends RecyclerView.ViewHolder {
        private TextView filz;
        private TextView dnld;

        public disviewHolder(@NonNull View itemView) {
            super(itemView);
            filz = (TextView) itemView.findViewById(R.id.fnm);
            dnld = (TextView) itemView.findViewById(R.id.fdnld);
        }
    }


}
