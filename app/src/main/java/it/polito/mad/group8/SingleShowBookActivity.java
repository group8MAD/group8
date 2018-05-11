package it.polito.mad.group8;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URI;

public class SingleShowBookActivity extends AppCompatActivity {


    TextView title;
    TextView authors;
    TextView publisher;
    TextView year;
    ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_show_book);

        title = findViewById(R.id.title);
        authors = findViewById(R.id.authors);
        publisher = findViewById(R.id.publisher);
        year = findViewById(R.id.year);
        image = findViewById(R.id.image);

        title.setText(getIntent().getStringExtra("title"));
        authors.setText(getIntent().getStringExtra("authors"));
        publisher.setText(getIntent().getStringExtra("publisher"));
        year.setText(getIntent().getStringExtra("year"));

        String imageUrl = getIntent().getStringExtra("url");
        Log.e("imageuri: ", imageUrl);
        if (!imageUrl.isEmpty()){
            Picasso.get().load(imageUrl).into(image);
        }
    }
}
