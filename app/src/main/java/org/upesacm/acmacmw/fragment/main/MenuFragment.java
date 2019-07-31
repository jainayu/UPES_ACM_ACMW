package org.upesacm.acmacmw.fragment.main;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.upesacm.acmacmw.R;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {
    public static final String SELECTED_MENU_ITEM_KEY = "selected menu item key";
    public static final int ACTION_ALUMNI = 1;
    public static final int ACTION_NEW_REGISTRATION = 2;
    public static final int ACTION_ABOUT_US = 3;
    public static final int ACTION_CONTACT_US = 4;
    public static final int ACTION_PRIVACY_POLICY = 5;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private OnFragmentInteractionListener listener;
    public MenuFragment() {
        // Required empty public constructor
    }


    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener)context;
            super.onAttach(context);
        } else {
            throw new IllegalStateException(context.toString()+" must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        toolbar = view.findViewById(R.id.toolbar_frag_menu);
        recyclerView = view.findViewById(R.id.recycler_view_frag_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        toolbar.setTitle("Menu");
        recyclerViewAdapter.addMenuItem("Alumni");
        recyclerViewAdapter.addMenuItem("New Member Registration");
        recyclerViewAdapter.addMenuItem("About Us");
        recyclerViewAdapter.addMenuItem("Contact Us");
        recyclerViewAdapter.addMenuItem("Privacy Policy");
        return view;
    }

    public interface OnFragmentInteractionListener {
        void onMenuItemSelected(int index);
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private List<String> menuItems = new ArrayList<>();
        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout_menu_item,viewGroup,false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            itemViewHolder.bindData(menuItems.get(i),i);
        }

        @Override
        public int getItemCount() {
            return menuItems.size();
        }

        void addMenuItem(String item) {
            if(item!=null) {
                menuItems.add(item);
                notifyItemInserted(menuItems.size()-1);
            }
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView;
        private int position;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view_item_layout_menu_item);
            itemView.setOnClickListener(this);
        }

        void bindData(String menuItem,int position) {
            textView.setText(menuItem);
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            int menuItemId;
            switch (position) {
                case 0 : {
                    menuItemId = ACTION_ALUMNI;
                    break;
                }
                case 1 : {
                    menuItemId = ACTION_NEW_REGISTRATION;
                    break;
                }
                case 2 : {
                    menuItemId = ACTION_ABOUT_US;
                    break;
                }
                case 3 : {
                    menuItemId = ACTION_CONTACT_US;
                    break;
                }
                case 4 : {
                    menuItemId = ACTION_PRIVACY_POLICY;
                    break;
                }
                default : {
                    menuItemId = -1;
                    break;
                }
            }
            listener.onMenuItemSelected(menuItemId);
        }
    }
}
