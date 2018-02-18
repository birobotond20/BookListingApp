package com.example.android.booklistingapp;

import android.content.SearchRecentSuggestionsProvider;

/**
 * A (@link QueryContentProvider) object to manage the recent search queries
 */

public class QueryContentProvider extends SearchRecentSuggestionsProvider{

    public final static String AUTHORITY = "com.example.android.booklistingapp.QueryContentProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public QueryContentProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
