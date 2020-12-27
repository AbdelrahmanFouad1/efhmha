package com.example.task_efhamha.ui.post;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_efhamha.R;
import com.example.task_efhamha.models.CommentModel;
import com.example.task_efhamha.models.PostModel;
import com.example.task_efhamha.models.UserModel;
import com.example.task_efhamha.utlis.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public  class ViewPostsActivity extends AppCompatActivity {

    TextView title, descImage, time, publisher, body, comments;
    ImageView image;
    EditText pushComment;
    FloatingActionButton pushFab;
    RecyclerView recyclerViewComment;

    List<CommentModel> queryList = new ArrayList<>();

    FirebaseAuth auth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    UserModel userModel;
    PostModel postModel;

    String currentComment;
    String postId;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posts);
        initView();
        getData();
        getPost();
    }

    private void initView() {
        title = findViewById(R.id.title_tv_second);
        descImage = findViewById(R.id.description_tv_second);
        time = findViewById(R.id.time_tv_second);
        publisher = findViewById(R.id.publisher_tv_second);
        body = findViewById(R.id.body_tv_second);
        comments = findViewById(R.id.comments_tv_second);
        image = findViewById(R.id.image_iv_second);

        pushComment = findViewById(R.id.pushComment_et_second);
        pushFab = findViewById(R.id.push_fab_second);

        recyclerViewComment = findViewById(R.id.recyclerView_comment);

//        recyclerViewComment.setAdapter(new commentAdapter(queryList));

        auth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        pushFab.setOnClickListener(v -> {
            currentComment = pushComment.getText().toString();

            if (currentComment.isEmpty()) {
                Toast.makeText(getApplication(), "برجاء أخال تعليق", Toast.LENGTH_SHORT).show();
                return;
            }

            createComment();
            pushComment.setText("");
        });

        getComment();
    }

    private void getData() {
        Intent intent = getIntent();

        Picasso.get()
                .load(intent.getStringExtra("image"))
                .placeholder(R.drawable.ic_clickcamera)
                .error(R.drawable.ic_clickcamera)
                .into(image);

        title.setText(intent.getStringExtra("title"));
        descImage.setText(intent.getStringExtra("descImage"));
        time.setText(intent.getStringExtra("ago"));
        publisher.setText(intent.getStringExtra("owner"));
        body.setText(intent.getStringExtra("body"));

        postId = intent.getStringExtra("postId");
    }

    private void getPost() {
        Constants.initRef().child("Users").child(Constants.getUid2(getApplication())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel = snapshot.getValue(UserModel.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createComment() {
         key = Constants.initRef().child("Comments").push().getKey();

        CommentModel commentModel = new CommentModel(key, currentComment, userModel, postId);

        Constants.initRef().child("Comments").child(key).setValue(commentModel).addOnCompleteListener(task ->
        {
            Toast.makeText(getApplication(), "تم أضافة التعليق بنجاح", Toast.LENGTH_SHORT).show();
        });

    }

    private void getComment() {

        Constants.initRef().child("Comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                queryList.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    CommentModel commentModel = d.getValue(CommentModel.class);

                    if (d.exists()) {
                        if (commentModel.getPostId().equals(postId)) {
                            queryList.add(commentModel);
                        }
                    }
                }

                recyclerViewComment.setAdapter(new commentAdapter(queryList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class commentAdapter extends RecyclerView.Adapter<commentViewHolder> {

        List<CommentModel> models;

        public commentAdapter(List<CommentModel> models) {
            this.models = models;
        }

        @NonNull
        @Override
        public commentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplication()).inflate(R.layout.comment_item, parent, false);
            return new commentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull commentViewHolder holder, int position) {
            CommentModel commentModel = models.get(position);

            String image = commentModel.getUserModel().getImage();
            String name = commentModel.getUserModel().getName();
            String comment = commentModel.getComment();

            holder.name.setText(name);
            holder.comment.setText(comment);
            Picasso.get().load(image).placeholder(R.drawable.ic_profile2).into(holder.circleImageView);


        }


        @Override
        public int getItemCount() {
            return models.size();
        }
    }

    class commentViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView name, comment;

        public commentViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.image_comment);
            name = itemView.findViewById(R.id.name_comment);
            comment = itemView.findViewById(R.id.comment_comment);
        }
    }
}