package org.upesacm.acmacmw.retrofit;

import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.model.TrialMember;
import org.upesacm.acmacmw.util.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static org.upesacm.acmacmw.util.Config.AUTH_TOKEN;

public interface HomePageClient {

   @GET("posts/{year}.json")
    Call<HashMap<String,HashMap<String,Post>>> getPosts(@Path("year") String year,@Query("auth") String idToken);

    @GET("posts/{year}/{month}.json")
    Call<HashMap<String,Post>> getPosts(@Path("year") String year, @Path("month") String month,@Query("auth") String idToken);

    @PUT("posts/{year}/{month}/{id}.json")
    Call<Post> createPost(@Path("year")String year, @Path("month")String month,
                          @Path("id") String id,@Body Post post,@Query("auth") String idToken);

    @PUT("postsTrialLogin/{sap}.json")
    Call<TrialMember> createTrialMember(@Path("sap") String sap,@Body TrialMember trialMember,@Query("auth") String idToken);

    @GET("postsTrialLogin/{sap}.json")
    Call<TrialMember> getTrialMember(@Path("sap") String sap,@Query("auth") String idToken);
}
