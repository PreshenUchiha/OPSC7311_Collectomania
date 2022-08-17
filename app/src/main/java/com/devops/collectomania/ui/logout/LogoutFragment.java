package com.devops.collectomania.ui.logout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.devops.collectomania.R;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutFragment extends Fragment {

    private Button logout;


    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Defines the xml file for the fragment

        return inflater.inflate(R.layout.fragment_logout, container, false);
    }


    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        logout = (Button) view.findViewById(R.id.btnSignout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();

                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
            }
        });

    }

}