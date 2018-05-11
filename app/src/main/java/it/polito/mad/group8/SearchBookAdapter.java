package it.polito.mad.group8;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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


        public BookHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            publisher = itemView.findViewById(R.id.bookPublisher);
            authors = itemView.findViewById(R.id.bookAuthor);
            year = itemView.findViewById(R.id.bookYear);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }

}
