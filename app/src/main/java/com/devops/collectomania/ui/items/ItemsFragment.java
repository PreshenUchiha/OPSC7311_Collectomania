package com.devops.collectomania.ui.items;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.devops.collectomania.R;
import com.devops.collectomania.TempUser;
import com.devops.collectomania.ui.collections.CollectionsFragment;
import com.devops.collectomania.ui.viewItemDesc.ViewItemDescFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ItemsFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    ListView ItemsListView;
    ImageView ivBackItem;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_items, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        ItemsListView = view.findViewById(R.id.listviewItems);
        ivBackItem = view.findViewById(R.id.ivBackItem);

        ArrayList<String> ItemsArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User Item").child(TempUser.encodedEmail()).child(TempUser.SelectedCollection);

        ivBackItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.ItemLayout,new CollectionsFragment(),"collection")
                        .commit();

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ItemsArrayList.clear();

                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    ItemsArrayList.add(snapshot1.getKey());
                }



                if (getActivity()!=null) {
                    ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_expandable_list_item_1,ItemsArrayList);

                    ItemsListView.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // can use switch statements for intents
                // i is the index clicked
                TempUser.SelectedItem = ItemsArrayList.get(i);

                ViewItemDescFragment viewItemDescFragment = new ViewItemDescFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.ItemLayout, viewItemDescFragment,viewItemDescFragment.getTag()).addToBackStack("items").commit();
            }
        });



    }
}
