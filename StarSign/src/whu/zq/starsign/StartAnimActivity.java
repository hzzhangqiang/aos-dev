package whu.zq.starsign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

public class StartAnimActivity extends Activity {

	private TextView tv_start = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startanim_layout);
		
		tv_start = (TextView) findViewById(R.id.start_text);
		Animation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(1000);
		tv_start.setAnimation(animation);
		animation.setAnimationListener(new OnAnimationListener());
	}

	private class OnAnimationListener implements AnimationListener{

		@Override
		public void onAnimationEnd(Animation animation) {
			startActivity(new Intent(StartAnimActivity.this, MainActivity.class));
			finish();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			
		}
		
	}
	
}
