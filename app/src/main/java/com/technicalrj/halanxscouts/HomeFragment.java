package com.technicalrj.halanxscouts;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.technicalrj.halanxscouts.Adapters.TaskAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");



        View v =  inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView task_recycler = v.findViewById(R.id.task_recycler);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        TaskAdapter adapter = new TaskAdapter(getActivity());


        task_recycler.setLayoutManager(lm);
        task_recycler.setAdapter(adapter);

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_scout_schedule);

        dialog.show();
        dialog.dismiss();


        return v;



    }

}
