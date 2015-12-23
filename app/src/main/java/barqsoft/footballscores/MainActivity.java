package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
{
    public static String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String SELECTED_MATCH = "Selected_match";

    public static final String CURRENT_PAGE = "barqsoft.footballscores.CURRENT_PAGE";

    public static int selected_match_id;
    public static int current_fragment = 2;

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

       /* if (savedInstanceState == null) {
            pagerFragment = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, pagerFragment)
                    .commit();
        }*/
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
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(CURRENT_PAGE,mViewPager.getCurrentItem());
        /*Log.v(SAVE_TAG,"will save");
        Log.v(SAVE_TAG,"fragment: "+String.valueOf(pagerFragment.mPagerHandler.getCurrentItem()));
        Log.v(SAVE_TAG,"selected id: "+selected_match_id);*/
//        outState.putInt(PAGER_CURRENT, pagerFragment.mPagerHandler.getCurrentItem());
        outState.putInt(SELECTED_MATCH,selected_match_id);
//        getSupportFragmentManager().putFragment(outState, PAGER_FRAGMENT, pagerFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
       /* Log.v(SAVE_TAG,"will retrive");
        Log.v(SAVE_TAG,"fragment: "+String.valueOf(savedInstanceState.getInt("Pager_Current")));
        Log.v(SAVE_TAG,"selected id: "+savedInstanceState.getInt("Selected_match"));*/
//        current_fragment = savedInstanceState.getInt(PAGER_CURRENT);
        selected_match_id = savedInstanceState.getInt(SELECTED_MATCH);
  //      pagerFragment = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState,PAGER_FRAGMENT);
        mViewPager.setCurrentItem(savedInstanceState.getInt(CURRENT_PAGE));
        super.onRestoreInstanceState(savedInstanceState);
    }
}
