package com.example.task_efhamha.ui.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.task_efhamha.R;
import com.example.task_efhamha.models.UserModel;
import com.example.task_efhamha.utlis.Constants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class RegisterFragment extends Fragment {
    View view;

    CircleImageView circleImageViewRegister;
    EditText userNameField;
    EditText emailField;
    EditText passwordField;
    EditText confirmPasswordField;
    Button register;
    TextView replaceToLogin;

    ProgressDialog progressDialog;

    FirebaseAuth auth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    Uri selectImage;
    String selectedImageUrl;

    UserModel userModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);
        initView();
        return view;
    }

    private void initView() {
        circleImageViewRegister = view.findViewById(R.id.circleImage_iv_register);
        userNameField = view.findViewById(R.id.userName_et_register);
        emailField = view.findViewById(R.id.email_et_register);
        passwordField = view.findViewById(R.id.password_et_register);
        confirmPasswordField = view.findViewById(R.id.confirmPassword_et_register);
        register = view.findViewById(R.id.register_btn_register);
        replaceToLogin = view.findViewById(R.id.login_tv_register);


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        circleImageViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                            .start(requireContext(), RegisterFragment.this);
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = userNameField.getText().toString();
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String confirmPassword = confirmPasswordField.getText().toString();


                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(getActivity(), "برجاء أكمال البيانات", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getActivity(), "الرقم السري غير متطابق", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();
                signIn(name, email, password, confirmPassword);

            }
        });

        replaceToLogin.setOnClickListener(view -> Constants.replaceFragment(RegisterFragment.this, new LoginFragment()));
    }

    private void signIn(final String name, final String email, final String password, String confirmPassword) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()){

//                    task.getResult().getUser().sendEmailVerification();
                Constants.replaceFragment(RegisterFragment.this, new LoginFragment());
                creatUsers(task.getResult().getUser().getUid(), name, email, selectedImageUrl);

            }else {
                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                selectImage = result.getUri();
                Picasso.get().load(selectImage).into(circleImageViewRegister);
                uploadImage(selectImage);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(Uri selectImage) {
        UploadTask uploadTask;

        final StorageReference ref = storageReference.child("images/" + selectImage.getLastPathSegment());
        uploadTask = ref.putFile(selectImage);

        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri downloadUri = task.getResult();

                selectedImageUrl = downloadUri.toString();
                
//                creatUsers(userModel.getuId(), userModel.getName(), userModel.getEmail(), selectedImageUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void creatUsers( final String uid, String name, String email, String image) {
        UserModel userModel = new UserModel(uid, name, email, image);
        databaseReference.child("Users").child(uid).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Constants.saveUid(getActivity(), uid);
                }else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



}
