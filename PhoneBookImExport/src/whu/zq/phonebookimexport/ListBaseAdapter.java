package whu.zq.phonebookimexport;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListBaseAdapter extends BaseAdapter {

	private Context context = null;
	private ArrayList<ListCellData> listDatas = null;
	
	public ListBaseAdapter(Context context) {
		this.context = context;
		this.listDatas = new ArrayList<ListCellData>();
	}
	
	public Context getContext() {
		return context;
	}
	
	@Override
	public int getCount() {
		return listDatas!=null?listDatas.size():0;
	}

	@Override
	public ListCellData getItem(int position) {
		return position<getCount()?listDatas.get(position):null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout ll;
		if( convertView == null){
			ll = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.activity_list_img_text, null);
		}else{
			ll = (LinearLayout) convertView;
		}
		
		ListCellData lData = getItem(position);
		
		ImageView imgView = (ImageView) ll.findViewById(R.id.list_img);
		TextView textView = (TextView) ll.findViewById(R.id.list_text);
		
		imgView.setImageResource(lData.getTypeId());
		textView.setTextColor(Color.parseColor(lData.getColorStr()));
		textView.setText(lData.getFile().getName());		

		return ll;
	}

	public void addListCell(ListCellData data){
		listDatas.add(data);
	}
	
	public void clearListCell(){
		listDatas.clear();
	}
}
