package it.polito.mad.group8;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OngoingExchangesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OngoingExchangesActivityAdapter adapter;
    private TextView nothing;
    private List<OngoingTransaction> ongoingTransactions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_exchanges);

        ongoingTransactions = new ArrayList<>();
        nothing = findViewById(R.id.nothing);
        recyclerView = findViewById(R.id.recyclerView_requests_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.transactions);
        adapter = new OngoingExchangesActivityAdapter(ongoingTransactions, OngoingExchangesActivity.this);
        recyclerView.setAdapter(adapter);

        getTransactions();


    }


    private void getTransactions(){
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("ongoing")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        OngoingTransaction requestTmp = dataSnapshot.getValue(OngoingTransaction.class);
                        ongoingTransactions.add(0, requestTmp);
                        adapter.notifyDataSetChanged();
                        nothing.setVisibility(View.GONE);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        ongoingTransactions.removeIf(t->t.getRequesterUid().equals(dataSnapshot.child("requesterUid").getValue().toString())
                                && t.getBookIsbn().equals(dataSnapshot.child("bookIsbn").getValue().toString()));
                        adapter.notifyDataSetChanged();
                        if (ongoingTransactions.size()==0){
                            nothing.setVisibility(View.VISIBLE);
                        }
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
