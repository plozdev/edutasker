package com.example.plearningapp.todo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.plearningapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final ArrayList<Task> taskList;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = mAuth.getCurrentUser();

    public TaskAdapter(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textViewTask.setText(task.getName());
        boolean isExpanded = task.isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.textViewSubject.setText(task.getSubject());
        holder.textViewDate.setText(task.getDate());

        holder.btnExpand.setOnClickListener(v -> {
            task.setExpanded(!task.isExpanded());
            notifyItemChanged(position); // Notify adapter to refresh this item
        });

        holder.cancelTask.setOnClickListener(v -> {
            taskList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, taskList.size());
        });
    }
    @Override
    public int getItemCount() {
        return taskList.size();
    }
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTask, textViewSubject, textViewDate;
        ImageView cancelTask,btnExpand;
        LinearLayout expandableLayout;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTask = itemView.findViewById(R.id.textViewTask);
            cancelTask = itemView.findViewById(R.id.cancel_task);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            btnExpand = itemView.findViewById(R.id.btnExpand);
            textViewSubject = itemView.findViewById(R.id.additional_subject);
            textViewDate = itemView.findViewById(R.id.additional_date);
        }
    }
}
