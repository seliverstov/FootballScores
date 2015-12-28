package barqsoft.footballscores;

import android.content.BroadcastReceiver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import barqsoft.footballscores.db.DatabaseContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class PageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{

    public static final String DATE = "DATE";
    private static final String TAG = PageFragment.class.getSimpleName();
    private ScoresAdapter mAdapter;
    private BroadcastReceiver receiver;

    public static final int SCORES_LOADER = 0;

    private int last_selected_item = -1;
    private TextView emptyView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.page, container, false);

        final RecyclerView scoreList = (RecyclerView) rootView.findViewById(R.id.scores_list);

        scoreList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ScoresAdapter(getActivity(),null);
        scoreList.setAdapter(mAdapter);

        emptyView = (TextView)rootView.findViewById(R.id.emptylist);

        mAdapter.selectedMatch = MainActivity.selectedMatch;

        /*scoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScoresAdapter.ViewHolder selected = (ScoresAdapter.ViewHolder) view.getTag();
                mAdapter.selectedMatch = selected.matchId;
                MainActivity.selectedMatch = (int) selected.matchId;
                mAdapter.notifyDataSetChanged();
            }
        });*/
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(getActivity(), DatabaseContract.ScoresEntry.buildScoreWithDate(),
                null,null,new String[] {bundle.getString(DATE)},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        if (cursor!=null && cursor.getCount()>0){
            emptyView.setVisibility(View.GONE);
        }else{
            emptyView.setVisibility(View.VISIBLE);
        }
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader){
        mAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        getLoaderManager().initLoader(SCORES_LOADER, getArguments(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
