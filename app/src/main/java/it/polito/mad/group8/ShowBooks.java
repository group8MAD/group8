package it.polito.mad.group8;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowBooks extends AppCompatActivity {

    private static final String TAG = ShowBooks.class.getName();

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;

    // Add firebase stuff
    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference myRef;
    private String userID;

    //The layout
    private ListView list;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_books);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout.addDrawerListener(mToggle);


        list = (ListView) findViewById(R.id.listview);
        button = (Button) findViewById(R.id.button_books);

        //declare the db reference object to access the db (if not signed in, not usable)

        auth= FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Private method that shows the data whenever its updated
                        showData(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("chats")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int counter = 0;
                        for (DataSnapshot chat: dataSnapshot.getChildren()){
                            counter += Integer.parseInt(chat.child("notRead").getValue().toString());
                        }
                        setMenuCounter(counter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setMenuCounter(int count) {
        if(mNavigationView.getMenu().findItem(R.id.chats).getActionView() != null){
            TextView view = (TextView) mNavigationView.getMenu().findItem(R.id.chats).getActionView();
            mNavigationView.getMenu().findItem(R.id.chats).setTitle("asd");
            view.setText(String.valueOf(count));
        }

    }
    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            User uInfo = new User();
            //im trying to get the books saved by title (the isbn is not a property in the book class)
            uInfo.setBooksSaved(ds.child(userID).getValue(User.class).getBookList());

            //show the info to see if its done correctly
            Log.d(TAG, "showData: booklist" + uInfo.getTitleList());

            ArrayList<String> array = new ArrayList<String>();
            //bucle that puts the information of each book in an array
            for(Book book : uInfo.getBookList()){
                array.add(book.getTitle());
                array.add(book.getAuthors());
                array.add(book.getEditionYear());
            }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, array);
            list.setAdapter(adapter);
        }
    }

    public void signOut(){
        mDrawerLayout.closeDrawers();
        AuthUI.getInstance()
                .signOut(ShowBooks.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(ShowBooks.this, SearchBookActivity.class));
                    }
                });
    }



}
