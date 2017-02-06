package com.av.ringtone.logic.ringtone;

import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseFragment;
import com.av.ringtone.logic.MainActivity;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.RecordModel;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.utils.FileUtils;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.av.ringtone.Constants.FILE_KIND_ALARM;
import static com.av.ringtone.Constants.FILE_KIND_MUSIC;
import static com.av.ringtone.Constants.FILE_KIND_NOTIFICATION;
import static com.av.ringtone.Constants.FILE_KIND_RINGTONE;

/**
 * cuttered
 */
public class CutteredFragment extends BaseFragment implements UserDatas.DataChangedListener {
    private RecyclerView mRecyclerView;
    private CuttersAdapter mAdapter;
    private TextView mEmptyTv;
    private TextView mPathTv;
    private LinearLayout mOpenFilell;

    private boolean mSortReverseByName = true;
    private boolean mSortReverseByLength = true;
    private boolean mSortReverseByDate = true;

    private boolean mIsInit = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cuttered;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        mEmptyTv = findViewById(R.id.empty_tv);
        mPathTv = findViewById(R.id.path_tv);
        mOpenFilell = findViewById(R.id.openll);

        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void initData() {
        List<CutterModel> list = UserDatas.getInstance().getCuttereds();
        if (list.size()==0){
            //load from sdcard---ringtone
            String ringtonePath = FileUtils.getRingtonePath(mActivity);
            File ringtonefile = new File(ringtonePath);
            File[] ringtonesubFile = ringtonefile.listFiles();
            for (int iFileLength = 0; iFileLength < ringtonesubFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!ringtonesubFile[iFileLength].isDirectory()) {
                    File temp = ringtonesubFile[iFileLength];
                    String fileName = temp.getName();
                    String artist = "" + getResources().getText(R.string.artist_name);

                    CutterModel tempModel = new CutterModel(FILE_KIND_RINGTONE,fileName, temp.getAbsolutePath(), artist, FileUtils.getAudioLength(getActivity(),temp), temp.length(),temp.lastModified());
                    UserDatas.getInstance().addCuttereds(tempModel);
                }
            }

            //load from sdcard---music
            String musicpath = FileUtils.getMusicPath(mActivity);
            File musicfile = new File(musicpath);
            File[] musicsubFile = musicfile.listFiles();
            for (int iFileLength = 0; iFileLength < musicsubFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!musicsubFile[iFileLength].isDirectory()) {
                    File temp = musicsubFile[iFileLength];
                    String fileName = temp.getName();
                    String artist = "" + getResources().getText(R.string.artist_name);

                    CutterModel tempModel = new CutterModel(FILE_KIND_MUSIC,fileName, temp.getAbsolutePath(), artist, FileUtils.getAudioLength(getActivity(),temp), temp.length(),new File(fileName).lastModified());
                    UserDatas.getInstance().addCuttereds(tempModel);
                }
            }

            //load from sdcard---notification
            String notipath = FileUtils.getNotificationPath(mActivity);
            File notifile = new File(notipath);
            File[] notisubFile = notifile.listFiles();
            for (int iFileLength = 0; iFileLength < notisubFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!notisubFile[iFileLength].isDirectory()) {
                    File temp = notisubFile[iFileLength];
                    String fileName = temp.getName();
                    String artist = "" + getResources().getText(R.string.artist_name);

                    CutterModel tempModel = new CutterModel(FILE_KIND_NOTIFICATION,fileName, temp.getAbsolutePath(), artist, FileUtils.getAudioLength(getActivity(),temp), temp.length(),new File(fileName).lastModified());
                    UserDatas.getInstance().addCuttereds(tempModel);
                }
            }
            //load from sdcard---notification
            String alarmpath = FileUtils.getAlarmPath(mActivity);
            File alarmfile = new File(alarmpath);
            File[] alarmsubFile = alarmfile.listFiles();
            for (int iFileLength = 0; iFileLength < alarmsubFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!alarmsubFile[iFileLength].isDirectory()) {
                    File temp = alarmsubFile[iFileLength];
                    String fileName = temp.getName();
                    String artist = "" + getResources().getText(R.string.artist_name);

                    CutterModel tempModel = new CutterModel(FILE_KIND_ALARM,fileName, temp.getAbsolutePath(), artist, FileUtils.getAudioLength(getActivity(),temp), temp.length(),new File(fileName).lastModified());
                    UserDatas.getInstance().addCuttereds(tempModel);
                }
            }
        }
        mPathTv.setText(FileUtils.getAppDir_show());
        mIsInit = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && mIsInit) {
            UserDatas.getInstance().resetCutteds();
            ((MainActivity)getActivity()).stop();
        }
    }

    @Override
    protected void initListener() {
        mOpenFilell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssignFolder(FileUtils.getAppDir(getActivity()));
            }
        });
    }

    //使用文件管理器打开指定文件夹
    private void openAssignFolder(String path){
        File file = new File(path);
        if(null==file || !file.exists()){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "file/*");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        UserDatas.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        UserDatas.getInstance().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void updateSongs(List<SongModel> list) {

    }

    @Override
    public void updateRecords(List<RecordModel> list) {

    }

    @Override
    public void updatePlayStatus(int mainType) {
        if (mainType != 3){
            return;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateCutters(List<CutterModel> list) {
        //时间倒序
        Collections.sort(list, new Comparator<CutterModel>(){
            public int compare(CutterModel o1, CutterModel o2) {
                if(o1.date < o2.date){
                    return 1;
                }
                if(o1.date == o2.date){
                    return 0;
                }
                return -1;
            }
        });

        mAdapter = new CuttersAdapter((MainActivity) getActivity(), list);
        if (mAdapter.getDatas().size() == 0) {
            mEmptyTv.setVisibility(View.VISIBLE);
        } else {
            mEmptyTv.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
    @Override
    public void sortByName(int sortType, boolean isNeedRevers) {
        if (sortType!= UserDatas.SORT_CUT){
            return;
        }
        List<CutterModel> list = mAdapter.getDatas();
        if (mSortReverseByName){
            Collections.sort(list, new Comparator<CutterModel>(){

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和o2相等，
                 * 返回正数表示：o1大于o2。
                 */
                public int compare(CutterModel o1, CutterModel o2) {
                    if(o1.title.compareTo(o2.title)<0){
                        return 1;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<CutterModel>(){

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和o2相等，
                 * 返回正数表示：o1大于o2。
                 */
                public int compare(CutterModel o1, CutterModel o2) {
                    if(o1.title.compareTo(o2.title)>0){
                        return 1;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        if (isNeedRevers) {
            mSortReverseByName = !mSortReverseByName;
        }
    }

    @Override
    public void sortByLength(int sortType, boolean isNeedRevers) {
        if (sortType!= UserDatas.SORT_CUT){
            return;
        }
        List<CutterModel> list = mAdapter.getDatas();

        if (mSortReverseByLength){
            Collections.sort(list, new Comparator<CutterModel>(){
                public int compare(CutterModel o1, CutterModel o2) {
                    if(o1.duration < o2.duration){
                        return 1;
                    }
                    if(o1.duration == o2.duration){
                        return 0;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<CutterModel>(){
                public int compare(CutterModel o1, CutterModel o2) {
                    if(o1.duration > o2.duration){
                        return 1;
                    }
                    if(o1.duration == o2.duration){
                        return 0;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        if (isNeedRevers) {
            mSortReverseByLength = !mSortReverseByLength;
        }
    }

    @Override
    public void sortByDate(int sortType, boolean isNeedRevers) {
        if (sortType!= UserDatas.SORT_CUT){
            return;
        }
        List<CutterModel> list = mAdapter.getDatas();
        if (mSortReverseByDate){
            Collections.sort(list, new Comparator<CutterModel>(){
                public int compare(CutterModel o1, CutterModel o2) {
                    if(o1.date < o2.date){
                        return 1;
                    }
                    if(o1.date == o2.date){
                        return 0;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<CutterModel>(){
                public int compare(CutterModel o1, CutterModel o2) {
                    //按照学生的年龄进行倒序排列
                    if(o1.date > o2.date){
                        return 1;
                    }
                    if(o1.date == o2.date){
                        return 0;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        if (isNeedRevers) {
            mSortReverseByDate = !mSortReverseByDate;
        }
    }
}
