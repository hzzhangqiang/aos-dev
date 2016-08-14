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
 *  ʵ�����һ���ָ��Ч��
 *  �����ļ���1.GuideViewActivity.java ��Activity�ļ�
 *  2.layout/slide_guide_view_main.xml 	��Activity�Ĳ����ļ�������һ��ViewPager��һ����ť�����������һҳʱ��ʾ�����Լ�������ʾ���������ĵ�
 *  3.layout/slide_guide_view_item.xml 	ViewPager���֣�����һ��ImageView�����ػ���ͼƬ
 *  4.drawable/circle_button.xml		��ת��ť����ʽ�ļ�
 *  5.drawable/guideviewpage0.jpg�ȣ�����ͼƬ
 *  6.drawable/guide_view_page_indicator.jpg��guide_view_page_indicator_focused.jpg ����dot
 *  ʹ��˵����
 *  1.���������ļ���5֮����ļ���
 *  2.����5��Ӧ�Ļ���ͼƬ��������GuideViewActivity�и��ı���ͼƬ��ԴID������guideViewResId
 *  3.���GuideViewActivity��AndroidManifest.xml�����������ޱ�����
 *  4.����GuideViewActivity�У�����btnJump����ת���ܣ���ת������OnClick����
 *  5.�������ͼƬ���������Ļ����Ҫ����slide_guide_view_item.xml������heightΪfill_parent
 * */

public class GuideViewActivity extends Activity implements OnClickListener {

	private Button btnJump;
	private ViewPager viewPager;
	private ArrayList<View> viewPagerList;
	private ImageView imageView;
	private ImageView[] imageViews;
	//��������ͼƬLinearLayout
	private ViewGroup mainGroup;
	//����СԲ���LinearLayout
	private ViewGroup dotGroup;
	private LayoutInflater inflater;
	// ����ͼƬID
	private int guideViewResId[] = new int[]{R.drawable.guideviewpage0,
			R.drawable.guideviewpage1, R.drawable.guideviewpage2,R.drawable.guideviewpage3};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        inflater = getLayoutInflater();
        
        // ��ʼ��PagerList
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
        	// Ĭ�ϵ�һ��ͼΪ��ʾ״̬
        	if (i== 0){
        		imageViews[i].setBackgroundResource(R.drawable.guide_view_page_indicator_focused);
        	}else{
        		imageViews[i].setBackgroundResource(R.drawable.guide_view_page_indicator);
        	}
        	
        	dotGroup.addView(imageViews[i]);
        }
        
        // ������ʾ����
        setContentView(mainGroup);
        // ������������������
        viewPager.setAdapter(new GuideViewPagerAdapter());
        // �����������������
        viewPager.setOnPageChangeListener(new GuideViewPageChangeListener());
        // �¼�������
        btnJump.setOnClickListener(this);
        
    }

    /*
     * ������������List
     * ǰ����������Ҫ��guideViewResId[]�����з���ͼƬ��ԴID
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
     * ��������������
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
    
    // ����ҳ������¼�������
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
			
			// ���һ��ͼƬ����ʾ��ת��ť
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
			// ����������ת
			Toast.makeText(this, "������ת����ҳ��", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
		
	}
    
}
