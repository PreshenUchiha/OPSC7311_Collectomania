package com.devops.collectomania.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devops.collectomania.Categorie;
import com.devops.collectomania.CategorieAdapter;
import com.devops.collectomania.Item;
import com.devops.collectomania.ItemAdapter;
import com.devops.collectomania.R;
import com.devops.collectomania.TempUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    RecyclerView recyclerView, recyclerViewItems;
    SearchView svItem;
    TextView collectionValue, recentItem , recentCategory;

    CategorieAdapter categorieAdapter;
    ItemAdapter itemAdapter;


    List<Categorie> categoryModelList = new ArrayList<>();
    List<Item> itemModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_home, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        recyclerView = view.findViewById(R.id.recyclerview);
        collectionValue = view.findViewById(R.id.txtCollectionValuePrice);
        recyclerViewItems = view.findViewById(R.id.recyclerviewitems);
        recentItem = view.findViewById(R.id.txtMyRecentItems);
        recentCategory = view.findViewById(R.id.txtMyRecentlyCategories);
        svItem = view.findViewById(R.id.searchView1);

        svItem.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.equals("")){
                    itemAdapter = new ItemAdapter(getActivity(),itemModelList);
                    recyclerViewItems.setAdapter(itemAdapter);
                    itemAdapter.notifyDataSetChanged();

                    categorieAdapter = new CategorieAdapter(getActivity(),categoryModelList);
                    recyclerView.setAdapter(categorieAdapter);
                    categorieAdapter.notifyDataSetChanged();

                    recentItem.setText("Recently Added Items");
                    recentCategory.setText("Recently Added Categories");
                }
                else{
                    filterList(newText);
                }
                return true;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.HORIZONTAL));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Category").child(TempUser.encodedEmail());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryModelList.clear();

                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    categoryModelList.add(snapshot1.getValue(Categorie.class));
                }


                recyclerView = view.findViewById(R.id.recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                categorieAdapter = new CategorieAdapter(getActivity(),categoryModelList);
                recyclerView.setAdapter(categorieAdapter);
                categorieAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                TempUser.Fullname = "User database unread";
            }
        });

        recyclerViewItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewItems.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.HORIZONTAL));


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User Recent Item").child(TempUser.encodedEmail());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemModelList.clear();

                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    itemModelList.add(snapshot1.getValue(Item.class));
                }

                double value = 0;
                for (Item i: itemModelList) {
                    value = value + (i.getValue()*i.getQuantity());
                }

                collectionValue.setText(String.valueOf(value));

                recyclerViewItems = view.findViewById(R.id.recyclerviewitems);
                recyclerViewItems.setLayoutManager(new LinearLayoutManager(getActivity()));

                itemAdapter = new ItemAdapter(getActivity(),itemModelList);
                recyclerViewItems.setAdapter(itemAdapter);
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void filterList(String text) {
        List<Item> filteredItemList = new ArrayList<>();
        List<Categorie> filteredCategorieList = new ArrayList<>();
        recentItem.setText("Recently Added Items");
        recentCategory.setText("Recently Added Categories");

        for (Item item: itemModelList) {
            if(item.getName().toLowerCase().contains(text.toLowerCase())){
                filteredItemList.add(item);
            }
        }

        for (Categorie c: categoryModelList) {
            if(c.getCatagory().toLowerCase().contains(text.toLowerCase())){
                filteredCategorieList.add(c);
            }
        }

        if(filteredCategorieList.isEmpty() && filteredItemList.isEmpty()){
            Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
        }
        else {
            if(filteredItemList.size()>=1){
                recentItem.setText("Search results found in Items");
                itemAdapter.setItemFilteredList(filteredItemList);
            }
            else {
                recentItem.setText("Recently Added Items");
                recyclerViewItems.setAdapter(itemAdapter);
                itemAdapter.notifyDataSetChanged();
            }
            if(filteredCategorieList.size()>=1){
                categorieAdapter.setCategorieFilteredList(filteredCategorieList);
                recentCategory.setText("Search results found in Categories");
            }
            else {
                categorieAdapter = new CategorieAdapter(getActivity(),categoryModelList);
                recyclerView.setAdapter(categorieAdapter);
                categorieAdapter.notifyDataSetChanged();
                recentCategory.setText("Recently Added Categories");
            }
        }

    }
}
/*
Coding With Dev,2020., Android RecyclerView With CardView and OnItemClickListener Example | RecyclerView OnClickListener
Code available : https://youtu.be/ZTg-oXaCgBk
Date accessed : [30/05/2022]
 */

/*
Coding With Dev,2019., How to Filter a ListView with SearchView - Android Studio | Android filter listview with searchview
Code available : https://youtu.be/o1ySXLSARh4
Date accessed : [31/05/2022]
 */
