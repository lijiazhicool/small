package com.example.test.logic.ringtone;

import java.io.File;
import java.util.List;

import com.example.test.R;
import com.example.test.base.BaseActivity;
import com.example.test.model.CutterModel;
import com.example.test.model.SongModel;
import com.example.test.utils.NavigationUtils;
import com.example.test.utils.ShareUtils;
import com.example.test.utils.ToastUtils;

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
import android.widget.TextView;

import static com.example.test.Constants.FILE_KIND_ALARM;
import static com.example.test.Constants.FILE_KIND_MUSIC;
import static com.example.test.Constants.FILE_KIND_NOTIFICATION;

/**
 * Created by LiJiaZhi on 16/12/19.
 */

public class CuttersAdapter extends RecyclerView.Adapter<CuttersAdapter.ItemHolder> {

    private List<CutterModel> mDatas;
    private BaseActivity mContext;

    public CuttersAdapter(BaseActivity context, List<CutterModel> list) {
        this.mDatas = list;
        this.mContext = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, null);
        ItemHolder ml = new ItemHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        CutterModel localItem = mDatas.get(i);
        if (localItem.type == FILE_KIND_MUSIC) {
            itemHolder.type.setImageResource(R.drawable.ic_music);
        } else if (localItem.type == FILE_KIND_ALARM) {
            itemHolder.type.setImageResource(R.drawable.ic_alarm);
        } else if (localItem.type == FILE_KIND_NOTIFICATION) {
            itemHolder.type.setImageResource(R.drawable.icon_notifications);
        } else {
            itemHolder.type.setImageResource(R.drawable.ic_phone);
        }

        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(getDuration(localItem.duration) + " " + localItem.artist + " " + localItem.path);
        setOnPopupMenuListener(itemHolder, i);
    }

    private String getDuration(int d) {
        return d / 60 + ":" + d % 60;
    }

    @Override
    public int getItemCount() {
        return (null != mDatas ? mDatas.size() : 0);
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu menu = new PopupMenu(mContext, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Uri newUri = Uri.fromFile(new File(mDatas.get(position).path));
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                SongModel model = new SongModel(mDatas.get(position).title, mDatas.get(position).path,
                                    mDatas.get(position).duration);
                                NavigationUtils.goToCutter(mContext, model);
                                break;
                            case R.id.menu_default:
                                RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE,
                                    newUri);
                                break;
                            case R.id.menu_notification:
                                RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_NOTIFICATION,
                                    newUri);
                                break;
                            case R.id.menu_alarm:
                                RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_ALARM,
                                    newUri);
                                break;
                            case R.id.menu_delete:
                                File file = new File(mDatas.get(position).path);
                                if (file.exists()) {
                                    if (file.delete()) {
                                        ToastUtils.makeToastAndShow(mContext,
                                            file.getPath() + mContext.getString(R.string.delete_success));
                                    } else {
                                        ToastUtils.makeToastAndShow(mContext,
                                            file.getPath() + mContext.getString(R.string.delete_failed));
                                    }
                                    String params[] = new String[] { file.getPath() };
                                    mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                        MediaStore.Images.Media.DATA + " LIKE ?", params);
                                    mDatas.remove(position);
                                    notifyItemRemoved(position);
                                }
                                break;
                            case R.id.menu_share:
                                ShareUtils.shareFile(mContext,newUri);
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

    public class ItemHolder extends RecyclerView.ViewHolder {
        protected TextView title, artist;
        protected ImageView type, popupMenu;

        public ItemHolder(View view) {
            super(view);

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