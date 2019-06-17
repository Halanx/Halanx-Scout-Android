package com.technicalrj.halanxscouts.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.Wallet.TaskPayment;

import java.util.ArrayList;




public class PaidAdapter extends RecyclerView.Adapter<PaidAdapter.PaymentViewHolder> {

    Context c;
    ArrayList<TaskPayment> taskPayments;


    public PaidAdapter(Context context, ArrayList<TaskPayment> taskPayments) {

        this.taskPayments = taskPayments;
        c = context;

        Log.i("InfoText","PaidAdapter");
    }

    @Override
    public PaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_payment_row, parent, false);
        PaymentViewHolder holder = new PaymentViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final PaymentViewHolder holder, final int position) {

        Log.i("InfoText","onBind"+taskPayments.get(0).getAmount());

        holder.tv_desc.setText(taskPayments.get(position).getDescription());
        holder.tv_amount.setText("₹ "+taskPayments.get(position).getAmount() );

        if(taskPayments.get(position).getPaidOn()!=null)
            holder.tv_date.setText(taskPayments.get(position).getPaidOn());


    }

    @Override
    public int getItemCount() {
//        Log.i("InfoText","getItemCount"+getItemCount());
        return taskPayments.size();
    }


    public class PaymentViewHolder extends RecyclerView.ViewHolder {

        TextView tv_desc,tv_amount,tv_date;

        public PaymentViewHolder(View itemView) {

            super(itemView);

            tv_desc = itemView.findViewById(R.id.description);
            tv_amount = itemView.findViewById(R.id.amount);
            tv_date = itemView.findViewById(R.id.date);


        }


    }

}
