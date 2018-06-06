package it.polito.mad.group8;

import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class SingleRequestShow extends AppCompatActivity {

    //Request info
    private String requesterUid;
    private String requesterNickname;
    private String requesterUri;
    private String bookIsbn;
    private String bookTitle;
    private String startDate;
    private String endDate;
    private String city;
    private String province;

    //View
    private TextView nicknameTV;
    private ImageView imageTV;
    private ProgressBar ratingBar;
    private TextView description1;
    private TextView description2;
    private TextView cityProvince;
    private TextView isbn;
    private Button accept;
    private Button deny;
    //Date formatter
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_request_show);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Getting view
        nicknameTV = findViewById(R.id.nickname);
        ratingBar = findViewById(R.id.progressBar);
        imageTV = findViewById(R.id.image);
        description1 = findViewById(R.id.description1);
        description2 = findViewById(R.id.description2);
        cityProvince = findViewById(R.id.cityProvince);
        isbn = findViewById(R.id.isbn);
        accept = findViewById(R.id.accept);
        deny = findViewById(R.id.deny);

        //Getting strings intent
        requesterUid = getIntent().getStringExtra("requesterUid");
        requesterNickname = getIntent().getStringExtra("requesterNickname");
        requesterUri = getIntent().getStringExtra("requesterImageUri");
        bookIsbn = getIntent().getStringExtra("bookIsbn");
        bookTitle = getIntent().getStringExtra("bookTitle");
        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");
        city = getIntent().getStringExtra("city");
        province = getIntent().getStringExtra("province");

        //setting data
        nicknameTV.setText(requesterNickname);
        String description1StringHTML = "<b>"+requesterNickname+"</b> "+getString(R.string.wantsToBorrow)+" \"<b>"+bookTitle+"</b>\"";
        String description2StringHTML = getString(R.string.from)+" <b>"+formatter.format(Long.parseLong(startDate))+"</b> "+ getString(R.string.to)+" <b>"+formatter.format(Long.parseLong(endDate))+"</b>";
        String cityProvinceString = city+" ( "+province+" )";
        String isbnStringHYML = "ISBN <b>"+bookIsbn+"</b>";
        description1.setText(Html.fromHtml(description1StringHTML));
        description2.setText(Html.fromHtml(description2StringHTML));
        cityProvince.setText(cityProvinceString);
        isbn.setText(Html.fromHtml(isbnStringHYML));
        if (!requesterUri.isEmpty())
            Picasso.get().load(requesterUri).into(imageTV);

        ratingBar.setVisibility(View.GONE);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SingleRequestShow.this);
                //setting positive button
                alertBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                //setting positive button
                alertBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertBuilder.setMessage(getString(R.string.reminderAccept));
                alertBuilder.show();
            }
        });


        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SingleRequestShow.this);
                //setting positive button
                alertBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("requests")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot request: dataSnapshot.getChildren()){
                                            if (request.child("requesterUid").getValue().toString().equals(requesterUid) && request.child("bookIsbn").getValue().toString().equals(bookIsbn)){
                                                request.getRef().removeValue();
                                                finish();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                });
                //setting positive button
                alertBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertBuilder.setMessage(getString(R.string.reminderDecline));
                alertBuilder.show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
