package com.chao.parallaxview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.chao.lib.parallax.ParallaxViewPager;

public class MainActivity extends AppCompatActivity {
    private ParallaxViewPager mParallaxViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mParallaxViewPager = (ParallaxViewPager) findViewById(R.id.viewpager);
        mParallaxViewPager.setLayout(new int[]
                {R.layout.fragment_page_first, R.layout.fragment_page_second,
                        R.layout.fragment_page_third}, getSupportFragmentManager());
    }
}
