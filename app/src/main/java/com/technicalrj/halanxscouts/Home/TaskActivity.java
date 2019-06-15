package com.technicalrj.halanxscouts.Home;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.technicalrj.halanxscouts.Home.TaskFolder.ScheduledTask;
import com.technicalrj.halanxscouts.Home.TaskFolder.SubTask;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;
import com.technicalrj.halanxscouts.Wallet.TaskPayment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskActivity extends AppCompatActivity {

    int id;
    String key;
    ScheduledTask scheduledTask;
    TextView date , time ,address1 ,address2,address3 , customer_name;
    EditText remarks;
    ImageView house_pic ,customer_img ;
    LinearLayout sub_task_layout;
    CardView customer_card;
    Button done_button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        id = getIntent().getIntExtra("id",0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final SharedPreferences prefs =getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);


        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        house_pic = findViewById(R.id.house_pic);
        address1 = findViewById(R.id.address1);
        address2 = findViewById(R.id.address2);
        address3 = findViewById(R.id.address3);
        customer_name = findViewById(R.id.customer_name);
        customer_img = findViewById(R.id.customer_img);
        customer_card = findViewById(R.id.customer_card);
        sub_task_layout = findViewById(R.id.sub_task_layout);
        remarks = findViewById(R.id.remarks);
        done_button = findViewById(R.id.done_button);





        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<ScheduledTask> call = availabilityInterface.getTasksById(id,"Token "+key);
        call.enqueue(new Callback<ScheduledTask>() {
            @Override
            public void onResponse(Call<ScheduledTask> call, Response<ScheduledTask> response) {
                scheduledTask = response.body();
                updateTaskDetails();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ScheduledTask> call, Throwable t) {
                progressDialog.dismiss();
            }
        });


    }

    private void updateTaskDetails() {


        getSupportActionBar().setTitle(scheduledTask.getCategory().getName());

        String[] parts = scheduledTask.getScheduledAt().split(" ") ;
        date.setText(parts[0]+" "+parts[1].substring(0,3).toUpperCase());
        time.setText( parts[3]+" "+parts[4]);


        Picasso.get()
                .load( scheduledTask.getHouse().getCoverPicUrl().toString())
                .into(house_pic);


        address1.setText(scheduledTask.getHouse().getAddress().getStreetAddress());
        address2.setText(scheduledTask.getHouse().getAddress().getCity() +", "+ scheduledTask.getHouse().getAddress().getState());
        address3.setText(scheduledTask.getHouse().getAddress().getPincode());


        if(scheduledTask.getCustomer()==null){
            customer_card.setVisibility(View.INVISIBLE);
        }else {

            String firstName = scheduledTask.getCustomer().getUser().getFirstName();
            String lastName = scheduledTask.getCustomer().getUser().getLastName();
            customer_name.setText(firstName.substring(0,1).toUpperCase() + firstName.substring(1)  +" "+ lastName.substring(0,1).toUpperCase() + lastName.substring(1));

            Picasso.get()
                    .load( scheduledTask.getCustomer().getProfilePicThumbnailUrl())
                    .into(customer_img);

        }



        ArrayList<SubTask> subTaskArrayList = (ArrayList<SubTask>) scheduledTask.getSubTasks();
        for (int i = 0; i <subTaskArrayList.size() ; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.subtask_layout,null);

            CheckBox checkBox = view.findViewById(R.id.checkbox);
            TextView sub_task_desc = view.findViewById(R.id.sub_task_desc);
            sub_task_desc.setText(subTaskArrayList.get(i).getName());

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i("InfoText","checkAllTaskDone():"+checkAllTaskDone());
                    if(checkAllTaskDone()){
                        //enable button

                        done_button.setEnabled(true);
                        done_button.setBackgroundTintList(getResources().getColorStateList(R.color.colorEnabled));

                    }else {
                        //disable it

                        done_button.setEnabled(false);
                        done_button.setBackgroundTintList(getResources().getColorStateList(R.color.colorDisabled));

                    }

                }
            });

            sub_task_layout.addView(view);
        }



    }

    private boolean checkAllTaskDone() {

        for (int i = 0; i <sub_task_layout.getChildCount() ; i++) {
            LinearLayout linearLayout = (LinearLayout) sub_task_layout.getChildAt(i);
            CheckBox checkbox= (CheckBox) linearLayout.getChildAt(0);
            Log.i("InfoText","checkbox"+i+" :"+checkbox.isChecked());
            if(!checkbox.isChecked())
                return false;



        }
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }

    public void houseLocation(View view) {

        double latitute = scheduledTask.getHouse().getAddress().getLatitude();
        double longitute =scheduledTask.getHouse().getAddress().getLongitude();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:28.6514074,77.2400794?q="+latitute+","+longitute+"(House)"));
        startActivity(intent);

    }

    public void callCustomer(View view) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_VIEW );
            callIntent.setData(Uri.parse("tel:"+scheduledTask.getCustomer().getPhoneNo()));
            startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            Toast.makeText(this,"Calling a Phone Number Call failed",Toast.LENGTH_SHORT).show();
        }
    }

    public void chatCustomer(View view) {
        startActivity(new Intent(this,ChatWindow.class));
    }

    public void saveData(View view) {



        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<Void> call = availabilityInterface.setTaskComplete(id,true,remarks.getText().toString().trim(),"Token "+key);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();


                Log.i("InfoText","saveData succ:"+response.body());
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                Log.i("InfoText","saveData error:"+t.getMessage());
            }
        });




    }

    public void cancelTask(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        final ProgressDialog progressDialog = new ProgressDialog(TaskActivity.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();

                        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
                        Call<String> call = availabilityInterface.cancelTask(id,"Token "+key);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                progressDialog.dismiss();
                                Log.i("InfoText","cancelTask:"+response.body());

                                finish();

                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                progressDialog.dismiss();

                            }
                        });



                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to Cancel this task?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();



    }
}
