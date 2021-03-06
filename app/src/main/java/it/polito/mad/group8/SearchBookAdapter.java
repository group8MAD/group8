package it.polito.mad.group8;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchBookAdapter extends RecyclerView.Adapter<SearchBookAdapter.BookHolder>{

    private List<Book> books;
    private Context context;

    public SearchBookAdapter(List<Book> books, Context context) {
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchBookAdapter.BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book, parent, false);
        return new BookHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchBookAdapter.BookHolder holder, int position) {
        Book book = books.get(position);

        holder.title.setText(book.getTitle());
        holder.publisher.setText(book.getPublisher());
        holder.authors.setText(book.getAuthors());
        holder.year.setText(book.getEditionYear());
        if (!book.getThumbnail().isEmpty()){
            Picasso.get().load(book.getThumbnail()).into(holder.thumbnail);
        }
        if (FirebaseAuth.getInstance().getCurrentUser()!= null && context instanceof SearchBookActivity) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SingleShowBookActivity.class);
                    intent.putExtra("isbn", book.getIsbn());
                    intent.putExtra("title", holder.title.getText().toString());
                    intent.putExtra("authors", holder.authors.getText().toString());
                    intent.putExtra("publisher", holder.publisher.getText().toString());
                    intent.putExtra("year", holder.year.getText().toString());
                    intent.putExtra("url", book.getThumbnail());
                    context.startActivity(intent);
                }

            });
        }else if (FirebaseAuth.getInstance().getCurrentUser()!= null){
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DeleteBook.class);
                    intent.putExtra("isbn", book.getIsbn());
                    intent.putExtra("title", holder.title.getText().toString());
                    intent.putExtra("authors", holder.authors.getText().toString());
                    intent.putExtra("publisher", holder.publisher.getText().toString());
                    intent.putExtra("year", holder.year.getText().toString());
                    intent.putExtra("url", book.getThumbnail());
                    context.startActivity(intent);
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView publisher;
        public TextView authors;
        public TextView year;
        public ImageView thumbnail;
        public CardView cardView;


        public BookHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            publisher = itemView.findViewById(R.id.bookPublisher);
            authors = itemView.findViewById(R.id.bookAuthor);
            year = itemView.findViewById(R.id.bookYear);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            cardView = itemView.findViewById(R.id.cardViewID);
        }
    }

}
