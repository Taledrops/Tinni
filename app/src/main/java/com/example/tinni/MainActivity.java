package com.example.tinni;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.tinni.helpers.Constants;
import com.example.tinni.ui.home.HomeFragment;
import com.example.tinni.ui.programs.ProgramsFragment;
import com.example.tinni.ui.sounds.SoundsFragment;
import com.example.tinni.ui.stats.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * <h1>Main Activity</h1>
 * Managing the app navigation
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   18.06.2020
 */

public class MainActivity extends AppCompatActivity
{
    private final Fragment fragment1 = new HomeFragment();
    private final Fragment fragment2 = new ProgramsFragment();
    private final Fragment fragment3 = new SoundsFragment();
    private final Fragment fragment4 = new StatsFragment();
    public Fragment active = fragment1;
    private final FragmentManager fm = getSupportFragmentManager();

    /**
     * <h2>On Create</h2>
     * Setting up the bottom navigation by attaching a listener
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment1, "1").commit();
        Constants.getInstance().init(this);
    }

    /**
     * <h2>On Navigation Item Selected Listener</h2>
     * Listening to navigation changes and hiding/adding corresponding fragments
     * The fragments are only added when selecting them to speed up loading times
     * Inactive fragments are hidden
     */

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item ->
    {
        switch (item.getItemId())
        {
            case R.id.navigation_home:
                if (active != fragment1)
                {
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                }
                return true;
            case R.id.navigation_programs:
                if (active != fragment2)
                {
                    Fragment f2 = fm.findFragmentByTag("2");
                    if (f2 != null)
                    {
                        fm.beginTransaction().hide(active).show(fragment2).commit();
                    }
                    else
                    {
                        fm.beginTransaction().add(R.id.nav_host_fragment, fragment2, "2").hide(active).commit();
                    }
                    active = fragment2;
                }
                else
                {
                    ProgramsFragment programsFragment = (ProgramsFragment) fragment2;
                    programsFragment.scrollToTop();
                }
                return true;
            case R.id.navigation_sounds:
                if (active != fragment3)
                {
                    Fragment f3 = fm.findFragmentByTag("3");
                    if (f3 != null)
                    {
                        fm.beginTransaction().hide(active).show(fragment3).commit();
                    }
                    else
                    {
                        fm.beginTransaction().add(R.id.nav_host_fragment, fragment3, "3").hide(active).commit();
                    }
                    active = fragment3;
                }
                else
                {
                    SoundsFragment soundsFragment = (SoundsFragment) fragment3;
                    soundsFragment.scrollToTop();
                }
                return true;
            case R.id.navigation_stats:
                if (active != fragment4)
                {
                    Fragment f4 = fm.findFragmentByTag("4");
                    if (f4 != null)
                    {
                        fm.beginTransaction().hide(active).show(fragment4).commit();
                    }
                    else
                    {
                        fm.beginTransaction().add(R.id.nav_host_fragment, fragment4, "4").hide(active).commit();
                    }
                    active = fragment4;
                }

                return true;
        }
        return false;
    };
}
