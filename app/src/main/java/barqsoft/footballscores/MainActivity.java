package barqsoft.footballscores;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import barqsoft.footballscores.sync.SyncAdapter;

public class MainActivity extends AppCompatActivity
{
    public static String TAG = MainActivity.class.getSimpleName();

    public static final String ACTION_UPDATE_SCORES = "barqsoft.footballscores.ACTION_UPDATE_SCORES";
    public static final String MESSAGE_UPDATE_SCORES = "barqsoft.footballscores.MESSAGE_UPDATE_SCORES";

    public static final String ACTION_SCORES_UPDATED = "barqsoft.footballscores.ACTION_SCORES_UPDATED";

    public static final String STORE_SELECTED_MATCH = "barqsoft.footballscores.SELECTED_MATH";
    public static final String STORE_CURRENT_PAGE = "barqsoft.footballscores.CURRENT_PAGE";

    public static final long SYNC_INTERVAL = 3600L;

    public static int selectedMatch;

    private ViewPager mViewPager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setProgressViewOffset(true,250,450);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });

        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), this));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
                }
            }
        });

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(mViewPager);



        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mSwipeRefreshLayout.setRefreshing(false);
                String message = intent.getStringExtra(MESSAGE_UPDATE_SCORES);
                if (message!=null){
                    Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
                }
            }
        };
        IntentFilter filter = new IntentFilter(MainActivity.ACTION_UPDATE_SCORES);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);

        if (savedInstanceState==null){
            mViewPager.setCurrentItem(2);
        }

        SyncAdapter.addPeriodicSync(this,SYNC_INTERVAL);
        update();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_about:{
                Intent start_about = new Intent(this,AboutActivity.class);
                startActivity(start_about);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putInt(STORE_CURRENT_PAGE,mViewPager.getCurrentItem());
        outState.putInt(STORE_SELECTED_MATCH, selectedMatch);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        selectedMatch = savedInstanceState.getInt(STORE_SELECTED_MATCH);
        mViewPager.setCurrentItem(savedInstanceState.getInt(STORE_CURRENT_PAGE));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    void update(){
        if (Utils.isNetworkConnectionAvailable(this)) {
            SyncAdapter.syncNow(this);
        }else {
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this.getApplicationContext());
            Intent messageIntent = new Intent(MainActivity.ACTION_UPDATE_SCORES);
            messageIntent.putExtra(MainActivity.MESSAGE_UPDATE_SCORES, this.getString(R.string.no_network));
            broadcastManager.sendBroadcast(messageIntent);
        }
    }
}
