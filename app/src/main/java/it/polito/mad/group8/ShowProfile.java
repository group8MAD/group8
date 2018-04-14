package it.polito.mad.group8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.File;
import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowProfile extends AppCompatActivity {


    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static final String PROFILE_PICTURE = "ProfilePicture";

    private EditText name;
    private EditText email;
    private EditText biography;
    private ImageView image;
    private File imageFile;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        biography = findViewById(R.id.bio);
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open,R.string.close);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("name", null);
        String email = prefs.getString("email",null);
        String biography = prefs.getString("biography", null);
        imageFile = new File(getFilesDir(), PROFILE_PICTURE);

        if (name != null) {
            this.name.setText(name);
        }
        if (email != null){
            this.email.setText(email);
        }
        if (biography!=null){
            this.biography.setText(biography);
        }
        if (imageFile.exists()){
            this.image.setImageURI(Uri.fromFile(imageFile));
        }

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_books_logged:
                        finish();
                        startActivity(new Intent(ShowProfile.this, BookList.class));
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
    }

    public void setHeaderDrawer(){
        View headerView = mNavigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.header_name);
        TextView email = headerView.findViewById(R.id.header_email);
        CircleImageView image = headerView.findViewById(R.id.header_image);

        name.setText(this.name.getText().toString());
        email.setText(this.email.getText().toString());
        if (imageFile.exists())
            image.setImageURI(Uri.fromFile(imageFile));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getMenuInflater().inflate(R.menu.menu_show_profile, menu);
        mNavigationView.inflateMenu(R.menu.menu_drawer_loggedin);
        setHeaderDrawer();



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_icon:
                Intent intent = new Intent(this, EditProfile.class);
                intent.putExtra("name", name.getText().toString());
                intent.putExtra("email", email.getText().toString());
                intent.putExtra("bio", biography.getText().toString());
                startActivityForResult(intent, 0);
                return true;

        }
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 0) {

            String stringName = data.getStringExtra("name");
            String stringEmail = data.getStringExtra("email");
            String stringBiography = data.getStringExtra("biography");
            String imageUriString = data.getStringExtra("imageUri");

            name.setText(stringName);
            email.setText(stringEmail);
            biography.setText(stringBiography);
            setHeaderDrawer();

            if(imageUriString!=null && imageUriString.equals("OK")) {
                recreate();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        if(!name.getText().toString().isEmpty()){
            editor.putString("name", name.getText().toString());
        }
        if(!email.getText().toString().isEmpty()){
            editor.putString("email", email.getText().toString());
        }
        if(!biography.getText().toString().isEmpty()){
            editor.putString("biography", biography.getText().toString());
        }
        editor.apply();
    }

    public void signOut(){
        mDrawerLayout.closeDrawers();
        AuthUI.getInstance()
                .signOut(ShowProfile.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(ShowProfile.this, BookList.class));
                    }
                });
    }
}
