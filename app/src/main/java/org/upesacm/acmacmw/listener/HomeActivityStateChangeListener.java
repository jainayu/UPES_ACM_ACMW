package org.upesacm.acmacmw.listener;

import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.TrialMember;

public interface HomeActivityStateChangeListener {
    void onSignedInMemberStateChange(Member member);
    void onMemberLogout();
    void onTrialMemberStateChange(TrialMember member);
    void onGoogleSignOut();
}
