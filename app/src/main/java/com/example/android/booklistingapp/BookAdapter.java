package com.example.android.booklistingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An {@link BookAdapter} knows how to create a list item layout for each book
 * in the data source (a list of {@link Book} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user
 */

public class BookAdapter extends ArrayAdapter<Book>{

    /**
     * Constructs a new {@link BookAdapter}
     * @param context of the app
     * @param books is the list of books, which is data source of the adapter
     */
    public BookAdapter(Context context, List<Book> books){
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, books);
    }



    /**
     * Get a View that displays the data at the specified position in the data set
     * @param position The position of the item within the adapter's data set whose view we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse.
        // Otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        // Initialize a new ViewHolder instance to reference the child views for later actions.
        // This way if a view already exists we can retrieve the holder, instead of calling
        // findViewById
        ViewHolder holder;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
            holder = new ViewHolder(listItemView);
            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder)listItemView.getTag();
        }

        // Find the Book at the given position in the list of books
        Book currentBook = getItem(position);

        if (currentBook != null) {
            // Find the TextView in the book_list_item.xml layout with the ID book_title
            if (!TextUtils.isEmpty(currentBook.getTitle())) {
                holder.bookTitle.setVisibility(View.VISIBLE);
                holder.bookTitle.setText(currentBook.getTitle());
            } else {
                holder.bookTitle.setVisibility(View.INVISIBLE);
            }

            // Find the TextView in the book_list_item.xml layout with the ID book_author
            if (!TextUtils.isEmpty(currentBook.getAuthor())) {
                holder.bookAuthor.setText(currentBook.getAuthor());
            } else holder.bookAuthor.setText(R.string.no_author_info);

            // Find the TextView in the book_list_item.xml layout with the ID book_price
            String formattedPriceAmount = formatPriceAmount(currentBook.getPriceAmount());
            String bookPrice = currentBook.getCurrency() + formattedPriceAmount;
            holder.bookPrice.setText(bookPrice);

            // Find the TextView in the book_list_item.xml layout with the ID book_average_rating
            if (currentBook.getRating() >= 1.0) {
                String formattedBookRating = formatBookRating(currentBook.getRating());
                holder.bookRating.setVisibility(View.VISIBLE);
                holder.bookRating.setText(formattedBookRating);
                holder.bookRatingStar.setVisibility(View.VISIBLE);
            } else {
                holder.bookRating.setVisibility(View.INVISIBLE);
                holder.bookRatingStar.setVisibility(View.GONE);
            }

            Picasso
                .with(getContext())
                .load(currentBook.getCoverImageUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.bookCover);

            return listItemView;
        } else return null;
    }
    /**
     * Return the formatted rating string showing 1 decimal place (i.e. "3.2")
     * from a decimal rating value.
     */
    private String formatBookRating(double rating){
        DecimalFormat ratingFormat;
        if (rating != 0.0) {
            ratingFormat = new DecimalFormat("0.0");
            return ratingFormat.format(rating);
        } else return "No rating";
    }

    private String formatPriceAmount(double price){
        DecimalFormat priceFormat;
        if (price != 0.0) {
            priceFormat = new DecimalFormat("#,###.##");
            return priceFormat.format(price);
        } else return "No price available";
    }

    /**
     * A {@link ViewHolder} class to cache child views at runtime
     */
    static class ViewHolder {
        @BindView(R.id.book_title) TextView bookTitle;
        @BindView(R.id.book_author) TextView bookAuthor;
        @BindView(R.id.book_price) TextView bookPrice;
        @BindView(R.id.book_average_rating) TextView bookRating;
        @BindView(R.id.book_rating_star) ImageView bookRatingStar;
        @BindView(R.id.book_cover) ImageView bookCover;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
