package it.polito.mad.group8;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookActivity extends AppCompatActivity {

    public final int SIGN_IN = 1000;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private User user;
    private String userID;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef;
    private DatabaseReference booksRef;
    Book book;

    Button button;
    ProgressDialog pd;

    TextView title;
    TextView authors;
    TextView publisher;
    TextView year;
    ImageView image;
    EditText isbn;
    Button scan;
    Button publish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.user = new User();
        setContentView(R.layout.activity_book);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout.addDrawerListener(mToggle);
        book = new Book();
        booksRef = database.getReference("books/"+FirebaseAuth.getInstance().getCurrentUser().getUid());

        title = findViewById(R.id.title);
        authors = findViewById(R.id.authors);
        publisher = findViewById(R.id.publisher);
        year = findViewById(R.id.year);
        image = findViewById(R.id.image);
        button = findViewById(R.id.button);
        isbn = findViewById(R.id.isbn);
        scan = findViewById(R.id.scan);
        publish = findViewById(R.id.publish);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(BookActivity.this).setOrientationLocked(true).initiateScan();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JsonTask().execute("https://www.googleapis.com/books/v1/volumes?q=isbn:"+isbn.getText().toString());
            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               booksRef.setValue(book);
            }
        });
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_profile:
                        startProfileActivity();
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

    @Override
    protected void onStart() {
        super.onStart();
        updateUi(FirebaseAuth.getInstance().getCurrentUser());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open,R.string.close);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                new JsonTask().execute("https://www.googleapis.com/books/v1/volumes?q=isbn:"+result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
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
    public void signOut(){
        mDrawerLayout.closeDrawers();
        AuthUI.getInstance()
                .signOut(BookActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUi(FirebaseAuth.getInstance().getCurrentUser());
                    }
                });
    }
    public void startProfileActivity(){
        Intent intent = new Intent(BookActivity.this,ShowProfile.class);
        finish();
        startActivity(intent);
    }
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
        TextView name = headerView.findViewById(R.id.header_name);
        TextView email = headerView.findViewById(R.id.header_email);
        CircleImageView image = headerView.findViewById(R.id.header_image);

        name.setText(this.user.getName());
        email.setText(this.user.getEmail());

    }


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(BookActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();

        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            try{
                JSONObject object = new JSONObject(result);
                JSONObject bookObject = object.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo");
                book.setTitle(bookObject.getString("title"));
                book.setEditionYear(bookObject.getString("publishedDate"));
                JSONArray authors = bookObject.getJSONArray("authors");
                StringBuilder authorsString = new StringBuilder();

                for (int i = 0; i < authors.length(); i++){
                    authorsString.append(authors.get(i).toString()+", ");
                }
                book.setAuthors(authorsString.toString());
                book.setPublisher(bookObject.getString("publisher"));


            }catch (JSONException e){
                Toast.makeText(BookActivity.this,"non riuscito", Toast.LENGTH_LONG).show();
            }
            try {
                JSONObject object = new JSONObject(result);
                String imageInfo = object.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail");
                book.setThumbnail(imageInfo);

            }catch (JSONException e){
                Toast.makeText(BookActivity.this,"no thumbnail", Toast.LENGTH_LONG).show();
            }
            book.setOwnerID(FirebaseAuth.getInstance().getCurrentUser().getUid());


            title.setText(book.getTitle());
            authors.setText(book.getAuthors());
            year.setText(book.getEditionYear());
            publisher.setText(book.getPublisher());

            if (!book.getThumbnail().isEmpty()){
                Picasso.get().load(book.getThumbnail()).into(image);
            }
        }
    }
}


