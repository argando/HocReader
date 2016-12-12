package com.eaccid.bookreader.pagerfragments.fragment_1;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.eaccid.bookreader.R;
import com.eaccid.hocreader.data.local.db.entity.Word;
import com.eaccid.bookreader.provider.DataProvider;
import com.eaccid.bookreader.provider.WordDatabaseDataProvider;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

public class SwipeOnLongPressRecyclerViewAdapter
        extends RecyclerView.Adapter<SwipeOnLongPressRecyclerViewAdapter.WordTranslationViewHolder>
        implements SwipeableItemAdapter<SwipeOnLongPressRecyclerViewAdapter.WordTranslationViewHolder> {

    private static final String TAG = "SwipeableItemAdapter";

    // NOTE: Make accessible with short name
    private interface Swipeable extends SwipeableItemConstants {
    }

    private DataProvider mProvider;
    private EventListener mEventListener;
    private View.OnClickListener mItemViewOnClickListener;
    private View.OnClickListener mSwipeableViewContainerOnClickListener;

    public interface EventListener {
        void onItemRemoved(int position);

        void onItemPinned(int position);

        void onItemViewClicked(View v, boolean pinned);

    }

    public SwipeOnLongPressRecyclerViewAdapter(WordDatabaseDataProvider dataProvider) {
        mProvider = dataProvider;
        mItemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClick(v);
            }
        };

        mSwipeableViewContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeableViewContainerClick(v);
            }
        };

        // SwipeableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    static class WordTranslationViewHolder extends AbstractSwipeableItemViewHolder {
        FrameLayout mContainer;
        TextView mTextView;
        TextView mTranslationView;

        WordTranslationViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mTextView = (TextView) v.findViewById(android.R.id.text1);
            mTranslationView = (TextView) v.findViewById(android.R.id.text2);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }

    @Override
    public WordTranslationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.word_item_fragment_1, parent, false);
        return new WordTranslationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WordTranslationViewHolder holder, int position) {
        final DataProvider.ItemDataProvider item = mProvider.getItem(position);

        // set listeners
        // (if the item is *pinned*, click event comes to the itemView)
        holder.itemView.setOnClickListener(mItemViewOnClickListener);
        // (if the item is *not pinned*, click event comes to the mContainer)
        holder.mContainer.setOnClickListener(mSwipeableViewContainerOnClickListener);

        // set text
        holder.mTextView.setText(item.getTestText());


        //todo del WORD
        Word word = (Word) item.getObject();
        holder.mTranslationView.setText(word.getTranslation());

        // set background resource (target view ID: container)
        final int swipeState = holder.getSwipeStateFlags();

        int bgResId;

        if (item.isLastAdded()) {
            bgResId = R.drawable.bg_item_session_state;
        } else {
            bgResId = R.drawable.bg_item_normal_state;
        }

//        if ((swipeState & Swipeable.STATE_FLAG_IS_UPDATED) != 0) {
//            if ((swipeState & Swipeable.STATE_FLAG_IS_ACTIVE) != 0) {
//                bgResId = R.drawable.bg_item_swiping_active_state;
//            } else if ((swipeState & Swipeable.STATE_FLAG_SWIPING) != 0) {
//                bgResId = R.drawable.bg_item_swiping_state;
//            }
//        }

        holder.mContainer.setBackgroundResource(bgResId);

        // set swiping properties
        holder.setSwipeItemHorizontalSlideAmount(
                item.isPinned() ? Swipeable.OUTSIDE_OF_THE_WINDOW_LEFT : 0);

    }


    /** from advanced recycler view library example*/

    @Override
    public long getItemId(int position) {
        return mProvider.getItem(position).getItemId();
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public int onGetSwipeReactionType(WordTranslationViewHolder holder, int position, int x, int y) {
        return Swipeable.REACTION_CAN_SWIPE_LEFT | Swipeable.REACTION_MASK_START_SWIPE_LEFT |
                Swipeable.REACTION_CAN_SWIPE_RIGHT | Swipeable.REACTION_MASK_START_SWIPE_RIGHT |
                Swipeable.REACTION_START_SWIPE_ON_LONG_PRESS;

    }

    @Override
    public void onSetSwipeBackground(WordTranslationViewHolder holder, int position, int type) {
        int bgRes = 0;
        switch (type) {
            case Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_neutral;
                break;
            case Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_left;
                break;
            case Swipeable.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_right;
                break;
        }
        holder.itemView.setBackgroundResource(bgRes);

    }

    @Override
    public SwipeResultAction onSwipeItem(WordTranslationViewHolder holder, final int position, int result) {
        Log.d(TAG, "onSwipeItem(position = " + position + ", result = " + result + ")");

        switch (result) {
            // swipe right
            case Swipeable.RESULT_SWIPED_RIGHT:
                if (mProvider.getItem(position).isPinned()) {
                    // pinned --- back to default position
                    return new UnpinResultAction(this, position);
                } else {
                    // not pinned --- remove
                    return new SwipeRightResultAction(this, position);
                }
                // swipe left -- pin
            case Swipeable.RESULT_SWIPED_LEFT:
                return new SwipeLeftResultAction(this, position);
            // other --- do nothing
            case Swipeable.RESULT_CANCELED:
            default:
                if (position != RecyclerView.NO_POSITION) {
                    return new UnpinResultAction(this, position);
                } else {
                    return null;
                }
        }
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    private void onItemViewClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(v, true); // true --- pinned
        }
    }

    private void onSwipeableViewContainerClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(RecyclerViewAdapterUtils.getParentViewHolderItemView(v), false);  // false --- not pinned
        }
    }

    private static class SwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection {
        private SwipeOnLongPressRecyclerViewAdapter mAdapter;
        private final int mPosition;
        private boolean mSetPinned;

        SwipeLeftResultAction(SwipeOnLongPressRecyclerViewAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            DataProvider.ItemDataProvider item = mAdapter.mProvider.getItem(mPosition);

            if (!item.isPinned()) {
                item.setPinned(true);
                mAdapter.notifyItemChanged(mPosition);
                mSetPinned = true;
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mSetPinned && mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onItemPinned(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class SwipeRightResultAction extends SwipeResultActionRemoveItem {
        private SwipeOnLongPressRecyclerViewAdapter mAdapter;
        private final int mPosition;

        SwipeRightResultAction(SwipeOnLongPressRecyclerViewAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            mAdapter.mProvider.removeItem(mPosition);
            mAdapter.notifyItemRemoved(mPosition);
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onItemRemoved(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class UnpinResultAction extends SwipeResultActionDefault {
        private SwipeOnLongPressRecyclerViewAdapter mAdapter;
        private final int mPosition;

        UnpinResultAction(SwipeOnLongPressRecyclerViewAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            DataProvider.ItemDataProvider item = mAdapter.mProvider.getItem(mPosition);
            if (item.isPinned()) {
                item.setPinned(false);
                mAdapter.notifyItemChanged(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }
}