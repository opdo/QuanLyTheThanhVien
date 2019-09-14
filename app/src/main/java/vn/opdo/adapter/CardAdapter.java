package vn.opdo.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import vn.opdo.model.Card;
import vn.opdo.quanlythethanhvien.R;

public class CardAdapter extends ArrayAdapter<Card> {
    Activity context;
    int resource;

    // List gốc và list để hiển thị
    private ArrayList<Card> lstGoc;
    private ArrayList<Card> lstHienThi;

    public CardAdapter(Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        lstGoc = new ArrayList<>();
        lstHienThi = new ArrayList<>();
    }

    @Override
    public void add(Card object) {
        lstGoc.add(object);
        lstHienThi.add(object);
    }


    @Override
    public void clear() {
        lstGoc.clear();
        lstHienThi.clear();
    }

    @Override
    public Card getItem(int position) {
        return lstHienThi.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return lstHienThi.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = this.context.getLayoutInflater().inflate(this.resource, parent, false);
        TextView txtCardName, txtCardType;
        final CheckBox cbChoose, cbLove;

        // tìm control
        txtCardName = convertView.findViewById(R.id.txtCardName);
        txtCardType = convertView.findViewById(R.id.txtCardType);
        cbChoose = convertView.findViewById(R.id.cbChoose);
        cbLove = convertView.findViewById(R.id.cbLove);

        // Nạp dữ liệu
        final Card c = getItem(position);

        txtCardName.setText(c.getName());
        if (c.getType() != null) txtCardType.setText(c.getType().getTypeName());
        cbChoose.setChecked(c.isChoose());
        cbLove.setChecked(c.isFavorite());
        // tạo event
        cbChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.setChoose(cbChoose.isChecked());
            }
        });
        cbLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.setFavorite(cbLove.isChecked());
                c.saveToSqlite();
            }
        });

        return convertView;
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Card> FilteredArrList = new ArrayList<Card>();

                if (lstGoc == null) {
                    lstGoc = new ArrayList<Card>(lstHienThi); // saves the original data in mOriginalValues
                }


                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = lstGoc.size();
                    results.values = lstGoc;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < lstGoc.size(); i++) {
                        String data = lstGoc.get(i).getName();
                        if (data.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(lstGoc.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                lstHienThi = (ArrayList<Card>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }
        };
        return filter;
    }
}
