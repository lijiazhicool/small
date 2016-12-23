package com.example.test.logic.song;

import java.io.File;
import java.util.List;

import com.example.test.R;
import com.example.test.base.BaseActivity;
import com.example.test.model.SongModel;
import com.example.test.utils.NavigationUtils;
import com.example.test.utils.ToastUtils;

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
        itemHolder.artist.setText(getDuration(localItem.duration / 1000) + " " + localItem.artistName+" "+ localItem.path);
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
                        switch (item.getItemId()) {
                            case R.id.popup_song_edit:
                                NavigationUtils.goToCutter(mContext, mDatas.get(position));
                                break;
                            case R.id.popup_song_delete:
                                File file = new File(mDatas.get(position).path);
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