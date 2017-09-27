package com.example.apollinariia.litup.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import static android.provider.ContactsContract.AUTHORITY;
import static com.example.apollinariia.litup.data.AlarmContract.AlarmEntry.TABLE_NAME;
import static com.example.apollinariia.litup.data.AlarmContract.PATH_ALARMS;

/**
 * Created by Apollinariia on 9/27/2017.
 */

public class AlarmProvider extends ContentProvider {

    private AlarmDbHelper db;

    public static final int ALARMS = 100;
    public static final int ALARMS_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();


    public static UriMatcher buildUriMatcher() {
        UriMatcher URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        URIMatcher.addURI(AUTHORITY, PATH_ALARMS, ALARMS);
        URIMatcher.addURI(AUTHORITY, PATH_ALARMS + "/#", ALARMS_ID);

        return URIMatcher;
    }


    @Override
    public boolean onCreate() {
        db = new AlarmDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase dataBase = db.getReadableDatabase();


        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case ALARMS:
                retCursor = dataBase.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ALARMS_ID:
                String id = uri.getPathSegments().get(1);

                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                retCursor = dataBase.query(TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}