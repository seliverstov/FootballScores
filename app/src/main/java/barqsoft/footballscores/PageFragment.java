package barqsoft.footballscores;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import barqsoft.footballscores.api.SelectedMatchChangedListener;
import barqsoft.footballscores.db.DatabaseContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class PageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SelectedMatchChangedListener {
    private static final String TAG = PageFragment.class.getSimpleName();

    public static final String DATE = "DATE";
    private RecyclerView mScoreList;
    private ScoresAdapter mAdapter;

    public static final int SCORES_LOADER = 0;

    private TextView emptyView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.page, container, false);

        mScoreList = (RecyclerView) rootView.findViewById(R.id.scores_list);

        mScoreList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ScoresAdapter(getActivity(),null);
        mScoreList.setAdapter(mAdapter);

        emptyView = (TextView)rootView.findViewById(R.id.emptylist);

        return rootView;
    }

    @Override
    public void notifySelectedItemChanged() {
        if (mAdapter!=null) {
            mAdapter.notifyDataSetChanged();
            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                if (MainActivity.sSelectedMatch == mAdapter.getItemId(i)) {
                    mScoreList.scrollToPosition(i);
                    return;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        notifySelectedItemChanged();
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
        notifySelectedItemChanged();
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
