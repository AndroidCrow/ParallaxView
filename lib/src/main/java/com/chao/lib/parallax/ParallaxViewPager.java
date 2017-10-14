package com.chao.lib.parallax;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.chao.lib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang2 on 2017/10/11.
 * 视差动画viewpager
 */

public class ParallaxViewPager extends ViewPager {

    private List<Fragment> mFragmentList = new ArrayList<>();

    public ParallaxViewPager(Context context) {
        this(context,null);
    }

    public ParallaxViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置布局数组
     * @param layouts
     */
    public void setLayout(int[] layouts,FragmentManager fm) {
        mFragmentList.clear();
        for (int layout : layouts) {
            ParallaxFragment parallaxFragment = new ParallaxFragment(layout);
            mFragmentList.add(parallaxFragment);
        }



        setAdapter(new ParallaxPagerAdapter(fm));

        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                Log.e("TAG","position->"+position+" positionOffset->"+positionOffset+" positionOffsetPixels->"+positionOffsetPixels);
                ParallaxFragment outFragment = (ParallaxFragment) mFragmentList.get(position);
                List<View> parallaxViewsOut = outFragment.getParallaxView();
                for (View parallaxView : parallaxViewsOut) {
                    ParallaxTag tag = (ParallaxTag) parallaxView.getTag(R.id.parallax_tag);
                    parallaxView.setTranslationX((- positionOffsetPixels) * tag.translationXOut);
                    parallaxView.setTranslationY((- positionOffsetPixels) * tag.translationYOut);
                }
                try {
                    ParallaxFragment inFragment = (ParallaxFragment) mFragmentList.get(position+1);

                    List<View> parallaxViewsIn = inFragment.getParallaxView();
                    for (View parallaxView : parallaxViewsIn) {
                        ParallaxTag tag = (ParallaxTag) parallaxView.getTag();
                        parallaxView.setTranslationX(( positionOffsetPixels) * tag.translationXIn);
                        parallaxView.setTranslationY((positionOffsetPixels) * tag.translationYIn);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    class ParallaxPagerAdapter extends FragmentPagerAdapter{

        public ParallaxPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

}
