package com.example.task_efhamha.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.task_efhamha.R;
import com.example.task_efhamha.utlis.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetFragment extends Fragment {
    View view;

    EditText confirmEmail;
    Button resetPassword;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_forget, container, false);
        initView();
        return view;
    }

    private void initView() {
        confirmEmail = view.findViewById(R.id.confirmEmail_et_forget);
        resetPassword = view.findViewById(R.id.resetPassword_forget);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
  //            Constants.replaceFragmentAndNotBack(ForgetFragment.this, new LoginFragment());
                String email = confirmEmail.getText().toString();

                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Constants.replaceFragmentAndNotBack(ForgetFragment.this, new LoginFragment());
                                }
                            }
                        });

            }
        });
    }
}
