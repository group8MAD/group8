package it.polito.mad.group8;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class RateUserActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextView titleTV;
    private TextView commentTV;
    private Button rateBT;

    //
    private String contactUserUid;
    private Review review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_user);

        //getting views
        ratingBar = findViewById(R.id.stars);
        titleTV = findViewById(R.id.title);
        commentTV = findViewById(R.id.comment);
        rateBT = findViewById(R.id.rate);
        //initialization
        contactUserUid = getIntent().getStringExtra("contactUserUid");
        review = new Review();
        review.setReviewerUid(getIntent().getStringExtra("currentUserUid"));
        review.setReviewerNickname(getIntent().getStringExtra("currentUserNickname"));
        review.setReviewerImageUri(getIntent().getStringExtra("currentUserImageUri"));

        rateBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleTV.getText().toString().isEmpty() || commentTV.getText().toString().isEmpty() || ratingBar.getRating()==0){
                    Toast.makeText(RateUserActivity.this,R.string.reviewNotComplete,Toast.LENGTH_LONG).show();
                }else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(RateUserActivity.this);
                    alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            review.setComment(commentTV.getText().toString());
                            review.setTitle(titleTV.getText().toString());
                            review.setRating(ratingBar.getRating());
                            String key = FirebaseDatabase.getInstance().getReference("users")
                                    .child(contactUserUid)
                                    .child("reviews").push().getKey();
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(contactUserUid)
                                    .child("reviews")
                                    .child(key)
                                    .setValue(review);
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(review.getReviewerUid())
                                    .child("ongoing")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot exchange: dataSnapshot.getChildren()){
                                                if (exchange.child("bookOwnerUid").getValue().toString().equals(review.getReviewerUid())){
                                                    if (exchange.child("requesterUid").getValue().toString().equals(contactUserUid)
                                                            && Long.parseLong(exchange.child("endDate").getValue().toString()) < Calendar.getInstance().getTimeInMillis())
                                                        exchange.getRef().removeValue();
                                                }else {
                                                    if (exchange.child("bookOwnerUid").getValue().toString().equals(contactUserUid)
                                                            && Long.parseLong(exchange.child("endDate").getValue().toString()) < Calendar.getInstance().getTimeInMillis())
                                                        exchange.getRef().removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                            finish();
                        }
                    });
                    alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.setMessage(R.string.sendReview);
                    alert.show();
                }
            }
        });
    }
}
