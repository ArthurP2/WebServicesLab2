package apanlili.uw.tacoma.edu.webserviceslab;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Map;

import apanlili.uw.tacoma.edu.webserviceslab.course.Course;

public class CourseActivity extends AppCompatActivity implements CourseFragment.OnListFragmentInteractionListener,
        CourseAddFragment.CourseAddListener, CourseEditFragment.CourseEditListener {

    private String UPLOAD_URL = "http://cssgate.insttech.washington.edu/~apanlili/upload2.php";
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";


    public static Context contextOfApplication;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        contextOfApplication = getApplicationContext();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CourseAddFragment courseAddFragment = new CourseAddFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, courseAddFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });



        if(savedInstanceState == null || getSupportFragmentManager().findFragmentById(R.id.list) == null) {
            CourseFragment courseFragment = new CourseFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, courseFragment)
                    .commit();
        }
    }

    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    @Override
    public void onListFragmentInteraction(Course course) {

        CourseDetailFragment courseDetailFragment =  new CourseDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(CourseDetailFragment.COURSE_ITEM_SELECTED, course);
        courseDetailFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,courseDetailFragment)
                .addToBackStack(null)
                .commit();

    }

    public void editCourse(String url, Bitmap bitmap, String id){
        uploadImage(bitmap, id);
        EditCourseTask task = new EditCourseTask();
        task.execute(new String[]{url.toString()});

        getSupportFragmentManager().popBackStackImmediate();
    }



    private class EditCourseTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url:urls){
                try{
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream conent = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(conent));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                } catch (Exception e) {
                    response = "Unable to edit course, Reason: "
                            + e.getMessage();
                }finally {
                    if(urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }
        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Course successfully changed!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to edit: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
                Log.i("TAG", e.getMessage());
            }
        }



    }






    public void addCourse(String url, Bitmap bitmap, String id){
        uploadImage(bitmap, id);
        AddCourseTask task = new AddCourseTask();
        task.execute(new String[]{url.toString()});

        getSupportFragmentManager().popBackStackImmediate();
    }

    private class AddCourseTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url:urls){
                try{
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream conent = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(conent));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                } catch (Exception e) {
                    response = "Unable to add course, Reason: "
                            + e.getMessage();
                }finally {
                    if(urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }
        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            Log.d("wawa",result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                Log.d("wawa", status);
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Course successfully added!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }



    }

    private String appendURL(String url, Bitmap bitmap) {
        String image = getStringImage(bitmap);
        StringBuilder sb = new StringBuilder(url);
        try {
            sb.append("$image=");
            sb.append(URLEncoder.encode(image, "UTF-8"));
        }
        catch (Exception e){
            Toast.makeText(CourseActivity.this, "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        Log.d("url", sb.toString());
        return sb.toString();
    }


    private void uploadImage(Bitmap theBitmap, String id) {
        final String courseId = id;
        final Bitmap bitmap = theBitmap;
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        //Toast.makeText(CourseActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        //Toast.makeText(CourseActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                String name = courseId;
                Log.d("url", courseId.toString());

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();


                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, name);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }




}
