package hacks.eng.tab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
   interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    public static MainFragment instance;


        public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        instance = fragment;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseUtils databaseUtils = new DatabaseUtils(myRef);
        TelephonyManager tm = (TelephonyManager) this.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String myPhoneNumber = tm.getLine1Number().substring(tm.getLine1Number().length() - 10);
        databaseUtils.totalSum(myPhoneNumber);
        return v;
    }

    public void updateTextViews(double sum, double debts){

        TextView debt = (TextView) getActivity().findViewById(R.id.Debt_Amount);
        debt.setText("$" + debts);

        TextView sums = (TextView) getActivity().findViewById(R.id.Credit_Amount);
        sums.setText("$" + sum);

    }

}
