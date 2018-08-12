package org.upesacm.acmacmw.retrofit;



import okhttp3.MultipartBody;

public class UploadImage {
    String name;
    MultipartBody.Part image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MultipartBody.Part getImage() {
        return image;
    }

    public void setImage(MultipartBody.Part image) {
        this.image = image;
    }
}
