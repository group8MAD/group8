package it.polito.mad.group8;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {
    public static final int REQUEST_PERMISSIONS = 200;
    public static final String PROFILE_PICTURE = "ProfilePicture";

    private EditText name;
    private EditText email;
    private EditText biography;
    private EditText province;
    private EditText city;
    private EditText nickname;
    private ImageButton image;
    private File imageCacheFile;
    private String userID;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://group8-12e04.appspot.com/");
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri downloadUri;
    private Uri imageUri;//variable where we save the direction of the image on the db
  //  private ProgressDialog progress;
    StorageReference filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        biography = findViewById(R.id.bio);
        image = findViewById(R.id.image);
        city = findViewById(R.id.city);
        province = findViewById(R.id.province);
        nickname = findViewById(R.id.nickname);
        this.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = database.getReference("users/"+this.userID);
        storageReference = storage.getReference();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        filepath = storageReference.child("Photos").child(userID.toString());  //path of the image in the db creates a folder Photos/userUID

        String nameString = getIntent().getStringExtra("name");
        String emailString = getIntent().getStringExtra("email");
        String bioString = getIntent().getStringExtra("bio");
        String cityString = getIntent().getStringExtra("city");
        String provinceString = getIntent().getStringExtra("province");
        String nicknameString = getIntent().getStringExtra("nickname");

        //See if the strings saved in the db are not empty, if so, show them in the profile

        if(nameString!=null)
            name.setText(nameString);
        if(emailString!=null)
            email.setText(emailString);
        if(bioString!=null)
            biography.setText(bioString);
        if(cityString!=null)
            city.setText(cityString);
        if(provinceString !=null)
            province.setText(provinceString);

        if(nicknameString != null)
            nickname.setText(nicknameString);

        image.setOnClickListener(myListener);
    }

    private View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (checkAndRequestPermissions()) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setMinCropResultSize(512, 512)
                        .setRequestedSize(512,512)
                        .start(EditProfile.this);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getApplicationContext().getString(R.string.grantedPermission), Toast.LENGTH_LONG).show();
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setMinCropResultSize(512,512)
                        .setRequestedSize(512,512)
                        .start(EditProfile.this);
            } else {
                Toast.makeText(this, getApplicationContext().getString(R.string.deniedPermission), Toast.LENGTH_LONG).show();
            }
        }
    }
    private  boolean checkAndRequestPermissions() {
        int readExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<String>();

        if (readExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (writeExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){


            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri = result.getUri(); //if the image is cropped, save the direction of it in imageUri

                Toast.makeText(EditProfile.this, "Image cropped", Toast.LENGTH_LONG).show();

               //progress.setMessage("Uploading picture...");
               //progress.show();

                imageCacheFile = new File(imageUri.getPath());
                image.setImageURI(imageUri);

                filepath.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                downloadUri = taskSnapshot.getDownloadUrl();
                                Log.e("donwloadUri","= "+downloadUri.toString());
                                Toast.makeText(EditProfile.this, "Image saved to the storage", Toast.LENGTH_LONG).show();
                                //progress.dismiss();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(EditProfile.this, "Sorry the image could not be saved", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageCacheFile!=null){
            File imageFileTmp = new File(getFilesDir(),"ProfilePictureTmp");
            if(imageCacheFile.renameTo(imageFileTmp))
                outState.putString("imageSaved","YES");
        }if (downloadUri!=null && !downloadUri.toString().isEmpty()){
            outState.putString("uri", downloadUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getString("imageSaved")!=null) {
            if (savedInstanceState.getString("imageSaved").equals("YES")) {
                imageCacheFile = new File(getFilesDir(), "ProfilePictureTmp");
                image.setImageURI(Uri.fromFile(imageCacheFile));

             }
             if (savedInstanceState.getString("uri") != null && !Objects.requireNonNull(savedInstanceState.getString("uri")).isEmpty()){
                downloadUri = Uri.parse(savedInstanceState.getString("uri").toString());
             }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home){
            Intent intent = new Intent(this, ShowProfile.class);

            updateDatabaseProfile();

            if (imageCacheFile!=null ){
                File fileDest = new File(getFilesDir(), PROFILE_PICTURE);
                if (imageCacheFile.renameTo(fileDest)) {
                intent.putExtra("imageUri", "OK");
                }

            }
        }
        finish();
        return true;
    }

    public void updateDatabaseProfile(){
        databaseReference.child("name").setValue(this.name.getText().toString());
        databaseReference.child("email").setValue(this.email.getText().toString());
        databaseReference.child("biography").setValue(this.biography.getText().toString());
        databaseReference.child("city").setValue(this.city.getText().toString());
        databaseReference.child("province").setValue(this.province.getText().toString());
        databaseReference.child("nickname").setValue(this.nickname.getText().toString());
        if (this.downloadUri != null)
            databaseReference.child("imageUri").setValue(this.downloadUri.toString()); //saves the url of the image in the db

    }
}

