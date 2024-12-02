package com.example.plearningapp.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private String getFileExtension(String name) {
        return name.substring(name.lastIndexOf("."));
    }
    @Override
    public void onBindViewHolder(@NonNull FileAdapterMain.FileViewHolder holder, int position) {
        FileModelMain fileModel = fileList.get(position);
        holder.textFileName.setText(fileModel.getName());
        holder.textFileDate.setText(fileModel.getDate());

        String extension = getFileExtension(fileModel.getName());
        if (extension.equals(".pdf")) {
            holder.fileIcon.setImageResource(R.drawable.pdf);
        } else if (extension.equals(".doc") || extension.equals(".docx")) {
            holder.fileIcon.setImageResource(R.drawable.word);
        } else if (extension.equals(".ppt") || extension.equals(".pptx")) {
            holder.fileIcon.setImageResource(R.drawable.pptx);
        } else if (extension.equals(".xls") || extension.equals(".xlsx")) {
            holder.fileIcon.setImageResource(R.drawable.excel);
        } else if (extension.equals(".txt")) {
            holder.fileIcon.setImageResource(R.drawable.txt);
        } else {
            holder.fileIcon.setImageResource(R.drawable.file);
        }

    }
    @Override
    public int getItemCount() {
        return 0;
    }
    public void updateData(List<FileModelMain> newFileUploadList) {
//        this.fileList.clear();
        fileList.addAll(newFileUploadList);
    }
    static class FileViewHolder extends RecyclerView.ViewHolder {
        ImageView fileIcon;
        TextView textFileName;
        TextView textFileDate;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileIcon = itemView.findViewById(R.id.file_icon);
            textFileName = itemView.findViewById(R.id.text_file_name);
            textFileDate = itemView.findViewById(R.id.text_file_date);
        }
    }
}
