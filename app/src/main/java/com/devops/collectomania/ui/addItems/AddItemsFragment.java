package com.devops.collectomania.ui.addItems;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.devops.collectomania.R;
import com.devops.collectomania.Item;
import com.devops.collectomania.TempUser;
import com.devops.collectomania.ui.addCategory.AddCategoryFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AddItemsFragment  extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    //declaration
    ImageView itemImage;
    EditText name,quantity,itemValue;
    DatePickerDialog.OnDateSetListener DateListener;
    Spinner category;
    TextView addCat,txtDate,pickdate;
    Button addItem;
    String dateStr;
    ActivityResultLauncher<String> getImageResult;
    Uri ItemUri = null;
    FirebaseStorage storage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_add_items, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        //Unresolved Error in try block crashing application



            addItem = view.findViewById(R.id.btnAdditem);
            name = view.findViewById(R.id.etAddItemName);
            addCat = view.findViewById(R.id.tvAddCategoryLink);
            category = view.findViewById(R.id.spinnerItemCategory);
            txtDate = view.findViewById(R.id.tvAddDate);
            quantity = view.findViewById(R.id.etItemQuantity);
            itemValue = view.findViewById(R.id.etItemValue);
            itemImage = view.findViewById(R.id.imgItempic);
            pickdate = view.findViewById(R.id.PickDate);

            //retrieving firebase instance
            storage = FirebaseStorage.getInstance();

            //registering activity result for image capture
            getImageResult = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {

                itemImage.setImageURI(result);
                ItemUri = result;
            });

            txtDate.setOnClickListener(view1 -> {
                Calendar cal = Calendar.getInstance();
                int yr = cal.get(Calendar.YEAR);
                int mn = cal.get(Calendar.MONTH);
                int d = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                        android.R.style.Theme_Holo_Dialog_MinWidth, DateListener,yr,mn,d);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            });

            DateListener = (datePicker, i, i1, i2) -> {

                i1 = i1+1;

                dateStr = i2+"/"+ i1 +"/"+i;
                pickdate.setText(dateStr);

            };

            //Creating a list which categories will be stored
            ArrayList<String> CategoryArrayList = new ArrayList<>();

            //calling db reference
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Category").child(TempUser.encodedEmail());

            //Event listener when data has been received for category list
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Clears list before use
                    CategoryArrayList.clear();

                    //Adds data to array list
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        CategoryArrayList.add(snapshot1.getKey());
                    }

                    if (getActivity()!=null) {
                        //Creates adapter to display category data
                        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_expandable_list_item_1,CategoryArrayList);

                        //Displays data
                        category.setAdapter(arrayAdapter);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {


                }
            });



            itemImage.setOnClickListener(view12 -> {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        //calls file path to activity result launcher
                        getImageResult.launch("image/*");
                    }catch (Exception e){
                        Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getActivity(), "Please change storage permissions for collectomania", Toast.LENGTH_SHORT).show();
                }

            });

            addCat.setOnClickListener(view13 -> {
                AddCategoryFragment ac = new AddCategoryFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.AddItemLayout, ac,ac.getTag()).addToBackStack("AddItem").commit();
            });

            addItem.setOnClickListener(view14 -> {

                if(ItemUri == null || TextUtils.isEmpty(name.getText().toString())
                        || TextUtils.isEmpty(category.getSelectedItem().toString())
                        || TextUtils.isEmpty(itemValue.getText().toString())
                        || TextUtils.isEmpty(quantity.getText().toString())
                        || TextUtils.isEmpty(dateStr)){


                    Toast.makeText(getActivity(), "Fields are empty", Toast.LENGTH_SHORT).show();
                }
                else{

                        StorageReference storageReference = storage.getReference().child(TempUser.encodedEmail()+"/"+name.getText().toString());

                        storageReference.putFile(ItemUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){

                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            try {
                                                Item item = new Item(uri.toString(),name.getText().toString().toUpperCase(),
                                                        category.getSelectedItem().toString(),Double.parseDouble(itemValue.getText().toString()),Integer.parseInt(quantity.getText().toString()),
                                                        dateStr);

                                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                                mDatabase.child("User Item").child(TempUser.encodedEmail()).child(item.getCategory()).child(item.getName()).setValue(item);

                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                databaseReference.child("User Recent Item").child(TempUser.encodedEmail()).child(item.getName()).setValue(item);

                                                ItemUri = null;
                                                Toast.makeText(getActivity(), "Details saved", Toast.LENGTH_SHORT).show();

                                            } catch (Exception e) {
                                                Toast.makeText(getActivity(), "Couldn't convert data", Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                    });

                                }
                            }
                        });


                }

            });
    }
}

/*
CodingWithMitch,2017., Android Beginner Tutorial #25 - DatePicker Dialog [Choosing a Date from a Dialog Pop-Up]
Code available : https://www.youtube.com/watch?v=hwe1abDO2Ag
Date accessed : [1/06/2022]
*/