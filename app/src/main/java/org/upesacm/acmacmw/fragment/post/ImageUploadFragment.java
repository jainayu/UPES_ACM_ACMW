package org.upesacm.acmacmw.fragment.post;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.MainActivity;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.retrofit.RetrofitHostingerApiClient;
import org.upesacm.acmacmw.retrofit.HomePageClient;
import org.upesacm.acmacmw.retrofit.MembershipClient;
import org.upesacm.acmacmw.retrofit.ResponseModel;
import org.upesacm.acmacmw.util.UploadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageUploadFragment extends Fragment implements
        View.OnClickListener,
        Callback<Post> {

    public static final int UPLOAD_SUCCESSFUL = 1;
    public static final int UPLOAD_CANCELLED = 2;
    public static final int UPLOAD_CANCEL_OPERATION_FAILED = 4;
    public static final int UPLOAD_FAILED = 3;

    HomePageClient homePageClient;
    byte[] byteArray;
    Post post;

    EditText caption;
    FloatingActionButton upload;
    ProgressBar progressBar;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    UploadResultListener resultListener;
    TextView textViewUpload;
    String ownerSapId;
    String ownerName;
    String yearId;
    String monthId;
    String postId;
    String day;
    String time;
    UploadTask uploadTask;
    private File destination;

    private MainActivity callback;

    public ImageUploadFragment() {
        // Required empty public constructor
    }

    public static ImageUploadFragment newInstance(HomePageClient homePageClient) {
        ImageUploadFragment fragment = new ImageUploadFragment();
        fragment.homePageClient = homePageClient;

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof MainActivity) {
            callback = (MainActivity)context;
            resultListener = callback.getPostController();
            super.onAttach(context);
        } else {
            throw new IllegalStateException(context.toString() + " must " +
                    "implement UploadResultListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_upload, container, false);

        Bundle bundle = getArguments();
        byteArray = bundle.getByteArray("image_data");
        ownerSapId = bundle.getString(getString(R.string.post_owner_id_key), null);
        ownerName = bundle.getString(getString(R.string.post_owner_name_key), null);
        System.out.println("image data : " + byteArray);

        System.out.println("image upload frag  : name : " + ownerName);

        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageBitmap(bmp);
        caption = view.findViewById(R.id.editText_caption);
        upload = view.findViewById(R.id.fab_upload);
        progressBar = view.findViewById(R.id.progress_bar_upload);
        textViewUpload = view.findViewById(R.id.text_view_upload);

        upload.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        System.out.println("on destroy view image upload frag");
        System.out.println("upload task : " + uploadTask);
        if (uploadTask != null && !uploadTask.isComplete())
            if (!uploadTask.cancel())
                resultListener.onUpload(this, UPLOAD_CANCEL_OPERATION_FAILED);
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        System.out.println("upload button clicked");
        showProgress(true);
        Calendar calendar = Calendar.getInstance();
        yearId = "Y" + calendar.get(Calendar.YEAR);
        monthId = "M" + calendar.get(Calendar.MONTH);
        postId = "ACM" + Calendar.getInstance().getTimeInMillis() + ownerSapId.substring(3, ownerSapId.length());
        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        hour = (hour.length() == 1) ? ("0" + hour) : hour;
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        minute = (minute.length() == 1) ? ("0" + minute) : minute;
        time = hour + ":" + minute;

        System.out.println("postId : " + postId);

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
    }

    private void uploadToServer(File destination) {
        UploadService.ProgressRequestBody fileBody = new UploadService.ProgressRequestBody(destination, new UploadService.ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
                Log.d("updating", "" + percentage);
                progressBar.setIndeterminate(false);
                textViewUpload.setText("Uploading... " + percentage + "% complete");
                progressBar.setProgress(percentage);
            }

            @Override
            public void onError() {

                //retryPopUp();
            }

            @Override
            public void onFinish() {

                Log.d("updateFlavourCall", "Finish");
            }
        });

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", destination.getName(), fileBody);
        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), destination.getName());
// Change base URL to your upload server URL.
        MembershipClient membershipClient = RetrofitHostingerApiClient.getInstance().create(MembershipClient.class);
        membershipClient.uploadFile(name,filePart).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                Log.d("Tag", "code" + response.code() + "");
                if (response.code()==200) {
                    String uri = response.body().getUrl();
                    if (uri != null) {
                        System.out.println("image url is : " + uri);
                        System.out.println("create the post object here");
                        post = new Post.Builder()
                                .setYearId(yearId)
                                .setMonthId(monthId)
                                .setPostId(postId)
                                .setImageUrl(uri.toString())
                                .setCaption(caption.getText().toString())
                                .setDay(day)
                                .setTime(time)
                                .setOwnerSapId(ownerSapId)
                                .setOwnerName(ownerName)
                                .build();
                        Call<Post> newPostCall = homePageClient.createPost(post.getYearId(),
                                post.getMonthId(),
                                post.getPostId(),
                                post);

                        newPostCall.enqueue(ImageUploadFragment.this);
                        System.out.println("initiated the post meta data upload process");
                    } else {
                        System.out.println("failed to get the download uri");
                        resultListener.onUpload(ImageUploadFragment.this, ImageUploadFragment.UPLOAD_FAILED);
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                t.printStackTrace();
                System.out.println("failed to get the download uri");
                resultListener.onUpload(ImageUploadFragment.this, ImageUploadFragment.UPLOAD_FAILED);
            }
        });
    }


    public void showProgress(boolean show) {
        if (show) {
            caption.setVisibility(View.INVISIBLE);
            upload.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            textViewUpload.setVisibility(View.VISIBLE);
        } else {
            caption.setVisibility(View.VISIBLE);
            upload.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            textViewUpload.setVisibility((View.INVISIBLE));
        }
    }


    @Override
    public void onResponse(Call<Post> call, Response<Post> response) {
        System.out.println("post save response : " + response.message());
        System.out.println("Post Metadata saved successfully");
        showProgress(false);
        resultListener.onUpload(this, ImageUploadFragment.UPLOAD_SUCCESSFUL);
    }

    @Override
    public void onFailure(Call<Post> call, Throwable t) {
        t.printStackTrace();
        System.out.println("posting of meta data failed");
        resultListener.onUpload(this, ImageUploadFragment.UPLOAD_FAILED);
    }

    public interface UploadResultListener {
        void onUpload(ImageUploadFragment imageUploadFragment, int resultCode);
    }

}
