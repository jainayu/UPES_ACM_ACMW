package org.upesacm.acmacmw.adapter.post;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.fragment.member.profile.LoginDialogFragment;
import org.upesacm.acmacmw.listener.OnRecyclerItemSelectListener;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.model.TrialMember;
import org.upesacm.acmacmw.retrofit.HomePageClient;

import java.util.ArrayList;

public class PostsRecyclerViewAdapter extends RecyclerView.Adapter {

    private HomeActivity callback;
    boolean isLoading=false;
    ArrayList<Post> posts;
    HomePageClient homePageClient;
    Member signedInMember;
    TrialMember trialMember;
    OnRecyclerItemSelectListener<Post> itemSelectListener;

    public PostsRecyclerViewAdapter(HomeActivity callback) {
        this.callback = callback;
        this.homePageClient = callback.getHomePageClient();
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
        if(viewType== R.layout.post_layout)
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
                return R.layout.loading_post_layout;
        return R.layout.post_layout;
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
        private DatabaseReference ownerReference;
        private DatabaseReference postReference;
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

            Typeface bold = Typeface.createFromAsset(callback.getAssets(),"Fonts/product_sans_bold.ttf");
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
            String postUrl = "posts/"+post.getYearId()+"/"+post.getMonthId()+"/"+post.getPostId();
            postReference = FirebaseDatabase.getInstance().getReference(postUrl);
            if(signedInMember != null || trialMember!=null) {
                String signedInUserSap;
                if(signedInMember!=null) {
                    post.syncOwnerData(signedInMember);
                    signedInUserSap = signedInMember.getSap();
                }
                else  {
                    post.syncOwnerData(trialMember);
                    signedInUserSap = trialMember.getSap();
                }

                boolean previouslyLiked = false;
                for (String ownerSapId : post.getLikesIds()) {
                    if (ownerSapId.equals(signedInUserSap)) {
                        previouslyLiked = true;
                        break;
                    }
                }
                if (previouslyLiked) {
                    imageButtonLike.setImageResource(R.drawable.ic_thumb_up_blue_24dp);
                } else {
                    imageButtonLike.setImageResource(R.drawable.like);
                }
            }
            postReference.setValue(post);
            username.setText(post.getOwnerName());
            textViewCaption.setText(post.getCaption());

            RequestOptions requestOptions=new RequestOptions();
            requestOptions.placeholder(R.drawable.post_image_holder)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .error(R.drawable.post_image_error);
            Glide.with(itemView.getContext())
                    .load(post.getImageUrl())
                    .apply(requestOptions)
                    .into(imageView);

            textViewLikeCount.setText(String.valueOf(post.getLikesIds().size()));

            /* ************************** Setting up the date and time *********************************** */
            String date=post.getDay()+"/"
                    +(Integer.parseInt(post.getMonthId().substring(1))+1)
                    +"/"+post.getYearId().substring(1);
            textViewDate.setText(date);
            textViewTime.setText(post.getTime());
            /* **************************************************************************************/

            imageButtonLike.setOnClickListener(this);

            boolean deleteButtonVisible = (signedInMember!=null && post.getOwnerSapId().equals(signedInMember.getSap()))
                    || (trialMember!=null && post.getOwnerSapId().equals(trialMember.getSap()));
            if(deleteButtonVisible)
                imageButtonDelete.setVisibility(View.VISIBLE);
            else
                imageButtonDelete.setVisibility(View.GONE);
            imageButtonDelete.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            itemSelectListener.onRecyclerItemSelect(view,post,position);

            System.out.println("Liked button pressed");
            if (view.getId() == R.id.image_button_post_like) {
                if(signedInMember != null || trialMember!=null) {
                    System.out.println("like button signedInMember is not null");
                    boolean previouslyLiked = false;
                    int pos = 0;

                    String signedInUserSap = (signedInMember==null)?trialMember.getSap():signedInMember.getSap();
                    for (String ownerSapId : post.getLikesIds()) {
                        System.out.println("member id : " + ownerSapId);
                        if (ownerSapId.equals(signedInUserSap)) {
                            previouslyLiked = true;

                            break;
                        }
                        pos++;
                        imageButtonLike.setImageResource(R.drawable.ic_thumb_up_blue_24dp);
                    }
                    if (previouslyLiked){
                        post.getLikesIds().remove(pos);
                        imageButtonLike.setImageResource(R.drawable.like);}
                    else
                        post.getLikesIds().add(signedInUserSap);
                    textViewLikeCount.setText(String.valueOf(post.getLikesIds().size()));
                    postReference.setValue(post);
                }
                else {
//                    AppCompatActivity activity = (AppCompatActivity)recyclerView.getContext();
                  /*  LoginDialogFragment loginDialogFragment =new LoginDialogFragment();
                    loginDialogFragment.show(callback.getSupportFragmentManager(),
                            callback.getString(R.string.dialog_fragment_tag_login));
                    Toast.makeText(callback,"Please log in to like the post",Toast.LENGTH_LONG).show();
                    System.out.println("like button User not signed in"); */
                }

            }
            else if(view.getId() == R.id.image_button_post_delete) {
                System.out.println("deleting post");
                if (itemView != null) {
                    final Context context = itemView.getContext();
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Delete this Post");
                    alertDialog.setMessage("Are you Sure ? ");
                    alertDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Post nullPost = new Post();
                            postReference.setValue(nullPost);
                            PostsRecyclerViewAdapter.this.removePost(position);
                            Toast.makeText(context,"Deleted Sucessfully",Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // DO SOMETHING HERE

                        }
                    });

                    AlertDialog dialog = alertDialog.create();
                    dialog.show();

                }
            }
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    synchronized public void removePost() {
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

    synchronized public void addPost(Post post) {
        posts.add(post);
        notifyItemInserted(posts.size()-1);
    }

    synchronized public void setPosts(ArrayList<Post> posts) {
        System.out.println("set Posts called : "+posts.size());
        this.posts=posts;
        notifyDataSetChanged();
    }

    public void setSignedInMember(Member signedInMember) {
        this.signedInMember = signedInMember;
        notifyDataSetChanged();
    }

    public void setTrialMember(TrialMember trialMember) {
        this.trialMember = trialMember;
        notifyDataSetChanged();
    }

    public void addPosts(ArrayList<Post> posts) {
        if(posts!=null) {
            int prevLast = this.posts.size() - 1;
            this.posts.addAll(posts);
            notifyItemRangeInserted(prevLast + 1, posts.size());
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
