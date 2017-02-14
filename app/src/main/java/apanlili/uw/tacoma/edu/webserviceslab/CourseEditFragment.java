package apanlili.uw.tacoma.edu.webserviceslab;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URLEncoder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CourseEditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CourseEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseEditFragment extends Fragment {

    private static final String COURSE_EDIT_URL = "http://cssgate.insttech.washington.edu/~apanlili/editCourse.php?";


    private CourseEditListener mListener;
    private EditText mCourseIdEditText;
    private EditText mCourseShortDescEditText;
    private EditText mCourseLongDescEditText;
    private EditText mCoursePrereqsEditText;
;



    public CourseEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseEditFragment.
     */
    public static CourseEditFragment newInstance(String param1, String param2) {
        CourseEditFragment fragment = new CourseEditFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public interface CourseEditListener {
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
        View v = inflater.inflate(R.layout.fragment_course_edit, container, false);

        mCourseIdEditText = (EditText)v.findViewById(R.id.select_course_id);
        mCourseShortDescEditText = (EditText)v.findViewById(R.id.edit_course_short_desc);
        mCourseLongDescEditText = (EditText)v.findViewById(R.id.edit_course_long_desc);
        mCoursePrereqsEditText = (EditText)v.findViewById(R.id.edit_course_prereqs);


       // floatingActionButton.hide();



        return v;
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof CourseEditListener) {
            mListener = (CourseEditListener)context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement CourseEditListener");
        }
    }


    private String buildCourseURL(View v){
        StringBuilder sb = new StringBuilder(COURSE_EDIT_URL);
        try {
            String courseId = mCourseIdEditText.getText().toString();
            sb.append("id=");
            sb.append(courseId);

            String courseShortDesc = mCourseShortDescEditText.getText().toString();
            sb.append("&shortDesc=");
            sb.append(URLEncoder.encode(courseShortDesc, "UTF-8"));

            String courseLongDesc = mCourseLongDescEditText.getText().toString();
            sb.append("&longDesc=");
            sb.append(URLEncoder.encode(courseLongDesc, "UTF-8"));

            String coursePrereqs = mCoursePrereqsEditText.getText().toString();
            sb.append("&prereqs=");
            sb.append(URLEncoder.encode(coursePrereqs, "UTF-8"));

            Log.i("CourseEditFragment", sb.toString());
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
