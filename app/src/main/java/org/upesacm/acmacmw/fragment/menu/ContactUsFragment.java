package org.upesacm.acmacmw.fragment.menu;


import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.MainActivity;
import org.upesacm.acmacmw.model.HeirarchyModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment implements
        View.OnClickListener,
        ValueEventListener {
    TextView textViewAcmFb;
    TextView textViewAcmwFb;
    TextView textViewAcmInsta;
    TextView textViewAcmwInsta;
    TextView textViewAcmYoutube;
    TextView textViewAcmwYoutube;
    TextView textViewAcmWeb;
    TextView textViewAcmwWeb;
    TextView textViewAcmMail;
    TextView textViewAcmwMail;
    TextView textViewAcmWhatsapp;
    TextView textViewAcmwWhatsapp;
    ImageView imageViewAvailablePrExec;
    ImageView imageViewAvailableMembChair;
    ImageView imageViewPrExecPic;
    ImageView imageViewMembChairPic;
    ImageView imageViewCallPrExec;
    ImageView imageViewCallMembChair;

    DatabaseReference reference;

    boolean isViewDestroyed;
    public ContactUsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        textViewAcmFb = view.findViewById(R.id.text_view_contact_us_acm_fb);
        textViewAcmwFb = view.findViewById(R.id.text_view_contact_us_acmw_fb);
        textViewAcmInsta = view.findViewById(R.id.text_view_contact_us_acm_insta);
        textViewAcmwInsta = view.findViewById(R.id.text_view_contact_us_acmw_insta);
        textViewAcmYoutube = view.findViewById(R.id.text_view_contact_us_acm_youtube);
        textViewAcmwYoutube = view.findViewById(R.id.text_view_contact_us_acmw_youtube);
        textViewAcmWeb = view.findViewById(R.id.text_view_contact_us_acm_web);
        textViewAcmwWeb = view.findViewById(R.id.text_view_contact_us_acmw_web);
        textViewAcmMail = view.findViewById(R.id.text_view_contact_us_acm_mail);
        textViewAcmwMail = view.findViewById(R.id.text_view_contact_us_acmw_mail);
        textViewAcmWhatsapp = view.findViewById(R.id.text_view_contact_us_acm_whatsapp);
        textViewAcmwWhatsapp = view.findViewById(R.id.text_view_contact_us_acmw_whatsapp);
        imageViewAvailableMembChair = view.findViewById(R.id.image_view_contact_us_memb_chair);
        imageViewAvailablePrExec = view.findViewById(R.id.image_view_contact_us_available_prexec);
        imageViewMembChairPic = view.findViewById(R.id.image_view_contact_us_memb_chair_pic);
        imageViewPrExecPic = view.findViewById(R.id.image_view_contact_us_prexec_pic);
        imageViewCallMembChair = view.findViewById(R.id.image_view_contact_us_call_membchair);
        imageViewCallPrExec = view.findViewById(R.id.image_view_contact_us_call_prexec);


        Typeface regular = Typeface.createFromAsset(getContext().getAssets(),"Fonts/product_sans_regular.ttf");
        textViewAcmFb.setTypeface(regular);
        textViewAcmFb.setOnClickListener(this);
        textViewAcmwFb.setTypeface(regular);
        textViewAcmwFb.setOnClickListener(this);
        textViewAcmwInsta.setTypeface(regular);
        textViewAcmInsta.setOnClickListener(this);
        textViewAcmwInsta.setTypeface(regular);
        textViewAcmwInsta.setOnClickListener(this);
        textViewAcmYoutube.setTypeface(regular);
        textViewAcmYoutube.setOnClickListener(this);
        textViewAcmwYoutube.setTypeface(regular);
        textViewAcmwYoutube.setOnClickListener(this);
        textViewAcmWeb.setTypeface(regular);
        textViewAcmWeb.setOnClickListener(this);
        textViewAcmwWeb.setTypeface(regular);
        textViewAcmwWeb.setOnClickListener(this);
        textViewAcmMail.setTypeface(regular);
        textViewAcmMail.setOnClickListener(this);
        textViewAcmwMail.setTypeface(regular);
        textViewAcmwMail.setOnClickListener(this);
        textViewAcmWhatsapp.setTypeface(regular);
        textViewAcmwWhatsapp.setTypeface(regular);
        textViewAcmWhatsapp.setOnClickListener(this);
        textViewAcmwWhatsapp.setOnClickListener(this);
        imageViewCallPrExec.setOnClickListener(this);
        imageViewCallMembChair.setOnClickListener(this);

        isViewDestroyed = false;
        reference = FirebaseDatabase.getInstance().getReference().child("Heirarchy");
        reference.addValueEventListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        isViewDestroyed = true;
        textViewAcmFb.setOnClickListener(null);
        textViewAcmwFb.setOnClickListener(null);
        textViewAcmInsta.setOnClickListener(null);
        textViewAcmwInsta.setOnClickListener(null);
        textViewAcmYoutube.setOnClickListener(null);
        textViewAcmwYoutube.setOnClickListener(null);
        textViewAcmWeb.setOnClickListener(null);
        textViewAcmwWeb.setOnClickListener(null);
        textViewAcmMail.setOnClickListener(null);
        textViewAcmwMail.setOnClickListener(null);
        textViewAcmWhatsapp.setOnClickListener(null);
        textViewAcmwWhatsapp.setOnClickListener(null);
        imageViewCallPrExec.setOnClickListener(null);
        imageViewCallMembChair.setOnClickListener(null);

        textViewAcmFb = null;
        textViewAcmwFb = null;
        textViewAcmInsta = null;
        textViewAcmwInsta = null;
        textViewAcmYoutube = null;
        textViewAcmwYoutube = null;
        textViewAcmWeb = null;
        textViewAcmwWeb = null;
        textViewAcmMail = null;
        textViewAcmwMail = null;
        textViewAcmWhatsapp = null;
        textViewAcmwWhatsapp = null;
        imageViewAvailableMembChair = null;
        imageViewAvailablePrExec = null;
        imageViewMembChairPic = null;
        imageViewPrExecPic = null;
        imageViewCallMembChair = null;
        imageViewCallPrExec = null;

        reference.removeEventListener(this);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {

        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_view_contact_us_acm_fb : {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.link_acm_fb))));
                break;
            }
            case R.id.text_view_contact_us_acmw_fb : {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.link_acmw_fb))));
                break;
            }
            case R.id.text_view_contact_us_acm_insta : {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.link_acm_insta))));
                break;
            }
            case R.id.text_view_contact_us_acmw_insta : {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.link_acmw_insta))));
                break;
            }
            case R.id.text_view_contact_us_acm_youtube : {
                Intent youtubeIntent= new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_acm_youtube)));
                Intent chooser = Intent.createChooser(youtubeIntent , "Open With");

                if (youtubeIntent .resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(chooser);
                }
                break;
            }
            case R.id.text_view_contact_us_acmw_youtube : {
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_acmw_youtube)));
                Intent chooser = Intent.createChooser(youtubeIntent , "Open With");

                if (youtubeIntent .resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(chooser);
                }
                break;
            }
            case R.id.text_view_contact_us_acm_web : {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.link_acm_website))));
                break;
            }
            case R.id.text_view_contact_us_acmw_web : {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.link_acmw_website))));
                break;
            }
            case R.id.text_view_contact_us_acm_mail : {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setData(Uri.parse(getString(R.string.mail_id_acm)));
                sendIntent.setType("plain/text");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { getString(R.string.mail_id_acm) });
                startActivity(sendIntent);
                break;
            }
            case R.id.text_view_contact_us_acmw_mail : {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setData(Uri.parse(getString(R.string.mail_id_acmw)));
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { getString(R.string.mail_id_acmw) });
                sendIntent.setType("plain/text");
                startActivity(sendIntent);
                break;
            }
            case R.id.text_view_contact_us_acm_whatsapp : {
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("91"+getString(R.string.whatsapp_acm)) + "@s.whatsapp.net");//phone number without "+" prefix
                getContext().startActivity(sendIntent);
                break;
            }
            case R.id.text_view_contact_us_acmw_whatsapp : {
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("91"+getString(R.string.whatsapp_acmw)) + "@s.whatsapp.net");//phone number without "+" prefix
                getContext().startActivity(sendIntent);
                break;
            }
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(!isViewDestroyed) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                final HeirarchyModel model = ds.getValue(HeirarchyModel.class);
                System.out.println("contact us available in campus membe chair : " + model.getAvailableInCampus());
                if (model.getSapId() == 500053778L && getActivity() != null) {
                    RequestOptions requestOptions=new RequestOptions();
                    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
                            ;
                    Glide.with(this)
                            .load(model.getImage())
                            .apply(requestOptions)
                            .thumbnail(Glide.with(this).load(R.drawable.loading_profile_pic))
                            .into(imageViewPrExecPic);
                    if (model.getAvailableInCampus() == 1) {
                        imageViewAvailablePrExec.setImageResource(R.drawable.ic_check_circle_green_24dp);
                    } else {
                        imageViewAvailablePrExec.setImageResource(R.drawable.ic_cancel_grey_24dp);
                    }
                    imageViewCallPrExec.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            String temp="tel:"+model.getContact();
                            callIntent.setData(Uri.parse(temp));
                            getActivity().startActivity(callIntent);
                        }
                    });
                } else if (model.getSapId() == 500052158L && getActivity() != null) {
                    RequestOptions requestOptions=new RequestOptions();
                    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
                            ;
                    Glide.with(this)
                            .load(model.getImage())
                            .apply(requestOptions)
                            .thumbnail(Glide.with(this).load(R.drawable.loading_profile_pic))
                            .into(imageViewMembChairPic);
                    if (model.getAvailableInCampus() == 1) {
                        System.out.println("setting green");
                        imageViewAvailableMembChair.setImageResource(R.drawable.ic_check_circle_green_24dp);
                    } else {
                        imageViewAvailableMembChair.setImageResource(R.drawable.ic_cancel_grey_24dp);
                    }
                    imageViewCallMembChair.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            String temp="tel:"+model.getContact();
                            callIntent.setData(Uri.parse(temp));
                            getActivity().startActivity(callIntent);
                        }
                    });
                }
            }
        }
        else {
            System.out.println("contact us onDataChange called, view is destroyed");
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
