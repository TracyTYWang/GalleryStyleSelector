package com.tracy.gallery;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private OilCardViewAdapter oilCardViewAdapter;
    private ViewPager viewPager;
    private ArrayList<OilCardDataItem> oilCardList;
    private ArrayList<View> oilCardLayoutList=new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyoil);
        initViews();
    }

    private void initViews(){
        //初始化数据
        oilCardList = new ArrayList<OilCardDataItem>();
        OilCardDataItem sinopec=new OilCardDataItem(R.drawable.card_sinopec);
        OilCardDataItem bpcard=new OilCardDataItem(R.drawable.card_oil_bp);
        OilCardDataItem gdcard=new OilCardDataItem(R.drawable.card_oil);
        oilCardList.add(gdcard);
        oilCardList.add(sinopec);
        oilCardList.add(bpcard);

        //因为是固定的几个选择，所以就提前加载好
        inflateCardView();
        viewPager = (ViewPager)findViewById(R.id.vp_oil_cards);
        //如果子布局比较复杂，还需要算出子布局的高度，这里按垂直线性无margin算的
        int childMaxHeight = 0;
        for (int i = 0; i < oilCardList.size(); i++) {
            int itemHeight = oilCardLayoutList.get(i).getMeasuredHeight();
            if (childMaxHeight < itemHeight) {
                childMaxHeight = itemHeight;
            }
        }
        //更新view pager的高度
        LinearLayout.LayoutParams layoutParams =  (LinearLayout.LayoutParams)viewPager.getLayoutParams();
        layoutParams.height=childMaxHeight;
        //设置适配器
        oilCardViewAdapter = new OilCardViewAdapter();
        viewPager.setAdapter(oilCardViewAdapter);
        // 1.设置幕后item的缓存数目
        viewPager.setOffscreenPageLimit(3);
        // 2.设置页与页之间的间距
        viewPager.setPageMargin(20);
        // 3.将父类的touch事件分发至viewPgaer，否则只能滑动中间的一个view对象
        View container = findViewById(R.id.ll_oil_card_container);
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return viewPager.dispatchTouchEvent(event);
            }
        });
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 把当前显示的position传递出去。获取当前选择的子元素必需要这么获得，否则position不是固定的
                setCurrentType(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    private void inflateCardView(){
        for(int position = 0 ; position<oilCardList.size();position++) {
            LinearLayout itemView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_oil_card, null);
            final OilCardDataItem dataItem = oilCardList.get(position);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_oil_card);
            imageView.setImageResource(dataItem.imageRes);
            oilCardLayoutList.add(itemView);
            itemView.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        }
    }
    public class OilCardDataItem{
        public Integer imageRes;
        public OilCardDataItem(Integer imageRes){
            this.imageRes=imageRes;
        }
    }
    public class OilCardViewAdapter extends PagerAdapter {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object)
        {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            View itemView= oilCardLayoutList.get(position);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public int getCount()
        {
            return oilCardList.size();
        }
    }
    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private  float MIN_SCALE = 0.5f;

        private  float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(MIN_ALPHA);
                view.setTranslationX(0);
                view.setScaleX(MIN_SCALE);
                view.setScaleY(MIN_SCALE);
            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to
                // shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }
                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
                        / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(MIN_ALPHA);
                view.setScaleX(MIN_SCALE);
                view.setScaleY(MIN_SCALE);
                view.setTranslationX(0);
            }
        }
    }
    private void setCurrentType(int position){
        Log.d(TAG," selected position: "+position);
    }
}
