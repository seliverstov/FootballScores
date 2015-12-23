package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import barqsoft.footballscores.service.ScoresFetchService;

public class MainActivity extends AppCompatActivity
{
    public static String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String UPDATE_SCORES = "barqsoft.footballscores.UPDATE_SCORES";
    public static final String SELECTED_MATCH = "barqsoft.footballscores.SELECTED_MATH";
    public static final String CURRENT_PAGE = "barqsoft.footballscores.CURRENT_PAGE";

    public static int selectedMath;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setLogo(R.drawable.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
        }*/

        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), this));

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(mViewPager);

        if (savedInstanceState==null){
            mViewPager.setCurrentItem(2);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putInt(CURRENT_PAGE,mViewPager.getCurrentItem());
        outState.putInt(SELECTED_MATCH, selectedMath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        selectedMath = savedInstanceState.getInt(SELECTED_MATCH);
        mViewPager.setCurrentItem(savedInstanceState.getInt(CURRENT_PAGE));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent messageIntent = new Intent(MainActivity.UPDATE_SCORES);
        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(messageIntent);

        Intent service = new Intent(this, ScoresFetchService.class);
        service.setAction(UPDATE_SCORES);
        startService(service);
    }
}
