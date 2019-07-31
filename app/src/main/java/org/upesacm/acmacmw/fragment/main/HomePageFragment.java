package org.upesacm.acmacmw.fragment.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.retrofit.RetrofitFirebaseApiClient;
import org.upesacm.acmacmw.util.SessionManager;
import org.upesacm.acmacmw.adapter.PostsRecyclerViewAdapter;
import org.upesacm.acmacmw.listener.OnLoadMoreListener;
import org.upesacm.acmacmw.listener.OnRecyclerItemSelectListener;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.model.TrialMember;
import org.upesacm.acmacmw.util.Config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomePageFragment extends Fragment
        implements OnLoadMoreListener,
        Callback<HashMap<String,Post>>,
        ValueEventListener,
        View.OnClickListener,
        OnRecyclerItemSelectListener<Post> {
    public static final String TAG = "HomePageFragment";
    public static final String INTERACTION_CODE_KEY = "interaction code key";
    public static final int REQUEST_AUTHENTICATION = 1;
    public static final int UPLOAD_POST = 2;
    private static final int CHOOSE_FROM_GALLERY=2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_AND_STORAGE_PERMISSION_REQUEST_CODE = 3;

    RecyclerView recyclerView;
    ProgressBar progressBar;
    private int monthCount=-1;
    private DatabaseReference postsReference;
    PostsRecyclerViewAdapter recyclerViewAdapter;
    FloatingActionButton floatingActionButton;
    SwipeRefreshLayout swipeContainer;
    RecyclerView.OnScrollListener scrollListener;
    Call<HashMap<String,Post>> loadMoreCall;
    private Uri fileUri;
    FragmentInteractionListener interactionListener;
    boolean viewAlive;
    private Toolbar toolbar;

    public HomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        if(context instanceof FragmentInteractionListener) {
            interactionListener = (FragmentInteractionListener)context;
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context.toString()+" must be instance of FragmentInteractionListener");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate post fragment");
        setHasOptionsMenu(true);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        recyclerViewAdapter=new PostsRecyclerViewAdapter(this.getContext());
        recyclerViewAdapter.setItemSelectListener(this);

        Calendar calendar = Calendar.getInstance();
        postsReference = FirebaseDatabase.getInstance()
                .getReference("posts/" + "Y" + calendar.get(Calendar.YEAR) + "/"
                        + "M" + calendar.get(Calendar.MONTH));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        toolbar = view.findViewById(R.id.toolbar_frag_post);
        toolbar.setTitle("Home");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        recyclerView=view.findViewById(R.id.posts_recyclerView);
        progressBar = view.findViewById(R.id.progress_bar_home);
        floatingActionButton = view.findViewById(R.id.cameraButton);
        swipeContainer = view.findViewById(R.id.fragment_home_swipe_container);
        floatingActionButton.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager=(LinearLayoutManager)recyclerView.getLayoutManager();
                int totalItemCount=linearLayoutManager.getItemCount();
                int visibleItemCount=linearLayoutManager.getChildCount();
                int scrolleditems=linearLayoutManager.findFirstVisibleItemPosition();
                if((scrolleditems+visibleItemCount)==totalItemCount) {
                    System.out.println("isloading : "+recyclerViewAdapter.isLoading());
                    onLoadMore();
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        recyclerView.setAdapter(recyclerViewAdapter);

        /* ***************************Adding ValueEvent Listener******************************/
        if(postsReference!=null) {
            postsReference.addValueEventListener(this);
        }
        /* **********************************************************************************/

        progressBar.setVisibility(View.VISIBLE);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(postsReference!=null)
                    postsReference.addValueEventListener(HomePageFragment.this);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        viewAlive = true;

        return view;
    }

    @Override
    public void onDestroyView() {
        postsReference.removeEventListener(this);

        floatingActionButton.setOnClickListener(null);
        floatingActionButton = null;

        recyclerView.removeOnScrollListener(scrollListener);
        scrollListener = null;
        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(null);
        recyclerView = null;

        //cancelling the request to load more posts
        if(loadMoreCall!=null) {
            loadMoreCall.cancel();
            loadMoreCall = null;
        }

        progressBar = null;

        swipeContainer.setOnRefreshListener(null);
        swipeContainer = null;  //MainActivity callback;

        monthCount = -1;

        super.onDestroyView();

        viewAlive = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public void onDestroy() {
        postsReference = null;
        recyclerViewAdapter = null;
        super.onDestroy();
        System.gc();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cameraButton) {
            if (SessionManager.getInstance(HomePageFragment.this.getContext()).getSessionID() == SessionManager.MEMBER_SESSION_ID) {
                //onCameraButtonClick();
                interactionListener.onPostFragmentInteraction(UPLOAD_POST);
            }
            else if(SessionManager.getInstance(HomePageFragment.this.getContext()).getSessionID() == SessionManager.GUEST_SESSION_ID) {
                TrialMember trialMember = SessionManager.getInstance(HomePageFragment.this.getContext()).getGuestMember();
                long trialPeriod=30*24*60*60*(1000L);
                long elapsedTime = Calendar.getInstance().getTimeInMillis() - Long.parseLong(trialMember.getCreationTimeStamp());
                System.out.println("trialPerion : "+trialPeriod);
                System.out.println("elasped : "+elapsedTime);
                if(elapsedTime > trialPeriod) {
                    Toast.makeText(getContext(),"Your free trial is over",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(), "Subscription left : " + ((trialPeriod - elapsedTime)/(1000*60*60*24))+"Days", Toast.LENGTH_LONG).show();
                    interactionListener.onPostFragmentInteraction(UPLOAD_POST);
                }
            }
            else {
               /* LoginFragment loginDialogFragment =new LoginFragment();
                loginDialogFragment.show(getActivity().getSupportFragmentManager(),getString(R.string.dialog_fragment_tag_login));*/
                interactionListener.onPostFragmentInteraction(REQUEST_AUTHENTICATION);
                Toast.makeText(getContext(), "Please Login First", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            System.out.println("unexpected on click callback");
        }
    }







    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@ Overriden methods of ValueEventListener @@@@@@@@@@@@@@@@@@@@@ */
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        System.out.println("onDataChange method called");
        if(recyclerViewAdapter!=null) {
            ArrayList<Post> posts = new ArrayList<>();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Post p = dataSnapshot.child(ds.getKey()).getValue(Post.class);
                posts.add(0, p);
            }
            if (recyclerViewAdapter.isLoading()) {
                posts.add(null);//if loading the current set of posts with the adapter will also have null value at the end
            } else {
                monthCount = -1;//to begin search from the start again
            }
            recyclerViewAdapter.setPosts(posts);
            progressBar.setVisibility(View.INVISIBLE);
            if (posts.size() < 3) {
                monthCount = -1;
                onLoadMore();
            } else {
                if (swipeContainer != null && swipeContainer.isRefreshing())
                    swipeContainer.setRefreshing(false);
            }
        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        System.out.println("Error is new fetching data");
    }
    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */




    /* ####################### Overriden methods of Callback<HashMap<String,Post>> ############### */
    @Override
    public void onResponse(Call<HashMap<String, Post>> call, Response<HashMap<String, Post>> response) {
        if(recyclerViewAdapter!=null) {
            HashMap<String, Post> hashMap = response.body();
            monthCount--;
            if (hashMap != null && !hashMap.isEmpty()) {
                System.out.println("onResponse hashmap : " + hashMap);
                ArrayList<Post> posts = new ArrayList<>();
                for (String key : hashMap.keySet()) {
                    posts.add(0, hashMap.get(key));
                    System.out.println(hashMap.get(key));
                    Collections.sort(posts);
                }
                if (swipeContainer != null && swipeContainer.isRefreshing()) {
                    swipeContainer.setRefreshing(false);
                } else {
                    recyclerViewAdapter.removePost();//remove the null post
                }
                recyclerViewAdapter.addPosts(posts);
                recyclerViewAdapter.setLoading(false);

                if (swipeContainer != null && swipeContainer.isRefreshing())
                    swipeContainer.setRefreshing(false);
            } else {
                if (recyclerViewAdapter.isLoading()) { //to check if one of the requests failed and has set the isLoading to false
                    System.out.println("hashmap is null");
                    //necesary to remove the null post when no changes are made to dataset
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.MONTH, monthCount);
                    if (c.get(Calendar.YEAR) >= 2018 && c.get(Calendar.MONTH) >= 0) {
                        RetrofitFirebaseApiClient.getInstance().getHomePageClient().getPosts("Y" + c.get(Calendar.YEAR),
                                "M" + c.get(Calendar.MONTH),Config.AUTH_TOKEN)
                                .enqueue(this);
                    } else {
                        System.out.println("removing posts");
                        if (swipeContainer != null && swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        } else {
                            recyclerViewAdapter.removePost();//remove the null post
                        }
                        recyclerViewAdapter.setLoading(false);
                    }
                }
            }
        }
    }
    @Override
    public void onFailure(Call<HashMap<String, Post>> call, Throwable t) {
        System.out.println("failed");
        if(recyclerViewAdapter!=null) {
            t.printStackTrace();
            if (swipeContainer!=null&&swipeContainer.isRefreshing()) {
                swipeContainer.setRefreshing(false);
            } else {
                recyclerViewAdapter.removePost();//remove the null post
            }
            recyclerViewAdapter.setLoading(false);
        }
    }
    /* ######################################################################################### */




    @Override
    synchronized public void onLoadMore() {
        System.out.println("on load more");
        if(!recyclerViewAdapter.isLoading()) {
            if(swipeContainer!=null && !swipeContainer.isRefreshing()) {
                recyclerViewAdapter.setLoading(true);//keep this above the addPost
                recyclerViewAdapter.addPost(null);//place holder for the progress bar
            }


            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, monthCount);
            loadMoreCall = RetrofitFirebaseApiClient.getInstance().getHomePageClient().getPosts("Y" + c.get(Calendar.YEAR), "M" + c.get(Calendar.MONTH),Config.AUTH_TOKEN);
            loadMoreCall.enqueue(this);
        }
        else {
            System.out.println("still loading");
        }
    }

    @Override
    public void onRecyclerItemSelect(View view,final Post post, int position) {
        if(view.getId() == R.id.image_button_post_like) {
            Log.i(TAG,"Post by "+post.getPostId()+"Liked");
            SessionManager sessionManager = SessionManager.getInstance(HomePageFragment.this.getContext());
            if(sessionManager.isSessionAlive()) {
                String loggedInUserSap = null;
                if(SessionManager.getInstance(HomePageFragment.this.getContext()).getSessionID() == SessionManager.MEMBER_SESSION_ID)
                    loggedInUserSap = SessionManager.getInstance(HomePageFragment.this.getContext()).getLoggedInMember().getSap();
                else if(SessionManager.getInstance(HomePageFragment.this.getContext()).getSessionID() == SessionManager.GUEST_SESSION_ID)
                    loggedInUserSap = SessionManager.getInstance(HomePageFragment.this.getContext()).getGuestMember().getSap();

                int noOfLikes = post.getLikesIds().size();
                int i = 0;
                while(i<noOfLikes) {
                    if(post.getLikesIds().get(i).equals(loggedInUserSap)) {
                        post.getLikesIds().remove(i);
                        break;
                    }
                    i++;
                }
                // if no current user id is not present in likesIds then add
                if(i==noOfLikes) {
                    System.out.println("liked");
                    post.getLikesIds().add(loggedInUserSap);
                }
                //save the like in database
                String postUrl = "posts/"+post.getYearId()+"/"+post.getMonthId()+"/"+post.getPostId();
                DatabaseReference postReference = FirebaseDatabase.getInstance().getReference(postUrl);
                postReference.setValue(post);

                //update the UI
                recyclerViewAdapter.modifyPost(post);

            } else {
                Log.i(TAG,"unable to like as user session is not in progress");
                interactionListener.onPostFragmentInteraction(REQUEST_AUTHENTICATION);
                Toast.makeText(this.getContext(),"Please log in to like the post",Toast.LENGTH_LONG).show();
            }
        }
        else if(view.getId() == R.id.image_button_post_delete) {
            Log.i(TAG,"Post by "+post.getPostId()+" Deleted");

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getContext());
            alertDialog.setTitle("Delete this Post");
            alertDialog.setMessage("Are you Sure ? ");
            alertDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String postUrl = "posts/"+post.getYearId()+"/"+post.getMonthId()+"/"+post.getPostId();
                    DatabaseReference postReference = FirebaseDatabase.getInstance().getReference(postUrl);
                    Post nullPost = new Post();
                    postReference.setValue(nullPost);

                    recyclerViewAdapter.removePost(post.getPostId());
                    Toast.makeText(HomePageFragment.this.getContext(),"Deleted Sucessfully by Post Controller",Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRecyclerAddToCartClick(Post event) {

    }

    public void addPost(Post post) {
        recyclerViewAdapter.addPostv2(post);
    }

    public void removePost(Post post) {
        recyclerViewAdapter.removePost(post.getPostId());
    }

    public void modifyPost(Post newPost) {
        recyclerViewAdapter.modifyPost(newPost);
    }

    public boolean isViewAlive() {
        return viewAlive;
    }

    public interface FragmentInteractionListener {
        void onPostFragmentInteraction(int code);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.post_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
