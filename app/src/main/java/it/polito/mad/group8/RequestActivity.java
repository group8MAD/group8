package it.polito.mad.group8;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private List<Request> requests;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        requests = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView_requests_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new RequestAdapter(requests, RequestActivity.this);
        recyclerView.setAdapter(adapter);

        getRequests();


    }


    private void getRequests(){
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("requests")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Request requestTmp = dataSnapshot.getValue(Request.class);
                        requests.add(0, requestTmp);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        requests.removeIf(t->t.getRequesterUid().equals(dataSnapshot.child("requesterUid").getValue().toString())
                                        && t.getBookIsbn().equals(dataSnapshot.child("bookIsbn").getValue().toString()));
                        adapter.notifyDataSetChanged();
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
