package vn.opdo.quanlythethanhvien;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import vn.opdo.adapter.CardAdapter;
import vn.opdo.model.Card;
import vn.opdo.model.CardType;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {
    SwipeMenuListView lvCard;
    ImageButton btnNew, btnDelete;
    View myView;
    public CardAdapter adapter;
    EditText edtSearch;

    public ListFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_list, container, false);
        addControls();
        addEvents();
        return myView;

    }

    @Override
    public void onResume() {
        addData();
        super.onResume();
    }

    public void addData() {
        adapter.clear();
        for (Card item: Card.getList()) {
            adapter.add(item);
        }
        adapter.notifyDataSetChanged();

    }

    private void fakeData() {
        CardType ct = new CardType(1, "Nam chiến đo");
        for (int i = 100; i > 1; i--)
        {
            adapter.add(new Card(i, ct,0, "Card số " + Integer.toString(i + 1), i + "", "note"));
        }
    }

    private void addEvents() {
        // search box
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s.toString());
            }
        });

        lvCard.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final Card c = adapter.getItem(position);
                switch (index)
                {
                    case 0:
                        // open
                        Intent i = new Intent(getContext(), CardInfoActivity.class);
                        i.putExtra("card", c);
                        startActivity(i);

                        break;
                    case 1:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.msgDeleteCard);
                        builder.setMessage(R.string.msgDeleteConfirm);
                        builder.setPositiveButton(R.string.msgDeleteOK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                c.deleteFromSqlite();;
                                addData();
                                dialog.cancel();
                            }
                        });
                        builder.setNegativeButton(R.string.msgDeleteNo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        // delete
                        break;
                }
                return false;
            }
        });

        lvCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Card c = adapter.getItem(position);
                c.setChoose(!c.isChoose());
                adapter.notifyDataSetChanged();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                for (int i = 0; i < adapter.getCount(); i++)
                {
                    Card c = adapter.getItem(i);
                    if (c.isChoose())
                    {
                        flag = true;
                        break;
                    }
                }
                if (!flag) return;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.msgDeleteCard);
                builder.setMessage(R.string.msgDeleteConfirm);
                builder.setPositiveButton(R.string.msgDeleteOK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < adapter.getCount(); i++)
                        {
                            Card c = adapter.getItem(i);
                            if (c.isChoose()) c.deleteFromSqlite();
                        }
                        addData();
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton(R.string.msgDeleteNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CardInfoActivity.class);
                startActivityForResult(i, 30);
            }
        });
    }

   // @Override
   // public void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  if (requestCode == 30 && resultCode == 11) addData();
        //super.onActivityResult(requestCode, resultCode, data);
   // }

    private void addControls() {
        // Listview
        lvCard = myView.findViewById(R.id.lvCard);
        btnNew = myView.findViewById(R.id.btnNew);
        btnDelete = myView.findViewById(R.id.btnDelete);
        edtSearch = myView.findViewById(R.id.edtSearch);

        // set adapter
        adapter = new CardAdapter(ListFragment.this.getActivity(), R.layout.item);
        lvCard.setAdapter(adapter);
        // tạo menu
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getActivity().getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(94, 172, 255)));
                openItem.setWidth(250);
                openItem.setIcon(R.drawable.icon_info_white);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(250);
                deleteItem.setIcon(R.drawable.icon_delete_white);
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);
            }
        };
        lvCard.setMenuCreator(creator);
    }


}
