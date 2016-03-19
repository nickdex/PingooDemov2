package com.nick.pingoodemov2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bumptech.glide.util.Util;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactFragment.OnContactFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{

    public static final String TAG = "Contact_Fragment";

    OnContactFragmentInteractionListener mListener;

    private ListView listView;
    private CustomAdapter customAdapter;
    private SwipeRefreshLayout refreshLayout;

    private List<CustomItem> contactItems = new ArrayList<>();


    public ContactFragment()
    {
        // Required empty public constructor
    }

    public static ContactFragment newInstance()
    {

        return new ContactFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact, container, false);

        listView = (ListView) v.findViewById(R.id.contact_list);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_contact);
        refreshLayout.setOnRefreshListener(this);
        customAdapter = new CustomAdapter(getActivity(), R.layout.listview_row, contactItems);
        listView.setAdapter(customAdapter);
        return v;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnContactFragmentInteractionListener)
        {
            mListener = (OnContactFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnContactFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh()
    {
        DatabaseUtility utility = DatabaseUtility.newInstance(getContext());
        List<CustomItem> list = utility.getItemListFromDatabase(TAG);
        if (list != null)
        {
            contactItems.clear();
            contactItems.addAll(list);
            customAdapter.notifyDataSetChanged();
            Log.i(TAG, "Adapter notified");
        }
        utility.setAllOld(TAG);

        Log.d(TAG, "Refresh Complete");
        refreshLayout.setRefreshing(false);
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
    public interface OnContactFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onContactFragmentInteraction(Uri uri);
    }
}
