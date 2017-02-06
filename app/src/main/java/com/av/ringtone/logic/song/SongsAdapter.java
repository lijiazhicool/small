package com.av.ringtone.logic.song;

import java.io.File;
import java.util.List;

import com.av.ringtone.R;
import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.logic.MainActivity;
import com.av.ringtone.logic.MediaListener;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.RecordModel;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.utils.FileUtils;
import com.av.ringtone.utils.NavigationUtils;
import com.av.ringtone.utils.ToastUtils;
import com.av.ringtone.views.CommonDialog;

import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
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

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ItemHolder> {

    private List<SongModel> mDatas;
    private BaseActivity mContext;
    private MediaListener mListener;
    private SongModel currentPlayItem = null;

    public SongsAdapter(MainActivity context, List<SongModel> list) {
        this.mDatas = list;
        this.mContext = context;
        mListener = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, null);
        ItemHolder ml = new ItemHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int i) {

        SongModel localItem = mDatas.get(i);
        itemHolder.type.setImageResource(R.drawable.ic_music_small);
        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(getDuration(localItem.duration) + " | " + FileUtils.getFileDir(localItem.path));
        itemHolder.rl.setTag(localItem);
        itemHolder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SongModel tempModel = (SongModel) v.getTag();
                NavigationUtils.goToCutter(mContext, tempModel);
            }
        });
        setOnPopupMenuListener(itemHolder, i);

        if (localItem.playStatus == 0) {
            itemHolder.type.setImageResource(R.drawable.icon_record);
        } else if (localItem.playStatus == 1) {
            itemHolder.type.setImageResource(R.drawable.ic_pause);
        } else {
            itemHolder.type.setImageResource(R.drawable.icon_play);
        }

        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(getDuration(localItem.duration) + " | " + FileUtils.getFileDir(localItem.path));
        setOnPopupMenuListener(itemHolder, i);
        itemHolder.rl.setTag(localItem);
        itemHolder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SongModel local = (SongModel) v.getTag();
                if (mListener != null) {
                    if (local.playStatus == 0) {
                        // ---播放
                        mListener.play(local);
                        local.playStatus = 1;
                    } else if (local.playStatus == 1) {
                        // ---暂停
                        mListener.pause();
                        local.playStatus = 2;
                    } else {
                        // ---播放
                        mListener.play(local);
                        local.playStatus = 1;
                    }
                    if (currentPlayItem != null && local != currentPlayItem) {
                        currentPlayItem.playStatus = 0;
                    }
                    currentPlayItem = local;
                    notifyDataSetChanged();
                }
            }
        });

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
                final SongModel tempModel = (SongModel) v.getTag();
                final PopupMenu menu = new PopupMenu(mContext, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_song_edit:
                                NavigationUtils.goToCutter(mContext, tempModel);
                                break;
                            case R.id.popup_song_delete:
                                CommonDialog deldialog =
                                        new CommonDialog(mContext, mContext.getString(R.string.delete_title),mContext.getString(R.string.delete_content),"Delete",new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                File file = new File(tempModel.path);
                                                if (file.exists()) {
                                                    file.delete();
                                                    String params[] = new String[] { file.getPath() };
                                                    mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                                            MediaStore.Images.Media.DATA + " LIKE ?", params);
                                                }
                                                mDatas.remove(tempModel);
                                                notifyDataSetChanged();
                                                ToastUtils.makeToastAndShow(mContext,mContext.getString(R.string.delete_success));
                                            }
                                        });
                                deldialog.setCancelable(false);
                                deldialog.show();
                                break;
                            case R.id.popup_song_default:
                                CommonDialog dialog =
                                        new CommonDialog(mContext, mContext.getString(R.string.set_ringtone_title),mContext.getString(R.string.set_ringtone_content),"",new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE,
                                                        Uri.fromFile(new File(tempModel.path)));
                                                ToastUtils.makeToastAndShowLong(mContext, mContext.getString(R.string.set_ringtone_success));
                                            }
                                        });
                                dialog.setCancelable(true);
                                dialog.show();
                                break;
                            case R.id.popup_song_notification:
                                CommonDialog notidialog =
                                        new CommonDialog(mContext, mContext.getString(R.string.set_notification_title), mContext.getString(R.string.set_notification_content),"",new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_NOTIFICATION,
                                                        Uri.fromFile(new File(tempModel.path)));
                                                ToastUtils.makeToastAndShowLong(mContext, mContext.getString(R.string.set_notification_success));
                                            }
                                        });
                                notidialog.setCancelable(true);
                                notidialog.show();
                                break;
                            case R.id.popup_song_alarm:
                                CommonDialog alarmdialog =
                                        new CommonDialog(mContext, mContext.getString(R.string.set_alarm_title), mContext.getString(R.string.set_alarm_content),"",new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_ALARM,
                                                        Uri.fromFile(new File(tempModel.path)));
                                                ToastUtils.makeToastAndShowLong(mContext, mContext.getString(R.string.set_alarm_success));
                                            }
                                        });
                                alarmdialog.setCancelable(true);
                                alarmdialog.show();
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

    public List<SongModel> getDatas() {
        return mDatas;
    }

    public void upateDatas(List<SongModel> list) {
        mDatas = list;
        notifyDataSetChanged();
    }
}