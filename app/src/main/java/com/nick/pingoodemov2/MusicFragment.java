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

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MusicFragment.OnMusicFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{

    public static final String TAG = "Music_Fragment";

    private OnMusicFragmentInteractionListener mListener;

    private ListView listView;
    private MusicAdapter musicAdapter;
    private SwipeRefreshLayout refreshLayout;
    private List<MusicItem> musicItems = new ArrayList<>();

    public MusicFragment()
    {
        // Required empty public constructor
    }

    public static MusicFragment newInstance()
    {
        return new MusicFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Music Fragment created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_music, container, false);

        listView = (ListView) v.findViewById(R.id.music_list);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_music);
        refreshLayout.setOnRefreshListener(this);
        musicAdapter = new MusicAdapter(getActivity(), R.layout.listview_row, musicItems);
        listView.setAdapter(musicAdapter);
        return v;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnMusicFragmentInteractionListener)
        {
            mListener = (OnMusicFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnMusicFragmentInteractionListener");
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
        List<MusicItem> list = utility.getMusicItemListFromDatabase();
        if (list != null)
        {
            musicItems.clear();
            musicItems.addAll(list);
            musicAdapter.notifyDataSetChanged();
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
    public interface OnMusicFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onMusicFragmentInteraction(Uri uri);
    }
}
