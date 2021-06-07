package com.example.pmpproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 4000;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast=
                        Log.d(TAG, token);
                    }
                });


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        } else {
            Log.d("main", "already logged in " + currentUser);

            showList(currentUser);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                showList(user);
            }
        }
    }

    private void showList(FirebaseUser firebaseUser) {
        List<ClassData> classDataList = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        MyListAdapter adapter = new MyListAdapter(firebaseUser, MainActivity.this, classDataList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference allClassesRef = database.getReference("all_classes");
        DatabaseReference favClassesRef = database.getReference(firebaseUser.getUid()).child("fav_classes");

        allClassesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<List<ClassData>> genericTypeIndicator = new GenericTypeIndicator<List<ClassData>>() {};
                List<ClassData> value = dataSnapshot.getValue(genericTypeIndicator);
                Log.d(TAG, "Value is 1: " + value);
                if (value == null) {
                    populateData(allClassesRef);
                } else {
                    classDataList.clear();
                    classDataList.addAll(value);
                    adapter.notifyDataSetChanged();

                    favClassesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {};
                            Map<String, String> value = dataSnapshot.getValue(genericTypeIndicator);
                            Log.d(TAG, "Value is 2: " + value);

                            if (value == null) {
                                value = new HashMap<>();
                            }
                            for (ClassData classData : classDataList) {
                                classData.setFavId("");
                                classData.setFavorite(false);
                                for (Map.Entry<String, String> entry : value.entrySet()) {
                                    if (entry.getValue() != null && entry.getValue().equals(classData.getId())) {
                                        classData.setFavId(entry.getKey());
                                        classData.setFavorite(true);
                                    }
                                }
                            }

                            Collections.sort(classDataList, new Comparator<ClassData>() {
                                @Override
                                public int compare(ClassData c1, ClassData c2) {
                                    return Boolean.compare(c2.isFavorite(),c1.isFavorite());
                                }
                            });


                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void populateData(DatabaseReference allClassesRef) {
        List<ClassData> classDataList = new ArrayList<>();
        classDataList.add(new ClassData("c1", "Yoga Nidra (meditation)", "Also known as yogic sleep, Yoga Nidra is a state of conscious deep sleep that brings about relaxation, clarity and tranquillity. During this class, students are guided through specific instructions to focus on different body parts and to relax them, while in a state of conscious sleep. The method is simple yet profound and it revitalises the entire system. Some preparatory asanas may be included.\n" +
                "\n" +
                "Suitable for students seeking to relax and reset the body and mind.", "", false));
        classDataList.add(new ClassData("c2", "Nada Yoga (meditation)", "Nada means flow of sound. During this class, students will concentrate on sound vibration generated by chanting, singing, mantra repetition or external sound such as singing bowls, tingsha or sacred musical instruments. The sound vibration balances and optimises the brain frequency and helps release negative emotions, reduce stress and relax the body. Some preparatory asanas may be included.\n" +
                "\n" +
                "Suitable for anyone looking to clear the mind and experience inner stillness.", "", false));
        classDataList.add(new ClassData("c3", "Immunity Booster Yoga", "Our immune system is affected by our lifestyle: what we eat, the quality of our sleep, and our physical and mental state. This class helps us to relax the body and mind so that our body can naturally boost its immunity.\n" +
                "\n" +
                "Specific poses will be practised to help stimulate the thymus gland, aid in detoxification of the body, and calm the mind. Some pranayama, chanting and meditation may be included.\n" +
                "\n" +
                "Suitable for anyone looking to restore their body and mind with gentle physical effort.", "", false));
        classDataList.add(new ClassData("c4", "Hot 26", "A non-vinyasa based set sequence practised in a heated room focused on increasing the flexibility of the spine. 26 postures are practised in this sequence with no upper body weight-bearing. This class includes asana and pranayama.\n" +
                "\n" +
                "The heat adds to the physical effort required to do the postures and aids to condition the body. This class is suitable for students with physical endurance. No inversions are taught in this class. Room temperature: 38-40 °C.", "", false));
        classDataList.add(new ClassData("c5", "Hot 37", "A non-vinyasa based set sequence practised in a heated room focused on connecting the body and mind. 37 postures are practised in this sequence, including some upper body weight bearing. This sequence includes asana and pranayama.\n" +
                "\n" +
                "The heat adds to the physical effort required to do the postures and aids to condition the body. This class is suitable for students with physical endurance. No inversions are taught in this class. Room temperature: 38-40 °C.", "", false));
        classDataList.add(new ClassData("c6", "Hot Vinyasa 1", "A dynamic practice in a heated room focusing on moving the body along with the breath. The experience in this class is one of fluidity, motion and heat. Poses are generally held for short periods of time and emphasis on the rhythm of the breath is encouraged with the purpose of aligning and focusing the mind. Some pranayama, chanting and meditation may be included.\n" +
                "\n" +
                "The heat adds to the physical effort required to do the postures and aids to condition the body. This class is suitable for students with some Vinyasa experience looking to have a challenging and dynamic experience. No full inversions are included in these classes. Some preparations for inversions and Level 2 postures may be introduced, with options for Level 1 students to ease the transition from Level 1 to Level 2 classes. Room temperature: 35-38 °C.", "", false));
        classDataList.add(new ClassData("c7", "Warm and Relaxing Stretch", "A healing practice done in a mildly heated room. This class focuses on opening the body and soothing the mind through gentle poses and the use of props. Students will experience more seated and supine postures and some longer asana holds. Some pranayama, chanting and meditation may be included.\n" +
                "\n" +
                "Suitable for anyone looking to stretch their bodies with moderate physical effort.", "", false));
        classDataList.add(new ClassData("c8", "Vinyasa 1", "A dynamic practice focused on moving the body along with the breath. The experience in this class is one of fluidity and motion. Poses are generally held for short periods of time and emphasis on the rhythm of the breath is encouraged with the purpose of aligning and focusing the mind. Some pranayama, chanting and meditation may be included.\n" +
                "\n" +
                "Suitable for students with some yoga experience looking to have an invigorating and dynamic experience. No full inversions are included in these classes. Some preparations for inversions and Level 2 postures may be introduced, with options for Level 1 students to ease the transition from Level 1 to Level 2 classes.", "", false));
        classDataList.add(new ClassData("c9", "Vinyasa 2", "A dynamic practice focused on moving the body along with the breath. The experience in this class is one of fluidity and motion. Poses are generally held for short periods of time and emphasis on the rhythm of the breath is encouraged with the purpose of aligning and focusing the mind. Some pranayama, chanting and meditation may be included.\n" +
                "\n" +
                "Suitable for intermediate yoga practitioners with a consistent Vinyasa practice. This class builds on the necessary body stability acquired in Level 1 classes to take on more challenging asanas and sequences. Classes are taught with modifications to allow an easy transition from Level 1 to Level 2. Full inversions may be included in these classes.", "", false));
        classDataList.add(new ClassData("c10", "Vinyasa Basics", "A dynamic practice focused on moving the body along with the breath. The experience in this class is one of fluidity and motion. Poses are generally held for short periods of time and emphasis on the rhythm of the breath is encouraged with the purpose of aligning and focusing the mind. Some pranayama, chanting and meditation may be included.\n" +
                "\n" +
                "Suitable for new practitioners and students who want to increase their knowledge of the fundamental Vinyasa sequences and breath alignment. This class sets the foundation for all the other Vinyasa classes.", "", false));
        classDataList.add(new ClassData("c11", "Vinyasa Gentle", "A dynamic practice focused on moving the body along with the breath. The experience in this class is one of fluidity and motion. Poses are generally held for short periods of time and emphasis on the rhythm of the breath is encouraged with the purpose of aligning and focusing the mind. Some pranayama, chanting and meditation may be included.\n" +
                "\n" +
                "Suitable for anyone looking to stretch their bodies with moderate physical effort. The focus of the class is more on stretching and awareness of body-breath synchronicity.", "", false));
        classDataList.add(new ClassData("c12", "Wall Rope Yoga 1", "This specialised class is practiced on a Yoga Wall system that uses ropes, pelvic swings, bars and other props to help enhance traction and extension of the body in different yoga poses. The system is designed to assist the understanding of alignment as well as deepening the opening of the body during practice. Some pranayama, chanting and meditation may be included.\n" +
                "\n" +
                "Suitable for students with some yoga experience looking to explore the Wall Rope Yoga practice. Some supported inversions and Level 2 postures may be introduced, with options for Level 1 students to ease the transition from Level 1 to Level 2 classes. This class is not suitable for beginners. \n" +
                "\n" +
                "Please note that it is necessary to arrive 5-10 minutes early before class to set up the equipment.", "", false));



        allClassesRef.setValue(classDataList);
    }
}