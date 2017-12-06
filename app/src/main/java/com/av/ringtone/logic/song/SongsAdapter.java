package com.av.ringtone.logic.song;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.logic.MainActivity;
import com.av.ringtone.logic.MediaListener;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.model.VoiceModel;
import com.av.ringtone.utils.FileUtils;
import com.av.ringtone.utils.NavigationUtils;
import com.av.ringtone.utils.ToastUtils;
import com.av.ringtone.views.CommonDialog;
import com.av.ringtone.views.MusicVisualizer;
import com.example.ad.ADManager;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.NativeAd;
import com.music.ringtonemaker.ringtone.cutter.maker.R;

import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LiJiaZhi on 16/12/19.
 */

public class SongsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == VoiceModel.AD) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ad, null);
            return new ADHolder(v);
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, null);
        ItemHolder ml = new ItemHolder(v);
        return ml;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDatas.get(position).catorytype == 4) {
            return VoiceModel.AD;
        }
        return VoiceModel.NORMAL;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof ADHolder) {
            ADHolder adHolder = (ADHolder) holder;
            NativeAd nativeAd = ADManager.getInstance().getNextAD();
            // Set the Text.
            adHolder.nativeAdTitle.setText(nativeAd.getAdTitle());
            // nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
            adHolder.nativeAdBody.setText(nativeAd.getAdBody());
            adHolder.nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

            // Download and display the ad_front icon.
            NativeAd.Image adIcon = nativeAd.getAdIcon();
            NativeAd.downloadAndDisplayImage(adIcon, adHolder.nativeAdIcon);
            AdChoicesView adChoicesView = new AdChoicesView(mContext, nativeAd, true);
            adHolder.adChoicesContainer.removeAllViews();
            adHolder.adChoicesContainer.addView(adChoicesView);

            // Register the Title and CTA button to listen for clicks.
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(adHolder.nativeAdTitle);
            clickableViews.add(adHolder.nativeAdCallToAction);
            nativeAd.registerViewForInteraction(adHolder.itemView, clickableViews);

        } else {
            ItemHolder itemHolder = (ItemHolder) holder;
            SongModel localItem = mDatas.get(i);
            Log.d("progress ", "onBindViewHolder " + localItem.title + " status " + localItem.playStatus);
            if (localItem.playStatus == 0) {
                itemHolder.type.setImageResource(R.drawable.ic_music_small);
                itemHolder.type.setVisibility(View.VISIBLE);
                itemHolder.musicVisualizer.setVisibility(View.GONE);
            } else {
                itemHolder.type.setImageResource(R.drawable.ic_music_small);
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
                    SongModel local = (SongModel) v.getTag();
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
                    final SongModel tempModel = (SongModel) v.getTag();
                    NavigationUtils.goToCutter(mContext, tempModel);
                }
            });
            itemHolder.progressBar.setProgress(localItem.progress);
        }
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
                                CommonDialog deldialog = new CommonDialog(mContext,
                                    mContext.getString(R.string.delete_title),
                                    mContext.getString(R.string.delete_content), "Delete", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            File file = new File(tempModel.path);
                                            if (file.exists()) {
                                                file.delete();
                                                String params[] = new String[] { file.getPath() };
                                                mContext.getContentResolver().delete(
                                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                                    MediaStore.Images.Media.DATA + " LIKE ?", params);
                                            }
                                            mDatas.remove(tempModel);
                                            notifyDataSetChanged();
                                            ToastUtils.makeToastAndShow(mContext,
                                                mContext.getString(R.string.delete_success));
                                        }
                                    });
                                deldialog.setCancelable(false);
                                deldialog.show();
                                break;
                            case R.id.popup_song_default:
                                CommonDialog dialog = new CommonDialog(mContext,
                                    mContext.getString(R.string.set_ringtone_title),
                                    mContext.getString(R.string.set_ringtone_content), "", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            RingtoneManager.setActualDefaultRingtoneUri(mContext,
                                                RingtoneManager.TYPE_RINGTONE, Uri.fromFile(new File(tempModel.path)));
                                            ToastUtils.makeToastAndShowLong(mContext,
                                                mContext.getString(R.string.set_ringtone_success));
                                        }
                                    });
                                dialog.setCancelable(true);
                                dialog.show();
                                break;
                            case R.id.popup_song_notification:
                                CommonDialog notidialog =
                                    new CommonDialog(mContext, mContext.getString(R.string.set_notification_title),
                                        mContext.getString(R.string.set_notification_content), "",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                RingtoneManager.setActualDefaultRingtoneUri(mContext,
                                                    RingtoneManager.TYPE_NOTIFICATION,
                                                    Uri.fromFile(new File(tempModel.path)));
                                                ToastUtils.makeToastAndShowLong(mContext,
                                                    mContext.getString(R.string.set_notification_success));
                                            }
                                        });
                                notidialog.setCancelable(true);
                                notidialog.show();
                                break;
                            case R.id.popup_song_alarm:
                                CommonDialog alarmdialog =
                                    new CommonDialog(mContext, mContext.getString(R.string.set_alarm_title),
                                        mContext.getString(R.string.set_alarm_content), "", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                RingtoneManager.setActualDefaultRingtoneUri(mContext,
                                                    RingtoneManager.TYPE_ALARM, Uri.fromFile(new File(tempModel.path)));
                                                ToastUtils.makeToastAndShowLong(mContext,
                                                    mContext.getString(R.string.set_alarm_success));
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
        private MusicVisualizer musicVisualizer;
        private LinearLayout typelayout;
        protected ProgressBar progressBar;

        public ItemHolder(View view) {
            super(view);
            this.rl = (RelativeLayout) view.findViewById(R.id.rl);
            this.type = (ImageView) view.findViewById(R.id.type_iv);
            this.typelayout = (LinearLayout) view.findViewById(R.id.type_ll);

            this.title = (TextView) view.findViewById(R.id.song_title);
            this.artist = (TextView) view.findViewById(R.id.song_detail);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu);
            this.musicVisualizer = (MusicVisualizer) view.findViewById(R.id.musicanimate);
            this.progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

            musicVisualizer.setColor(view.getResources().getColor(R.color.colorAccent));
        }
    }

    public class ADHolder extends RecyclerView.ViewHolder {
        protected View itemView;
        protected CircleImageView nativeAdIcon;
        protected TextView nativeAdTitle;
        protected TextView nativeAdBody;
        protected Button nativeAdCallToAction;
        protected LinearLayout adChoicesContainer;

        public ADHolder(View view) {
            super(view);
            this.itemView = view;
            this.nativeAdIcon = (CircleImageView) view.findViewById(R.id.native_ad_icon);
            this.nativeAdTitle = (TextView) view.findViewById(R.id.native_ad_title);
            this.nativeAdBody = (TextView) view.findViewById(R.id.native_ad_body);
            this.nativeAdCallToAction = (Button) view.findViewById(R.id.native_ad_call_to_action);
            this.adChoicesContainer = (LinearLayout) view.findViewById(R.id.ad_choices_container);
        }
    }

    public List<SongModel> getDatas() {
        return mDatas;
    }

    public void upateDatas(List<SongModel> list) {
        mDatas = list;
        if (ADManager.getInstance().getFeeds().size()>0) {
            removeADs();
            addADs();
        }
        notifyDataSetChanged();
    }
    private void removeADs(){
        Iterator<SongModel> sListIterator = mDatas.iterator();
        while(sListIterator.hasNext()){
            SongModel e = sListIterator.next();
            if(e.catorytype == 4){
                sListIterator.remove();
            }
        }
    }

    private void addADs(){
        if (null == mDatas&& mDatas.size()==0){
            return;
        }
        int mStartIndex = getRandomIndex(mDatas.size() - 1);
        for (int i = mStartIndex;i<mDatas.size()-1;i+=5){
            SongModel item = mDatas.get(mStartIndex);
            SongModel temp = new SongModel(item.id, item.albumId, item.artistId, item.title, item.artist, item.albumName, item.duration, item.trackNumber,item.path,item.date);
            temp.catorytype = 4;
            mDatas.add(i,temp);
        }
    }

    private int getRandomIndex(int max) {
        Random random = new Random();
        int index = random.nextInt(5);
        return index < max ? index : max;
    }

    public void updatePlayStatus(VoiceModel model) {
        int index = mDatas.indexOf(model);
        Log.d("progress ", "index " + index + " title " + model.title + " status " + model.playStatus);
        if (model.playStatus == 0) {
            // 播放完成了
            if (index < mDatas.size() - 1) {
                if (mListener != null) {
                    currentPlayItem = mDatas.get(index + 1);
                    mListener.play(currentPlayItem);
                }
            }
        }
        notifyItemChanged(index);
    }
}