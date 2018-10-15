package org.upesacm.acmacmw.activity;

import android.content.Context;
import android.content.SharedPreferences;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.model.Member;
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

    private static SessionManager sessionManager;
    /* ********************************************************************************************************* */


    private SharedPreferences preferences;
    private int sessionID;

    private String userSap;

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
                sessionManager.userSap = sessionManager.preferences.getString(MEMBER_SAP_KEY,null);
                break;
            }
            case GUEST_SESSION_ID : {
                sessionManager.userSap = sessionManager.preferences.getString(GUEST_MEMBER_SAP_KEY,null);
            }
            default : {
                System.out.println("no session has been in progress");
            }
        }

        if(sessionManager.userSap != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("acm_acmw_members")
                    .child(sessionManager.userSap)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(sessionManager.sessionID == MEMBER_SESSION_ID) {
                                sessionManager.loggedInMember = dataSnapshot.getValue(Member.class);
                                System.out.println("session manager : data fetched");
                            } else {
                                sessionManager.guestMember = dataSnapshot.getValue(TrialMember.class);
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

    /**
     * This method creates a new Member Session if it has not already been created
     * @param memberSap - The Sap ID of the member for whom the session is being created
     * @return true - if the session has been created and false if a session is already alive
     */
    private boolean  createMemberSession(@NonNull String memberSap) {
        if(!isSharedPreferencesSet())
            throw new SessionManagerNotInitializedException("SharedPreference must be set for the SessionManger" +
                    "to function");

        if(isSessionAlive())
            return false;

        //create the memeber session
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MEMBER_SAP_KEY,memberSap);
        editor.putInt(SESSION_ID_STORE_KEY,MEMBER_SESSION_ID);
        editor.commit();

        this.userSap = memberSap;

        sessionID = SessionManager.MEMBER_SESSION_ID;

        return true;
    }

    public boolean createMemberSession(@NonNull Member member) {
        boolean created = createMemberSession(member.getSap());
        if(created) {
            this.loggedInMember = member;
        }
        System.out.println("Member session created : "+created);
        return created;
    }

    /**
     * This method creates a new Guest Session if it has not already been created
     * @param trialMemberSap - The Sap ID of the guest for whom the session is being created
     * @return true - if the session has been created and - false if a session is already alive
 createMemberSession    */
    private boolean createGuestSession(@NonNull String trialMemberSap) {
        if(!isSharedPreferencesSet())
            throw new SessionManagerNotInitializedException("SharedPreference must be set for the SessionManger" +
                    "to function");

        if(isSessionAlive())
            return false;

        //create the guest session
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(GUEST_MEMBER_SAP_KEY,trialMemberSap);
        editor.putInt(SESSION_ID_STORE_KEY,GUEST_SESSION_ID);
        editor.commit();

        this.userSap = trialMemberSap;

        sessionID = SessionManager.GUEST_SESSION_ID;

        return true;
    }

    public boolean createGuestSession(@NonNull TrialMember guestMember) {
        boolean created = createGuestSession(guestMember.getSap());
        if(created) {
            this.guestMember = guestMember;
        }
        System.out.println("Guest session created : "+created);
        return created;
    }

    /**
     * @return The sap id of the member if a Member Session is alive otherwise null
     */
    private String retriveMemberSap() {
        if(!isSharedPreferencesSet())
            throw new SessionManagerNotInitializedException("SharedPreference must be set for the SessionManger" +
                    "to function");
        if(userSap==null)
            userSap = preferences.getString(MEMBER_SAP_KEY,null);

        if(userSap == null)
            throw new IllegalStateException("Member Sap stored by SessionManager was null");

        return userSap;
    }

    /**
     * @return The Sap id of the guest if a Guest Session is alive otherwise null
     */
    private String retrieveGuestMemberSap() {
        if(!isSharedPreferencesSet())
            throw new SessionManagerNotInitializedException("SharedPreference must be set for the SessionManger" +
                    "to function");

        if(userSap == null) //fetch the guestMemberSap from the preference file
            userSap = preferences.getString(GUEST_MEMBER_SAP_KEY,null);

        if(userSap == null)
            throw new IllegalStateException("Guest Sap stored by SessionManager was null");

        return userSap;
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

        userSap = null;
        loggedInMember = null;
        guestMember = null;
        sessionID = SessionManager.NONE;

        return true;
    }
    /* ******************************************************************************************/

    @Nullable
    public String getUserSap() {
        switch (sessionID) {
            case GUEST_SESSION_ID : {
                return retrieveGuestMemberSap();
            }

            case MEMBER_SESSION_ID : {
                return retriveMemberSap();
            }

            default : {
                return null;
            }
        }
    }

    @Nullable
    public Member getLoggedInMember() {
        return loggedInMember;
    }

    @Nullable
    public TrialMember getGuestMember() {
        return guestMember;
    }
}