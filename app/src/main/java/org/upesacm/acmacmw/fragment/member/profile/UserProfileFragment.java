package org.upesacm.acmacmw.fragment.member.profile;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.retrofit.ApiClient;
import org.upesacm.acmacmw.retrofit.MembershipClient;
import org.upesacm.acmacmw.retrofit.ResponseModel;
import org.upesacm.acmacmw.util.UploadService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static org.upesacm.acmacmw.activity.HomeActivity.BASE_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment implements
        View.OnClickListener{

    FragmentInteractionListener listener;
    HomeActivity homeActivity;

    ImageView imageViewProfilePic;
    TextView textViewName;
    TextView textViewYear;
    TextView textViewBranch;
    TextView textViewSap;
    TextView textViewContact;
    TextView textViewWhatsapp;
    TextView memberId;
    TextView textViewDob;
    TextView textViewAddress;
    TextView textViewEmail;
    ProgressBar progressBarUserProfile;
    TextView fabEdit;
    TextView fabLogout;

    Member member;

    private static final int CHOOSE_PROFILE_PICTURE = 4;
    public static final int CAMERA_AND_STORAGE_PERMISSION_REQUEST_CODE = 10;
    private Retrofit retrofit;

    public UserProfileFragment() {
        // Required empty public constructor
    }
    public static UserProfileFragment fragment=null;
    public static UserProfileFragment newInstance(Member member) {
        if(member ==  null) {
            throw new IllegalStateException("Member not signed in");
        }
        if(fragment==null){
            fragment=new UserProfileFragment();
        fragment.member = member;
        }
        else
            fragment.member=member;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof HomeActivity) {
            super.onAttach(context);
            homeActivity = (HomeActivity)context;
            listener = homeActivity.getUserController();
        }
        else
            throw new IllegalStateException(context.toString()+" must implement" +
                    "UserProfileFragment.FragmentInteractionListener");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
            member = savedInstanceState.getParcelable(getString(R.string.member_parcel_key));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ImageView editImage=view.findViewById(R.id.edit_image);
        imageViewProfilePic = view.findViewById(R.id.image_view_profile_pic);
        textViewName = view.findViewById(R.id.text_view_profile_name);
        textViewYear = view.findViewById(R.id.text_view_profile_year);
        textViewBranch = view.findViewById(R.id.text_view_profile_branch);
        textViewSap = view.findViewById(R.id.text_view_profile_sap);
        textViewContact = view.findViewById(R.id.text_view_profile_contact);
        textViewWhatsapp = view.findViewById(R.id.text_view_profile_whatsapp);
        textViewDob = view.findViewById(R.id.text_view_profile_dob);
        textViewAddress = view.findViewById(R.id.text_view_profile_address);
        memberId=view.findViewById(R.id.memberId);
        textViewEmail = view.findViewById(R.id.text_view_profile_email);
        progressBarUserProfile=view.findViewById(R.id.progress_bar_user_profile);
        fabEdit = view.findViewById(R.id.fab_profile_edit);
        fabLogout = view.findViewById(R.id.fab_profile_logout);
        progressBarUserProfile.setVisibility(View.GONE);
        if(member.getProfilePicture()!=null)
        {
            Glide.with(getContext()).load(member.getProfilePicture()).into(imageViewProfilePic);
        }
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)) {
                    System.out.println("Permission for camera or storage not granted. Requesting Permission");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                CAMERA_AND_STORAGE_PERMISSION_REQUEST_CODE);
                    }
                    return;
                }
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                progressBarUserProfile.setVisibility(View.VISIBLE);
                getActivity().startActivityForResult(Intent.createChooser(intent, "Select Photo"), CHOOSE_PROFILE_PICTURE);

            }
        });
        textViewName.setText(member.getName());
        textViewYear.setText(member.getYear());
        textViewBranch.setText(member.getBranch());
        textViewSap.setText(member.getSap());
        textViewContact.setText(member.getContact());
        memberId.setText(member.getMemberId());
        textViewWhatsapp.setText(member.getWhatsappNo());
        textViewDob.setText(member.getDob());
        textViewAddress.setText(member.getCurrentAdd());
        textViewEmail.setText(member.getEmail());

        Typeface regular = Typeface.createFromAsset(getContext().getAssets(),"Fonts/product_sans_regular.ttf");
        textViewName.setTypeface(regular);


        fabEdit.setOnClickListener(this);
        fabLogout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(getString(R.string.member_parcel_key),member);
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab_profile_logout) {
            listener.onSignOutClicked(this);
        }
        else if(view.getId() == R.id.fab_profile_edit) {
            listener.onEditClicked(this);
        }
    }

    public interface FragmentInteractionListener {
        void onSignOutClicked(UserProfileFragment fragment);
        void onEditClicked(UserProfileFragment fragment);
    }
    Bitmap imageBitmap;
    private File destination;
    byte[] byteArray;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_PROFILE_PICTURE && resultCode == RESULT_OK && resultCode!=RESULT_CANCELED)
        {
            System.out.println("choose from gallery");
            Uri uri = data.getData();


            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                int nh = (int) ( imageBitmap.getHeight() * (1024.0 / imageBitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(imageBitmap, 1024, nh, true);
                scaled.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
                new AsyncTask<byte[], Void, File>() {
                    @Override
                    protected File doInBackground(byte[]... bytes) {
                        try {
                            destination = new File(Environment.getExternalStorageDirectory(),
                                    System.currentTimeMillis() + ".jpg");
                            destination.createNewFile();
                            FileOutputStream fo = new FileOutputStream(destination);
                            fo.write(byteArray);
                            fo.close();
                            return destination;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(File file) {
                        super.onPostExecute(file);
                        uploadToServer(destination);
                    }
                }.execute(byteArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            progressBarUserProfile.setVisibility(View.GONE);
        }
    }
    private void uploadToServer(File destination) {
        UploadService.ProgressRequestBody fileBody = new UploadService.ProgressRequestBody(destination, new UploadService.ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
            }

            @Override
            public void onError() {
            }

            @Override
            public void onFinish() {

            }
        });

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", destination.getName(), fileBody);
        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), destination.getName());
// Change base URL to your upload server URL.
        final MembershipClient membershipClient = ApiClient.getClient().create(MembershipClient.class);
        membershipClient.uploadFile(name,filePart).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                Log.d("Tag", "code" + response.code() + "");
                if (response.code()==200) {
                    String uri = response.body().getUrl();
                    if (uri != null) {
                        System.out.println("image url is : " + uri);
                        System.out.println("create the post object here");
                        if(member!=null)
                        {

                            final Member newMember=new Member.Builder(member).setProfilePicture(uri).build();

                            retrofit=new Retrofit.Builder()
                                    .baseUrl(BASE_URL)
                                    .addConverterFactory(JacksonConverterFactory.create())
                                    .build();
                            MembershipClient membershipClient1=retrofit.create(MembershipClient.class);
                            membershipClient1.createMember(newMember.getSap(),newMember).enqueue(new Callback<Member>() {
                                @Override
                                public void onResponse(Call<Member> call, Response<Member> response) {
                                    if(response.code()==200)
                                    {
                                       progressBarUserProfile.setVisibility(View.GONE);
                                       if(newMember.getProfilePicture()!=null)
                                       {
                                           Glide.with(getContext()).load(newMember.getProfilePicture()).into(imageViewProfilePic);

                                       }

                                    }
                                }

                                @Override
                                public void onFailure(Call<Member> call, Throwable t) {

                                }
                            });

                        }
                    } else {
                        System.out.println("failed to get the download uri");
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                t.printStackTrace();
                System.out.println("failed to get the download uri");
            }
        });
    }
}
