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

public class CategorieAdapter extends RecyclerView.Adapter<CategorieAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private List<Categorie> categories;
    private List<Categorie> getCategoriesListFiltered;
    private String SelectedCategory;
    private final Context contextdb;

    public CategorieAdapter(Context context, List<Categorie> userCategories){
        this.categories = userCategories;
        this.layoutInflater = LayoutInflater.from(context);
        this.contextdb = context;
    }

    public void setCategorieFilteredList(List<Categorie> filteredList){
        this.categories = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.custom_card_view,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Categorie categorie = categories.get(position);
        holder.CategoryName.setText(categorie.getCatagory());
        holder.Goal.setText(String.valueOf(categorie.getGoal()));
        Picasso.with(contextdb).load(categorie.getImageuri()).into(holder.recentCatImage);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        TextView CategoryName,Goal;
        ImageView recentCatImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CategoryName = itemView.findViewById(R.id.txtRecentCatName);
            recentCatImage = itemView.findViewById(R.id.recentCatImage);
            Goal = itemView.findViewById(R.id.txtGoalValue);
        }
    }

}

