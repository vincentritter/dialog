package com.dialogapp.dialog.ui.mainscreen.timeline;

import android.content.Context;
import android.view.ViewGroup;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Post;
import com.dialogapp.dialog.ui.mainscreen.common.BaseRecyclerAdapter;
import com.dialogapp.dialog.ui.mainscreen.common.PostViewHolder;

public class TimelineAdapter extends BaseRecyclerAdapter<Post, PostViewHolder> {

    public TimelineAdapter(Context context) {
        super(context);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostViewHolder(inflate(R.layout.post_item, parent, false));
    }
}
