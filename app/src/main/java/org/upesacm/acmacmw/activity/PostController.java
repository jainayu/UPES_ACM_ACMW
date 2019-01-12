package org.upesacm.acmacmw.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.post.ImageUploadFragment;
import org.upesacm.acmacmw.fragment.homepage.PostsFragment;
import org.upesacm.acmacmw.fragment.member.profile.LoginDialogFragment;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.util.SessionManager;


public class PostController implements
        ImageUploadFragment.UploadResultListener,
        PostsFragment.FragmentInteractionListener {
    private static final String TAG = "PostController";
    private static PostController postController;
    private MainActivity mainActivity;
    private PostsFragment postsFragment;

    private PostController() {
    }

    public static PostController getInstance(@NonNull MainActivity mainActivity) {
        if(postController == null) {
            postController = new PostController();
            postController.mainActivity = mainActivity;
        }

        return postController;
    }

    public PostsFragment getPostsFragmentInstance() {
        if(postsFragment == null)
            postsFragment = new PostsFragment();

        return postsFragment;
    }

    @Override
    public void onUpload(ImageUploadFragment imageUploadFragment, int resultCode) {
        String msg=null;
        if(resultCode == ImageUploadFragment.UPLOAD_SUCCESSFUL)
            msg="New Post Uploaded";
        else if(resultCode == ImageUploadFragment.UPLOAD_CANCELLED)
            msg="Upload Cancelled";
        else if(resultCode == ImageUploadFragment.UPLOAD_FAILED)
            msg="Upload Failed. Please Try Again";
        else if(resultCode == ImageUploadFragment.UPLOAD_CANCEL_OPERATION_FAILED)
            msg="Upload cancel failed";
        Toast.makeText(mainActivity,msg,Toast.LENGTH_LONG).show();
        if(mainActivity.isVisible((mainActivity.getString(R.string.fragment_tag_image_upload)))) {
            mainActivity.displayHomePage();
        }
        mainActivity.getSupportActionBar().show();
        mainActivity.setDrawerEnabled(true);
    }

    @Override
    public void onCameraButtonClicked() {

    }

    @Override
    public void onPostLiked(Post post) {
        Log.i(TAG,"Post by "+post.getPostId()+"Liked");
        SessionManager sessionManager = SessionManager.getInstance();
        if(sessionManager.isSessionAlive()) {
            String loggedInUserSap = null;
            if(SessionManager.getInstance().getSessionID() == SessionManager.GUEST_SESSION_ID)
                loggedInUserSap = SessionManager.getInstance().getLoggedInMember().getSap();
            else if(SessionManager.getInstance().getSessionID() == SessionManager.MEMBER_SESSION_ID)
                loggedInUserSap = SessionManager.getInstance().getGuestMember().getSap();

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
            postsFragment.modifyPost(post);

        } else {
            Log.i(TAG,"unable to like as user session is not in progress");
            LoginDialogFragment loginDialogFragment =new LoginDialogFragment();
            loginDialogFragment.show(mainActivity.getSupportFragmentManager(),
                    mainActivity.getString(R.string.dialog_fragment_tag_login));
            Toast.makeText(mainActivity,"Please log in to like the post",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPostDeleted(final Post post) {
        Log.i(TAG,"Post by "+post.getPostId()+" Deleted");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);
        alertDialog.setTitle("Delete this Post");
        alertDialog.setMessage("Are you Sure ? ");
        alertDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String postUrl = "posts/"+post.getYearId()+"/"+post.getMonthId()+"/"+post.getPostId();
                DatabaseReference postReference = FirebaseDatabase.getInstance().getReference(postUrl);
                Post nullPost = new Post();
                postReference.setValue(nullPost);

                postsFragment.removePost(post);
                Toast.makeText(mainActivity,"Deleted Sucessfully by Post Controller",Toast.LENGTH_SHORT).show();
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
