package com.devops.collectomania.ui.profile;

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

import com.devops.collectomania.R;
import com.devops.collectomania.TempUser;
import com.devops.collectomania.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    Button submit;
    EditText name, lastName,phoneNumber;
    ImageView profile;
    Uri profileUri;
    FirebaseStorage storage;

    ActivityResultLauncher<String> GetProfileImage;
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_profile, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        submit = view.findViewById(R.id.btnSaveDetails);
        name = view.findViewById(R.id.etName);
        lastName = view.findViewById(R.id.etLastName);
        phoneNumber = view.findViewById(R.id.etPhoneNumber);
        profile = view.findViewById(R.id.imgProfilePic);

        storage = FirebaseStorage.getInstance();

        if(TempUser.userDeets.size() > 1){
            name.setText(TempUser.userDeets.get(1));
            lastName.setText(TempUser.userDeets.get(0));
            phoneNumber.setText(TempUser.userDeets.get(2));
            Picasso.with(getActivity()).load(TempUser.userDeets.get(3)).into(profile);
        }


        GetProfileImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {

                profile.setImageURI(result);
                profileUri = result;


            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        //calls file path to activity result launcher
                        GetProfileImage.launch("image/*");

                    }catch (Exception e){
                        Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
                    }


                }else {
                    Toast.makeText(getActivity(), "Please change storage permissions for collectomania", Toast.LENGTH_SHORT).show();
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                    if(TextUtils.isEmpty(profileUri.toString()) || TextUtils.isEmpty(name.getText().toString()) ||
                            TextUtils.isEmpty(lastName.getText().toString()) || TextUtils.isEmpty(phoneNumber.getText().toString())){

                        Toast.makeText(getActivity(), "Enter all details", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        StorageReference storageReference = storage.getReference().child(TempUser.encodedEmail());

                        storageReference.putFile(profileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            User userObject = new User(name.getText().toString(),lastName.getText().toString(),phoneNumber.getText().toString(), uri.toString());

                                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                                            mDatabase.child("User Profile").child(TempUser.encodedEmail()).setValue(userObject);

                                            Toast.makeText(getActivity(), "Details saved", Toast.LENGTH_SHORT).show();

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