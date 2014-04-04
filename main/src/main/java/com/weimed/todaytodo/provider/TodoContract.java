package com.weimed.todaytodo.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Richard Lee on 4/3/14.
 *
 * Field and table name constants for
 * {@link com.weimed.todaytodo.provider.TodoProvider}
 */
public class TodoContract {
    private TodoContract() {}

    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = "com.weimed.todaytodo";

    /**
     * Base URI. (content://com.weimed.todaytodo)
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path component for "entry"-type resources..
     */
    private static final String PATH_ENTRIES = "entries";

    /**
     * Columns supported by "entries" records.
     */
    public static class Entry implements BaseColumns {
        /**
         * MIME type for lists of entries.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.todaytodo.entries";

        /**
         * MIME type for individual entries.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.todaytodo.entry";

        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "entry";

        /**
         * Atom ID. (NOTE: Not to be confused with the database primary key, which is _ID.
         */
        public static final String COLUMN_NAME_ENTRY_ID = "entry_id";

        /**
         * TodayTodo title
         */
        public static final String COLUMN_NAME_TITLE = "title";

        /**
         * TodayTodo content
         */
        public static final String COLUMN_NAME_CONTENT = "content";

        /**
         * TodayTodo is completed?
         */
        public static final String COLUMN_NAME_IS_COMPLETED = "completed";
    }
}
