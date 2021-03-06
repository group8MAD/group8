package it.polito.mad.group8;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.Resource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SingleShowBookActivity extends AppCompatActivity {


    // Fields of the book
    private TextView title;
    private TextView authors;
    private TextView publisher;
    private TextView year;
    private ImageView image;
    private String isbn;
    private List<String> owners = new ArrayList<String>();
    private ListView chatList;
    // Button to open ChatActivity to send a message to the owner of the book



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_show_book);

        title = findViewById(R.id.title);
        authors = findViewById(R.id.authors);
        publisher = findViewById(R.id.publisher);
        year = findViewById(R.id.year);
        image = findViewById(R.id.image);
        chatList = findViewById(R.id.listView);

        isbn = getIntent().getStringExtra("isbn");
        title.setText(getIntent().getStringExtra("title"));
        authors.setText(getIntent().getStringExtra("authors"));
        publisher.setText(getIntent().getStringExtra("publisher"));
        year.setText(getIntent().getStringExtra("year"));

        Objects.requireNonNull(getSupportActionBar()).setTitle(title.getText().toString());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String imageUrl = getIntent().getStringExtra("url");
        Log.e("imageuri: ", imageUrl);
        if (!imageUrl.isEmpty()){
            Picasso.get().load(imageUrl).into(image);
        }

        FirebaseDatabase.getInstance().getReference("books/"+isbn+"/owners")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot owner: dataSnapshot.getChildren()){
                            if (Objects.requireNonNull(owner.child("status").getValue()).toString().equals("available"))
                                owners.add(owner.getKey()+"-"+isbn);
                        }
                        chatList.setAdapter(new SingleShowBookAdapter(getApplicationContext(), owners.toArray(new String[0])));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ownerUidAndIsbn = String.valueOf(parent.getItemAtPosition(position));
                String uid2 = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                Intent intent = new Intent(getApplication(), PublicShowProfile.class);

                String[] split = ownerUidAndIsbn.split("-");
                String uid1 = split[0];

                intent.putExtra("contactUid", uid1);
                intent.putExtra("currentUserUid", uid2);

                Log.e("\t\tSingleShowBookActivity\t\t\t\tCurrent user UID: ", uid2);
                Log.e("\t\tSingleShowBookActivity\t\t\t\tContact user UID: ", uid1);

                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
