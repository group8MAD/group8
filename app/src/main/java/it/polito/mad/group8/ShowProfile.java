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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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


import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowProfile extends AppCompatActivity {



    public static final String PROFILE_PICTURE = "ProfilePicture";

    private TextView name;
    private TextView email;
    private TextView biography;
    private ImageView image;
    private TextView cityProvince;
    private TextView nickname;
    private RatingBar ratingBar;
    private User user;
    private String userID;
    private Uri imageUri;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private Button readReviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_profile);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        biography = findViewById(R.id.biography);
        nickname = findViewById(R.id.nickname);
        readReviews = findViewById(R.id.readReviews);
        cityProvince = findViewById(R.id.cityProvince);
        ratingBar = findViewById(R.id.ratingBar);

        user = new User();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getSupportActionBar().setTitle(R.string.profile);


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

        FirebaseDatabase.getInstance().getReference("users")
                .child(userID)
                .child("reviews")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        float rating = 0;
                        for (DataSnapshot review: dataSnapshot.getChildren()){
                            rating += Float.parseFloat(review.child("rating").getValue().toString());
                        }
                        ratingBar.setRating(rating / dataSnapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getMenuInflater().inflate(R.menu.menu_show_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_icon:
                Intent intent = new Intent(this, EditProfile.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("email", user.getEmail());
                intent.putExtra("bio", user.getBiography());
                intent.putExtra("city", user.getCity());
                intent.putExtra("province", user.getProvince());
                intent.putExtra("nickname", user.getNickname());
                startActivity(intent);
                return super.onOptionsItemSelected(item);
            default:
                finish();
                return super.onOptionsItemSelected(item);

        }
    }



    private void getData(DataSnapshot dataSnapshot){
        if(!dataSnapshot.exists()){
            user.setName(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
            user.setEmail(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
            ref.setValue(this.user);


        }else{
            this.user.setName(dataSnapshot.getValue(User.class).getName());
            this.user.setEmail(dataSnapshot.getValue(User.class).getEmail());
            this.user.setBiography(dataSnapshot.getValue(User.class).getBiography());
            this.user.setCity(dataSnapshot.getValue(User.class).getCity());
            this.user.setProvince(dataSnapshot.getValue(User.class).getProvince());
            this.user.setNickname(dataSnapshot.getValue(User.class).getNickname());
            this.name.setText(user.getName());
            this.email.setText(user.getEmail());
            this.biography.setText(user.getBiography());
            String cityProvinceString = user.getCity()+" ( "+user.getProvince()+" )";
            cityProvince.setText(cityProvinceString);

            this.nickname.setText(user.getNickname());

            //check if imageUrl is empty
            //if it is don't load it because this will make the app crash
            if(!dataSnapshot.getValue(User.class).getImageUri().isEmpty())
                Picasso.get().load(dataSnapshot.getValue(User.class).getImageUri()).into(image);
            readReviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ShowReviewsActivity.class);
                    intent.putExtra("user", dataSnapshot.getKey());
                    startActivity(intent);
                }
            });

        }
    }


}
