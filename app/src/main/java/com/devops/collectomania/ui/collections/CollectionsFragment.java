package com.devops.collectomania.ui.collections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.devops.collectomania.R;
import com.devops.collectomania.TempUser;
import com.devops.collectomania.ui.editCollection.EditCollectionFragment;
import com.devops.collectomania.ui.items.ItemsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CollectionsFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    ListView listview;
    FloatingActionButton fabEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_collections, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        listview = (ListView) view.findViewById(R.id.listviewCollections);
        fabEdit = view.findViewById(R.id.fab);

        ArrayList<String> CategoryArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Category").child(TempUser.encodedEmail());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CategoryArrayList.clear();

                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    CategoryArrayList.add(snapshot1.getKey());
                }



                if (getActivity()!=null) {
                    ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_expandable_list_item_1, CategoryArrayList);
                    listview.setAdapter(arrayAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // can use switch statements for intents
                // i is the index clicked
                fabEdit.setVisibility(View.GONE);

                TempUser.SelectedCollection = CategoryArrayList.get(i);

                ItemsFragment itemsFragment = new ItemsFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.myCollectionsLayout, itemsFragment,itemsFragment.getTag()).addToBackStack("collection").commit();

            }
        });

        fabEdit.setOnClickListener(view1 -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.myCollectionsLayout,new EditCollectionFragment(),"editCollection").commit();
        });

    }
}