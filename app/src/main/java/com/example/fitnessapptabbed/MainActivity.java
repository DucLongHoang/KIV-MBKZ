package com.example.fitnessapptabbed;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.fitnessapptabbed.databinding.ActivityMainBinding;
import com.example.fitnessapptabbed.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private static final long TWO_SECONDS = 2000;

    private SwitchCompat modeSwitch;
    private long backPressedTime;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getBaseContext().deleteDatabase("plans_database.db");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(1).select();

        modeSwitch = findViewById(R.id.nightModeSwitch);
        setNightModeSwitch();
    }

    @Override
    public void onBackPressed() {
        if((backPressedTime + TWO_SECONDS) > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        }
        else {
            Toast.makeText(getBaseContext(), R.string.back_again_to_exit_msg, Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    /**
     * Method sets up night mode switch
     */
    private void setNightModeSwitch() {
        // set initial switch position
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES: { modeSwitch.setChecked(true); break; }
            case Configuration.UI_MODE_NIGHT_NO: { modeSwitch.setChecked(false); break; }
        }

        // set switch check listener
        modeSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            int mode = (isChecked) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);
        });
    }

    /**
     * Method enables or disables the night mode switch
     * @param enable true to enable switch, false to disable it
     */
    public void enableSwitch(boolean enable) {
        modeSwitch.setEnabled(enable);
    }
}