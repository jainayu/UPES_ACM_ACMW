package org.upesacm.acmacmw.adapter.hierarchy;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.hierarchy.BottomsheetFragment;
import org.upesacm.acmacmw.model.HeirarchyModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HeirarchyAdapter extends RecyclerView.Adapter<HeirarchyAdapter.HeirarchyViewHolder> {
    private static final String TAG = "HeirarchyAdapter";
    private List<HeirarchyModel> heirarchyModels;
    private Context context;


    public HeirarchyAdapter(List<HeirarchyModel> heirarchyModels) {
        this.heirarchyModels = heirarchyModels;
    }


    @NonNull
    @Override
    public HeirarchyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.heirarchy_holder_v3, parent, false);
        HeirarchyViewHolder heirarchyViewHolder = new HeirarchyViewHolder(view);
        return heirarchyViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final HeirarchyViewHolder holder, final int position) {
        context = holder.itemView.getContext();
        if (heirarchyModels.get(position) != null) {
            Typeface regular = Typeface.createFromAsset(context.getAssets(), "Fonts/product_sans_regular.ttf");
            final Typeface bold = Typeface.createFromAsset(context.getAssets(), "Fonts/product_sans_bold.ttf");
            holder.name.setText(heirarchyModels.get(position).getName());
//            holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cancel_grey_24dp, 0);
            if (heirarchyModels.get(position).getAvailableInCampus() == 1) {
                holder.availableincampus.setImageResource(R.drawable.ic_available_in_campus);
//               holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_green_24dp, 0);
            }
            /*else if (heirarchyModels.get(position).getAvailableInCampus() == 0) {
                holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cancel_grey_24dp, 0);
                holder.availableincampus.setImageResource(R.drawable.ic_cancel_grey_24dp);
            }*/

            if (heirarchyModels.get(position).getAcm_acmw().equals("ACM") || heirarchyModels.get(position).getAcm_acmw().equals("ACMW")) {
                holder.position.setText(heirarchyModels.get(position).getPostion());
                holder.position.setTextSize(20);
            }
            /*else{
                holder.position.setVisibility(View.GONE);
            }*/

            holder.position.setTypeface(bold);
            holder.about.setText(heirarchyModels.get(position).getAbout());

           /* if (heirarchyModels.get(position).getAvailableInCampus() == 0) {
                holder.availabeInCampus.setImageResource(R.drawable.ic_cancel_grey_24dp);
            } else if (heirarchyModels.get(position).getAvailableInCampus() == 1) {
                holder.availabeInCampus.setImageResource(R.drawable.ic_check_circle_green_24dp);
            }
            holder.availabeInCampus.setVisibility(View.GONE);*/

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            Glide.with(context)
                    .load(heirarchyModels.get(position).getImage())
                    .apply(requestOptions)
                    .thumbnail(Glide.with(context).load(R.drawable.loading_profile_pic))
                    .into(holder.image);


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Starts");

                    BottomsheetFragment bottomsheetFragment = new BottomsheetFragment();

                    // passing Data in BottomSheetFragment class
                    bottomsheetFragment.GetData(heirarchyModels.get(position).getName(), heirarchyModels.get(position).getImage(),
                            heirarchyModels.get(position).getWhatsapp(), heirarchyModels.get(position).getLinkedin(),
                            heirarchyModels.get(position).getContact(), heirarchyModels.get(position).getGithub(),
                            heirarchyModels.get(position).getCurrentproject());

                    BottomSheetDialogFragment bottomSheetDialogFragment = new BottomsheetFragment();
                    bottomSheetDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());   // initiating Bottomsheet

                }
            });

            holder.setIsRecyclable(false);

//            holder.whatsapp.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                   Uri uri = Uri.parse("smsto:"+heirarchyModels.get(position).getWhatsappNo());
//                  Intent i = new Intent(Intent.ACTION_SENDTO, uri);
//                  i.setPackage("com.whatsapp");
//                  context.startActivity(i);
//                    Intent sendIntent = new Intent("android.intent.action.MAIN");
//                    sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
//                    sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("91" + heirarchyModels.get(position).getWhatsapp()) + "@s.whatsapp.net");//phone number without "+" prefix
//                    context.startActivity(sendIntent);
//                }
//            });
//            holder.linkedin.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    String temp = heirarchyModels.get(position).getLinkedin();
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(temp));
//                    final PackageManager packageManager = context.getPackageManager();
//                    final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//                    if (list.isEmpty()) {
//                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://you"));
//                    }
//                    context.startActivity(intent);
//                }
//            });
//            holder.contact.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
//                    String temp = "tel:" + heirarchyModels.get(position).getContact();
//                    callIntent.setData(Uri.parse(temp));
//                    context.startActivity(callIntent);
//                }
//            });

        }
    }

    public class HeirarchyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView position;
        TextView about;
        CircleImageView availableincampus;
        //        ImageView whatsapp;
//        ImageView linkedin;
//        ImageView contact;
        // ImageView availabeInCampus;
        TextView id_firebase;
        CardView cardView;

        public HeirarchyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            position = itemView.findViewById(R.id.position);
            about = itemView.findViewById(R.id.about);
            cardView = itemView.findViewById(R.id.CardView);
            availableincampus = itemView.findViewById(R.id.availableincampus);
//            whatsapp = itemView.findViewById(R.id.image_view_hierarchy_whatsapp);
//            linkedin = itemView.findViewById(R.id.linkedin);
//            contact = itemView.findViewById(R.id.contact);

            //availabeInCampus = itemView.findViewById(R.id.availabe_in_campus);
        }
    }


    public void setHeirarchyModels(List<HeirarchyModel> models) {
        this.heirarchyModels = models;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return heirarchyModels.size();
    }

}
