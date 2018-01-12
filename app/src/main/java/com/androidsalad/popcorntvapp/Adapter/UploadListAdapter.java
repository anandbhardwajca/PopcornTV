package com.androidsalad.popcorntvapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsalad.popcorntvapp.Model.UploadItem;
import com.androidsalad.popcorntvapp.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.MyViewHolder> {


    private Context context;
    private List<UploadItem> uploadItemList;


    public UploadListAdapter(Context context) {
        this.context = context;
        this.uploadItemList = new ArrayList<>();
    }

    public void updateList(List<UploadItem> uploadItemList) {
        this.uploadItemList = uploadItemList;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_image_item, parent, false);

        return new MyViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.setData(uploadItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return uploadItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView, statusImageView;
        private TextView imageNameTextView;

        public MyViewHolder(View itemView) {
            super(itemView);

            findViews(itemView);
        }

        private void findViews(View itemView) {

            imageView = (ImageView) itemView.findViewById(R.id.singleImageItemImageView);
            statusImageView = (ImageView) itemView.findViewById(R.id.singleImageItemStatusImageView);
            imageNameTextView = (TextView) itemView.findViewById(R.id.singleImageItemNameTextView);

        }

        public void setData(UploadItem item) {

            Glide.with(context).load(item.getImageUrl()).into(imageView);
            if (item.getUploadStatus()) {
                Glide.with(context).load(R.drawable.check).into(statusImageView);
            } else {
                Glide.with(context).load(R.drawable.circle).into(statusImageView);
            }

            imageNameTextView.setText(item.getItemName());

        }

    }

}
