package org.upesacm.acmacmw.fragment.hierarchy;


import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.telephony.PhoneNumberUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.upesacm.acmacmw.R;

import java.util.List;

public class BottomsheetFragment extends BottomSheetDialogFragment {


    TextView Name_bottomsheet;
    ImageView Profile_bottomsheet;
    TextView Current_project;
    ImageView Whatsapp;
    ImageView Linkedin;
    ImageView Contact;
    ImageView Github;

    static String Name;
    static String Image;
    static long getWhatsappNo;
    static String CurrentProj;
    static String LinkedinUrl;
    static long ContactNo;
    static String GithubUrl;


    public BottomsheetFragment GetData(String name, String image, long whatsappno, String linkedinUrl, long contactNo, String githubUrl,String currentProj) {
        Name = name;
        Image = image;
        getWhatsappNo = whatsappno;
        LinkedinUrl = linkedinUrl;
        ContactNo = contactNo;
        GithubUrl = githubUrl;
        CurrentProj = currentProj;
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {  // Inflating view for bottom sheet
        View v = inflater.inflate(R.layout.heirarchy_bottomsheet, container, false);

        Name_bottomsheet = (TextView) v.findViewById(R.id.name_bottomsheet);
        Profile_bottomsheet = (ImageView) v.findViewById(R.id.profile_bottomsheet);
        Current_project = (TextView) v.findViewById(R.id.current_project);
        Whatsapp = (ImageView) v.findViewById(R.id.whatsapp_bottomsheet);
        Linkedin = (ImageView) v.findViewById(R.id.linkedin_bottomsheet);
        Contact = (ImageView) v.findViewById(R.id.contact_bottomsheet);
        Github = (ImageView) v.findViewById(R.id.github_bottomsheet);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        if (isAdded()) {
            final Typeface bold = Typeface.createFromAsset(getContext().getAssets(), "Fonts/product_sans_bold.ttf");
            Name_bottomsheet.setTypeface(bold);
            Name_bottomsheet.setText(Name);
            Current_project.setText(CurrentProj);
            Current_project.setMovementMethod(new ScrollingMovementMethod());
            if (Image.equals("")) {
                Profile_bottomsheet.setImageResource(R.drawable.acm);
            } else {
                Glide.with(getContext())
                        .load(Image).thumbnail(Glide.with(getContext()).load(R.drawable.loading_profile_pic))
                        .into(Profile_bottomsheet);
            }
        }
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("91" + getWhatsappNo) + "@s.whatsapp.net");//phone number without "+" prefix
                getContext().startActivity(sendIntent);
            }
        });

        Linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = LinkedinUrl;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(temp));
                final PackageManager packageManager = getContext().getPackageManager();
                final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.isEmpty()) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://you"));
                }
                getContext().startActivity(intent);
            }
        });
        Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                String temp = "tel:" + ContactNo;
                callIntent.setData(Uri.parse(temp));
                getContext().startActivity(callIntent);
            }
        });
        Github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = GithubUrl;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(temp));
                final PackageManager packageManager = getContext().getPackageManager();
                final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.isEmpty()) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("Github://you"));
                }
                getContext().startActivity(intent);
            }
        });

    }
}