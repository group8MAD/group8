package it.polito.mad.group8;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BorrowBook extends AppCompatActivity {

    private Button chooseBookButton;
    private Button startDateButton;
    private Button endDateButton;
    private Button sendRequest;
    private Calendar calendarStart;
    private Calendar calendarEnd;
    private Map<String, String> books;

    private String bookOwnerUid;
    private String currentUserUid;
    private String currentUserNickname;
    Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_book);

        bookOwnerUid = getIntent().getStringExtra("contactUid");
        currentUserUid = getIntent().getStringExtra("currentUserUid");
        currentUserNickname = getIntent().getStringExtra("currentUserNickname");
        books = new HashMap<>();
        //getting View
        chooseBookButton = findViewById(R.id.chooseBook);
        endDateButton = findViewById(R.id.endDateButton);
        startDateButton = findViewById(R.id.startDateButton);
        sendRequest = findViewById(R.id.sendRequest);
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();
        //setting dates
        calendarEnd.set(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH)+1, calendarStart.get(Calendar.DAY_OF_MONTH));
        //setting request
        request = new Request();
        request.setRequesterUid(currentUserUid);
        request.setRequesterNickname(currentUserNickname);


        Log.e("\t\tBorrowBook\t\t\t\t", bookOwnerUid);

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(BorrowBook.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendarStart.set(year,month,dayOfMonth);
                        calendarEnd.set(year,month+1,dayOfMonth);
                        startDateButton.setText(calendarStart.get(Calendar.DAY_OF_MONTH) +"/"+ String.valueOf(calendarStart.get(Calendar.MONTH)+1) +"/"+calendarStart.get(Calendar.YEAR));
                        request.setStartDate(String.valueOf(calendarStart.getTimeInMillis()));
                    }
                },calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(calendarStart.getTimeInMillis());
                datePickerDialog.show();

            }
        });


        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(BorrowBook.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendarEnd.set(year,month+1,dayOfMonth);
                        endDateButton.setText(calendarEnd.get(Calendar.DAY_OF_MONTH) +"/"+ String.valueOf(calendarEnd.get(Calendar.MONTH)+1) +"/"+calendarEnd.get(Calendar.YEAR));
                        request.setEndDate(String.valueOf(calendarEnd.getTimeInMillis()));
                    }
                },calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(calendarStart.getTimeInMillis());
                datePickerDialog.getDatePicker().setMaxDate(calendarEnd.getTimeInMillis());
                datePickerDialog.show();

            }
        });

        chooseBookButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                List<String> booksTitle = books.entrySet().stream()
                                            .sorted((e1, e2)-> e2.getValue().compareTo(e1.getValue()))
                                            .map(Map.Entry::getValue)
                                            .collect(Collectors.toList());
                AlertDialog.Builder builder = new AlertDialog.Builder(BorrowBook.this);
                builder.setTitle(getString(R.string.selectBook));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            chooseBookButton.setText(booksTitle.get(((AlertDialog)dialog).getListView().getCheckedItemPosition()));
                            request.setBookTitle(chooseBookButton.getText().toString());
                            request.setBookIsbn(books.entrySet().stream()
                                                    .filter(entry -> Objects.equals(entry.getValue(),request.getBookTitle()))
                                                    .map(Map.Entry::getKey)
                                                    .collect(Collectors.joining())
                            );
                        }
                });
                builder.setSingleChoiceItems(booksTitle.toArray(new CharSequence[booksTitle.size()]), 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("requesterUID\t\t", request.getRequesterUid());
                Log.e("requesterNickname\t\t", request.getRequesterNickname());
                Log.e("bookISBN\t\t", request.getBookIsbn());
                Log.e("bookTitle\t\t", request.getBookTitle());
                Log.e("startDate\t\t", request.getStartDate());
                Log.e("endDate\t\t", request.getEndDate());

                FirebaseDatabase.getInstance().getReference("users")
                        .child(request.getRequesterUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                request.setCity(dataSnapshot.child("city").getValue().toString());
                                request.setProvince(dataSnapshot.child("province").getValue().toString());
                                request.setRequesterImageUri(dataSnapshot.child("imageUri").getValue().toString());
                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(bookOwnerUid)
                                        .child("requests")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                int flag = 0;
                                                for (DataSnapshot requestTmp: dataSnapshot.getChildren()){
                                                    if (        Objects.requireNonNull(requestTmp.child("bookIsbn").getValue()).toString().equals(request.getBookIsbn())
                                                            &&  Objects.requireNonNull(requestTmp.child("requesterUid").getValue()).toString().equals(request.getRequesterUid())){

                                                        flag = 1;
                                                        //alertDialog
                                                        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(BorrowBook.this);
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
                                                        alertBuilder.setMessage(getString(R.string.alreadySent));
                                                        alertBuilder.show();
                                                    }
                                                }
                                                if (flag == 0){
                                                    FirebaseDatabase.getInstance().getReference("users")
                                                            .child(bookOwnerUid)
                                                            .child("requests")
                                                            .child(dataSnapshot.getRef().push().getKey())
                                                            .setValue(request);
                                                    Toast.makeText(BorrowBook.this,R.string.requestSent,Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });





            }
        });


        FirebaseDatabase.getInstance().getReference("users")
                .child(bookOwnerUid)
                .child("books")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot book: dataSnapshot.getChildren()){
                            if (book.child("status").getValue().toString().equals("available")){
                                books.put(book.getKey(), book.child("title").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
