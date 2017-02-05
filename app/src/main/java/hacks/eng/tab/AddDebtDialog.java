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
import android.widget.ListAdapter;
import android.widget.ListView;

import com.onegravity.contactpicker.contact.Contact;
import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.picture.ContactPictureType;

import java.util.ArrayList;
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
    private static final String ARG_PARAM2 = "CONTACTS";

    public static final int PICK_CONTACT = 10;

    // TODO: Rename and change types of parameters
    private int total;
    private HashMap<String,String> contacts;


    public AddDebtDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     *
     * this fragment using the provided parameters.
     *
     * @param total    The total cost of the debt
     * @param contacts The friends that are being shared with
     * @return A new instance of fragment AddDebtDialog.
     */
    public static AddDebtDialog newInstance(int total, HashMap<String,String> contacts) {
        AddDebtDialog fragment = new AddDebtDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, total);
        args.putSerializable(ARG_PARAM2, contacts);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            total = getArguments().getInt(ARG_PARAM1);
            contacts = (HashMap<String, String>) getArguments().getSerializable(ARG_PARAM2);
        }
    }


    public void getContactList(){
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
    public Dialog onCreateDialog(Bundle savedInstance){

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_add_debt, null);
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
                    }
                })
                .setNegativeButton("Cancel",null);

        ListView list = (ListView) view.findViewById(R.id.listView);
        ArrayList<String> strings = new ArrayList<>();
        for(String key : contacts.keySet()){
            strings.add(key + " " + contacts.get(key));
        }
        ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, strings);
        list.setAdapter(listAdapter);
        return builder.create();
    }

}
