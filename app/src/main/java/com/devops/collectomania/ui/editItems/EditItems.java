package com.devops.collectomania.ui.editItems;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.devops.collectomania.Item;
import com.devops.collectomania.R;
import com.devops.collectomania.TempUser;
import com.devops.collectomania.ui.addCategory.AddCategoryFragment;
import com.devops.collectomania.ui.collections.CollectionsFragment;
import com.devops.collectomania.ui.items.ItemsFragment;
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

public class EditItems extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    //declaration
    ImageView itemImage,back;
    EditText name,quantity,itemValue,category;
    DatePickerDialog.OnDateSetListener DateListener;
    TextView addCat,txtDate,pickdate;
    Button addItem;
    String dateStr;
    ActivityResultLauncher<String> getImageResult;
    Uri ItemUri = null;
    FirebaseStorage storage;
    boolean imageInputNotChanged = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_edit_items, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        //Unresolved Error in try block crashing application



        addItem = view.findViewById(R.id.btnEdititem);
        name = view.findViewById(R.id.etAddItemEditName);
        category = view.findViewById(R.id.etItemEditCategory);
        txtDate = view.findViewById(R.id.tvAddDateEdit);
        quantity = view.findViewById(R.id.etItemEditQuantity);
        itemValue = view.findViewById(R.id.etItemEditValue);
        itemImage = view.findViewById(R.id.imgItemEditpic);
        pickdate = view.findViewById(R.id.EditPickDate);
        back = view.findViewById(R.id.ivBackEditItem);

        //retrieving firebase instance
        storage = FirebaseStorage.getInstance();

        name.setEnabled(true);

        category.setText(TempUser.editDeets.get(0));
        category.setEnabled(false);
        Picasso.with(getActivity()).load(TempUser.editDeets.get(2)).into(itemImage);
        itemValue.setText(TempUser.editDeets.get(5));
        quantity.setText(TempUser.editDeets.get(4));
        name.setText(TempUser.editDeets.get(3));
        name.setEnabled(false);
        pickdate.setText(TempUser.editDeets.get(1));
        dateStr = TempUser.editDeets.get(1);
        ItemUri = Uri.parse(TempUser.editDeets.get(2));
        TempUser.editDeets.clear();

        //registering activity result for image capture
        getImageResult = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {

            itemImage.setImageURI(result);
            ItemUri = result;
            imageInputNotChanged =false;
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

        back.setOnClickListener(view15 -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.EditItemLayout,new CollectionsFragment(),"EditItems")
                    .commit();
        });
        addItem.setOnClickListener(view14 -> {

            if(ItemUri == null || TextUtils.isEmpty(name.getText().toString())
                    || TextUtils.isEmpty(category.getText().toString())
                    || TextUtils.isEmpty(itemValue.getText().toString())
                    || TextUtils.isEmpty(quantity.getText().toString())
                    || TextUtils.isEmpty(dateStr)){


                Toast.makeText(getActivity(), "Fields are empty", Toast.LENGTH_SHORT).show();
            }
            else{

                if(imageInputNotChanged){

                    try {
                        Item item = new Item(ItemUri.toString(),name.getText().toString().toUpperCase(),
                                category.getText().toString(),Double.parseDouble(itemValue.getText().toString()),Integer.parseInt(quantity.getText().toString()),
                                dateStr);

                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("User Item").child(TempUser.encodedEmail()).child(item.getCategory()).child(item.getName()).setValue(item);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("User Recent Item").child(TempUser.encodedEmail()).child(item.getName()).setValue(item);

                        Toast.makeText(getActivity(), "Details saved", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Couldn't convert data", Toast.LENGTH_SHORT).show();
                    }
                }

                if(!imageInputNotChanged){
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
                                                    category.getText().toString(),Double.parseDouble(itemValue.getText().toString()),Integer.parseInt(quantity.getText().toString()),
                                                    dateStr);

                                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                            mDatabase.child("User Item").child(TempUser.encodedEmail()).child(item.getCategory()).child(item.getName()).setValue(item);

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.child("User Recent Item").child(TempUser.encodedEmail()).child(item.getName()).setValue(item);

                                            imageInputNotChanged =true;
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

            }

        });
    }
}

/*
CodingWithMitch,2017., Android Beginner Tutorial #25 - DatePicker Dialog [Choosing a Date from a Dialog Pop-Up]
Code available : https://www.youtube.com/watch?v=hwe1abDO2Ag
Date accessed : [1/06/2022]
*/
