package it.polito.mad.group8;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PersistableBundle;
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
    String isbn;

    Button button;
    ProgressDialog pd;

    TextView title;
    TextView authors;
    TextView publisher;
    TextView year;
    ImageView image;
    EditText isbnEditTextView;
    Button scan;
    Button publish;
    int choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.user = new User();
        setContentView(R.layout.activity_book);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout.addDrawerListener(mToggle);
        book = new Book();
        booksRef = database.getReference("books/");

        title = findViewById(R.id.title);
        authors = findViewById(R.id.authors);
        publisher = findViewById(R.id.publisher);
        year = findViewById(R.id.year);
        image = findViewById(R.id.image);
        button = findViewById(R.id.button);
        isbnEditTextView = findViewById(R.id.isbn);
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
                isbn = isbnEditTextView.getText().toString();
                new JsonTask().execute("https://www.googleapis.com/books/v1/volumes?q=isbn:"+isbn);
            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String options[] = new String[] {getResources().getString(R.string.asNew),
                        getResources().getString(R.string.veryGood),
                        getResources().getString(R.string.good),
                        getResources().getString(R.string.fair),
                        getResources().getString(R.string.poor)};

                choice = 2;

                AlertDialog.Builder builder = new AlertDialog.Builder(BookActivity.this);
                builder.setTitle(R.string.dialogTitle)
                        .setPositiveButton(R.string.publish, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                                    publishBook(options[choice]);
                                }else{
                                    Toast.makeText(BookActivity.this,R.string.notSignedIn,Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setSingleChoiceItems(options, 2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                choice = which;
                            }
                        })
                ;
                builder.show();

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
                isbn = result.getContents();
                new JsonTask().execute("https://www.googleapis.com/books/v1/volumes?q=isbn:"+isbn);
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


    //signOut User and update DrawerMenu
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

    //Switching to ShowProfile activity
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

        CircleImageView image = headerView.findViewById(R.id.header_image);



    }

    //Getting the JSON text from the https://www.googleapis.com/books/v1/volumes?q=isbn:.....
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
                    Log.d("Response: ", "> " + line);

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
        protected void onPostExecute(String result) {   //result contains JSON text
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            //creating a JSON Object from JSON text
            try{
                JSONObject object = new JSONObject(result);
                JSONObject bookObject = object.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo"); //navigating the DOM tree and getting the first book found on the file
                book.setTitle(bookObject.getString("title"));
                book.setEditionYear(bookObject.getString("publishedDate"));
                JSONArray authors = bookObject.getJSONArray("authors");

                //creating a String in order to append all the authors
                StringBuilder authorsString = new StringBuilder();
                for (int i = 0; i < authors.length(); i++){
                    authorsString.append(authors.get(i).toString()+", ");
                }

                book.setAuthors(authorsString.toString().substring(0,authorsString.length()-2));
                book.setPublisher(bookObject.getString("publisher"));


            }catch (JSONException e){  //throw if some data doesn't exist
                Toast.makeText(BookActivity.this, R.string.noData, Toast.LENGTH_LONG).show();
            }
            //trying to get book thumbnail
            try {
                JSONObject object = new JSONObject(result);
                String imageInfo = object.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail");
                book.setThumbnail(imageInfo);
            }catch (JSONException e){
                Toast.makeText(BookActivity.this,R.string.noThumbnail, Toast.LENGTH_SHORT).show();
            }


            title.setText(book.getTitle());
            authors.setText(book.getAuthors());
            year.setText(book.getEditionYear());
            publisher.setText(book.getPublisher());

            //Check if thumbnail exists
            if (!book.getThumbnail().isEmpty()){
                Picasso.get().load(book.getThumbnail()).into(image); //Using Picasso library to load and URL into imageView
            }
        }
    }

    //publishing book on the database like /books/isbn/{bookInfo}
    public void publishBook(final String condition){
        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isbn!= null && !isbn.isEmpty() && !book.getTitle().isEmpty()) {
                    if (dataSnapshot.hasChild(isbn)) {
                        //adding owner and book condition if there is already a book in the database
                        //path /books/{bookISBN}/users/{userID}/condition
                        dataSnapshot.getRef().child(isbn).child("owners").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("condition").setValue(condition);
                    } else {
                        //adding book
                        //path /books/{bookISBN}
                        dataSnapshot.getRef().child(isbn).setValue(book);
                        //adding owner and book condition
                        //path /books/{bookISBN}/users/{userID}/condition
                        dataSnapshot.getRef().child(isbn).child("owners").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("condition").setValue("Condition should go here");
                    }
                }else{
                    Toast.makeText(BookActivity.this,R.string.bookNotValid,Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", book.getTitle());
        outState.putString("authors", book.getAuthors());
        outState.putString("publisher", book.getPublisher());
        outState.putString("year", book.getEditionYear());
        outState.putString("thumbnail", book.getThumbnail());
        outState.putString("isbn", isbn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        book.setTitle(savedInstanceState.getString("title"));
        book.setAuthors(savedInstanceState.getString("authors"));
        book.setPublisher(savedInstanceState.getString("publisher"));
        book.setEditionYear(savedInstanceState.getString("year"));
        book.setThumbnail(savedInstanceState.getString("thumbnail"));
        this.isbn = savedInstanceState.getString("isbn");


        title.setText(book.getTitle());
        authors.setText(book.getAuthors());
        year.setText(book.getEditionYear());
        publisher.setText(book.getPublisher());

        //Check if thumbnail exists
        if (!book.getThumbnail().isEmpty()){
            Picasso.get().load(book.getThumbnail()).into(image); //Using Picasso library to load and URL into imageView
        }
    }
}

