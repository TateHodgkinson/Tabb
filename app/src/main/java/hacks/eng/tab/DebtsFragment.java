package hacks.eng.tab;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * interface
 * to handle interaction events.
 * Use the {@link DebtsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DebtsFragment extends Fragment {

    public static DebtsFragment instance;
    String temp_amount = "0";
    List<Data> data;
    Recycler_View_Adapter adapter;
    RecyclerView recyclerView;

    public DebtsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DebtsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DebtsFragment newInstance() {
        DebtsFragment fragment = new DebtsFragment();
        instance = fragment;
        return fragment;
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create recycled viewed
        View v = inflater.inflate(R.layout.fragment_debts, container, false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseUtils databaseUtils = new DatabaseUtils(myRef);

        TelephonyManager tm = (TelephonyManager) this.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String myPhoneNumber = tm.getLine1Number().substring(tm.getLine1Number().length() - 10);

        databaseUtils.createList(myPhoneNumber);

        data = new ArrayList<>();

        recyclerView = (RecyclerView) v.findViewById(R.id.cardList);
        adapter = new Recycler_View_Adapter(data, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.addOnItemTouchListener(new CustomRVItemTouchListener(this.getContext(), recyclerView, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                // only if value is a debit, than can you pay money towards it
                if (data.get(position).amount < 0) {
                    LayoutInflater li = LayoutInflater.from(getContext());
                    View promptsView = li.inflate(R.layout.clear_debts_dialog, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            getContext());

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialog);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // get user input and set it to result
                                            // edit text
                                            temp_amount = (userInput.getText()).toString();
                                            //updateRequestedAmount(data.get(position).name, )
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }else{

                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirm Payment")
                            .setMessage("Are you sure you want to confirm?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                System.out.println(getContactName(getContext(), "6479847862"));
                Toast.makeText(getActivity(), "On Long Click: " + position, Toast.LENGTH_SHORT).show();
            }
        }));

        return v;
    }

    public void fill_with_data(Data data, boolean modified) {

        List<Data> temp = new ArrayList<>();
        int size = adapter.list.size();

            if(!modified) {
                adapter.insert(0, new Data(getContactName(getContext(), data.name), data.amount, R.mipmap.ic_launcher));
            }else{
                for(int i = 0; i < adapter.list.size(); i++){
                    if(getContactName(getContext(), data.name).equals(adapter.list.get(i).name)){
                        adapter.remove(adapter.list.get(i));
                        Log.d("FIND THIS", data.amount + "");
                        adapter.insert(0,new Data(getContactName(getContext(), data.name), data.amount, R.mipmap.ic_launcher));
                    }
                }

            }


    }



    public interface RecyclerViewItemClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

}
