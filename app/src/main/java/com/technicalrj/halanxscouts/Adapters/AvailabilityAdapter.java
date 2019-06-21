package com.technicalrj.halanxscouts.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;


import com.technicalrj.halanxscouts.Home.AvailabiltyTime;
import com.technicalrj.halanxscouts.Home.HomeFragment;
import com.technicalrj.halanxscouts.R;

import java.util.ArrayList;

import static com.technicalrj.halanxscouts.Home.HomeFragment.fromTime;
import static com.technicalrj.halanxscouts.Home.HomeFragment.save_button;
import static com.technicalrj.halanxscouts.Home.HomeFragment.toTime;

public class AvailabilityAdapter extends BaseAdapter {


    private ArrayList<AvailabiltyTime> availibilityList;
    private Context mContext;
    public String selectedDate;
    public boolean isDateSelected;

    public AvailabilityAdapter(Context c,ArrayList<AvailabiltyTime> availibilityList) {
        this.availibilityList = availibilityList;
        mContext = c;

    }

    public int getCount() {
        return availibilityList.size();
    }

    public Object getItem(int position) {
        return availibilityList.get(position);
    }

    // Require for structure, not really used in my code. Can
    // be used to get the id of an item in the adapter for
    // manual control.
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {





        TextView day,date,month;


        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.seven_dates_row, null);
        }


        day = (TextView) convertView.findViewById(R.id.day);
        date = (TextView) convertView.findViewById(R.id.date);
        month = (TextView) convertView.findViewById(R.id.month);


        day.setText(availibilityList.get(position).getDay());
        date.setText(availibilityList.get(position).getDate());
        month.setText(availibilityList.get(position).getMonth());


        final View finalConvertView = convertView;


        if(selectedDate!=null && ( selectedDate.split(" ")[0].equals(availibilityList.get(position).getDate()))) {

            convertView.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorFace));

        }



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isDateSelected = true;
                selectedDate = availibilityList.get(position).getFullDate();


                View singleView;
                for (int i = 0; i < 7; i++) {
                    singleView = parent.getChildAt(i);
                    if(singleView.getBackgroundTintList()==mContext.getResources().getColorStateList(R.color.colorFace)){
                        singleView.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorGrey));
                        break;
                    }
                }
                finalConvertView.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorFace));

                if(fromTime && toTime && isDateSelected){
                    save_button.setEnabled(true);
                    save_button.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorGreen));
                }

            }
        });

        return convertView;
    }
}
