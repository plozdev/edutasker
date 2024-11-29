package com.example.plearningapp.file;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plearningapp.R;

import java.util.List;

public class FileAdapterMain extends RecyclerView.Adapter<FileAdapterMain.FileViewHolder> {

    private List<FileModelMain> fileList;
    public FileAdapterMain(List<FileModelMain> fileList) {
        this.fileList = fileList;
    }
    @NonNull
    @Override
    public FileAdapterMain.FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_file_main, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileAdapterMain.FileViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView textFileName;
        TextView textFileDate;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            textFileName = itemView.findViewById(R.id.text_file_name);
            textFileDate = itemView.findViewById(R.id.text_file_date);
        }
    }
}
