package com.av.ringtone.logic.ringtone;

import java.io.File;
import java.util.List;

import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.logic.MainActivity;
import com.av.ringtone.logic.MediaListener;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.utils.FileUtils;
import com.av.ringtone.utils.NavigationUtils;
import com.av.ringtone.utils.ShareUtils;
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

import static com.av.ringtone.Constants.FILE_KIND_ALARM;
import static com.av.ringtone.Constants.FILE_KIND_MUSIC;
import static com.av.ringtone.Constants.FILE_KIND_NOTIFICATION;

/**
 * Created by LiJiaZhi on 16/12/19.
 */

public class CuttersAdapter extends RecyclerView.Adapter<CuttersAdapter.ItemHolder> {

    private List<CutterModel> mDatas;
    private BaseActivity mContext;
    private MediaListener mListener;

    private CutterModel currentPlayItem = null;

    public CuttersAdapter(MainActivity context, List<CutterModel> list) {
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
        final CutterModel localItem = mDatas.get(i);
        if (localItem.playStatus == 0) {
            if (localItem.type == FILE_KIND_MUSIC) {
                itemHolder.type.setImageResource(R.drawable.ic_music);
            } else if (localItem.type == FILE_KIND_ALARM) {
                itemHolder.type.setImageResource(R.drawable.ic_alarm);
            } else if (localItem.type == FILE_KIND_NOTIFICATION) {
                itemHolder.type.setImageResource(R.drawable.icon_notifications);
            } else {
                itemHolder.type.setImageResource(R.drawable.ic_phone);
            }
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
                CutterModel local = (CutterModel) v.getTag();
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
                final CutterModel tempModel = (CutterModel) v.getTag();
                PopupMenu menu = new PopupMenu(mContext, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final Uri newUri = Uri.fromFile(new File(tempModel.path));
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                SongModel model =
                                    new SongModel(tempModel.title, tempModel.path, tempModel.duration, tempModel.date);
                                NavigationUtils.goToCutter(mContext, model);
                                break;
                            case R.id.menu_default:
                                CommonDialog dialog =
                                        new CommonDialog(mContext, mContext.getString(R.string.set_ringtone_title),mContext.getString(R.string.set_ringtone_content),"",new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE,
                                                        newUri);
                                                ToastUtils.makeToastAndShowLong(mContext, mContext.getString(R.string.set_ringtone_success));
                                            }
                                        });
                                dialog.setCancelable(true);
                                dialog.show();
                                break;
                            case R.id.menu_notification:
                                CommonDialog notidialog =
                                        new CommonDialog(mContext, mContext.getString(R.string.set_notification_title), mContext.getString(R.string.set_notification_content),"",new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_NOTIFICATION,
                                                        newUri);
                                                ToastUtils.makeToastAndShowLong(mContext, mContext.getString(R.string.set_notification_success));
                                            }
                                        });
                                notidialog.setCancelable(true);
                                notidialog.show();
                                break;
                            case R.id.menu_alarm:
                                CommonDialog alarmdialog =
                                        new CommonDialog(mContext, mContext.getString(R.string.set_alarm_title), mContext.getString(R.string.set_alarm_content),"",new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_ALARM,
                                                        newUri);
                                                ToastUtils.makeToastAndShowLong(mContext, mContext.getString(R.string.set_alarm_success));
                                            }
                                        });
                                alarmdialog.setCancelable(true);
                                alarmdialog.show();
                                break;
                            case R.id.menu_delete:
                                CommonDialog deldialog =
                                        new CommonDialog(mContext, mContext.getString(R.string.delete_title), mContext.getString(R.string.delete_content),"Delete",new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (tempModel.playStatus == 1 && null != mListener) {
                                                    mListener.stop();
                                                }
                                                File file = new File(tempModel.path);
                                                if (file.exists()) {
                                                    file.delete();
                                                    String params[] = new String[] { file.getPath() };
                                                    mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                                            MediaStore.Images.Media.DATA + " LIKE ?", params);
                                                }
                                                mDatas.remove(tempModel);
                                                notifyDataSetChanged();
                                                UserDatas.getInstance().setCuttereds(mDatas);
                                                ToastUtils.makeToastAndShowLong(mContext,
                                                        mContext.getString(R.string.delete_success));
                                            }
                                        });
                                deldialog.setCancelable(false);
                                deldialog.show();
                                break;
                            case R.id.menu_share:
                                ShareUtils.shareFile(mContext, Uri.fromFile(new File(tempModel.path)));
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_ringtone);
                menu.show();
            }
        });
    }

    public void upateDatas(List<CutterModel> list) {
        mDatas = list;
        notifyDataSetChanged();
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

    public List<CutterModel> getDatas() {
        return mDatas;
    }
}