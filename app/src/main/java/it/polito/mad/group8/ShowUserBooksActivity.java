package it.polito.mad.group8;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShowUserBooksActivity extends AppCompatActivity {

    private TextView nothing;
    private RecyclerView recyclerView;
    private SearchBookAdapter adapter;

    List<Book> books;
    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_books);

        books = new ArrayList<>();

        nothing = findViewById(R.id.nothing);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchBookAdapter(books, ShowUserBooksActivity.this);

        recyclerView.setAdapter(adapter);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.showbooks);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        userUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        FirebaseDatabase.getInstance().getReference("users")
                .child(userUid)
                .child("books")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        FirebaseDatabase.getInstance().getReference("books")
                                .child(dataSnapshot.getKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Book book = dataSnapshot.getValue(Book.class);
                                        books.add(book);
                                        adapter.notifyDataSetChanged();
                                        nothing.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        books.removeIf(b->b.getIsbn().equals(dataSnapshot.getKey()));
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
