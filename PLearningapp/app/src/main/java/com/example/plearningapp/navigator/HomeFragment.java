package com.example.plearningapp.navigator;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.plearningapp.R;
import com.example.plearningapp.func.add.AddDataActivity;

public class HomeFragment extends Fragment{
    ImageButton createData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        createData = view.findViewById(R.id.create_data_button);
        createData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddDataActivity.class));
            }
        });
        return view;
    }
}