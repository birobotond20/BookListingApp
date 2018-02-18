package com.example.android.booklistingapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving book data from Google Play Books API.
 */

public final class QueryUtils {

    /**
     * Tag for the LOG messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google Play Books dataset and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl){

        // Create url object
        URL url = createURL(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request",e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}
        // objects
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the {@link List<Book>}
        return books;
    }

    /**
     * Returns a new URL object from the given string URL
     */
    private static URL createURL(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating url: ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code" + urlConnection.getResponseCode()+url);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON response", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies that an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractFeatureFromJson(String bookJSON){

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)){
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        try {
            // Create a JSONObject from the bookJSON string
            JSONObject rootJsonObject = new JSONObject(bookJSON);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of features (or books)
            JSONArray bookArray = rootJsonObject.optJSONArray("items");

            if (bookArray != null) {

                // For each book in the bookArray, create a {@link Book} object
                for (int i = 0; i < bookArray.length(); i++) {

                    // Get a single book at position i within the list of books
                    JSONObject currentBook = bookArray.getJSONObject(i);

                    // For a given book, extract the JSONObject associated with the
                    // key called "volumeInfo", which represents a list of key properties
                    // for that book.
                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                    // Extract the value for the key called "title"
                    String title = volumeInfo.getString("title");

                    // For a given book, extract the JSONObject associated with the
                    // key called "authors"
                    JSONArray authorsArray = volumeInfo.optJSONArray("authors");
                    String bookAuthors;
                    if (authorsArray != null) {
                        StringBuilder authors = new StringBuilder();
                        for (int nrOfAuthors = 0; nrOfAuthors < authorsArray.length(); nrOfAuthors++) {
                            authors.append(", ").append(authorsArray.getString(nrOfAuthors));
                        }
                        bookAuthors = authors.toString().replaceFirst(",", "").replace(";", "").trim();
                    } else {
                        bookAuthors = "";
                    }

                    // Extract the value for the key called "canonicalVolumeLink"
                    String bookUrl = volumeInfo.getString("canonicalVolumeLink");

                    // Extract the value for the key called "averageRating"
                    double rating = volumeInfo.optDouble("averageRating");
                    if (Double.isNaN(rating)) {
                        rating = 0.0;
                    }

                    // Extract the JSONObject associated with the key called "imageLinks"
                    JSONObject imageLinksObject = volumeInfo.optJSONObject("imageLinks");
                    String coverImageUrl;
                    if (imageLinksObject != null) {
                        //Extract the value for the key called "thumbnail"
                        coverImageUrl = imageLinksObject.getString("thumbnail").replace("&edge=curl", "").replace("&zoom=1", "&zoom=2");
                    } else {
                        coverImageUrl = null;
                    }

                    // For a given book extract the JSONObject associated with the
                    // key called "saleInfo", which represents a list of sale properties
                    // for that book.
                    JSONObject saleInfo = currentBook.getJSONObject("saleInfo");
                    // Extract the saleability information for the given book,
                    // and find out if the book can be bought or not. If the book is for sale,
                    // find the price for it, otherwise the price should be equal to "No price found".
                    double priceAmount;
                    String currencyCode;
                    String saleability = saleInfo.getString("saleability");
                    if (saleability.equals("FOR_SALE")) {
                        JSONObject retailPrice = saleInfo.getJSONObject("retailPrice");
                        priceAmount = retailPrice.getDouble("amount");
                        currencyCode = retailPrice.getString("currencyCode");
                    } else {
                        priceAmount = 0;
                        currencyCode = "";
                    }

                    // Create a new {@link Book} object with the title, author, price,
                    // average rating, cover image url and book url from the JSON response.
                    Book book = new Book(coverImageUrl, title, bookAuthors, priceAmount, currencyCode, rating, bookUrl);

                    // Add the new {@link Book} to the list of books.
                    books.add(book);
                }
            } else return null;
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return books;
    }
}
