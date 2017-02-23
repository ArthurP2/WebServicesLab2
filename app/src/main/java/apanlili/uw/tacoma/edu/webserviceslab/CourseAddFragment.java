package apanlili.uw.tacoma.edu.webserviceslab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import apanlili.uw.tacoma.edu.webserviceslab.data.CourseDB;

import static android.R.attr.bitmap;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CourseAddFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CourseAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseAddFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String COURSE_ADD_URL = "http://cssgate.insttech.washington.edu/~apanlili/addCourse.php?";
    private static final String COURSE_EDIT_URL = "http://cssgate.insttech.washington.edu/~apanlili/editCourse.php?";

    private CourseAddListener mListener;
    private EditText mCourseIdEditText;
    private EditText mCourseShortDescEditText;
    private EditText mCourseLongDescEditText;
    private EditText mCoursePrereqsEditText;
    private String mCourseId;
    private Button uploadButton;
    private CourseDB mCourseDB;

    private int PICK_IMAGE_REQUEST = 1;

    private Bitmap bitmap;

    private ImageView imageView;


    public CourseAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseAddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseAddFragment newInstance(String param1, String param2) {
        CourseAddFragment fragment = new CourseAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public interface CourseAddListener {
        public void addCourse(String url, Bitmap bitmap, String id);
        public void editCourse(String url, Bitmap bitmap, String id);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mCourseDB = new CourseDB(getActivity());
        View v = inflater.inflate(R.layout.fragment_course_add, container, false);
        imageView = (ImageView)v.findViewById(R.id.imageView);
        mCourseIdEditText = (EditText)v.findViewById(R.id.add_course_id);
        mCourseShortDescEditText = (EditText)v.findViewById(R.id.add_course_short_desc);
        mCourseLongDescEditText = (EditText)v.findViewById(R.id.add_course_long_desc);
        mCoursePrereqsEditText = (EditText)v.findViewById(R.id.add_course_prereqs);
        FloatingActionButton floatingActionButton = (FloatingActionButton)
                getActivity().findViewById(R.id.fab);
        floatingActionButton.hide();

        Button addCourseButton = (Button)v.findViewById(R.id.add_course_button);
        Button editCourseButton = (Button)v.findViewById(R.id.edit_course_button);
        uploadButton = (Button)v.findViewById(R.id.upload_button);
        addCourseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    String url = buildCourseURL(v, COURSE_ADD_URL);
                    Log.i("TAG", url);

                    mListener.addCourse(url, bitmap, mCourseId);

                }else {
                    mCourseDB.add(mCourseIdEditText.getText().toString(), mCourseShortDescEditText.getText().toString(), mCourseLongDescEditText.getText().toString(), mCoursePrereqsEditText.getText().toString());
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        editCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    String url = buildCourseURL(v, COURSE_EDIT_URL);
                    mListener.editCourse(url, bitmap, mCourseId);
                } else {
                    Log.d("CHECK", "YES");
                    mCourseDB.edit(mCourseIdEditText.getText().toString(), mCourseShortDescEditText.getText().toString(), mCourseLongDescEditText.getText().toString(), mCoursePrereqsEditText.getText().toString());
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        return v;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Context applicationContext = CourseActivity.getContextOfApplication();
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }





    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof CourseAddListener) {
            mListener = (CourseAddListener)context;
        } else {
            throw new RuntimeException(context.toString()
                + "must implement CourseAddListener");
        }
    }

    private String buildCourseURL(View v, String url){
        StringBuilder sb = new StringBuilder(url);
        Log.i("TAG", COURSE_ADD_URL);
        try {
            String courseId = mCourseIdEditText.getText().toString();
            sb.append("id=");
            sb.append(courseId);
            mCourseId = courseId;

            String courseShortDesc = mCourseShortDescEditText.getText().toString();
            sb.append("&shortDesc=");
            sb.append(URLEncoder.encode(courseShortDesc, "UTF-8"));

            String courseLongDesc = mCourseLongDescEditText.getText().toString();
            sb.append("&longDesc=");
            sb.append(URLEncoder.encode(courseLongDesc, "UTF-8"));

            String coursePrereqs = mCoursePrereqsEditText.getText().toString();
            sb.append("&prereqs=");
            sb.append(URLEncoder.encode(coursePrereqs, "UTF-8"));

            Log.i("CourseAddFragment", sb.toString());
        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
