package org.upesacm.acmacmw.util;

import android.content.Context;
import android.content.SharedPreferences;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.Participant;
import org.upesacm.acmacmw.model.TrialMember;


public class SessionManager  {

    /* ************************** static fields of SessionManager ********************************************* */
    private static final String SESSION_DATA_PREFERENCE_FILE_KEY = "org.upesacmacmw.session_preference_file_key" ;
    public static final int NONE = -1;
    public static final int MEMBER_SESSION_ID = 0;
    public static final int GUEST_SESSION_ID = 1;
    private static final String MEMBER_SAP_KEY = "member sap preferences key" ;
    private static final String GUEST_MEMBER_SAP_KEY = "guest member sap preference key";
    private static final String SESSION_ID_STORE_KEY = "session id preference key";
    private static final String MEMBER_ID = "member id key";
    private static final String NAME_KEY = "member name key";
    private static final String PASSWORD_KEY = "password key";
    private static final String BRANCH_KEY = "member branch key";
    private static final String YEAR_KEY = "member year key";
    private static final String EMAIL_KEY = "member email key";
    private static final String CONTACT_KEY = "member contact key";
    private static final String WHATSAPP_NO_KEY = "member whatsapp key";
    private static final String DOB_KEY = "member dob key";
    private static final String ADDRESS_KEY = "member address key";
    private static final String RECIPIENT_SAP_KEY = "recipient sap key";
    private static final String PREMIUM_KEY = "member premium key";
    private static final String MEMBERSHIP_TYPE_KEY = "membership type key";
    private static final String PROFILE_PICTURE_KEY = "member profile pic key";
    private static final String GUEST_CREATING_TIME_STAMP_KEY = "guest creating time stamp key";
    private static final String GUEST_OTP_KEY = "guest otp key";
    private static final String GUEST_VERIFIED_KEY = "guest verified";

    private static SessionManager sessionManager;
    /* ********************************************************************************************************* */


    private SharedPreferences preferences;
    private int sessionID;

    private Member loggedInMember;
    private TrialMember guestMember;

    private SessionManager() {
        sessionID = SessionManager.NONE;
    }

    public static void init(Context context) {
        if(sessionManager == null)
            sessionManager = new SessionManager();
        else
            return;

        sessionManager.preferences = context.getSharedPreferences(SESSION_DATA_PREFERENCE_FILE_KEY,
                Context.MODE_PRIVATE);

        sessionManager.sessionID = sessionManager.preferences.getInt(SESSION_ID_STORE_KEY,NONE);
        switch (sessionManager.sessionID) {
            case MEMBER_SESSION_ID : {
                sessionManager.loggedInMember = sessionManager.retrieveMember();
                break;
            }
            case GUEST_SESSION_ID : {
                sessionManager.guestMember = sessionManager.retrieveGuestMember();
            }
            default : {
                System.out.println("no session has been in progress");
            }
        }

        if(sessionManager.loggedInMember != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseConfig.ACM_ACMW_MEMBERS)
                    .child(sessionManager.loggedInMember.getSap())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(sessionManager.sessionID == MEMBER_SESSION_ID) {
                                sessionManager.loggedInMember = dataSnapshot.getValue(Member.class);
                                System.out.println("session manager : data fetched");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            System.out.println("session manager : cancelled");
                        }
                    });
        }
    }

    public static SessionManager getInstance() {
        if(sessionManager==null)
            throw new SessionManagerNotInitializedException("Session Manager must be initialized before use." +
                    "Use SessionManager.init(Context)");
        return sessionManager;
    }


    //this function will be called each time any operation is requested from the session manager
    private boolean isSharedPreferencesSet() {
        if(preferences != null)
            return true;
        return false;
    }

    /* ************* Methods to interact with SessionManager after it is created *****************/

    /** This method checks whether any session is alive
     * @return true or false
     * */
    public boolean isSessionAlive() {
        if(!isSharedPreferencesSet())
            throw new SessionManagerNotInitializedException("SharedPreference must be set for the SessionManger" +
                    "to function");

        return sessionID!=SessionManager.NONE;
    }

    /** This method returns the sessionID for the session which is alive
     * @return sessionID or SessionManager.NONE
     * @See SessionManager.MEMBER_SESSION_ID and SessionManager.GUEST_MEMEBER_SESSION_ID
     */
    public int getSessionID() {
        if(!isSharedPreferencesSet())
            throw new SessionManagerNotInitializedException("SharedPreference must be set for the SessionManger" +
                    "to function");

        return sessionID;
    }

    private void saveMember(@NonNull Member member) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MEMBER_ID,member.getMemberId());
        editor.putString(NAME_KEY,member.getName());
        editor.putString(PASSWORD_KEY,member.getPassword());
        editor.putString(MEMBER_SAP_KEY,member.getSap());
        editor.putString(BRANCH_KEY,member.getBranch());
        editor.putString(YEAR_KEY,member.getBranch());
        editor.putString(EMAIL_KEY,member.getEmail());
        editor.putString(CONTACT_KEY,member.getContact());
        editor.putString(WHATSAPP_NO_KEY,member.getWhatsappNo());
        editor.putString(DOB_KEY,member.getDob());
        editor.putString(ADDRESS_KEY,member.getCurrentAdd());
        editor.putString(RECIPIENT_SAP_KEY,member.getRecepientSap());
        editor.putBoolean(PREMIUM_KEY,member.isPremium());
        editor.putString(MEMBERSHIP_TYPE_KEY,member.getMembershipType());
        editor.putString(PROFILE_PICTURE_KEY,member.getProfilePicture());
        editor.commit();
    }

    private Member retrieveMember() {

        Member member = new Member.Builder()
                .setSAPId(preferences.getString(MEMBER_SAP_KEY,null))
                .setmemberId(preferences.getString(MEMBER_ID,null))
                .setName(preferences.getString(NAME_KEY,null))
                .setPassword(preferences.getString(PASSWORD_KEY,null))
                .setBranch(preferences.getString(BRANCH_KEY,null))
                .setYear(preferences.getString(YEAR_KEY,null))
                .setEmail(preferences.getString(EMAIL_KEY,null))
                .setContact(preferences.getString(CONTACT_KEY,null))
                .setWhatsappNo(preferences.getString(WHATSAPP_NO_KEY,null))
                .setDob(preferences.getString(DOB_KEY,null))
                .setCurrentAdd(preferences.getString(ADDRESS_KEY,null))
                .setRecipientSap(preferences.getString(RECIPIENT_SAP_KEY,null))
                .setPremium(preferences.getBoolean(PREMIUM_KEY,false))
                .setMembershipType(preferences.getString(MEMBERSHIP_TYPE_KEY,null))
                .setProfilePicture(preferences.getString(PROFILE_PICTURE_KEY,null))
                .build();
        return member;
    }

    public boolean createMemberSession(@NonNull Member member) {
        if(!isSharedPreferencesSet())
            throw new SessionManagerNotInitializedException("SharedPreference must be set for the SessionManger" +
                    "to function");

        if(isSessionAlive())
            return false;

        //create the memeber session
        saveMember(member);
        this.loggedInMember = member;
        sessionID = SessionManager.MEMBER_SESSION_ID;

        return true;
    }

    private void saveGuestMember(TrialMember guestMember) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MEMBER_SAP_KEY,guestMember.getSap());
        editor.putString(EMAIL_KEY,guestMember.getEmail());
        editor.putString(NAME_KEY,guestMember.getEmail());
        editor.putString(PROFILE_PICTURE_KEY,guestMember.getImageUrl());
        editor.putString(GUEST_CREATING_TIME_STAMP_KEY,guestMember.getCreationTimeStamp());
        editor.putString(GUEST_OTP_KEY,guestMember.getOtp());
        editor.putBoolean(GUEST_VERIFIED_KEY,guestMember.isVerified());
        editor.commit();
    }

    private TrialMember retrieveGuestMember() {

        TrialMember trialMember = new TrialMember.Builder(preferences.getString(GUEST_CREATING_TIME_STAMP_KEY,null))
                .setSap(preferences.getString(MEMBER_SAP_KEY,null))
                .setEmail(preferences.getString(EMAIL_KEY,null))
                .setName(preferences.getString(NAME_KEY,null))
                .setImageUrl(preferences.getString(PROFILE_PICTURE_KEY,null))
                .setOtp(preferences.getString(GUEST_OTP_KEY,null))
                .setVerified(preferences.getBoolean(GUEST_VERIFIED_KEY,false))
                .build();

        return trialMember;
    }

    public boolean createGuestSession(@NonNull TrialMember guestMember) {
        if(!isSharedPreferencesSet())
            throw new SessionManagerNotInitializedException("SharedPreference must be set for the SessionManger" +
                    "to function");

        if(isSessionAlive())
            return false;


        saveGuestMember(guestMember);
        this.guestMember = guestMember;
        sessionID = SessionManager.GUEST_SESSION_ID;

        return true;
    }



    /**
     * This method destroys any alive session
     * @return true - if destroyed - false - if no session is alive
     */
    public boolean destroySession() {
        if(!isSharedPreferencesSet())
            throw new SessionManagerNotInitializedException("SharedPreference must be set for the SessionManger" +
                    "to function");
        System.out.println("Session destroyed");
        if(!isSessionAlive())
            return false;

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        loggedInMember = null;
        guestMember = null;
        sessionID = SessionManager.NONE;

        return true;
    }
    /* ******************************************************************************************/


    @Nullable
    public Member getLoggedInMember() {
        return loggedInMember;
    }

    @Nullable
    public TrialMember getGuestMember() {
        return guestMember;
    }

    public static class SessionManagerNotInitializedException extends RuntimeException{
        public SessionManagerNotInitializedException(String msg) {
            super(msg);
        }
    }
}