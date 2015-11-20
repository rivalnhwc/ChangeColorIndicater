package rival.com.changecolorindicater;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import rival.com.changecolorindicater.views.ChangeColorIconWithText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {


    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;

    private String[] mTitles = new String[]{"First Fragment",
            "Second Fragment", "Third Fragment", "Forth Fragment"};

    private List<ChangeColorIconWithText> mTabIndicator = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        inidDatas();

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);


    }


    private void inidDatas() {

        for (String title : mTitles) {
            TabFragment tabFragment = new TabFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            tabFragment.setArguments(bundle);
            mTabs.add(tabFragment);
        }
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return mTabs.get(position);
            }

            @Override
            public int getCount() {
                return mTabs.size();
            }
        };
        initTabIndicator();
    }

    private ChangeColorIconWithText one, two, three, four;

    private void initTabIndicator() {

        one = (ChangeColorIconWithText) findViewById(R.id.cciw_one);
        two = (ChangeColorIconWithText) findViewById(R.id.cciw_two);
        three = (ChangeColorIconWithText) findViewById(R.id.cciw_three);
        four = (ChangeColorIconWithText) findViewById(R.id.cciw_four);

        mTabIndicator.add(one);
        mTabIndicator.add(two);
        mTabIndicator.add(three);
        mTabIndicator.add(four);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);

        one.setPositionOffset(1.0f, -1);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        resetTabs();
        int position = -1;
        switch (v.getId()) {
            case R.id.cciw_one:
                position = 0;
                break;
            case R.id.cciw_two:
                position = 1;
                break;
            case R.id.cciw_three:
                position = 2;
                break;
            case R.id.cciw_four:
                position = 3;
                break;
        }
        if (position != -1) {
            mTabIndicator.get(position).setPositionOffset(1.0f, -1);
            mViewPager.setCurrentItem(position, false);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        Log.e("test", positionOffset + "++++++" + position);
        if (positionOffset > 0) {
            mTabIndicator.get(position).setPositionOffset(1 - positionOffset, ChangeColorIconWithText.DIRECTION_RIGHT);
            mTabIndicator.get(position + 1).setPositionOffset(positionOffset, ChangeColorIconWithText.DIRECTION_LEFT);

        }

    }

    private void resetTabs() {

        for (int i = 0; i < mTabIndicator.size(); i++) {
            mTabIndicator.get(i).setPositionOffset(0, -1);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
