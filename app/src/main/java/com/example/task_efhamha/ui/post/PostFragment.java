package com.example.task_efhamha.ui.post;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.task_efhamha.R;
import com.example.task_efhamha.models.PostModel;
import com.example.task_efhamha.models.UserModel;
import com.example.task_efhamha.ui.main.MainActivity;
import com.example.task_efhamha.utlis.Constants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

import static android.app.Activity.RESULT_OK;

public class PostFragment extends Fragment {
    View view;

    ImageView pickImage;
    TextView descriptionImage, titleField, bodyField;
    Button publish;

    UserModel userModel;

    FirebaseAuth auth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    Uri selectImage;
    String selectedImageUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        initView();
        getUser();
    }

    private void getUser()
    {
        Constants.initRef().child("Users").child(Constants.getUid(requireActivity())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                userModel = snapshot.getValue(UserModel.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView()
    {
        pickImage = view.findViewById(R.id.image_iv_post);
        descriptionImage = view.findViewById(R.id.descriptionImage_et_post);
        titleField = view.findViewById(R.id.title_et_post);
        bodyField = view.findViewById(R.id.body_et_post);
        publish = view.findViewById(R.id.publisher_btn_post);

        auth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        pickImage.setOnClickListener(v -> CropImage.activity()
                .start(requireActivity(), PostFragment.this));

        publish.setOnClickListener(v -> {
            String descImage = descriptionImage.getText().toString();
            String title = titleField.getText().toString();
            String body = bodyField.getText().toString();

            if (descImage.isEmpty() || title.isEmpty() || body.isEmpty()){
                Toast.makeText(requireActivity(), "برجاء أكمال البيانات", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectImage == null){
                Toast.makeText(requireActivity(), "برجاء أدخال الصورة!", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadImage(selectImage, descImage, title, body);

            startActivity(new Intent(requireActivity(), MainActivity.class));
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                selectImage = result.getUri();
                Picasso.get().load(selectImage).into(pickImage);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void uploadImage(Uri selectImage, String descImage, String title, String body)
    {
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
                createPost(selectedImageUrl , descImage, title, body);

            }
        }).addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void createPost(String selectedImageUrl, String descImage, String title, String body)
    {
        long now = System.currentTimeMillis();

        String key = Constants.initRef().child("Posts").push().getKey();

        PostModel postModel = new PostModel(selectedImageUrl, descImage, title, body, now, key, userModel);


        Constants.initRef().child("Posts").child(key).setValue(postModel).addOnCompleteListener(task ->
        {
            Toast.makeText(requireActivity(), "تم أضافه المقالة بنجاح", Toast.LENGTH_SHORT).show();
        });
    }

}
