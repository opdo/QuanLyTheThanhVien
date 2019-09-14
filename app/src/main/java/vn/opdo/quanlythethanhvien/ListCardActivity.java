package vn.opdo.quanlythethanhvien;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import vn.opdo.adapter.CardAdapter;
import vn.opdo.model.Card;
import vn.opdo.model.CardType;

public class ListCardActivity extends AppCompatActivity {
    SwipeMenuListView lvCard;
    View myView;
    CardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_card);
        addControls();
        addEvents();
    }

    public void addData() {
        adapter.clear();
        adapter.addAll(Card.getList());
    }

    @Override
    protected void onResume() {
        addData();
        super.onResume();
    }

    private void fakeData() {
        CardType ct = new CardType(1, "Nam chiến đo");
        for (int i = 0; i < 100; i++)
        {
            Card c = new Card(i, ct,0, "Card số " + i, i + "", "note");
            adapter.add(c);
        }
    }

    private void addEvents() {

    }

    private void addControls() {
        // Listview
        lvCard = myView.findViewById(R.id.lvCard);
        // set adapter
        adapter = new CardAdapter(ListCardActivity.this, R.layout.item);
        lvCard.setAdapter(adapter);
        // tạo menu
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(94, 172, 255)));
                // set item width
                openItem.setWidth(200);
                // set item title
                openItem.setIcon(R.drawable.icon_info_white);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(200);
                // set a icon
                deleteItem.setIcon(R.drawable.icon_delete_white);
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        lvCard.setMenuCreator(creator);
    }
}
