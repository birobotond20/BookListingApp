package com.example.android.booklistingapp;

/**
 * An {@link Book} object contains information related to a single book
 */

public class Book {

    /** The cover image url of the book */
    private String mCoverImageUrl;

    /** Title of the book */
    private String mTitle;

    /** Author of the book */
    private String mAuthor;

    /** Price amount of the book */
    private double mPriceAmount;

    /** Currency associated with the price of the book */
    private String mPriceCurrency;

    /** Average rating of the book */
    private double mRating;

    /** The url of the book */
    private String mUrl;

    public Book (String coverImageUrl, String title, String author, double priceAmount,
                 String priceCurrency, double rating, String url){
        mCoverImageUrl = coverImageUrl;
        mTitle = title;
        mAuthor = author;
        mPriceAmount= priceAmount;
        mPriceCurrency = priceCurrency;
        mRating = rating;
        mUrl = url;
    }

    /** Returns the cover image url of the book */
    public String getCoverImageUrl() {
        return mCoverImageUrl;
    }

    /** Returns the title of the book */
    public String getTitle() {
        return mTitle;
    }

    /** Returns the author of the book */
    public String getAuthor() {
        return mAuthor;
    }

    /** Returns the price of the book */
    public double getPriceAmount() {
        return mPriceAmount;
    }

    /** Returns the currency associated with the price of the book */
    public String getCurrency(){
        return mPriceCurrency;
    }

    /** Returns the average rating of the book */
    public double getRating() {
        return mRating;
    }

    /** Returns the url of the book */
    public String getUrl() {
        return mUrl;
    }


}
