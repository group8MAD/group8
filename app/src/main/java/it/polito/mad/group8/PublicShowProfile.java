package it.polito.mad.group8;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class PublicShowProfile extends AppCompatActivity {


    private ProgressBar progressBar;
    private TextView nicknameTV;
    private ImageView userImageIV;
    private Button readReview;
    private Button contactUser;
    private Button rateUser;



    //contact user info
    private String contactUserNickname;
    private String contactUserUid;
    private String contactUserImageUrl;
    //current user info
    private String currentUserUid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_show_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Getting views
        nicknameTV = findViewById(R.id.nickname);
        userImageIV = findViewById(R.id.image);
        progressBar = findViewById(R.id.progressBar);
        readReview = findViewById(R.id.readReviews);
        contactUser = findViewById(R.id.contactUser);
        rateUser = findViewById(R.id.rateUser);




        progressBar.setVisibility(View.VISIBLE);
        //getting data from intent
        contactUserUid = getIntent().getStringExtra("contactUid");
        currentUserUid = getIntent().getStringExtra("currentUserUid");

        if (contactUserUid.equals(currentUserUid)){
            contactUser.setVisibility(View.GONE);
        }

        FirebaseDatabase.getInstance().getReference("users")
                .child(contactUserUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        contactUserNickname = Objects.requireNonNull(dataSnapshot.child("nickname").getValue()).toString();
                        contactUserImageUrl = Objects.requireNonNull(dataSnapshot.child("imageUri").getValue()).toString();
                        //Setting elements
                        if (!contactUserImageUrl.isEmpty())
                            Picasso.get().load(contactUserImageUrl).into(userImageIV);
                        nicknameTV.setText(contactUserNickname);

                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        contactUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChatRoom.class);
                intent.putExtra("contactUid", contactUserUid);
                intent.putExtra("currentUserUid", currentUserUid);
                FirebaseDatabase.getInstance().getReference("chats/")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(contactUserUid+"-"+currentUserUid)){
                                    String chatRoomName = contactUserUid+"-"+currentUserUid;
                                    intent.putExtra("chatRoomName", chatRoomName);
                                    intent.putExtra("chat", "old");
                                    startActivity(intent);
                                }else  if (dataSnapshot.hasChild(currentUserUid+"-"+contactUserUid)){
                                    String chatRoomName = currentUserUid+"-"+contactUserUid;
                                    intent.putExtra("chatRoomName", chatRoomName);
                                    intent.putExtra("chat", "old");
                                    startActivity(intent);
                                }else{
                                    String chatRoomName = currentUserUid+"-"+contactUserUid;
                                    intent.putExtra("chatRoomName", chatRoomName);
                                    intent.putExtra("chat", "new");
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
