package whu.zq.starsign;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class StarActivity extends Activity {
	
	private int starId = 0;
	private ImageView imgStarL, imgStarR;
	private TextView tv_info, tv_dec;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_star);
		starId = getIntent().getIntExtra("StarIndex", -1);
		imgStarL = (ImageView) findViewById(R.id.star_img_l);
		imgStarR = (ImageView) findViewById(R.id.star_img_r);
		tv_dec = (TextView) findViewById(R.id.tv_star_dec);
		tv_info = (TextView) findViewById(R.id.tv_star_info);
		
		imgStarL.setImageResource(getItemId(starId, 0));
		imgStarR.setImageResource(getItemId(starId, 0));
		tv_dec.setText(this.getString(getItemId(starId, 1)));
		tv_info.setText(this.getString(getItemId(starId, 2)));
		
	}
	
	private int getItemId(int pos, int style){
		int itemId, titleId, decId;
		switch (pos) {		
		case 0:
			itemId = R.drawable.aries;
			titleId = R.string.Aries;
			decId = R.string.Aries_dec;
			break;
		case 1:
			itemId = R.drawable.taurus;
			titleId = R.string.Taurus;
			decId = R.string.Taurus_dec;
			break;
		case 2:
			itemId = R.drawable.gemini;
			titleId = R.string.Gemini;
			decId = R.string.Gemini_dec;
			break;
		case 3:
			itemId = R.drawable.cancer;
			titleId = R.string.Cancer;
			decId = R.string.Cancer_dec;
			break;
		case 4:
			itemId = R.drawable.leo;
			titleId = R.string.Leo;
			decId = R.string.Leo_dec;
			break;
		case 5:
			itemId = R.drawable.virgo;
			titleId = R.string.Virgo;
			decId = R.string.Virgo_dec;
			break;
		case 6:
			itemId = R.drawable.libra;
			titleId = R.string.Libra;
			decId = R.string.Libra_dec;
			break;
		case 7:
			itemId = R.drawable.scorpio;
			titleId = R.string.Scorpio;
			decId = R.string.Scorpio_dec;
			break;
		case 8:
			itemId = R.drawable.sagittarius;
			titleId = R.string.Sagittarius;
			decId = R.string.Sagittarius_dec;
			break;
		case 9:
			itemId = R.drawable.capricorn;
			titleId = R.string.Capricorn;
			decId = R.string.Capricorn_dec;
			break;
		case 10:
			itemId = R.drawable.aquarius;
			titleId = R.string.Aquarius;
			decId = R.string.Aquarius_dec;
			break;
		case 11:
			itemId = R.drawable.pisces;
			titleId = R.string.Pisces;
			decId = R.string.Pisces_dec;
			break;	
		default:
			itemId = R.drawable.libra;
			titleId = R.string.Libra;
			decId = R.string.Libra_dec;
			break;
		}
		
		switch (style) {
		case 0:
			return itemId;
		case 1:
			return titleId;
		case 2:
			return decId;
		default:
			return titleId;
		}	
	}
}
