package it.polito.mad.group8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URI;

public class SingleShowBookActivity extends AppCompatActivity {


    // Fields of the book
    TextView title;
    TextView authors;
    TextView publisher;
    TextView year;
    ImageView image;

    // Button to open ChatActivity to send a message to the owner of the book
    Button send_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_show_book);

        title = findViewById(R.id.title);
        authors = findViewById(R.id.authors);
        publisher = findViewById(R.id.publisher);
        year = findViewById(R.id.year);
        image = findViewById(R.id.image);
        send_message = findViewById(R.id.message_button);


        title.setText(getIntent().getStringExtra("title"));
        authors.setText(getIntent().getStringExtra("authors"));
        publisher.setText(getIntent().getStringExtra("publisher"));
        year.setText(getIntent().getStringExtra("year"));

        String imageUrl = getIntent().getStringExtra("url");
        Log.e("imageuri: ", imageUrl);
        if (!imageUrl.isEmpty()){
            Picasso.get().load(imageUrl).into(image);
        }

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent_message = new Intent(getApplicationContext(), ChatActivity.java);
                //startActivity(intent_message);
            }
        });
    }
}
