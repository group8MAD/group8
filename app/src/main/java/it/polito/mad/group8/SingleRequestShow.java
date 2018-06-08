package it.polito.mad.group8;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
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

import java.text.SimpleDateFormat;
import java.util.Objects;

public class SingleRequestShow extends AppCompatActivity {

    //Request info
    private OngoingTransaction ongoingTransaction;

    //View
    private TextView nicknameTV;
    private ImageView imageTV;
    private ProgressDialog progressDialog;
    private TextView description1;
    private TextView description2;
    private TextView cityProvince;
    private TextView isbn;
    private Button accept;
    private Button deny;
    //Date formatter
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_request_show);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(SingleRequestShow.this);
        progressDialog.show();
        ongoingTransaction = new OngoingTransaction();
        //Getting view
        nicknameTV = findViewById(R.id.nickname);
        imageTV = findViewById(R.id.image);
        description1 = findViewById(R.id.description1);
        description2 = findViewById(R.id.description2);
        cityProvince = findViewById(R.id.cityProvince);
        isbn = findViewById(R.id.isbn);
        accept = findViewById(R.id.accept);
        deny = findViewById(R.id.deny);

        //Getting strings intent
        ongoingTransaction.setRequesterUid(getIntent().getStringExtra("requesterUid"));
        ongoingTransaction.setRequesterNickname(getIntent().getStringExtra("requesterNickname"));
        ongoingTransaction.setRequesterImageUri(getIntent().getStringExtra("requesterImageUri"));
        ongoingTransaction.setBookIsbn(getIntent().getStringExtra("bookIsbn"));
        ongoingTransaction.setBookTitle( getIntent().getStringExtra("bookTitle"));
        ongoingTransaction.setStartDate(getIntent().getStringExtra("startDate"));
        ongoingTransaction.setEndDate(getIntent().getStringExtra("endDate"));
        ongoingTransaction.setCity(getIntent().getStringExtra("city"));
        ongoingTransaction.setProvince(getIntent().getStringExtra("province"));
        ongoingTransaction.setBookOwnerUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //setting data
        nicknameTV.setText(ongoingTransaction.getRequesterNickname());
        String description1StringHTML = "<b>"+ ongoingTransaction.getRequesterNickname()+"</b> "+getString(R.string.wantsToBorrow)+" \"<b>"+ ongoingTransaction.getBookTitle()+"</b>\"";
        String description2StringHTML = getString(R.string.from)+" <b>"+formatter.format(Long.parseLong(ongoingTransaction.getStartDate()))+"</b> "+ getString(R.string.to)+" <b>"+formatter.format(Long.parseLong(ongoingTransaction.getEndDate()))+"</b>";
        String cityProvinceString = ongoingTransaction.getCity()+" ( "+ ongoingTransaction.getProvince()+" )";
        String isbnStringHYML = "ISBN <b>"+ ongoingTransaction.getBookIsbn()+"</b>";
        description1.setText(Html.fromHtml(description1StringHTML));
        description2.setText(Html.fromHtml(description2StringHTML));
        cityProvince.setText(cityProvinceString);
        isbn.setText(Html.fromHtml(isbnStringHYML));
        if (!ongoingTransaction.getRequesterImageUri().isEmpty())
            Picasso.get().load(ongoingTransaction.getRequesterImageUri()).into(imageTV);

        progressDialog.dismiss();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SingleRequestShow.this);
                //setting positive button
                alertBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String id = FirebaseDatabase.getInstance().getReference("users")
                                    .child(ongoingTransaction.getRequesterUid())
                                    .child("ongoing")
                                    .push().getKey();

                        FirebaseDatabase.getInstance().getReference("users")
                                .child(ongoingTransaction.getRequesterUid())
                                .child("ongoing")
                                .child(id)
                                .setValue(ongoingTransaction);


                        FirebaseDatabase.getInstance().getReference("users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("ongoing")
                                .child(id)
                                .setValue(ongoingTransaction);


                        FirebaseDatabase.getInstance().getReference("books")
                                .child(ongoingTransaction.getBookIsbn())
                                .child("owners")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("status")
                                .setValue("unavailable");

                        FirebaseDatabase.getInstance().getReference("users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("requests")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot requestTmp: dataSnapshot.getChildren()){
                                            if (requestTmp.child("requesterUid").getValue().toString().equals(ongoingTransaction.getRequesterUid()) && requestTmp.child("bookIsbn").getValue().toString().equals(ongoingTransaction.getBookIsbn())){
                                                requestTmp.getRef().removeValue();
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
                                        for (DataSnapshot requestTmp: dataSnapshot.getChildren()){
                                            if (requestTmp.child("requesterUid").getValue().toString().equals(ongoingTransaction.getRequesterUid()) && requestTmp.child("bookIsbn").getValue().toString().equals(ongoingTransaction.getBookIsbn())){
                                                requestTmp.getRef().removeValue();
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
