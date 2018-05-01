package it.polito.mad.group8;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;


import de.hdodenhof.circleimageview.CircleImageView;

public class ShowProfile extends AppCompatActivity {



    public static final String PROFILE_PICTURE = "ProfilePicture";

    private EditText name;
    private EditText email;
    private EditText biography;
    private ImageView image;
    private EditText direction;
    private EditText province;
    private EditText city;
    private EditText cap;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private User user;
    private String userID;
    private Uri imageUri;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_profile);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        biography = findViewById(R.id.bio);
        direction = findViewById(R.id.direction);
        city = findViewById(R.id.city);
        province = findViewById(R.id.province);
        cap = findViewById(R.id.cap);
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open,R.string.close);
        user = new User();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_books_logged:
                        finish();
                        startActivity(new Intent(ShowProfile.this, BookActivity.class));
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

        ref = database.getReference("users/"+this.userID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getMenuInflater().inflate(R.menu.menu_show_profile, menu);
        mNavigationView.inflateMenu(R.menu.menu_drawer_loggedin);
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
                intent.putExtra("direction", direction.getText().toString());
                intent.putExtra("city", city.getText().toString());
                intent.putExtra("province", province.getText().toString());
                intent.putExtra("cap", cap.getText().toString());
                startActivity(intent);
                return true;
        }
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void signOut(){
        mDrawerLayout.closeDrawers();
        AuthUI.getInstance()
                .signOut(ShowProfile.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(ShowProfile.this, BookActivity.class));
                    }
                });
    }

    private void getData(DataSnapshot dataSnapshot){
        if(!dataSnapshot.exists()){
            ref.setValue(this.user);
        }else{
            this.user.setName(dataSnapshot.getValue(User.class).getName());
            this.user.setEmail(dataSnapshot.getValue(User.class).getEmail());
            this.user.setBiography(dataSnapshot.getValue(User.class).getBiography());
            this.user.setDirection(dataSnapshot.getValue(User.class).getDirection());
            this.user.setCity(dataSnapshot.getValue(User.class).getCity());
            this.user.setProvince(dataSnapshot.getValue(User.class).getProvince());
            this.user.setCap(dataSnapshot.getValue(User.class).getCap());
            this.name.setText(user.getName());
            this.email.setText(user.getEmail());
            this.biography.setText(user.getBiography());
            this.direction.setText(user.getDirection());
            this.city.setText(user.getCity());
            this.province.setText(user.getProvince());
            this.cap.setText(user.getCap());
            Picasso.get().load(dataSnapshot.getValue(User.class).getImageUri()).into(image);

            setHeaderDrawer();
        }
    }

}
