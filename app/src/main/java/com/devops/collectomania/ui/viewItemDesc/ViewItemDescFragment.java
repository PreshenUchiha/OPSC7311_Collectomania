package com.devops.collectomania.ui.viewItemDesc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.devops.collectomania.R;
import com.devops.collectomania.TempUser;
import com.devops.collectomania.ui.addItems.AddItemsFragment;
import com.devops.collectomania.ui.editItems.EditItems;
import com.devops.collectomania.ui.items.ItemsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewItemDescFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    TextView tvName, tvCategory,tvQuantity,tvValue,tvDate,tvEdit;
    ImageView ivItemPic,ivBackItemDesc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_item_description, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        
        tvName = view.findViewById(R.id.tvItemName);
        tvCategory = view.findViewById(R.id.tvItemCategory);
        ivItemPic = view.findViewById(R.id.ivItemPic);
        tvQuantity = view.findViewById(R.id.tvItemQty);
        tvDate = view.findViewById(R.id.tvPurchaseDate);
        tvValue = view.findViewById(R.id.tvItemPrice);
        ivBackItemDesc = view.findViewById(R.id.ivBackItemDescription);
        tvEdit = view.findViewById(R.id.tvEdit);

        ArrayList<String> ItemDescArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User Item").child(TempUser.encodedEmail())
                .child(TempUser.SelectedCollection).child(TempUser.SelectedItem);

        ivBackItemDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.itemDescriptionLayout,new ItemsFragment(),"items")
                .commit();

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ItemDescArrayList.clear();

                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    ItemDescArrayList.add(snapshot1.getValue().toString());
                }

                tvCategory.setText("Categorie: "+ItemDescArrayList.get(0));
                Picasso.with(getActivity()).load(ItemDescArrayList.get(2)).into(ivItemPic);
                tvValue.setText("Value: R "+ItemDescArrayList.get(5));
                tvQuantity.setText("Quantity: "+ItemDescArrayList.get(4));
                tvName.setText("Item Name: "+ItemDescArrayList.get(3));
                tvDate.setText("Date: "+ItemDescArrayList.get(1));
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tvEdit.setOnClickListener(view1 -> {

            for (String s :ItemDescArrayList) {
                TempUser.editDeets.add(s);
            }
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.itemDescriptionLayout,new EditItems(),"items")
                    .commit();
        });

    }
}
