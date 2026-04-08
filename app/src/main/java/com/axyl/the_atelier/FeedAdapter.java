package com.axyl.the_atelier;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private final List<FeedArticle> items = new ArrayList<>();
    private final LayoutInflater inflater;

    public FeedAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void clear() {
        int n = items.size();
        if (n == 0) {
            return;
        }
        items.clear();
        notifyItemRangeRemoved(0, n);
    }

    public void addItems(List<FeedArticle> more) {
        int start = items.size();
        items.addAll(more);
        notifyItemRangeInserted(start, more.size());
    }

    public void setItems(List<FeedArticle> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_feed_article, parent, false);
        return new FeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        holder.bind(items.get(position), inflater.getContext());
    }

    static final class FeedViewHolder extends RecyclerView.ViewHolder {

        private final View avatar;
        private final TextView textHeader;
        private final TextView textTime;
        private final ImageView iconPremium;
        private final TextView textBody;
        private final View mediaFrame;
        private final View mediaBackdrop;
        private final View mediaCenter;
        private final TextView textLikeCount;
        private final TextView textCommentCount;
        private final TextView textShareCount;

        FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            textHeader = itemView.findViewById(R.id.textHeader);
            textTime = itemView.findViewById(R.id.textTime);
            iconPremium = itemView.findViewById(R.id.iconPremium);
            textBody = itemView.findViewById(R.id.textBody);
            mediaFrame = itemView.findViewById(R.id.mediaFrame);
            mediaBackdrop = itemView.findViewById(R.id.mediaBackdrop);
            mediaCenter = itemView.findViewById(R.id.mediaCenter);
            textLikeCount = itemView.findViewById(R.id.textLikeCount);
            textCommentCount = itemView.findViewById(R.id.textCommentCount);
            textShareCount = itemView.findViewById(R.id.textShareCount);
        }

        void bind(FeedArticle item, Context context) {
            SpannableString header = new SpannableString(item.authorName + item.actionText);
            header.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    0,
                    item.authorName.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textHeader.setText(header);

            textTime.setText(context.getString(R.string.feed_time_line, item.timeAgo));

            iconPremium.setVisibility(item.premium ? View.VISIBLE : View.GONE);

            textBody.setText(item.bodyText);

            textLikeCount.setText(item.likeCount);
            textCommentCount.setText(item.commentCount);
            textShareCount.setText(item.shareCount);

            float d = context.getResources().getDisplayMetrics().density;
            float cornerMedia = 10f * d;

            GradientDrawable avatarBg = new GradientDrawable();
            avatarBg.setShape(GradientDrawable.OVAL);
            avatarBg.setColor(item.avatarColor);
            avatar.setBackground(avatarBg);

            GradientDrawable backdrop = new GradientDrawable();
            backdrop.setCornerRadius(cornerMedia);
            backdrop.setColor(item.mediaColor);
            mediaBackdrop.setBackground(backdrop);

            int innerColor = ColorUtils.blendARGB(item.mediaColor, Color.BLACK, 0.12f);
            GradientDrawable center = new GradientDrawable();
            center.setShape(GradientDrawable.OVAL);
            center.setColor(innerColor);
            mediaCenter.setBackground(center);

            mediaFrame.setClipToOutline(true);
            mediaFrame.setOutlineProvider(
                    new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, Outline outline) {
                            outline.setRoundRect(
                                    0,
                                    0,
                                    view.getWidth(),
                                    view.getHeight(),
                                    cornerMedia);
                        }
                    });
        }
    }
}
