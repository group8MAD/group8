package it.polito.mad.group8;


import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BookHolder extends RecyclerView.ViewHolder {

    private final TextView title;
    private final TextView publisher;
    private final TextView authors;
    private final TextView year;
    private final ImageView thumbnail;


    public BookHolder(View itemView){
        super(itemView);
        title = itemView.findViewById(R.id.bookTitle);
        publisher = itemView.findViewById(R.id.bookPublisher);
        authors = itemView.findViewById(R.id.bookAuthor);
        year = itemView.findViewById(R.id.bookYear);
        thumbnail = itemView.findViewById(R.id.thumbnail);
    }

    public void setTitle(String title){
        this.title.setText(title);
    }

    public void setAuthors(String authors){
        this.authors.setText(authors);
    }

    public void setPublisher(String publisher){
        this.publisher.setText(publisher);
    }

    public void setYear(String year){
        this.year.setText(year);
    }

    public void setThumbnail(String thumbnail){
        if (thumbnail != null && !thumbnail.isEmpty())
            Picasso.get().load(thumbnail).into(this.thumbnail);
    }
}
