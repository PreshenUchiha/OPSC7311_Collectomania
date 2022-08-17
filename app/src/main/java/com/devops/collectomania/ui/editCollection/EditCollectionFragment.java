package com.devops.collectomania.ui.editCollection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.devops.collectomania.Categorie;
import com.devops.collectomania.Item;
import com.devops.collectomania.R;
import com.devops.collectomania.TempUser;
import com.devops.collectomania.ui.addItems.AddItemsFragment;
import com.devops.collectomania.ui.collections.CollectionsFragment;
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

public class EditCollectionFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    EditText CategoryGoal;
    Spinner CategoryName;
    Button btnAddCat;
    ImageView back,catImg;
    FirebaseStorage storage;
    String uriCat;
    Uri uri;
    boolean imageNotChanged;

    ActivityResultLauncher<String> GetCategorys;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_edit_collection, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        CategoryName = view.findViewById(R.id.EditCategory);
        btnAddCat = view.findViewById(R.id.btnEditCategory);
        back = view.findViewById(R.id.ivBackEditcategoru);
        catImg = view.findViewById(R.id.ivEditCatImg);
        CategoryGoal = view.findViewById(R.id.etEditCategoryGoal);

        storage = FirebaseStorage.getInstance();

        imageNotChanged = true;

        //Creating a list which categories will be stored
        ArrayList<Categorie> CategoryArrayList = new ArrayList<>();
        ArrayList<String> CategoryNames = new ArrayList<>();

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
                    CategoryArrayList.add(snapshot1.getValue(Categorie.class));
                }

                if (getActivity()!=null) {
                    CategoryNames.clear();

                    for (Categorie c:CategoryArrayList) {
                        CategoryNames.add(c.getCatagory());
                    }
                    //Creates adapter to display category data
                    ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_expandable_list_item_1,CategoryNames);

                    //Displays data
                    CategoryName.setAdapter(arrayAdapter);

                    CategoryName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            for (Categorie c: CategoryArrayList) {
                                if(CategoryNames.get(i) == c.getCatagory()){
                                    String tempGoal = Integer.toString(c.goal);
                                   CategoryGoal.setText(tempGoal);
                                    Picasso.with(getActivity()).load(c.imageuri).into(catImg);
                                    uriCat = c.getImageuri();
                                }
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

        GetCategorys = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {

                imageNotChanged = false;
                catImg.setImageURI(result);
                uriCat = result.toString();
                uri = result;
            }
        });

        catImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    try {
                        //calls file path to activity result launcher
                        GetCategorys.launch("image/*");
                    }catch (Exception e){
                        Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getActivity(), "Please change storage permissions for collectomania", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.editCategoryLayout,new CollectionsFragment(),"EditCollection")
                        .commit();
            }
        });

        btnAddCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (TextUtils.isEmpty(CategoryName.getSelectedItem().toString()) || TextUtils.isEmpty(uriCat.toString()) || TextUtils.isEmpty(CategoryGoal.toString())) {

                        Toast.makeText(getActivity(), "No category info entered", Toast.LENGTH_SHORT).show();
                    } else {

                        if (imageNotChanged) {
                            StorageReference storageReference = storage.getReference().child(TempUser.encodedEmail() + "/Category/" + CategoryName.getSelectedItem().toString());

                            try {
                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                Categorie c = new Categorie(CategoryName.getSelectedItem().toString().toUpperCase(), uriCat, Integer.parseInt(CategoryGoal.getText().toString()));
                                db.child("Category").child(TempUser.encodedEmail()).child(c.catagory).setValue(c);

                                Toast.makeText(getActivity(), "Category Added", Toast.LENGTH_SHORT).show();

                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.editCategoryLayout, new CollectionsFragment(), "AddItem")
                                        .commit();

                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Couldn't convert data", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (!imageNotChanged) {
                            StorageReference storageReference = storage.getReference().child(TempUser.encodedEmail() + "/Category/" + CategoryName.getSelectedItem().toString());

                            storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                try {
                                                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                                    Categorie c = new Categorie(CategoryName.getSelectedItem().toString().toUpperCase(), uri.toString(), Integer.parseInt(CategoryGoal.getText().toString()));
                                                    db.child("Category").child(TempUser.encodedEmail()).child(c.catagory).setValue(c);

                                                    imageNotChanged = true;
                                                    Toast.makeText(getActivity(), "Category Added", Toast.LENGTH_SHORT).show();

                                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.editCategoryLayout, new CollectionsFragment(), "EditItem")
                                                            .commit();

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
                }
                catch (Exception e){
                    Toast.makeText(getActivity(), "Can not convert Data", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
