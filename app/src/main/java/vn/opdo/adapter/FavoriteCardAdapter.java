package vn.opdo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import vn.opdo.model.Card;
import vn.opdo.model.OnItemClickListener;
import vn.opdo.quanlythethanhvien.R;

public class FavoriteCardAdapter extends RecyclerView.Adapter<FavoriteCardAdapter.ViewHolder> {
    Activity context;
    int resource;
    ArrayList<Card> listItem;
    private OnItemClickListener listener;

    public FavoriteCardAdapter(ArrayList<Card> list) {
        this.listItem = list;
    }

    public FavoriteCardAdapter() {
        listItem = new ArrayList<>();
    }

    public void add(Card c)
    {
        listItem.add(c);
    }

    public void addAll(ArrayList<Card> lst)
    {
        listItem.addAll(lst);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View custom  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemcard, viewGroup, false);
        return new ViewHolder(custom);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(listItem.get(i), listener, i);
        
        //Card c = listItem.get(i);

        //viewHolder.txtCardName.setText(c.getName());
        //viewHolder.imgCard.setImageResource(c.getImage());
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public OnItemClickListener getListener() {
        return listener;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void clear() {
        listItem.clear();
    }

    public Card getItem(int i) {
        return  listItem.get(i);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCard;
        TextView txtCardName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCard = itemView.findViewById(R.id.imgCard);
            txtCardName = itemView.findViewById(R.id.txtCardName);
        }

        public void bind(final Card card, final OnItemClickListener listener, final int i) {
            this.txtCardName.setText(card.getName());
            this.imgCard.setImageResource(card.getImage());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(card, false, i);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemClick(card, true, i);
                    return false;
                }
            });
        }
    }


    /**
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = this.context.getLayoutInflater().inflate(this.resource, parent, false);

        ImageView imgCard = convertView.findViewById(R.id.imgCard);
        TextView txtCardName = convertView.findViewById(R.id.txtCardName);

        Card c = getItem(position);

        txtCardName.setText(c.getName());
        imgCard.setImageResource(c.getImage());

        return convertView;
    } **/
}