package com.av.ringtone.logic.record;

import java.io.File;
import java.util.List;

import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.logic.MainActivity;
import com.av.ringtone.logic.MediaListener;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.RecordModel;
import com.av.ringtone.model.VoiceModel;
import com.av.ringtone.utils.FileUtils;
import com.av.ringtone.utils.NavigationUtils;
import com.av.ringtone.utils.ToastUtils;
import com.av.ringtone.views.CommonDialog;
import com.av.ringtone.views.MusicVisualizer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.av.ringtone.Constants.FILE_KIND_ALARM;
import static com.av.ringtone.Constants.FILE_KIND_MUSIC;
import static com.av.ringtone.Constants.FILE_KIND_NOTIFICATION;

/**
 * Created by LiJiaZhi on 16/12/19.
 */

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ItemHolder> {

    private List<RecordModel> mDatas;
    private BaseActivity mContext;
    private MediaListener mListener;

    private RecordModel currentPlayItem = null;

    public RecordsAdapter(MainActivity context, List<RecordModel> list) {
        this.mDatas = list;
        this.mContext = context;
        mListener = context;
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
        if (localItem.playStatus == 0) {
            itemHolder.type.setImageResource(R.drawable.icon_record);
            itemHolder.type.setVisibility(View.VISIBLE);
            itemHolder.musicVisualizer.setVisibility(View.GONE);
        } else {
            itemHolder.type.setImageResource(R.drawable.icon_record);
            itemHolder.type.setVisibility(View.GONE);
            itemHolder.musicVisualizer.setVisibility(View.VISIBLE);
        }

        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(getDuration(localItem.duration) + " | " + FileUtils.getFileDir(localItem.path));
        setOnPopupMenuListener(itemHolder, i);
        itemHolder.typelayout.setTag(localItem);
        itemHolder.typelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordModel local = (RecordModel) v.getTag();
                if (mListener != null) {
                    if (local.playStatus == 0) {
                        // ---播放
                        mListener.play(local);
                        local.playStatus = 1;
                    } else {
                        // ---暂停
                        mListener.pause();
                        local.playStatus = 0;
                        local.progress = 0;
                    }
                    if (currentPlayItem != null && local != currentPlayItem) {
                        currentPlayItem.playStatus = 0;
                        currentPlayItem.progress = 0;
                    }
                    currentPlayItem = local;
                    notifyDataSetChanged();
                }
            }
        });
        itemHolder.rl.setTag(localItem);
        itemHolder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RecordModel tempModel = (RecordModel) v.getTag();
                NavigationUtils.goToCutter(mContext, tempModel);
            }
        });
        itemHolder.progressBar.setProgress(localItem.progress);
    }

    private String getDuration(int d) {
        int min = d / 60;
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
                                CommonDialog dialog = new CommonDialog(mContext,
                                    mContext.getString(R.string.delete_title),
                                    mContext.getString(R.string.delete_content), "Delete", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            File file = new File(tempModel.path);
                                            if (file.exists()) {
                                                file.delete();
                                            }
                                            mDatas.remove(tempModel);
                                            UserDatas.getInstance().setRecords(mDatas);
                                            notifyDataSetChanged();
                                            ToastUtils.makeToastAndShowLong(mContext,
                                                mContext.getString(R.string.delete_success));
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
        private MusicVisualizer musicVisualizer;
        private LinearLayout typelayout;
        protected ProgressBar progressBar;

        public ItemHolder(View view) {
            super(view);
            this.rl = (RelativeLayout) view.findViewById(R.id.rl);
            this.type = (ImageView) view.findViewById(R.id.type_iv);
            this.typelayout = (LinearLayout)view.findViewById(R.id.type_ll);
            this.title = (TextView) view.findViewById(R.id.song_title);
            this.artist = (TextView) view.findViewById(R.id.song_detail);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu);
            this.musicVisualizer = (MusicVisualizer) view.findViewById(R.id.musicanimate);
            this.progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            musicVisualizer.setColor(view.getResources().getColor(R.color.colorAccent));
        }
    }

    public List<RecordModel> getDatas() {
        return mDatas;
    }

    public void upateDatas(List<RecordModel> list) {
        mDatas = list;
        notifyDataSetChanged();
    }

    public void updatePlayStatus(VoiceModel model){
        int index = mDatas.indexOf(model);
        if (model.playStatus ==0 ){
            //播放完成了
            if (index<mDatas.size()-1){
                if (mListener!= null){
                    currentPlayItem = mDatas.get(index+1);
                    mListener.play(currentPlayItem);
                }
            }
        }
        notifyItemChanged(index);
    }

}