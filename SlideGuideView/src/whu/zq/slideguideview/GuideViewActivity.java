package whu.zq.slideguideview;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/*
 *  实现左右滑动指引效果
 *  包含文件：1.GuideViewActivity.java 主Activity文件
 *  2.layout/slide_guide_view_main.xml 	主Activity的布局文件，包括一个ViewPager，一个按钮（滑动到最后一页时显示），以及用于显示滑动索引的点
 *  3.layout/slide_guide_view_item.xml 	ViewPager布局，包含一个ImageView，承载滑动图片
 *  4.drawable/circle_button.xml		跳转按钮的样式文件
 *  5.drawable/guideviewpage0.jpg等，滑动图片
 *  6.drawable/guide_view_page_indicator.jpg和guide_view_page_indicator_focused.jpg 索引dot
 *  使用说明：
 *  1.引入上述文件除5之外的文件；
 *  2.引入5对应的滑动图片，并在类GuideViewActivity中更改保存图片资源ID的数组guideViewResId
 *  3.添加GuideViewActivity到AndroidManifest.xml，设置主题无标题栏
 *  4.在类GuideViewActivity中，完善btnJump的跳转功能，跳转功能在OnClick函数
 *  5.如果想让图片填充整个屏幕，需要更改slide_guide_view_item.xml，设置height为fill_parent
 * */

public class GuideViewActivity extends Activity implements OnClickListener {

	private Button btnJump;
	private ViewPager viewPager;
	private ArrayList<View> viewPagerList;
	private ImageView imageView;
	private ImageView[] imageViews;
	//包裹滑动图片LinearLayout
	private ViewGroup mainGroup;
	//包裹小圆点的LinearLayout
	private ViewGroup dotGroup;
	private LayoutInflater inflater;
	// 引导图片ID
	private int guideViewResId[] = new int[]{R.drawable.guideviewpage0,
			R.drawable.guideviewpage1, R.drawable.guideviewpage2,R.drawable.guideviewpage3};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        inflater = getLayoutInflater();
        
        // 初始化PagerList
        inflatePagerList();
                
        imageViews = new ImageView[viewPagerList.size()];
        mainGroup = (ViewGroup)inflater.inflate(R.layout.slide_guide_view_main, null);
        dotGroup = (ViewGroup) mainGroup.findViewById(R.id.ll_view_dot_group);
        viewPager = (ViewPager) mainGroup.findViewById(R.id.vp_guide_view);
        btnJump = (Button) mainGroup.findViewById(R.id.btn_to_main);
        
        for(int i=0; i<viewPagerList.size(); i++){
        	imageView = new ImageView(this);
        	imageView.setLayoutParams(new LayoutParams(20, 20));
        	imageView.setPadding(20, 0, 20, 0);
        	imageViews[i] = imageView;
        	// 默认第一张图为显示状态
        	if (i== 0){
        		imageViews[i].setBackgroundResource(R.drawable.guide_view_page_indicator_focused);
        	}else{
        		imageViews[i].setBackgroundResource(R.drawable.guide_view_page_indicator);
        	}
        	
        	dotGroup.addView(imageViews[i]);
        }
        
        // 设置显示布局
        setContentView(mainGroup);
        // 设置引导界面适配器
        viewPager.setAdapter(new GuideViewPagerAdapter());
        // 设置引导界面监听器
        viewPager.setOnPageChangeListener(new GuideViewPageChangeListener());
        // 事件监听器
        btnJump.setOnClickListener(this);
        
    }

    /*
     * 填充引导界面的List
     * 前置条件：需要在guideViewResId[]数组中放置图片资源ID
     * */
    private void inflatePagerList(){
    	viewPagerList = new ArrayList<View>();
    	ImageView imgView;
    	View view;
    	
    	for (int index=0; index<guideViewResId.length; index++){
    		view = inflater.inflate(R.layout.slide_guide_view_item, null);
    		imgView = (ImageView) view.findViewById(R.id.guide_view_image);
    		imgView.setBackgroundResource(guideViewResId[index]);
        	viewPagerList.add(view);
    	}
    }
    
    /*
     * 引导界面适配器
     * */
    class GuideViewPagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return viewPagerList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		
		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}
    	
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager)container).removeView(viewPagerList.get(position));
		}
		
		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager)container).addView(viewPagerList.get(position));
			
			return viewPagerList.get(position);
		}
		
		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
			super.restoreState(state, loader);
		}
    }
    
    // 引导页面更改事件监听器
    class GuideViewPageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int arg0) {
			for (int i=0; i<imageViews.length; i++){
				if (arg0 == i){
					imageViews[i].setBackgroundResource(R.drawable.guide_view_page_indicator_focused);
				}else{
					imageViews[i].setBackgroundResource(R.drawable.guide_view_page_indicator);
				}
			}
			
			// 最后一张图片，显示跳转按钮
			if (arg0 == (guideViewResId.length-1)){
				btnJump.setVisibility(View.VISIBLE);
			}else{
				btnJump.setVisibility(View.INVISIBLE);
			}
		}
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_to_main:
			// 引导界面跳转
			Toast.makeText(this, "即将跳转到主页面", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
		
	}
    
}
