package it.polito.mad.group8;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

    ArrayList<Book> booksSaved = new ArrayList<>();

    // Add widgets
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_books);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout.addDrawerListener(mToggle);

        recyclerView = findViewById(R.id.recyclerView);

        //declare the db reference object to access the db (if not signed in, not usable)
        auth= FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();

        updateUi(FirebaseAuth.getInstance().getCurrentUser());


        // Creation of the lateral menu
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_profile:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(ShowBooks.this, ShowProfile.class));
                        return true;

                    case R.id.nav_user_books:
                        mDrawerLayout.closeDrawers();
                        return true;

                    case R.id.nav_share_books_logged:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(getApplicationContext(), ShareBookActivity.class));
                        return true;

                    case R.id.ongoing:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(getApplicationContext(), OngoingExchangesActivity.class));
                        return true;

                    case R.id.chats:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(getApplicationContext(), ChatList.class));
                        return true;

                    case R.id.nav_search_books:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(getApplicationContext(), SearchBookActivity.class));
                        return true;

                    case R.id.requests:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(getApplicationContext(), RequestActivity.class));
                        return true;

                    case R.id.logout:
                        signOut();
                        return true;

                    default:
                        mDrawerLayout.closeDrawers();
                }
                return true;
            }

        });

        // Assignment of a Layout Manager, in this case, Liner Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //myRef = db.getReference("users/"+this.userID);
        myRef = db.getReference("users/"+"MHEQvCHw7BYxpVufiejb0A4JM9q1");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateUi(FirebaseAuth.getInstance().getCurrentUser());

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
                        //setMenuCounter(counter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /*private void setMenuCounter(int count) {
        TextView view = (TextView) mNavigationView.getMenu().findItem(R.id.chats).getActionView();
        view.setText(String.valueOf(count));
    }*/

    private void showData(DataSnapshot dataSnapshot) {
        if(!dataSnapshot.child("books").exists()){
            return;
        }else {
            //User uInfo = new User();
            ArrayList<Book> booksSaved = new ArrayList<>();
            //DatabaseReference dbAux = db.getReference("books");

            //im trying to get the books saved by title (the isbn is not a property in the book class)
            for (DataSnapshot ds: dataSnapshot.child("books").getChildren()) {
                Book bookAux = new Book();
                String idBook = ds.getKey();
                DatabaseReference dbAux = db.getReference("books/" + idBook);
                dbAux.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        bookAux.setTitle(dataSnapshot.child("title").getValue().toString());
                        Log.d(TAG, "Title: "+ bookAux.getTitle().toString());
                        bookAux.setAuthors(dataSnapshot.child("authors").getValue().toString());
                        Log.d(TAG, "Author: "+ bookAux.getAuthors().toString());
                        bookAux.setEditionYear(dataSnapshot.child("editionYear").getValue().toString());
                        Log.d(TAG, "Edition Year: "+ bookAux.getEditionYear().toString());
                        booksSaved.add(bookAux);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            //uInfo.setBooksSaved(dataSnapshot.getValue(User.class).getBookList());

            //show the info to see if its done correctly
            //Log.d(TAG, "showData: booklist" + uInfo.getTitleList());

            /*ArrayList<Book> books = new ArrayList<Book>();
            //bucle that puts the information of each book in an array
            for (Book book : booksSaved) {
                books.add(book);
            }*/

            ShowBooksAdapter adapter = new ShowBooksAdapter(booksSaved, ShowBooks.this);
            recyclerView.setAdapter(adapter);
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

    //Update the interface in case of signing in or out
    public void updateUi(FirebaseUser currentUser){
        if (currentUser != null){
            this.userID = currentUser.getUid();
            mNavigationView.getMenu().clear();
            mNavigationView.inflateMenu(R.menu.menu_drawer_loggedin);
            myRef = db.getReference("users/"+"MHEQvCHw7BYxpVufiejb0A4JM9q1");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    showData(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else{
            mNavigationView.getMenu().clear();
            mNavigationView.inflateMenu(R.menu.menu_drawer_not_loggedin);
        }
    }



}
