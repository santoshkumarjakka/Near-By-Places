package com.example.demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.demo.modal.PlacesDetails_Modal;
import com.example.demo.easyroads.R;
import com.example.demo.adapter.Rv_adapter.MyViewHolder;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.demo.easyroads.R.id.distance;

/**
 * Created by samarthkejriwal on 10/08/17.
 */

public class Rv_adapter extends RecyclerView.Adapter<MyViewHolder> {

    private ArrayList<PlacesDetails_Modal> storeModels;
    private Context context;
    private String current_address;



    public Rv_adapter(Context context ,ArrayList<PlacesDetails_Modal> storeModels,String current_address)
    {

        this.context = context;
        this.storeModels = storeModels;
        this.current_address = current_address;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_listitem, parent, false);

            return new MyViewHolder(itemView,viewType);
    }

    @Override
    public int getItemViewType(int position) {

return position;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
                holder.res_name.setText(storeModels.get(holder.getAdapterPosition()).name);

                Picasso.with(context).load(storeModels.get(holder.getAdapterPosition()).photourl)
                        .placeholder(R.drawable.placeholder).into(holder.res_image);


                holder.res_address.setText(storeModels.get(holder.getAdapterPosition()).address);

                if(storeModels.get(holder.getAdapterPosition()).phone_no == null)
                {
                    holder.res_phone.setText("N/A");
                }
                else  holder.res_phone.setText(storeModels.get(holder.getAdapterPosition() ).phone_no);

                holder.res_rating.setText(String.valueOf(storeModels.get(holder.getAdapterPosition() ).rating));

                holder.res_distance.setText(storeModels.get(holder.getAdapterPosition() ).distance);


    }

    @Override
    public int getItemCount() {

        return  storeModels.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView res_name;
        TextView res_rating;
        TextView res_address;
        TextView res_phone;
        TextView res_distance;
        ImageView res_image;


        public MyViewHolder(final View itemView, final int viewType) {
            super(itemView);

                this.res_name = (TextView) itemView.findViewById(R.id.name);
                this.res_rating = (TextView) itemView.findViewById(R.id.rating);
                this.res_address = (TextView) itemView.findViewById(R.id.address);
                this.res_phone = (TextView) itemView.findViewById(R.id.phone);
                this.res_distance = (TextView) itemView.findViewById(distance);
                this.res_image = (ImageView) itemView.findViewById(R.id.res_image);
            }


    }
}
