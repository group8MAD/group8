package it.polito.mad.group8;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchBookActivity extends AppCompatActivity {
    public final int SIGN_IN = 1000;
    public static final String TAG = SearchBookActivity.class.getName();

    // Add Layout and Navigation menu
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;

    // Add firebase stuff
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference myRef;

    //User...is initialized in updateUi if the user is logged in
    private User user = new User();;
    private String userID;
    private DatabaseReference usersRef;


    // Add widgets



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout.addDrawerListener(mToggle);

        updateUi(FirebaseAuth.getInstance().getCurrentUser());

        // Creation of the lateral menu
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_profile:
                        finish();
                        startActivity(new Intent(SearchBookActivity.this, ShowProfile.class));
                        return true;

                    case R.id.nav_user_books:
                        finish();
                        startActivity(new Intent(SearchBookActivity.this, ShowBooks.class));
                        return true;

                    case R.id.nav_share_books_logged:
                        finish();
                        startActivity(new Intent(SearchBookActivity.this, ShareBookActivity.class));
                        return true;

                    case R.id.nav_search_books:
                        mDrawerLayout.closeDrawers();
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


    }
    //This is useful for when you're not logged in and you log in
    //if you don't updateUi onStart lateral menu won't change
    @Override
    protected void onStart() {
        super.onStart();
        updateUi(FirebaseAuth.getInstance().getCurrentUser());
    }

    public void signOut(){
        mDrawerLayout.closeDrawers();
        AuthUI.getInstance()
                .signOut(SearchBookActivity.this);
        updateUi(FirebaseAuth.getInstance().getCurrentUser());
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

    //Update the interface in case of signing in or out
    public void updateUi(FirebaseUser currentUser){
        if (currentUser != null){
            this.userID = currentUser.getUid();
            mNavigationView.getMenu().clear();
            mNavigationView.inflateMenu(R.menu.menu_drawer_loggedin);
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
            usersRef.setValue(this.user);
        }else{
            this.user.setName(dataSnapshot.getValue(User.class).getName());
            this.user.setEmail(dataSnapshot.getValue(User.class).getEmail());
            this.user.setBiography(dataSnapshot.getValue(User.class).getBiography());

            setHeaderDrawer();
        }
    }

    public void setHeaderDrawer(){
        View headerView = mNavigationView.getHeaderView(0);

        CircleImageView image = headerView.findViewById(R.id.header_image);
    }

}
