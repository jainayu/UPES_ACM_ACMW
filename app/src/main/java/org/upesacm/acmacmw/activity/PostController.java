package org.upesacm.acmacmw.activity;

import android.support.annotation.NonNull;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.homepage.post.ImageUploadFragment;


public class PostController implements
        ImageUploadFragment.UploadResultListener {

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
}
