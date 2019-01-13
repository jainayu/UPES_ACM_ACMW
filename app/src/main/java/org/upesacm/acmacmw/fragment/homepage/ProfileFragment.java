package org.upesacm.acmacmw.fragment.homepage;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    public static final String SELECTED_OPT_KEY = "selected opt key";
    public static final int PROFILE_IMAGE = 1;
    public static final int MY_PROFILE = 2;
    public static final int PRIVILEGED_ACTION_REQUEST = 3;
    private Toolbar toolbar;
    private OnFragmentInteractionListener listener;
    private RecyclerView recyclerView;
    private ImageView imageViewProfile;
    private TextView textViewName;
    private TextView textViewExtra;
    RecyclerViewAdapter recyclerViewAdapter;
    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener)context;
            super.onAttach(context);
        } else {
            throw new IllegalStateException(context.toString()+"must implement OnFragmentInteractionListener");
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        toolbar = view.findViewById(R.id.toolbar_frag_profile);
        recyclerView = view.findViewById(R.id.recycler_view_frag_profile);
        imageViewProfile = view.findViewById(R.id.image_view_frag_profile);
        textViewName = view.findViewById(R.id.text_view__frag_profile_name);
        textViewExtra = view.findViewById(R.id.text_view_frag_profile_extra);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProfileFragmentInteraction(PROFILE_IMAGE);
            }
        });
        recyclerViewAdapter.addMenuItem("My Profile");
        return view;
    }

    void customizeProfilePage() {
        boolean show = false;
        if(SessionManager.getInstance().getSessionID() == SessionManager.MEMBER_SESSION_ID) {
            textViewName.setText(SessionManager.getInstance().getLoggedInMember().getName());
            textViewExtra.setText("Welcome");
            Glide.with(getContext())
                    .load(SessionManager.getInstance().getLoggedInMember().getProfilePicture()).thumbnail(0.9f).into(imageViewProfile);
            show = true;
        } else if(SessionManager.getInstance().getSessionID() == SessionManager.GUEST_SESSION_ID) {
            textViewName.setText(SessionManager.getInstance().getGuestMember().getName());
            textViewExtra.setText(SessionManager.getInstance().getGuestMember().getEmail());
            RequestOptions requestOptions=new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(this)
                    .load(SessionManager.getInstance().getGuestMember().getImageUrl())
                    .apply(requestOptions)
                    .into(imageViewProfile);
            show = true;
        } else {
            show = false;
        }
        textViewExtra.setVisibility(show?View.VISIBLE:View.GONE);
        textViewName.setVisibility(show?View.VISIBLE:View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        customizeProfilePage();
    }

    public interface OnFragmentInteractionListener {
        void onProfileFragmentInteraction(int viewId);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        int position;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view_item_layout_profile_menu);
            textView.setOnClickListener(this);
        }

        void bindData(String data,int position) {
            textView.setText(data);
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (position) {
                case 0: {
                    listener.onProfileFragmentInteraction(MY_PROFILE);
                }
                default: {
                    break;
                }
            }
        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private List<String> menuList = new ArrayList<>();
        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout_profile_menu,viewGroup,false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            itemViewHolder.bindData(menuList.get(i),i);
        }

        @Override
        public int getItemCount() {
            return menuList.size();
        }

        void addMenuItem(String item) {
            if(item!=null) {
                menuList.add(item);
                notifyItemInserted(menuList.size()-1);
            }
        }
    }

}
