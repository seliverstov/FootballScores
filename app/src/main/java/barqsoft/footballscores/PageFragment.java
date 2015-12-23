package barqsoft.footballscores;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import barqsoft.footballscores.db.DatabaseContract;
import barqsoft.footballscores.service.ScoresFetchService;

/**
 * A placeholder fragment containing a simple view.
 */
public class PageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{

    public static final String DATE = "DATE";
    private static final String TAG = PageFragment.class.getSimpleName();
    private ScoresAdapter mAdapter;
    private ProgressBar mProgressBar;
    private BroadcastReceiver receiver;

    public static final int SCORES_LOADER = 0;

    private int last_selected_item = -1;

    private void updateScores(){
        if (mProgressBar!=null) mProgressBar.setVisibility(View.VISIBLE);
        Intent service = new Intent(getActivity(), ScoresFetchService.class);
        service.setAction(MainActivity.UPDATE_SCORES);
        getActivity().startService(service);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.page, container, false);

        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progressbar);

        final ListView scoreList = (ListView) rootView.findViewById(R.id.scores_list);

        mAdapter = new ScoresAdapter(getActivity(),null,0);
        scoreList.setAdapter(mAdapter);

        scoreList.setEmptyView(rootView.findViewById(R.id.emptylist));

        mAdapter.detail_match_id = MainActivity.selectedMath;

        scoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScoresAdapter.ViewHolder selected = (ScoresAdapter.ViewHolder) view.getTag();
                mAdapter.detail_match_id = selected.match_id;
                MainActivity.selectedMath = (int) selected.match_id;
                mAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null,null,new String[] {bundle.getString(DATE)},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader){
        mAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mProgressBar!=null) {
                    mProgressBar.setVisibility(mProgressBar.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
                }
            }
        };
        IntentFilter filter = new IntentFilter(MainActivity.UPDATE_SCORES);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);

        getLoaderManager().initLoader(SCORES_LOADER, getArguments(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }
}
