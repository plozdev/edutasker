package com.example.plearningapp.main;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.plearningapp.R;
import java.util.ArrayList;
import java.util.List;

public class FileAdapterTodo extends RecyclerView.Adapter<FileAdapterTodo.FileViewHolder> {

    private List<String> fileList = new ArrayList<>();

    public FileAdapterTodo() {

    }

    @NonNull
    @Override
    public FileAdapterTodo.FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_todo_main, parent, false);
        return new FileAdapterTodo.FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileAdapterTodo.FileViewHolder holder, int position) {

        holder.title.setText(fileList.get(position));
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
    public void setUpFileList(List<String> newFileList) {
        fileList.clear();
        fileList.addAll(newFileList);
    }
    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_task_main);
        }
    }

}
