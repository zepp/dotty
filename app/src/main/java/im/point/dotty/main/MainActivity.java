package im.point.dotty.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import im.point.dotty.R;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager pager;

    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.main_tab_layout);
        pager = findViewById(R.id.main_pager);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        tabLayout.setupWithViewPager(pager);
        pager.setAdapter(new Adapter(getSupportFragmentManager()));
    }

    private class Adapter extends FragmentStatePagerAdapter {
        Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new RecentFragment();
                case 1:
                    return new CommentedFragment();
                default:
                    return new AllFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.main_recent);
                case 1:
                    return getString(R.string.main_commented);
                default:
                    return getString(R.string.main_all);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
