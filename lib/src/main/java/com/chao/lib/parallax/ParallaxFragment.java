package com.chao.lib.parallax;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;


import com.chao.lib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang2 on 2017/10/11.
 */

public class ParallaxFragment extends Fragment implements LayoutInflaterFactory {

    private AppCompatViewInflater mAppCompatViewInflater;
    private int mlayoutId;

    private int[] mAttrs = new int[]{R.attr.translationXIn, R.attr.translationXOut,R.attr.translationYIn, R.attr.translationYOut};
    private List<View> parallaxView = new ArrayList<>();


    public ParallaxFragment(int layoutId) {
        this.mlayoutId = layoutId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        LayoutInflater layoutInflater = inflater.cloneInContext(getActivity());

        LayoutInflaterCompat.setFactory(layoutInflater, this);

        return layoutInflater.inflate(mlayoutId, container, false);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        View view = createView(parent, name, context, attrs);
        if (view != null) {
            // Log.e("TAG", "我来创建View");
            // 解析所有的我们自己关注属性
            analysisAttrs(view, context, attrs);
        }
        return view;
    }

    private void analysisAttrs(View view, Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, mAttrs);
        if (view != null && typedArray.getIndexCount() != 0) {
            int n = typedArray.getIndexCount();
            ParallaxTag parallaxTag = new ParallaxTag();
            for (int i = 0; i <= n; i++) {
                int attr = typedArray.getIndex(i);
                switch (attr) {
                    case 0:
                        parallaxTag.translationXIn = typedArray.getFloat(attr, 0f);
                        break;
                    case 1:
                        parallaxTag.translationXOut = typedArray.getFloat(attr, 0f);
                        break;
                    case 2:
                        parallaxTag.translationYIn = typedArray.getFloat(attr, 0f);
                        break;
                    case 3:
                        parallaxTag.translationYOut = typedArray.getFloat(attr, 0f);
                        break;
                }
            }
            view.setTag(R.id.parallax_tag,parallaxTag);
            parallaxView.add(view);
            Log.e("TAG",parallaxTag.toString());
        }
        typedArray.recycle();
    }

    public View createView(View parent, final String name, @NonNull Context context,
                           @NonNull AttributeSet attrs) {
        final boolean isPre21 = Build.VERSION.SDK_INT < 21;

        if (mAppCompatViewInflater == null) {
            mAppCompatViewInflater = new AppCompatViewInflater();
        }

        // We only want the View to inherit it's context if we're running pre-v21
        final boolean inheritContext = isPre21 && true
                && shouldInheritContext((ViewParent) parent);

        return mAppCompatViewInflater.createView(parent, name, context, attrs, inheritContext,
                isPre21, /* Only read android:theme pre-L (L+ handles this anyway) */
                true /* Read read app:theme as a fallback at all times for legacy reasons */
        );
    }

    private boolean shouldInheritContext(ViewParent parent) {
        if (parent == null) {
            // The initial parent is null so just return false
            return false;
        }
        while (true) {
            if (parent == null) {
                // Bingo. We've hit a view which has a null parent before being terminated from
                // the loop. This is (most probably) because it's the root view in an inflation
                // call, therefore we should inherit. This works as the inflated layout is only
                // added to the hierarchy at the end of the inflate() call.
                return true;
            } else if (ViewCompat.isAttachedToWindow((View) parent)) {
                // We have either hit the window's decor view, a parent which isn't a View
                // (i.e. ViewRootImpl), or an attached view, so we know that the original parent
                // is currently added to the view hierarchy. This means that it has not be
                // inflated in the current inflate() call and we should not inherit the context.
                return false;
            }
            parent = parent.getParent();
        }
    }


    public List<View> getParallaxView() {
        return parallaxView;
    }
}
