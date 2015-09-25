package br.com.javace.javou.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import br.com.javace.javou.R;
import br.com.javace.javou.interfaces.OnItemClickListener;
import br.com.javace.javou.interfaces.OnItemLongClickListener;
import br.com.javace.javou.model.participant.Participant;
import br.com.javace.javou.util.Util;

/**
 * Created by Rudsonlive on 10/07/15.
 */

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {

    private int mBackground;
    private Context mContext;
    private HashSet<Integer> checkedItems;
    private ArrayList<Participant> mParticipants;
    private ArrayList<Participant> mSearchParticipants;

    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private static final int ITEM_VIEW_TYPE_HEADER = 0;

    private static OnItemClickListener mOnItemClickListener;
    private static OnItemLongClickListener mOnItemLongClickListener;

    public ParticipantAdapter(Context context, ArrayList<Participant> participants) {
        this.mContext = context;
        this.mParticipants = participants;
        this.checkedItems = new HashSet<>();
        this.mSearchParticipants = new ArrayList<>();
        this.mSearchParticipants.addAll(this.mParticipants);

        if (context != null) {
            TypedValue mTypedValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, mTypedValue, true);
            this.mBackground = mTypedValue.resourceId;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mContainer;

        public TextView mTitle;

        public TextView mName;
        public TextView mEmail;
        public TextView mShirtSize;

        public ViewHolder(View view) {
            super(view);

            mContainer = view;

            mTitle = (TextView) view.findViewById(R.id.txtTitle);
            mName = (TextView) view.findViewById(R.id.txtName);
            mEmail = (TextView) view.findViewById(R.id.txtEmail);
            mShirtSize = (TextView) view.findViewById(R.id.txtShirtSize);

            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // -1 Refers to the header
                    int position = getAdapterPosition() - 1;
                    if (mOnItemClickListener != null && position >= 0) {
                        mOnItemClickListener.onItemClick(v, position);
                    }
                }
            });

            mContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition() - 1;
                    if (mOnItemLongClickListener != null && position >= 0) {
                        mOnItemLongClickListener.onItemLongClick(v, position);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public ParticipantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_participant_header, parent, false);
            return new ViewHolder(view);
        }

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_participant_item, parent, false);
        view.setBackgroundResource(this.mBackground);
        return new ViewHolder(view);
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (mParticipants == null || mParticipants.size() == 0){
            return;
        }

        if (isHeader(position)) {
            holder.mTitle.setText(mParticipants.get(position).getNameEvent());
            return;
        }

        // -1 Refers to the header
        Participant participant = mParticipants.get(position - 1);

        holder.mName.setText(participant.getName());
        holder.mName.setCompoundDrawablesWithIntrinsicBounds(participant.isAttend() ? R.drawable.ic_check_green_18dp : 0, 0, 0, 0);

        holder.mEmail.setText(participant.getEmail());

        if (participant.isGroup()) {
            int shirtSize = participant.getShirtSize();
            holder.mShirtSize.setVisibility(View.VISIBLE);
            GradientDrawable gradientDrawable = (GradientDrawable) holder.mShirtSize.getBackground();
            gradientDrawable.setColor(mContext.getResources().getColor(Util.shirtSizeColor[shirtSize]));
            holder.mShirtSize.setText(this.mContext.getString(Util.shirtSize[shirtSize]));
        }else{
            holder.mShirtSize.setVisibility(View.INVISIBLE);
        }

        if (checkedItems.contains(Integer.valueOf(position))) {
            holder.mContainer.setBackgroundResource(R.drawable.seletor_long_click_item);
        } else {
            holder.mContainer.setBackgroundResource(this.mBackground);
        }
    }

    public void readAttendParticipant(int position, boolean attend){
        mParticipants.get(position).setAttend(attend);
        notifyDataSetChanged();
    }

    public void remove(int position){
        mParticipants.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        // +1 Refers to the header
        return (mParticipants == null ? 0 : mParticipants.size()+1);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public void resetarCheck() {
        this.checkedItems.clear();
        this.notifyDataSetChanged();
    }

    public void setChecked(int position, boolean checked) {
        resetarCheck();

        if (checked) {
            this.checkedItems.add(position);
        } else {
            this.checkedItems.remove(position);
        }

        this.notifyDataSetChanged();
    }

    public void searchParticipantes(CharSequence charText) {

        charText = Util.removeAccent((String) charText).toLowerCase(Locale.getDefault());

        mParticipants.clear();
        if (charText.length() == 0) {
            mParticipants.addAll(mSearchParticipants);
        } else {
            for (Participant participant: mSearchParticipants) {
                String name = Util.removeAccent(participant.getName());
                if (name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mParticipants.add(participant);
                }
            }
        }

        notifyDataSetChanged();
    }
}

