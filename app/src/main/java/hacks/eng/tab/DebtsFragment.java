package hacks.eng.tab;

import android.content.Context;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DebtsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DebtsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DebtsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

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
               return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View v = inflater.inflate(R.layout.fragment_debts, container, false);
        List<Data> data = fill_with_data();

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.cardList);
        Recycler_View_Adapter adapter = new Recycler_View_Adapter(data, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.addOnItemTouchListener(new CustomRVItemTouchListener(this.getContext(), recyclerView, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getActivity(), "On Click: " + position , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getActivity(), "On Long Click: " + position, Toast.LENGTH_SHORT).show();
            }
        }));

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public List<Data> fill_with_data() {

        List<Data> data = new ArrayList<>();

        data.add(new Data("Willam", "All 5% Precent", R.mipmap.ic_launcher));
        data.add(new Data("Tate", "Huge Cunt Master", R.mipmap.ic_launcher));
        data.add(new Data("Nathan", "Small Dude", R.mipmap.ic_launcher));
        data.add(new Data("Joseph", "I like bondage", R.mipmap.ic_launcher));
        data.add(new Data("Jack", "Stole your bitch", R.mipmap.ic_launcher));
        data.add(new Data("Jamie", "Physcopathic suicdal mess", R.mipmap.ic_launcher));
        data.add(new Data("Ryan", "Confused", R.mipmap.ic_launcher));
        data.add(new Data("Abhinav", "Shirtless", R.mipmap.ic_launcher));


        return data;
    }

    public interface RecyclerViewItemClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }
}
