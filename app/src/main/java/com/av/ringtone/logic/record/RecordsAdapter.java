package com.av.ringtone.logic.record;

import java.io.File;
import java.util.List;

import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.model.RecordModel;
import com.av.ringtone.utils.FileUtils;
import com.av.ringtone.utils.NavigationUtils;
import com.av.ringtone.utils.ToastUtils;
import com.av.ringtone.views.CommonDialog;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by LiJiaZhi on 16/12/19.
 */

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ItemHolder> {

    private List<RecordModel> mDatas;
    private BaseActivity mContext;

    public RecordsAdapter(BaseActivity context, List<RecordModel> list) {
        this.mDatas = list;
        this.mContext = context;
    }

    public void setDatas(List<RecordModel> datas) {
        this.mDatas = datas;
        notifyDataSetChanged();

    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, null);
        ItemHolder ml = new ItemHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        RecordModel localItem = mDatas.get(i);
        itemHolder.type.setImageResource(R.drawable.icon_record);
        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(getDuration(localItem.duration) + " | " + FileUtils.getFileDir(localItem.path));
        itemHolder.rl.setTag(localItem);
        itemHolder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RecordModel tempModel = (RecordModel) v.getTag();
                NavigationUtils.goToCutter(mContext, tempModel);
            }
        });
        setOnPopupMenuListener(itemHolder, i);
    }

    private String getDuration(int d) {
        int min = d/60;
        int sec = d - 60 * min;
        return String.format("%02d:%02d", min, sec);
    }

    @Override
    public int getItemCount() {
        return (null != mDatas ? mDatas.size() : 0);
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {
        itemHolder.popupMenu.setTag(mDatas.get(position));
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RecordModel tempModel = (RecordModel) v.getTag();
                final PopupMenu menu = new PopupMenu(mContext, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_song_edit:
                                NavigationUtils.goToCutter(mContext, tempModel);
                                break;
                            case R.id.popup_song_delete:
                                CommonDialog dialog =
                                        new CommonDialog(mContext,mContext.getString(R.string.delete_title), mContext.getString(R.string.delete_content),"Delete", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                File file = new File(tempModel.path);
                                                if (file.exists()) {
                                                    file.delete();
                                                }
                                                mDatas.remove(tempModel);
                                                UserDatas.getInstance().setRecords(mDatas);
                                                notifyDataSetChanged();
                                                ToastUtils.makeToastAndShowLong(mContext,mContext.getString(R.string.delete_success));
                                            }
                                        });
                                dialog.setCancelable(false);
                                dialog.show();
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_song);
                menu.show();
            }
        });
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        protected RelativeLayout rl;
        protected TextView title, artist;
        protected ImageView type, popupMenu;

        public ItemHolder(View view) {
            super(view);
            this.rl = (RelativeLayout) view.findViewById(R.id.rl);
            this.type = (ImageView) view.findViewById(R.id.type_iv);
            this.title = (TextView) view.findViewById(R.id.song_title);
            this.artist = (TextView) view.findViewById(R.id.song_detail);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu);
        }
    }

    public List<RecordModel> getDatas() {
        return mDatas;
    }

    public void upateDatas(List<RecordModel> list){
        mDatas = list;
        notifyDataSetChanged();
    }
}