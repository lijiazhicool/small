package com.example.test.logic.record;

import java.util.List;

import com.example.test.R;
import com.example.test.UserDatas;
import com.example.test.base.BaseActivity;
import com.example.test.model.RecordModel;
import com.example.test.model.SongModel;
import com.example.test.utils.NavigationUtils;

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

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ItemHolder> {

    private List<RecordModel> mDatas;
    private BaseActivity mContext;

    public RecordsAdapter(BaseActivity context, List<RecordModel> list) {
        this.mDatas = list;
        this.mContext = context;
    }

    public void setDatas(List<RecordModel> datas) {
        mDatas.clear();
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
        itemHolder.type.setImageResource(R.drawable.ic_record_blue);
        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(getDuration(localItem.duration) + " " + localItem.path);
        setOnPopupMenuListener(itemHolder, i);
    }

    private String getDuration(int d) {
        int min = d/60;
        float sec = (float)(d - 60 * min);
        return String.format("%d:%05.2f", min, sec);
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
                                SongModel model = new SongModel(mDatas.get(position).title,mDatas.get(position).path,mDatas.get(position).duration);
                                NavigationUtils.goToCutter(mContext, model);
                                break;
                            case R.id.popup_song_delete:
                                //只删除纪录，不删除文件
                                mDatas.remove(position);
                                UserDatas.getInstance().setRecords(mDatas);
                                notifyItemRemoved(position);
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

    public List<RecordModel> getDatas() {
        return mDatas;
    }
}