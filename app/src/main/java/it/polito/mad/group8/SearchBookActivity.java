package it.polito.mad.group8;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchBookActivity extends AppCompatActivity {
    public final int SIGN_IN = 1000;
    public static final String TAG = SearchBookActivity.class.getName();

    // Add Layout and Navigation menu
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private TextView requestsNr;
    private TextView chatsNr;
    // Add firebase stuff
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference myRef;
    private int chatsCounter, requestsCounter;

    //User...is initialized in updateUi if the user is logged in
    private User user = new User();
    private String userID;
    private DatabaseReference usersRef;

    List<Book> books = new ArrayList<>();
    SearchBookAdapter adapter;
    // Add widgets
    RecyclerView recyclerView;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        chatsCounter = 0;
        requestsCounter = 0;

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout.addDrawerListener(mToggle);

        recyclerView = findViewById(R.id.recyclerView);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.search_books);

        updateUi(FirebaseAuth.getInstance().getCurrentUser());
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            //Adding listener on messageCounter
            FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            chatsCounter = 0;
                            requestsCounter = 0;
                            for (DataSnapshot chat : dataSnapshot.child("chats").getChildren()) {
                                chatsCounter += Integer.parseInt(chat.child("notRead").getValue().toString());
                            }
                            requestsCounter =(int) dataSnapshot.child("requests").getChildrenCount();
                            setMenuCounter();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
        // Creation of the lateral menu
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_profile:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(SearchBookActivity.this, ShowProfile.class));
                        return true;

                    case R.id.nav_show_books:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(SearchBookActivity.this, ShowUserBooksActivity.class));
                        return true;

                    case R.id.nav_share_books_logged:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(SearchBookActivity.this, ShareBookActivity.class));
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
                        return true;

                    case R.id.requests:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(getApplicationContext(), RequestActivity.class));
                        return true;

                    case R.id.logout:
                        signOut();
                        return true;

                    case R.id.sign:
                        signIn();
                        return true;

                    default:
                        mDrawerLayout.closeDrawers();
                }
                return true;
            }
        });

        // Assignment of a Layout Manager, in this case, Liner Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchBookAdapter(books, SearchBookActivity.this);
        recyclerView.setAdapter(adapter);
        getBooks();



    }


    private void setMenuCounter() {
        chatsNr.setText(String.valueOf(chatsCounter));
        requestsNr.setText(String.valueOf(requestsCounter));
    }

    //This is useful for when you're not logged in and you log in
    //if you don't updateUi onResume lateral menu won't change
    @Override
    protected void onResume() {
        super.onResume();
        updateUi(FirebaseAuth.getInstance().getCurrentUser());
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            setMenuCounter();
    }



    public void signOut(){
        mDrawerLayout.closeDrawers();
        AuthUI.getInstance()
                .signOut(SearchBookActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUi(FirebaseAuth.getInstance().getCurrentUser());
                    }
                });
    }

    public void signIn(){
        mDrawerLayout.closeDrawers();
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .build(),
                SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else if(requestCode == SIGN_IN) {
                // Update the UI if receive the corresponding parameter SIGN_IN from startActivityForResult
                updateUi(FirebaseAuth.getInstance().getCurrentUser());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MenuInflater inflater = getMenuInflater();

        // Inflates the Searching menu and puts the icon of searching on the top bar
        inflater.inflate(R.menu.menu_search, menu);
        // Gets the id of the icon and gives it functionality
        MenuItem item = menu.findItem(R.id.searchBook);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String newText) {
                filterBookList(newText);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Update the interface in case of signing in or out
    public void updateUi(FirebaseUser currentUser){
        if (currentUser != null){
            this.userID = currentUser.getUid();
            mNavigationView.getMenu().clear();
            mNavigationView.inflateMenu(R.menu.menu_drawer_loggedin);
            requestsNr = (TextView) mNavigationView.getMenu().findItem(R.id.requests).getActionView();
            chatsNr = (TextView) mNavigationView.getMenu().findItem(R.id.chats).getActionView();
            usersRef = database.getReference("users/"+this.userID);
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getData(dataSnapshot);
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

    //Getting user data to be shown in the header
    private void getData(DataSnapshot dataSnapshot){
        if(!dataSnapshot.exists()){
            user.setName(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
            user.setEmail(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
            dataSnapshot.getRef().setValue(user);
        }else{
            this.user.setName(dataSnapshot.getValue(User.class).getName());
            this.user.setEmail(dataSnapshot.getValue(User.class).getEmail());
            this.user.setBiography(dataSnapshot.getValue(User.class).getBiography());
            this.user.setNickname(dataSnapshot.getValue(User.class).getNickname());
            /*TODO The field Biography is not include in the file drawer_header.xml*/

            setHeaderDrawer();
        }
    }

    public void setHeaderDrawer(){
        View headerView = mNavigationView.getHeaderView(0);

        CircleImageView image = headerView.findViewById(R.id.header_image);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getBooks(){
        FirebaseDatabase.getInstance()
                .getReference("books")
                .orderByChild("title")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Book book = dataSnapshot.getValue(Book.class);
                        assert book != null;
                        book.setIsbn(dataSnapshot.getKey());
                        Log.e("SingleBookTitle", book.getTitle());
                        if (!books.contains(book))
                            books.add(book);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

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



    private int matchedKeyWords(Book book, String search){
        int i = 0;
        List<String> keyWords = new ArrayList<>();
        String[] requestedKeyWords = search.toLowerCase().split(" ");
        keyWords.addAll(Arrays.asList(book.getTitle().toLowerCase().split(" ")));
        keyWords.addAll(Arrays.asList(book.getAuthors().toLowerCase().split(" ")));
        keyWords.addAll(Arrays.asList(book.getPublisher().toLowerCase().split(" ")));
        for (String s : requestedKeyWords){
            if (keyWords.contains(s)){
                i++;
            }
        }
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void filterBookList(String search){
        if (!search.isEmpty()){
            Map<Book, Integer> filteredBooks = new HashMap<>();
            for (Book b: books){
                int occurrences = matchedKeyWords(b, search);
                if (occurrences > 0){
                    filteredBooks.put(b,occurrences);
                }
                Log.e("Number of ocurrences: ", String.valueOf(occurrences));
            }

            SearchBookAdapter searchBookAdapter = new SearchBookAdapter(filteredBooks.entrySet().stream()
                                                            .sorted((e1, e2)-> e2.getValue().compareTo(e1.getValue()))
                                                            .map(e -> e.getKey())
                                                            .collect(Collectors.toList()), SearchBookActivity.this);
            recyclerView.setAdapter(searchBookAdapter);
        }else{
            SearchBookAdapter searchBookAdapter = new SearchBookAdapter(books, SearchBookActivity.this);
            recyclerView.setAdapter(searchBookAdapter);
        }

    }

}
