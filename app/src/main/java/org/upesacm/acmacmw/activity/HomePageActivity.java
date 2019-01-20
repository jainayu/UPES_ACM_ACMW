package org.upesacm.acmacmw.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.main.HomePageFragment;
import org.upesacm.acmacmw.fragment.profile.LoginFragment;
import org.upesacm.acmacmw.fragment.hompage.ImageUploadFragment;

public class HomePageActivity extends AppCompatActivity implements
        ImageUploadFragment.UploadResultListener {
    private FrameLayout frameLayout;
    private int selectedOpt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        frameLayout = findViewById(R.id.frame_layout_activity_post);
        Bundle args = getIntent().getExtras();
        if(args == null) {
            args = savedInstanceState;
        }
        selectedOpt = args.getInt(HomePageFragment.INTERACTION_CODE_KEY);
        updateUI();
    }

    private void updateUI() {
        switch (selectedOpt) {
            case HomePageFragment.REQUEST_AUTHENTICATION: {
                setCurrentFragment(LoginFragment.newInstance(),false);
                break;
            }
            case HomePageFragment.UPLOAD_IMAGE: {
                Bundle data = getIntent().getExtras().getBundle(ImageUploadFragment.UPLOAD_DATA_KEY);
                setCurrentFragment(ImageUploadFragment.newInstance(data),false);
                break;
            }
            default: {
                break;
            }
        }
    }

    void setCurrentFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(frameLayout.getId(),fragment);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
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
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
        this.finish();
    }
}
