![GIF1.gif](http://upload-images.jianshu.io/upload_images/5700138-37e742dcfce87120.gif?imageMogr2/auto-orient/strip)
#### 要做到这种动画效果我们需要用到捕获所有view并对view 进行一系列操作
#### 上一节讲了了解了view的创建流程现，现在通过demo的形式来完成此功能


#### 首先创建一个framgment 来实现LayoutInflaterFactory接口，这一步骤做完 所有的view都会走接口中的方法了，可以照抄系统的源码一直往下走 AppCompatViewInflater 这个类发现是new不出来的，直接把这个类的内容复制出来我们新建一个类命名相同，这样整个fragment内的布局里面的view都会走我们自己的方法


```     

        //我重新复制一个LayoutInflater ，因为LayoutInflater是单例 否则下面会报错
        LayoutInflater layoutInflater = inflater.cloneInContext(getActivity());

        LayoutInflaterCompat.setFactory(layoutInflater, this);

        return layoutInflater.inflate(mlayoutId, container, false);
        
    实现接口所调用的方法
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

    //获取所有的view 并拿到view中的参数 设置到tag中 通过viewpager的滑动而一起滑动
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
    
    
    
```
#### 上面的代码已经把所有的view拿到，现在自定义一个viewpager实现监听接口

```
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
```
#### 使用方法

```
        mParallaxViewPager = (ParallaxViewPager) findViewById(R.id.viewpager);
mParallaxViewPager.setLayout(new int[]
              {R.layout.fragment_page_first, R.layout.fragment_page_second, 
                        R.layout.fragment_page_third}, getSupportFragmentManager());
```
