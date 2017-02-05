package hacks.eng.tab;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddDebtDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddDebtDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "TOTAL";
    private static final String ARG_PARAM2 = "CONTACTS";

    // TODO: Rename and change types of parameters
    private int total;
    private String[] contacts;


    public AddDebtDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param total    The total cost of the debt
     * @param contacts The friends that are being shared with
     * @return A new instance of fragment AddDebtDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static AddDebtDialog newInstance(int total, String[] contacts) {
        AddDebtDialog fragment = new AddDebtDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, total);
        args.putStringArray(ARG_PARAM2, contacts);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            total = getArguments().getInt(ARG_PARAM1);
            contacts = getArguments().getStringArray(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_debt, container, false);
    }

}
