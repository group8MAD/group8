package it.polito.mad.group8;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShowReviewsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView nothing;
    private List<Review> reviews;
    private ShowReviewsActivityAdapter adapter;
    private String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reviews);

        user = getIntent().getStringExtra("user");
        //getting view
        recyclerView = findViewById(R.id.recyclerView_reviews_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        nothing = findViewById(R.id.nothing);

        reviews = new ArrayList<>();
        adapter = new ShowReviewsActivityAdapter(reviews, this);
        recyclerView.setAdapter(adapter);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.reviews);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        FirebaseDatabase.getInstance().getReference("users")
                .child(user)
                .child("reviews")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Review review = dataSnapshot.getValue(Review.class);
                        reviews.add(review);
                        adapter.notifyDataSetChanged();
                        nothing.setVisibility(View.GONE);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
