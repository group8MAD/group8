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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;

public class SearchBookActivity extends AppCompatActivity {

    public static final String TAG = SearchBookActivity.class.getName();

    // Add Layout and Navigation menu
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;

    // Add firebase stuff
    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference myRef;

    // Add widgets
    private EditText filter; // Word to be searched for
    private Button button; // Searching button
    private ProgressBar bar; // Progress bar


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout.addDrawerListener(mToggle);

        //declare the db reference object to access the db (if not signed in, not usable)
        auth= FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference();
        FirebaseUser user = auth.getCurrentUser();

        button = findViewById(R.id.search_button);
        filter = findViewById(R.id.search_box);
        bar = findViewById(R.id.progress_bar);


        // Give utility to the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClik");
                // Send a message that indicates that app is charging
                Toast.makeText(SearchBookActivity.this, "Charging", Toast.LENGTH_SHORT).show();
                // Retrieve the text introduced in the TextView to be searched
                String words= filter.getText().toString();
                //Create an object AsyncTask  to be able to execute the threads en second plane
                RetrieveRssTask task = new RetrieveRssTask();
                task.execute(url, words);
            }
        });



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

                    default:
                        mDrawerLayout.closeDrawers();
                }
                return true;
            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public void signOut(){
        mDrawerLayout.closeDrawers();
        AuthUI.getInstance()
                .signOut(SearchBookActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(SearchBookActivity.this, ShareBookActivity.class));
                    }
                });
    }

    // Private class to retrieve the books that have been searched for
    private class RetrieveRssTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
            Log.d(TAG, "Entering on onPreExecute");
        }

        @Override
        protected Void doInBackground(String... strings) {
            Log.d(TAG, "Entering on doInBackground");
            FeedDownloader rss = new FeedDownloader();
            try {
                List<RssContent.EntryRss> entries = rss.loadXmlFromNetwork(strings[0]);
                FilteredRssFeed.reset(strings[1]);
                for (RssContent.EntryRss item : entries) {
                    FilteredRssFeed.add(item);
                    Log.d(TAG, " item added");
                }
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            } catch (ParseException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Log.d(TAG, "Entering on the method onPostExecute");
            bar.setVisibility(View.INVISIBLE);
            // Creation of the intent EXPLICIT to change to ListViewActivity
            Intent intent = new Intent(SearchBookActivity.this, ListViewActivity.class);
            startActivity(intent);
        }
    }

}
