package com.example.task_efhamha.ui.articles;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.task_efhamha.R;
import com.example.task_efhamha.models.PostModel;
import com.example.task_efhamha.ui.post.PostFragment;
import com.example.task_efhamha.ui.post.ViewPostsActivity;
import com.example.task_efhamha.utlis.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ArticlesFragment extends Fragment {
    View view;

    RecyclerView recyclerViewPost;
    FloatingActionButton fab;
    SwipeRefreshLayout swipeRefreshLayout;

    int commentCount, viewCount;

    FirebaseAuth auth;

    List<PostModel> queryList = new ArrayList<>();


    int counter = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_articles, container, false);
        initView();
        return view;
    }

    private void initView() {
        recyclerViewPost = view.findViewById(R.id.recyclerView_article);
        fab = view.findViewById(R.id.fab_article);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh2);

        auth = FirebaseAuth.getInstance();

        fab.setOnClickListener((View v) -> {
            if (auth.getCurrentUser() == null) {
                Toast.makeText(requireActivity(), "برجاء تسجيل الدخول أولا", Toast.LENGTH_SHORT).show();
                return;
            }
            Constants.replaceFragment(ArticlesFragment.this, new PostFragment());
        });

        getPosts();
        swipeRefreshLayout.setOnRefreshListener(() -> getPosts());


    }

    private void getPosts() {

        Constants.initRef().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                queryList.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    PostModel postModel = d.getValue(PostModel.class);
                    queryList.add(postModel);
                }

                recyclerViewPost.setAdapter(new PostsAdapter(queryList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        swipeRefreshLayout.setRefreshing(false);

    }

    class PostsAdapter extends RecyclerView.Adapter<PostsViewHolder> {
        List<PostModel> models;

        public PostsAdapter(List<PostModel> models) {
            this.models = models;
        }

        @NonNull
        @Override
        public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.post_item, parent, false);
            return new PostsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
            PostModel postModel = models.get(position);

            String image = postModel.getImage();
            String descImage = postModel.getDescImage();
            String title = postModel.getTitle();
            String body = postModel.getBody();
            String owner = postModel.getUserModel().getName();
            String postId = postModel.getPostId();
            view.setTag(holder);

            long now = System.currentTimeMillis();

            CharSequence ago = DateUtils.getRelativeTimeSpanString(postModel.getTime(), now, DateUtils.MINUTE_IN_MILLIS);


            Picasso.get().load(image).placeholder(R.drawable.ic_profile2).into(holder.postImage);

            holder.descImage.setText(descImage);
            holder.titleText.setText(title);
            holder.bodyText.setText(body);
            holder.ownerText.setText("كتب بواسطة: " + owner);

            holder.timeText.setText("منذ: " + ago.toString());

            if (auth.getCurrentUser() == null) {


                getLikes(postModel.getPostId(), holder.likeText);
//                getCommentCount(postModel.getPostId(), holder.commentText);
                holder.likeLinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(requireActivity(), "برجاء تسجيل الدخول أولا", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.commentLinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(requireActivity(), "برجاء تسجيل الدخول أولا", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.bodyText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(requireActivity(), "برجاء تسجيل الدخول أولا", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            } else {

                getLikes(postModel.getPostId(), holder.likeText);
                setLikesColor(postModel.getPostId(), holder.likeImage);
//                getCommentCount(postModel.getPostId(), holder.commentText);

                holder.likeLinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PostsAdapter.this.setLikes(postModel.getPostId(), holder.likeImage);
                    }
                });

                holder.bodyText.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), ViewPostsActivity.class);
                    intent.putExtra("image", image);
                    intent.putExtra("descImage", descImage);
                    intent.putExtra("title", title);
                    intent.putExtra("body", body);
                    intent.putExtra("owner", owner);
                    intent.putExtra("ago", ago);
                    intent.putExtra("postId", postId);
                    startActivity(intent);

                    counter++;
                    holder.viewsText.setText(Integer.toString(counter));

                });


            }

        }

        void setLikes(String postId, ImageView imageView) {
            Constants.initRef().child("Likes").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(Constants.getUid(requireActivity()))) {
                        ColorStateList csl = AppCompatResources.getColorStateList(requireContext(), R.color.grey);
                        imageView.setImageTintList(csl);

                        Constants.initRef().child("Likes").child(postId).child(Constants.getUid(requireActivity())).removeValue();
                    } else {
                        ColorStateList csl = AppCompatResources.getColorStateList(requireContext(), R.color.main);
                        imageView.setImageTintList(csl);

                        Constants.initRef().child("Likes").child(postId).child(Constants.getUid(requireActivity())).setValue(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        void getLikes(String postId, TextView textView) {
            Constants.initRef().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    textView.setText(snapshot.getChildrenCount() + "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        void setLikesColor(String postId, ImageView imageView) {
            Constants.initRef().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(Constants.getUid(requireActivity()))) {
                        ColorStateList csl = AppCompatResources.getColorStateList(requireContext(), R.color.main);
                        imageView.setImageTintList(csl);
                    } else {
                        ColorStateList csl = AppCompatResources.getColorStateList(requireContext(), R.color.grey);
                        imageView.setImageTintList(csl);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        void getCommentCount(String postId, TextView commentText) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if (snapshot.exists()) {
                        commentCount = (int) snapshot.getChildrenCount();
                        commentText.setText(Integer.toString(commentCount));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return models.size();
        }
    }


    class PostsViewHolder extends RecyclerView.ViewHolder {


        ImageView postImage, likeImage, commentImage, viewImage;
        TextView descImage, titleText, bodyText, ownerText, timeText, likeText, commentText, viewsText;
        LinearLayout likeLinear, commentLinear, viewsLinear;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.image_iv_item_post);
            likeImage = itemView.findViewById(R.id.like_iv_item);
            commentImage = itemView.findViewById(R.id.comment_iv_item);
            viewImage = itemView.findViewById(R.id.view_iv_item);

            descImage = itemView.findViewById(R.id.description_tv_item);
            titleText = itemView.findViewById(R.id.title_tv_item);
            bodyText = itemView.findViewById(R.id.body_tv_item);
            ownerText = itemView.findViewById(R.id.publisher_tv_item);
            timeText = itemView.findViewById(R.id.time_tv_item);

            likeText = itemView.findViewById(R.id.like_tv_item);
//            commentText = itemView.findViewById(R.id.comment_tv_item);
            viewsText = itemView.findViewById(R.id.view_tv_item);

            likeLinear = itemView.findViewById(R.id.like_linear_item);
            commentLinear = itemView.findViewById(R.id.comment_linear_item);
            viewsLinear = itemView.findViewById(R.id.view_linear_item);
        }
    }

}

//searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//@Override
//public boolean onQueryTextSubmit(String query) {
//        queryList.clear();
//
//        for (PostModel d : detailsList){
//        if (d.getUserModel().getName().contains(query)){
//        queryList.add(d);
//        }
//        }
//
//        recyclerViewPost.setAdapter(new PostsAdapter(queryList));
//        return false;
//        }
//
//@Override
//public boolean onQueryTextChange(String newText) {
//        if (newText.length() == 0){
//        queryList.clear();
//        recyclerViewPost.setAdapter(new PostsAdapter(detailsList));
//        }
//        return false;
//        }
//        });