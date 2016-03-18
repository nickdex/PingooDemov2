package com.nick.pingoodemov2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import com.blunderer.materialdesignlibrary.fragments.ListViewFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MusicFragment.OnMusicFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "Music_Fragment";

    private OnMusicFragmentInteractionListener mListener;

    private ListView listView;
    private CustomAdapter customAdapter;
    private SwipeRefreshLayout refreshLayout;

    private CursorLoader cursorLoader;

    private List<CustomItem> musicItems = new ArrayList<CustomItem>();

    private static final int MUSIC_LOADER = 0;
    private String[] projection = {MediaStore.Audio.Media.TITLE};
    private String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND "+
            MediaStore.Audio.Media.DATA + " NOT LIKE ?";
    private String[] selectionArgs = new String[]{"%WhatsApp%"};
    private boolean firstRun = true;

    public MusicFragment() {
        // Required empty public constructor
    }

    public static MusicFragment newInstance() {
        return new MusicFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Music Fragment created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getLoaderManager().initLoader(MUSIC_LOADER, null, this);
            }
        } else {
            getLoaderManager().initLoader(MUSIC_LOADER, null, this);
            Log.i(TAG, "Loader Initialised");
        }

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact, container, false);

        listView = (ListView) v.findViewById(R.id.contact_list);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_contact);
        refreshLayout.setOnRefreshListener(this);
        customAdapter = new CustomAdapter(getActivity(), R.layout.listview_row, musicItems);
        listView.setAdapter(customAdapter);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMusicFragmentInteractionListener) {
            mListener = (OnMusicFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMusicFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        if(firstRun)
        {
            firstRun = false;
            getLoaderManager().restartLoader(MUSIC_LOADER, null, this);
            Log.i(TAG, "List was empty");
        }
        else
        {
            DatabaseUtility utility = DatabaseUtility.newInstance(getContext());
            List<CustomItem> list = utility.getItemListFromDatabase();
            if(list != null)
            {
                musicItems.clear();
                musicItems.addAll(list);
            }
        }
        customAdapter.notifyDataSetChanged();
        Log.i(TAG, "Adapter notified");

        Log.d(TAG, "Refresh Complete");
        refreshLayout.setRefreshing(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        cursorLoader = new CursorLoader(
                getActivity(),
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                MediaStore.Audio.Media.TITLE);
        Log.d(TAG, "Loader Created");
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG, "Load finished");

        DatabaseUtility utility = DatabaseUtility.newInstance(getContext());

        List<CustomItem> list =  utility.getListFromCursor(data);

        utility.insertList(list);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
    public interface OnMusicFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMusicFragmentInteraction(Uri uri);
    }
}
