package com.devops.collectomania;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private List<Item> items;
    private final Context contextdb;

    public ItemAdapter(Context context, List<Item> Items){
        this.items = Items;
        this.layoutInflater = LayoutInflater.from(context);
        this.contextdb = context;
    }

    public void setItemFilteredList(List<Item> filteredList){
        this.items = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.custom_card_view_items,parent,false);
        return new ItemAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.recentName.setText(item.name);
        holder.recentCategory.setText(item.category);
        holder.recentPrice.setText("R "+ item.value);
        holder.recentDate.setText(" - "+item.doPurchase);
        holder.recentQty.setText(String.valueOf(item.quantity));
        Picasso.with(contextdb).load(item.getItemUri()).into(holder.recentImage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        TextView recentName,recentDate,recentQty,recentPrice,recentCategory;
        ImageView recentImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recentName = itemView.findViewById(R.id.txtRecentItemName);
            recentImage = itemView.findViewById(R.id.ivRecentItemImage);
            recentDate = itemView.findViewById(R.id.txtRecentItemDate);
            recentQty = itemView.findViewById(R.id.txtRecentItemQtyValue);
            recentCategory = itemView.findViewById(R.id.txtRecentItemCategory);
            recentPrice = itemView.findViewById(R.id.txtRecentItemPrice);
        }
    }
}