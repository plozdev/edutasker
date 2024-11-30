package com.example.plearningapp.file.add;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plearningapp.R;

import java.util.List;

public class FileAdapterAdd extends RecyclerView.Adapter<FileAdapterAdd.FileViewHolder> {

    private List<String> fileList;

    public FileAdapterAdd(List<String> fileList) {
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_list_item, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        String fileName = fileList.get(position);
        holder.textViewFileName.setText(fileName);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
    public void updateData(List<String> newFileUploadList) {
        this.fileList.clear();
        this.fileList.addAll(newFileUploadList);
        notifyDataSetChanged();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFileName;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFileName = itemView.findViewById(R.id.file_name_tv);
        }
    }
}
