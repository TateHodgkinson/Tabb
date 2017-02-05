package hacks.eng.tab;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onegravity.contactpicker.contact.Contact;
import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.picture.ContactPictureType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddDebtDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddDebtDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "TOTAL";
    private static final String ARG_PARAM2 = "NAMES";
    private static final String ARG_PARAM3 = "PHONES";

    public static final int PICK_CONTACT = 10;

    // TODO: Rename and change types of parameters
    private double total;
    private String[] names;
    private String[] phoneNumbers;


    public AddDebtDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * <p>
     * this fragment using the provided parameters.
     *
     * @param total The total cost of the debt
     * @param names The friends that are being shared with
     * @return A new instance of fragment AddDebtDialog.
     */
    public static AddDebtDialog newInstance(double total, String[] names, String[] phoneNumbers) {
        AddDebtDialog fragment = new AddDebtDialog();
        Bundle args = new Bundle();
        args.putDouble(ARG_PARAM1, total);
        args.putStringArray(ARG_PARAM2, names);
        args.putStringArray(ARG_PARAM3, phoneNumbers);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            total = getArguments().getDouble(ARG_PARAM1);
            names = getArguments().getStringArray(ARG_PARAM2);
            phoneNumbers = getArguments().getStringArray(ARG_PARAM3);
        }
    }


    public void getContactList() {
        Intent intent = new Intent(getActivity(), ContactPickerActivity.class)
                .putExtra(ContactPickerActivity.EXTRA_THEME, R.style.ContactPicker_Theme_Light)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE, ContactPictureType.ROUND.name())
                .putExtra(ContactPickerActivity.EXTRA_SHOW_CHECK_ALL, true)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION, ContactDescription.ADDRESS.name())
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER, ContactSortOrder.AUTOMATIC.name());
        getActivity().startActivityForResult(intent, PICK_CONTACT);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {

        LayoutInflater inflater = getActivity().getLayoutInflater();

       final View view = inflater.inflate(R.layout.fragment_add_debt, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle("Add Debt")
                .setNeutralButton("Choose Contacts", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getContactList();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Submit Data
                        System.out.println("ONCLICK");
                        EditText text = (EditText)view.findViewById(R.id.editTextDialog);
                        double total = Double.valueOf(text.getText().toString());
                        runTransaction(total);
                        addCred(total);
                    }
                })
                .setNegativeButton("Cancel", null);

        ListView list = (ListView) view.findViewById(R.id.listView);
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; names != null && i < names.length; i++) {
            strings.add(names[i] + " " + phoneNumbers[i]);
        }
        ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, strings);
        list.setAdapter(listAdapter);

        EditText text = (EditText) view.findViewById(R.id.editTextDialog);
        text.setText("" + total);
        return builder.create();
    }

    void runTransaction(double total) {

        double amount = total / phoneNumbers.length;
        String[] people = phoneNumbers;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        Transaction myTransaction = new Transaction(amount,people,strDate);
        DatabaseUtils databaseUtils = new DatabaseUtils(myRef);

        databaseUtils.performTransaction(myTransaction);


    }

    void addCred(double total) {
        String userCred = phoneNumbers[0];

        for(int i = 1; i < phoneNumbers.length; i++) {
            double amount = total / phoneNumbers.length;
            String userDebt = phoneNumbers[i];
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
            DatabaseUtils databaseUtils = new DatabaseUtils(myRef);
            databaseUtils.updateAmount(userDebt, userCred, amount, names[i]);
        }
    }

}