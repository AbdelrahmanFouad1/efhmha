package com.example.task_efhamha.utlis;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;

import com.example.task_efhamha.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants  {


    // shared preferences
    private static SharedPreferences sharedPreferences;

    private static DatabaseReference databaseReference;

    public static DatabaseReference initRef ()
    {
        if(databaseReference == null)
        {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }

        return databaseReference;
    }

    public static void replaceFragment(Fragment from, Fragment to) {
        from
                .requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, to)
                .addToBackStack(null)
                .commit();
    }

    public static void replaceFragmentAndNotBack(Fragment from, Fragment to) {
        from
                .requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, to)
                .disallowAddToBackStack()
                .commit();
    }



    public static void saveUid(Activity activity, String id){

        sharedPreferences = activity.getSharedPreferences("SOCIAL", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Uid", id);
        editor.apply();
    }

    public static String getUid(Activity activity){

        sharedPreferences = activity.getSharedPreferences("SOCIAL", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Uid", "empty");
    }

    public static String getUid2(Application application){

        sharedPreferences = application.getSharedPreferences("SOCIAL", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Uid", "empty");
    }

//    public static DatabaseReference initRef(){
//        if (databaseReference == null){
//            databaseReference = FirebaseDatabase.getInstance().getReference();
//        }
//        return databaseReference;
//    }

}
