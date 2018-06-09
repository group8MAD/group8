package it.polito.mad.group8;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DeleteBook extends AppCompatActivity {

    private String currentUserUid;
    private String isbn;
    private String title;
    private String authors;
    private String publisher;
    private String year;
    private String url;

    private TextView titleTV;
    private TextView authorsTV;
    private TextView publisherTV;
    private TextView isbnTV;
    private TextView yearTV;
    private ImageView thumbnailTV;
    private Button deleteBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_book);

        titleTV = findViewById(R.id.title);
        authorsTV = findViewById(R.id.authors);
        publisherTV = findViewById(R.id.publisher);
        isbnTV = findViewById(R.id.isbn);
        yearTV = findViewById(R.id.year);
        thumbnailTV = findViewById(R.id.image);
        deleteBT = findViewById(R.id.delete);

        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        isbn = getIntent().getStringExtra("isbn");
        title = getIntent().getStringExtra("title");
        authors = getIntent().getStringExtra("authors");
        publisher = getIntent().getStringExtra("publisher");
        year = getIntent().getStringExtra("year");
        url = getIntent().getStringExtra("url");

        titleTV.setText(title);
        authorsTV.setText(authors);
        publisherTV.setText(publisher);
        yearTV.setText(year);
        isbnTV.setText(isbn);

        if (!url.isEmpty()){
            Picasso.get().load(url).into(thumbnailTV);
        }

        getSupportActionBar().setTitle(R.string.deleteBook);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deleteBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(DeleteBook.this);
                alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(currentUserUid)
                                .child("books")
                                .child(isbn).removeValue();
                        FirebaseDatabase.getInstance().getReference("books")
                                .child(isbn)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child("owners").getChildrenCount() == 1){
                                            dataSnapshot.getRef().removeValue();
                                        }else {
                                            dataSnapshot.child("owners").child(currentUserUid).getRef().removeValue();
                                        }
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                });
                alert.setMessage(R.string.confirmDelete);
                alert.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
