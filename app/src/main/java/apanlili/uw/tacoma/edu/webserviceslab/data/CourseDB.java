package apanlili.uw.tacoma.edu.webserviceslab.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import apanlili.uw.tacoma.edu.webserviceslab.R;
import apanlili.uw.tacoma.edu.webserviceslab.course.Course;

/**
 * Created by Marky on 2/18/2017.
 */

public class CourseDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Course.db";

    private CourseDBHelper mCourseDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public CourseDB(Context context) {
        mCourseDBHelper = new CourseDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mCourseDBHelper.getWritableDatabase();
    }

    /**
     * Inserts the course into the local sqlite table. Returns true if successful, false otherwise.
     * @param id
     * @param shortDesc
     * @param longDesc
     * @param prereqs
     * @return true or false
     */
    public boolean insertCourse(String id, String shortDesc, String longDesc, String prereqs) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("shortDesc", shortDesc);
        contentValues.put("longDesc", longDesc);
        contentValues.put("prereqs", prereqs);
        StringBuilder sb = new StringBuilder();
        sb.append(id);
        sb.append(".png");
        contentValues.put("image", sb.toString());


        long rowId = mSQLiteDatabase.insert("Course", null, contentValues);
        return rowId != -1;
    }

    public void add(String did, String shortD, String longD, String preR){
        Log.d("TAT", did + shortD + longD + preR);
        mSQLiteDatabase.execSQL("INSERT INTO Course (id, shortDesc, longDesc, prereqs) VALUES ('" + did + "', '" + shortD + "', '" + longD + "', '" + preR +"' );");
    }

    public void edit(String did, String shortD, String longD, String preR){
        mSQLiteDatabase.execSQL("UPDATE Course SET shortDesc = '" + shortD + "', longDesc = '" + longD + "', prereqs = '" + preR + "' WHERE id = '" + did + "'");
    }


    public void closeDB() {
        mSQLiteDatabase.close();
    }


    class CourseDBHelper extends SQLiteOpenHelper {

        private final String CREATE_COURSE_SQL;

        private final String DROP_COURSE_SQL;

        public CourseDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_COURSE_SQL = context.getString(R.string.CREATE_COURSE_SQL);
            DROP_COURSE_SQL = context.getString(R.string.DROP_COURSE_SQL);

        }



        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_COURSE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_COURSE_SQL);
            onCreate(sqLiteDatabase);
        }
    }

    private static final String COURSE_TABLE = "Course";

    /**
     * Returns the list of courses from the local Course table.
     * @return list
     */
    public List<Course> getCourses() {

        String[] columns = {
                "id", "shortDesc", "longDesc", "prereqs", "image"
        };

        Cursor c = mSQLiteDatabase.query(
                COURSE_TABLE,  // The table to query
                columns,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        List<Course> list = new ArrayList<Course>();
        for (int i=0; i<c.getCount(); i++) {
            String id = c.getString(0);
            String shortDesc = c.getString(1);
            String longDesc = c.getString(2);
            String prereqs = c.getString(3);
            Course course = new Course(id, shortDesc, longDesc, prereqs);
            list.add(course);
            c.moveToNext();
        }

        return list;
    }

    /**
     * Delete all the data from the COURSE_TABLE
     */
    public void deleteCourses() {
        mSQLiteDatabase.delete(COURSE_TABLE, null, null);
    }



}
