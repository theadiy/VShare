package org.terna.vshare.ui.Signout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.terna.vshare.HomeFeedActivity;
import org.terna.vshare.MainActivity;
import org.terna.vshare.R;

import static android.content.Context.MODE_PRIVATE;

public class SignoutFragment extends Fragment {


    String TAG = "SignoutFragment";

    FirebaseAuth firebaseAuth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor myEdit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_signout, container, false);
        final TextView textView = root.findViewById(R.id.text_signOut);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        String Name = user.getDisplayName();

        sharedPreferences = this.getActivity().getSharedPreferences("user", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        SignOut();



        return root;
    }
    public void SignOut(){
        myEdit.clear();
        myEdit.apply();


        mGoogleSignInClient.signOut();
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(),"Signed out",Toast.LENGTH_SHORT).show();
                Log.e(TAG,"---------------"+firebaseAuth.getCurrentUser().getDisplayName());
                Intent i = new Intent(getContext(),MainActivity.class);
                startActivity(i);

            }
        });
    }
}