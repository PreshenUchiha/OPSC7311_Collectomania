package com.devops.collectomania.ui.addCategory;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.devops.collectomania.Categorie;
import com.devops.collectomania.R;
import com.devops.collectomania.TempUser;
import com.devops.collectomania.ui.addItems.AddItemsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddCategoryFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    EditText CategoryName, CategoryGoal;
    Button btnAddCat;
    ImageView back,catImg;
    FirebaseStorage storage;
    Uri uriCat;

    ActivityResultLauncher<String> GetCategorys;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_add_catagorie, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        CategoryName = view.findViewById(R.id.etAddCategory);
        btnAddCat = view.findViewById(R.id.btnAddCategory);
        back = view.findViewById(R.id.ivBackAddcategoru);
        catImg = view.findViewById(R.id.ivAddCatImg);
        CategoryGoal = view.findViewById(R.id.etAddCategoryGoal);

        storage = FirebaseStorage.getInstance();

        GetCategorys = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {

                catImg.setImageURI(result);
                uriCat = result;
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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.AddCategoryLayout,new AddItemsFragment(),"AddItem")
                        .commit();
            }
        });

        btnAddCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(CategoryName.getText().toString())||TextUtils.isEmpty(uriCat.toString())||TextUtils.isEmpty(CategoryGoal.toString())){

                    Toast.makeText(getActivity(), "No category info entered", Toast.LENGTH_SHORT).show();
                }
                else {

                    try{
                        StorageReference storageReference = storage.getReference().child(TempUser.encodedEmail()+"/Category/"+ CategoryName.getText().toString());

                        storageReference.putFile(uriCat).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){

                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            try {
                                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                                Categorie c = new Categorie(CategoryName.getText().toString().toUpperCase(),uri.toString(),Integer.parseInt(CategoryGoal.getText().toString()));
                                                db.child("Category").child(TempUser.encodedEmail()).child(c.catagory).setValue(c);

                                                Toast.makeText(getActivity(), "Category Added", Toast.LENGTH_SHORT).show();

                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.AddCategoryLayout,new AddItemsFragment(),"AddItem")
                                                        .commit();

                                            } catch (Exception e) {
                                                Toast.makeText(getActivity(), "Couldn't convert data", Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                    });

                                }
                            }
                        });
                    }catch (Exception e){
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }


                }

            }
        });

    }
}
