package org.upesacm.acmacmw.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.util.SessionManager;
import org.upesacm.acmacmw.listener.OnRecyclerItemSelectListener;
import org.upesacm.acmacmw.model.Post;

import java.util.ArrayList;

public class PostsRecyclerViewAdapter extends RecyclerView.Adapter {

    //private MainActivity callback;
    boolean isLoading=false;
    ArrayList<Post> posts;
    //HomePageClient homePageClient;
    OnRecyclerItemSelectListener<Post> itemSelectListener;
    private Context context;

    public PostsRecyclerViewAdapter(Context context) {
        this.context = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("onCreateViewHolder : "+viewType);
        //NOTE : Apprently when inflating views inside constraint layout it is important to specify the root(constraint layout)
        //      Otherwise the inflator wont inflate it according to the parameters of constraint layoout
        //     Eg ; match_constraint is equivalent to 0dp in constaint layout
        //      But if root is not specified then, the inflator will inflate the child with 0dp such that its dimension(whichever
        //      we have set as match_constraint) is 0dp.
        View viewitem = LayoutInflater.from(parent.getContext()).inflate(viewType, parent,
                false);
        if(viewType== R.layout.item_layout_post)
            return new PostViewHolder(viewitem);
        else
            return new LoadingViewHolder(viewitem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        System.out.println("onBinViewHolder Called");
        System.out.println(posts.get(position));
        if(holder instanceof PostViewHolder) {
            Post post=posts.get(position);
            ((PostViewHolder) holder).bindData(post,position);
        }
    }

    @Override
    public int getItemCount() {
        if(posts==null)
            return 0;
        System.out.println("getItem Cont "+posts.size());
        return posts.size();
    }

    @Override
    public int getItemViewType(final int position) {
        if(posts.get(position)==null && isLoading)
                return R.layout.item_layout_post_loading;
        return R.layout.item_layout_post;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder
            implements
            View.OnClickListener {
        private TextView username;
        private TextView textViewCaption;
        private ImageView imageView;
        private ImageButton imageButtonLike;
        private ImageButton imageButtonDelete;
        private TextView textViewLikeCount;
        private TextView textViewDate;
        private TextView textViewTime;
        private Post post;
        private int position;
        public PostViewHolder(View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.text_view_post_username);
            textViewCaption = itemView.findViewById(R.id.text_view_post_caption);
            imageView=itemView.findViewById(R.id.image_view_post);
            imageButtonLike = itemView.findViewById(R.id.image_button_post_like);
            imageButtonDelete = itemView.findViewById(R.id.image_button_post_delete);
            textViewLikeCount = itemView.findViewById(R.id.text_view_post_likecount);
            textViewDate = itemView.findViewById(R.id.text_view_post_date);
            textViewTime = itemView.findViewById(R.id.text_view_post_time);

            Typeface bold = Typeface.createFromAsset(context.getAssets(),"Fonts/product_sans_bold.ttf");
            username.setTypeface(bold);

            imageButtonLike.setOnClickListener(this);
            imageButtonDelete.setOnClickListener(this);
        }

        //This function has been defined to seperate the code of binding the data with the views
        //Othewise the data binding could be done inside the Adapter's onBindViewHolder function
        @SuppressLint("CheckResult")
        void bindData(final Post post,final int position) {
            System.out.println("bindData called");
            this.post=post;
            this.position = position;

            username.setText(post.getOwnerName());
            textViewCaption.setText(post.getCaption());
            textViewLikeCount.setText(String.valueOf(post.getLikesIds().size()));

            String date=post.getDay()+"/"
                    +(Integer.parseInt(post.getMonthId().substring(1))+1)
                    +"/"+post.getYearId().substring(1);
            textViewDate.setText(date);
            textViewTime.setText(post.getTime());


            RequestOptions requestOptions=new RequestOptions();
            requestOptions.placeholder(R.drawable.post_image_holder)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .error(R.drawable.post_image_error);
            Glide.with(itemView.getContext())
                    .load(post.getImageUrl())
                    .apply(requestOptions)
                    .into(imageView);


            String loggedInUserSap = null;
            if(SessionManager.getInstance(context).getSessionID() == SessionManager.MEMBER_SESSION_ID)
                loggedInUserSap = SessionManager.getInstance(context).getLoggedInMember().getSap();
            else if(SessionManager.getInstance(context).getSessionID() == SessionManager.GUEST_SESSION_ID)
                loggedInUserSap = SessionManager.getInstance(context).getGuestMember().getSap();

            boolean deleteButtonVisible = post.getOwnerSapId().equals(loggedInUserSap);
            if(deleteButtonVisible)
                imageButtonDelete.setVisibility(View.VISIBLE);
            else
                imageButtonDelete.setVisibility(View.GONE);

            if(SessionManager.getInstance(context).isSessionAlive()) {
                int noOfLikes = post.getLikesIds().size();
                int i = 0;
                while (i < noOfLikes) {
                    if (post.getLikesIds().get(i).equals(loggedInUserSap)) {
                        imageButtonLike.setImageResource(R.drawable.ic_thumb_up_blue_24dp);
                        break;
                    }
                    i++;
                }
                // if no current user id is not present in likesIds
                if (i == noOfLikes) {
                    imageButtonLike.setImageResource(R.drawable.like);
                }
            }
        }

        @Override
        public void onClick(View view) {
            System.out.println("Liked button pressed");
            itemSelectListener.onRecyclerItemSelect(view,post,position);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    synchronized public void removePost() { //removes the last element
        int pos=posts.size()-1;
        posts.remove(pos);
        notifyItemRemoved(pos);
    }

    synchronized public void removePost(int position) {
        if(posts!=null) {
            posts.remove(position);
            notifyItemRemoved(position);
        }
    }

    synchronized public void removePost(String postId) {
        if(postId!=null) {
            for(int i=0;i<posts.size();i++) {
                if(postId.equals(posts.get(i))) {
                    removePost(i);
                    break;
                }
            }
        }
    }

    synchronized public void addPost(Post post) {
        posts.add(post);
        notifyItemInserted(posts.size()-1);
    }
    
    synchronized public void addPostv2(Post post) {
        if(post!=null) {
            int i;
            for(i=0;i<posts.size()-1;i++) {
                boolean found = (posts.get(i).getPostId().compareTo(post.getPostId()) < 0 ) &&
                        (posts.get(i+1).getPostId().compareTo(post.getPostId()) > 0 );
                if(found)
                    break;
            }
            if(i==posts.size()-1)
                i +=2;
            else if(i!=0)
                i +=1;

            posts.add(i,post);
            notifyItemInserted(i);
        }
    }

    synchronized public void addPosts(ArrayList<Post> posts) {
        if(posts!=null) {
            int prevLast = this.posts.size() - 1;
            this.posts.addAll(posts);
            notifyItemRangeInserted(prevLast + 1, posts.size());
        }
    }

    synchronized public void setPosts(ArrayList<Post> posts) {
        System.out.println("set Posts called : "+posts.size());
        this.posts=posts;
        notifyDataSetChanged();
    }

    synchronized public void modifyPost(Post post) {
        if(post!=null) {
            for(int i=0;i<posts.size();i++) {
                if(posts.get(i)!=null && posts.get(i).getPostId().equals(post.getPostId())) {
                    posts.set(i,post);
                    notifyItemChanged(i);
                }
            }
        }
    }

    public void setItemSelectListener(OnRecyclerItemSelectListener<Post> listener) {
        this.itemSelectListener = listener;
    }

    public void setLoading(boolean value) {
        isLoading=value;
    }

    public boolean isLoading() {
        return isLoading;
    }
}
