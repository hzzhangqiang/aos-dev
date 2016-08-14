package whu.zq.startanimation;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class MainActivity extends Activity {

	ImageView welcomeImg = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_animation);
        welcomeImg = (ImageView) findViewById(R.id.welcome_img);
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(3000);
        animation.setAnimationListener(new AnimationImp());
        welcomeImg.startAnimation(animation);
        
    }

    private class AnimationImp implements AnimationListener{

		@Override
		public void onAnimationEnd(Animation animation) {
			skip();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			welcomeImg.setBackgroundResource(R.drawable.ic_launcher);
			
		}
    	
		
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void skip(){
    	//startActivity(new Intent(this))
    }
}
