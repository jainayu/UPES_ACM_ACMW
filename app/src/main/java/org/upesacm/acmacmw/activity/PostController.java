package org.upesacm.acmacmw.activity;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.homepage.post.ImageUploadFragment;
import org.upesacm.acmacmw.fragment.homepage.post.PostsFragment;
import org.upesacm.acmacmw.fragment.member.profile.LoginDialogFragment;
import org.upesacm.acmacmw.model.Post;


public class PostController implements
        ImageUploadFragment.UploadResultListener,
        PostsFragment.FragmentInteractionListener {
    private static final String TAG = "PostController";
    private static PostController postController;
    private HomeActivity homeActivity;

    private PostController() {
    }

    public static PostController getInstance(@NonNull HomeActivity homeActivity) {
        if(postController == null) {
            postController = new PostController();
            postController.homeActivity = homeActivity;
        }

        return postController;
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
        Toast.makeText(homeActivity,msg,Toast.LENGTH_LONG).show();
        if(homeActivity.isVisible((homeActivity.getString(R.string.fragment_tag_image_upload)))) {
            homeActivity.displayHomePage();
        }
        homeActivity.getSupportActionBar().show();
        homeActivity.setDrawerEnabled(true);
    }

    @Override
    public void onCameraButtonClicked() {

    }

    @Override
    public void onPostLiked(Post post) {
        Log.i(TAG,"Post by "+post.getPostId()+"Liked");
        SessionManager sessionManager = SessionManager.getInstance();
        if(sessionManager.isSessionAlive()) {
            //save the like in database

        } else {
            Log.i(TAG,"unable to like as user session is not in progress");
            LoginDialogFragment loginDialogFragment =new LoginDialogFragment();
            loginDialogFragment.show(homeActivity.getSupportFragmentManager(),
                    homeActivity.getString(R.string.dialog_fragment_tag_login));
            Toast.makeText(homeActivity,"Please log in to like the post",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPostDeleted(Post post) {
        Log.i(TAG,"Post by "+post.getPostId()+" Deleted");
    }
}
