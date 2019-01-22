package org.upesacm.acmacmw.model;


import android.support.annotation.NonNull;

import java.util.ArrayList;

public class Post implements Comparable<Post>  {

    private String imageUrl;
    private String caption;
    private String ownerSapId;
    private String yearId;
    private String monthId;
    private String day;
    private String time;
    private ArrayList<String> likesIds;
    private String postId;
    private String ownerName;
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }



    public String getPostId() {
        return postId;
    }

    public String getYearId() {
        return yearId;
    }

    public String getMonthId() {
        return monthId;
    }

    public ArrayList<String> getLikesIds() {
        if(likesIds==null)
            likesIds=new ArrayList<>();
        return likesIds;
    }

    public String getCaption() {
        return caption;
    }

    public String getOwnerSapId() {
        return ownerSapId;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    public Post() {}

    @Override
    public int compareTo(@NonNull Post post) {
        return post.getPostId().compareTo(this.postId);
    }


    public static class Builder {
        private String imageUrl;
        private String caption;
        private String ownerSapId;
        private String yearId;
        private String monthId;
        private String day;
        private String time;
        private ArrayList<String> likesIds;
        private String postId;
        private String ownerName;

        public Post build() {
            Post post=new Post();
            post.yearId=yearId;
            post.monthId=monthId;
            post.imageUrl=imageUrl;
            post.caption=caption;
            post.ownerSapId=ownerSapId;
            post.likesIds=likesIds==null?new ArrayList():likesIds;
            post.postId=postId;
            post.day=day;
            post.time=time;
            post.ownerName=ownerName;
            return post;
        }

        public Builder setImageUrl(String imageUrl) {
            this.imageUrl=imageUrl;
            return this;
        }

        public Builder setCaption(String caption) {
            this.caption=caption;
            return this;
        }

        public Builder setOwnerSapId(String ownerSapId) {
            this.ownerSapId=ownerSapId;
            return this;
        }

        public Builder setYearId(String yearId) {
            this.yearId=yearId;
            return this;
        }

        public Builder setMonthId(String monthId) {
            this.monthId=monthId;
            return this;
        }

        public Builder setLikesIds(ArrayList<String> likesIds) {
            this.likesIds=likesIds;
            return this;
        }

        public Builder setPostId(String postId) {
            this.postId=postId;
            return this;
        }

        public Builder setDay(String day) {
            this.day=day;
            return this;
        }

        public Builder setTime(String time) {
            this.time=time;
            return this;
        }
        
        public Builder setOwnerName(String ownerName) {
            this.ownerName=ownerName;
            return this;
        }
    }

    public boolean syncOwnerData(Member owner) {
        if(owner.getSap().equals(this.getOwnerSapId())) {
            this.ownerName = owner.getName();
            return true;
        }
        return false;
    }

    public boolean syncOwnerData(TrialMember owner) {
        if(owner.getSap().equals(this.getOwnerSapId())) {
            this.ownerName = owner.getName();
        }
        return false;
    }
}
