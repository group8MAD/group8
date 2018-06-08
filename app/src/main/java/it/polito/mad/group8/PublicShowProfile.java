package it.polito.mad.group8;


import android.app.ProgressDialog;
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


    private ProgressDialog progressDialog;
    private TextView nicknameTV;
    private TextView cityProvince;
    private ImageView userImageIV;
    private Button readReview;
    private Button contactUser;
    private Button borrow;
    private Button rateButton;

    //contact user info
    private String contactUserNickname;
    private String contactUserUid;
    private String contactUserImageUrl;
    private String contactUserCity;
    private String contactUserProvince;
    //current user info
    private String currentUserUid;

    private String rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_show_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(PublicShowProfile.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.wait));
        //Getting views
        rateButton = findViewById(R.id.rate);
        nicknameTV = findViewById(R.id.nickname);
        userImageIV = findViewById(R.id.image);
        readReview = findViewById(R.id.readReviews);
        contactUser = findViewById(R.id.contactUser);
        borrow = findViewById(R.id.sendRequest);
        cityProvince = findViewById(R.id.cityProvince);
        progressDialog.show();
        //getting data from intent
        contactUserUid = getIntent().getStringExtra("contactUid");
        currentUserUid = getIntent().getStringExtra("currentUserUid");
        rate = getIntent().getStringExtra("rate");

        if (contactUserUid.equals(currentUserUid)){
            contactUser.setVisibility(View.GONE);
            borrow.setVisibility(View.GONE);
        }

        FirebaseDatabase.getInstance().getReference("users")
                .child(contactUserUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        contactUserNickname = Objects.requireNonNull(dataSnapshot.child("nickname").getValue()).toString();
                        contactUserImageUrl = Objects.requireNonNull(dataSnapshot.child("imageUri").getValue()).toString();
                        contactUserCity = Objects.requireNonNull(dataSnapshot.child("city").getValue()).toString();
                        contactUserProvince = Objects.requireNonNull(dataSnapshot.child("province").getValue()).toString();
                        //Setting elements
                        if (!contactUserImageUrl.isEmpty())
                            Picasso.get().load(contactUserImageUrl).into(userImageIV);

                        String cityProvinceString = contactUserCity+" ( "+contactUserProvince+" )";
                        cityProvince.setText(cityProvinceString);
                        nicknameTV.setText(contactUserNickname);

                        progressDialog.dismiss();
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


        borrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BorrowBook.class);
                intent.putExtra("contactUid", contactUserUid);
                intent.putExtra("currentUserUid", currentUserUid);
                FirebaseDatabase.getInstance().getReference("users")
                        .child(currentUserUid)
                        .child("nickname")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                intent.putExtra("currentUserNickname", dataSnapshot.getValue().toString());
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });

        if (rate != null && rate.equals("yes")){
            rateButton.setVisibility(View.VISIBLE);
            rateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RateUserActivity.class);
                    intent.putExtra("currentUserUid",currentUserUid);
                    intent.putExtra("contactUserUid",contactUserUid);
                    FirebaseDatabase.getInstance().getReference("users")
                            .child(currentUserUid)
                            .child("nickname")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    intent.putExtra("currentUserNickname", dataSnapshot.getValue().toString());
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
