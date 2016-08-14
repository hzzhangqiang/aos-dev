package whu.zq.starsign;

import android.content.Context;
import android.provider.ContactsContract.Contacts.Data;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListBaseAdapter extends BaseAdapter {
	
	private Context context = null;
	private ListCellData data[];
	
	
	public ListBaseAdapter(Context context){
		this.context = context;
		data = new ListCellData[]{
				new ListCellData(context.getString(R.string.Aries),context.getString(R.string.Aries_dec),R.drawable.aries),
				new ListCellData(context.getString(R.string.Taurus),context.getString(R.string.Taurus_dec),R.drawable.taurus),
				new ListCellData(context.getString(R.string.Gemini),context.getString(R.string.Gemini_dec),R.drawable.gemini),
				new ListCellData(context.getString(R.string.Cancer),context.getString(R.string.Cancer_dec),R.drawable.cancer),
				new ListCellData(context.getString(R.string.Leo),context.getString(R.string.Leo_dec),R.drawable.leo),
				new ListCellData(context.getString(R.string.Virgo),context.getString(R.string.Virgo_dec),R.drawable.virgo),
				new ListCellData(context.getString(R.string.Libra),context.getString(R.string.Libra_dec),R.drawable.libra),
				new ListCellData(context.getString(R.string.Scorpio),context.getString(R.string.Scorpio_dec),R.drawable.scorpio),
				new ListCellData(context.getString(R.string.Sagittarius),context.getString(R.string.Sagittarius_dec),R.drawable.sagittarius),
				new ListCellData(context.getString(R.string.Capricorn),context.getString(R.string.Capricorn_dec),R.drawable.capricorn),
				new ListCellData(context.getString(R.string.Aquarius),context.getString(R.string.Aquarius_dec),R.drawable.aquarius),
				new ListCellData(context.getString(R.string.Pisces),context.getString(R.string.Pisces_dec),R.drawable.pisces)
		};
	}

	
	
	public Context getContext() {
		return context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LinearLayout ll = null;
		if (convertView == null){
			ll = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.activity_listcell, null);
		}else{
			ll = (LinearLayout) convertView;
		}
		
		ListCellData dataCell = getItem(position);
		
		ImageView icon = (ImageView) ll.findViewById(R.id.img_icon);
		TextView title = (TextView) ll.findViewById(R.id.tv_title);
		TextView dec = (TextView) ll.findViewById(R.id.tv_dec);
		
		icon.setImageResource(dataCell.iconId);
		title.setText(dataCell.title);
		dec.setText("\t"+dataCell.dec);
		
		return ll;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public ListCellData getItem(int position) {
		return data[position];
	}
	
	@Override
	public int getCount() {
		return 12;
	}

}
