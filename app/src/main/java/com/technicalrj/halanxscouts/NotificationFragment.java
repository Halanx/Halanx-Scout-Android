package com.technicalrj.halanxscouts;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.technicalrj.halanxscouts.Adapters.NotificationAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Notification");
        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        RecyclerView rv_notiification = v.findViewById(R.id.rv_notifications);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        NotificationAdapter adapter = new NotificationAdapter(getActivity());
        rv_notiification.setAdapter(adapter);
        rv_notiification.setLayoutManager(lm);

        return v;
    }

}
