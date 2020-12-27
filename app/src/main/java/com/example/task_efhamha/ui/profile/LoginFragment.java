package com.example.task_efhamha.ui.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.task_efhamha.R;
import com.example.task_efhamha.models.UserModel;
import com.example.task_efhamha.ui.main.MainActivity;
import com.example.task_efhamha.utlis.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginFragment extends Fragment {

    //TODO :  It is about two screens overlapping in some.

    private View view;

    //Login Screen
    EditText emailField;
    EditText passwordField;
    TextView forget;
    ImageView loginWithEmail;
    TextView replaceToRegister;
    TextView backSpace;

    ProgressDialog progressDialog;

    FirebaseAuth auth;

    LinearLayout loginLayout;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;


    //profile Screen
    CircleImageView circleImageViewProfile;
    TextView userName_tv;
    TextView email_tv;
    Button logOut;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    RelativeLayout profileLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewLogin();
        initViewProfile();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        // [START_EXCLUDE silent]
        progressDialog.show();
        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            startActivity(new Intent(requireActivity(), MainActivity.class));
                            getActivity().finish();
//
                        }
                        // [START_EXCLUDE]
                        progressDialog.dismiss();
                        // [END_EXCLUDE]
                    }
                });
    }

    //  Login screen
    private void initViewLogin() {

        emailField = view.findViewById(R.id.email_et_login);
        passwordField = view.findViewById(R.id.password_et_login);
        forget = view.findViewById(R.id.forget_tv_login);
        Button login = view.findViewById(R.id.login_btn_login);
        loginWithEmail = view.findViewById(R.id.email_iv_login);
        replaceToRegister = view.findViewById(R.id.register_tv_login);
        backSpace = view.findViewById(R.id.back_space);
        loginLayout = view.findViewById(R.id.login_layout);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            loginLayout.setVisibility(View.VISIBLE);
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);


        backSpace.setOnClickListener(v -> startActivity(new Intent(getActivity(), MainActivity.class)));

        forget.setOnClickListener(view -> Constants.replaceFragment(LoginFragment.this, new ForgetFragment()));

        login.setOnClickListener(view -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "برجاء أكتمال البيانات بشكل صحيح", Toast.LENGTH_SHORT).show();
                return;
            }
            progressDialog.show();
            singIn(email, password);
        });

        loginWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGmail();
            }
        });

        replaceToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.replaceFragment(LoginFragment.this, new RegisterFragment());
            }
        });
    }

    // inside login screen because signIn with gmail
    private void signInWithGmail() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // inside login screen because signIn
    private void singIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {

//                        Constants.replaceFragmentAndNotBack(LoginFragment.this, new ProfileFragment());
                    Constants.saveUid(getActivity(), task.getResult().getUser().getUid());
                    startActivity(new Intent(getActivity(), MainActivity.class));
//                        getActivity().finish();


                } else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //  profile screen
    private void initViewProfile() {
        circleImageViewProfile = view.findViewById(R.id.circleImage_iv_profile);
        userName_tv = view.findViewById(R.id.userName_tv_profile);
        email_tv = view.findViewById(R.id.email_tv_profile);
        logOut = view.findViewById(R.id.logOut_btn_profile);
        profileLayout = view.findViewById(R.id.profile_layout);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            loginLayout.setVisibility(View.GONE);
            profileLayout.setVisibility(View.VISIBLE);

        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireActivity());

        if (account != null) {
            Uri image = account.getPhotoUrl();
            String name = account.getDisplayName();
            String email = account.getEmail();

            userName_tv.setText(name);
            email_tv.setText(email);

            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.ic_profile2)
                    .into(circleImageViewProfile);
        } else {
            databaseReference.child("Users").child(Constants.getUid(getActivity())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel userModel = snapshot.getValue(UserModel.class);



                    if (auth.getCurrentUser() != null) {
                        if (userModel.getName()!= null && userModel.getEmail() != null) {
                            userName_tv.setText(userModel.getName());
                            email_tv.setText(userModel.getEmail());
                            Picasso.get()
                                    .load(userModel.getImage())
                                    .placeholder(R.drawable.ic_profile2)
                                    .into(circleImageViewProfile);
                        }else {
                            Toast.makeText(requireActivity(), "خطأ", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(),
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });

                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
    }

}
