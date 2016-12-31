package com.av.ringtone.logic.song;

import java.io.File;
import java.util.List;

import com.av.ringtone.R;
import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.utils.NavigationUtils;
import com.av.ringtone.utils.ToastUtils;

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

/**
 * Created by LiJiaZhi on 16/12/19.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ItemHolder> {

    private List<SongModel> mDatas;
    private BaseActivity mContext;

    public SongsAdapter(BaseActivity context, List<SongModel> list) {
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
        SongModel localItem = mDatas.get(i);
        itemHolder.type.setImageResource(R.drawable.ic_music_small);
        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(getDuration(localItem.duration / 1000) + " " + localItem.artist +" "+ localItem.path);
        setOnPopupMenuListener(itemHolder, i);
    }

    private String getDuration(int d) {
        int min = d/60;
        int sec = (int)(d - 60 * min);
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
                                File file = new File(tempModel.path);
                                if (file.exists()) {
                                    if (file.delete()) {
                                        ToastUtils.makeToastAndShow(mContext, file.getPath() + mContext.getString(R.string.delete_success));
                                    } else {
                                        ToastUtils.makeToastAndShow(mContext, file.getPath() + mContext.getString(R.string.delete_failed));
                                    }
                                    String params[] = new String[] { file.getPath() };
                                    mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                        MediaStore.Images.Media.DATA + " LIKE ?", params);
                                    mDatas.remove(position);
                                    notifyItemRemoved(position);
                                }
                                break;
                            case R.id.popup_song_default:
                                RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE,
                                        Uri.fromFile(new File(tempModel.path)));
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

    public List<SongModel> getDatas() {
        return mDatas;
    }
}