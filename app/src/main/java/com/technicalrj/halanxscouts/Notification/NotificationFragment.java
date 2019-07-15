package com.technicalrj.halanxscouts.Notification;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.technicalrj.halanxscouts.Adapters.NotificationAdapter;
import com.technicalrj.halanxscouts.Notification.NoficationPojo.Notification;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    ArrayList<Notification> notificationArrayList;
    String key;
    NotificationAdapter adapter;
    ImageView noNotification;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Notification");
        View v = inflater.inflate(R.layout.fragment_notification, container, false);


        final SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);

        noNotification = v.findViewById(R.id.no_notification);

        RecyclerView rv_notiification = v.findViewById(R.id.rv_notifications);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        notificationArrayList = new ArrayList<>();
        adapter = new NotificationAdapter(getActivity(),notificationArrayList);
        rv_notiification.setAdapter(adapter);
        rv_notiification.setLayoutManager(lm);

        updateNotificationList();

        return v;
    }

    private void updateNotificationList() {


        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RetrofitAPIClient.DataInterface retrofitAPIClient = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<List<Notification>> call = retrofitAPIClient.getNotifications("Token "+key);
        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {



                ArrayList<Notification> notifications = (ArrayList<Notification>) response.body();
                if(notifications.size()==0){
                    noNotification.setVisibility(View.VISIBLE);
                }else {
                    noNotification.setVisibility(View.GONE);
                    notificationArrayList.addAll(notifications);
                    adapter.notifyDataSetChanged();
                }

//                Log.i("InfoText","notificationArrayList.size()"+ notificationArrayList.size());
//                ArrayList<Notification> notiList  = new ArrayList<>();
//                for (Notification notification:(ArrayList<Notification>) response.body()) {
//                    if(notification.getPayload()==null){
//                        notiList.add(notification);
//                    }
//                }
//                notificationArrayList.clear();


                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                t.printStackTrace();
                Log.i("InfoText","Throwable"+ t.getMessage());
                progressDialog.dismiss();
            }
        });


    }


}
