package com.av.ringtone.logic.ringtone;

import static com.av.ringtone.Constants.FILE_KIND_ALARM;
import static com.av.ringtone.Constants.FILE_KIND_MUSIC;
import static com.av.ringtone.Constants.FILE_KIND_NOTIFICATION;
import static com.av.ringtone.Constants.FILE_KIND_RINGTONE;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.av.ringtone.App;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseFragment;
import com.av.ringtone.logic.MainActivity;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.RecordModel;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.model.VoiceModel;
import com.av.ringtone.utils.FileUtils;
import com.example.ad.ADManager;
import com.facebook.ads.NativeAd;
import com.music.ringtonemaker.ringtone.cutter.maker.R;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * cuttered
 */
public class CutteredFragment extends BaseFragment implements UserDatas.DataChangedListener {
    private RecyclerView mRecyclerView;
    private CuttersAdapter mAdapter;
    private LinearLayout mEmptyll;
    private TextView mPathTv;
    private LinearLayout mOpenFilell;

    private LinearLayout mAdll;

    private boolean mSortReverseByName = true;
    private boolean mSortReverseByDate = true;

    private boolean mIsInit = false;
    private List<CutterModel> mDataLists;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cuttered;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        mEmptyll = findViewById(R.id.empty_ll);
        mPathTv = findViewById(R.id.path_tv);
        mOpenFilell = findViewById(R.id.openll);
        mAdll = findViewById(R.id.ad_ll);

        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void initData() {
        List<CutterModel> list = UserDatas.getInstance().getCuttereds();
        if (list.size() == 0
            && EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // load from sdcard---ringtone
            String ringtonePath = FileUtils.getRingtonePath(mActivity);
            File ringtonefile = new File(ringtonePath);
            File[] ringtonesubFile = ringtonefile.listFiles();
            for (int iFileLength = 0; iFileLength < ringtonesubFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!ringtonesubFile[iFileLength].isDirectory()) {
                    File temp = ringtonesubFile[iFileLength];
                    String fileName = temp.getName();
                    String artist = "" + getResources().getText(R.string.artist_name);

                    CutterModel tempModel = new CutterModel(FILE_KIND_RINGTONE, fileName, temp.getAbsolutePath(),
                        artist, FileUtils.getAudioLength(getActivity(), temp), temp.length(), temp.lastModified());
                    UserDatas.getInstance().addCuttereds(tempModel);
                }
            }

            // load from sdcard---music
            String musicpath = FileUtils.getMusicPath(mActivity);
            File musicfile = new File(musicpath);
            File[] musicsubFile = musicfile.listFiles();
            for (int iFileLength = 0; iFileLength < musicsubFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!musicsubFile[iFileLength].isDirectory()) {
                    File temp = musicsubFile[iFileLength];
                    String fileName = temp.getName();
                    String artist = "" + getResources().getText(R.string.artist_name);

                    CutterModel tempModel = new CutterModel(FILE_KIND_MUSIC, fileName, temp.getAbsolutePath(), artist,
                        FileUtils.getAudioLength(getActivity(), temp), temp.length(),
                        new File(fileName).lastModified());
                    UserDatas.getInstance().addCuttereds(tempModel);
                }
            }

            // load from sdcard---notification
            String notipath = FileUtils.getNotificationPath(mActivity);
            File notifile = new File(notipath);
            File[] notisubFile = notifile.listFiles();
            for (int iFileLength = 0; iFileLength < notisubFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!notisubFile[iFileLength].isDirectory()) {
                    File temp = notisubFile[iFileLength];
                    String fileName = temp.getName();
                    String artist = "" + getResources().getText(R.string.artist_name);

                    CutterModel tempModel = new CutterModel(FILE_KIND_NOTIFICATION, fileName, temp.getAbsolutePath(),
                        artist, FileUtils.getAudioLength(getActivity(), temp), temp.length(),
                        new File(fileName).lastModified());
                    UserDatas.getInstance().addCuttereds(tempModel);
                }
            }
            // load from sdcard---notification
            String alarmpath = FileUtils.getAlarmPath(mActivity);
            File alarmfile = new File(alarmpath);
            File[] alarmsubFile = alarmfile.listFiles();
            for (int iFileLength = 0; iFileLength < alarmsubFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!alarmsubFile[iFileLength].isDirectory()) {
                    File temp = alarmsubFile[iFileLength];
                    String fileName = temp.getName();
                    String artist = "" + getResources().getText(R.string.artist_name);

                    CutterModel tempModel = new CutterModel(FILE_KIND_ALARM, fileName, temp.getAbsolutePath(), artist,
                        FileUtils.getAudioLength(getActivity(), temp), temp.length(),
                        new File(fileName).lastModified());
                    UserDatas.getInstance().addCuttereds(tempModel);
                }
            }
        }
        mPathTv.setText(FileUtils.getAppDir_show());
        mIsInit = true;
        ADManager.getInstance().getNativeAdlist(new ADManager.ADNumListener() {
            @Override
            public void onLoadedSuccess(List<NativeAd> list, boolean needGif) {
                if (list.size() > 0 && null != mAdapter) {
                    mAdapter.upateDatas(mDataLists);
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && mIsInit) {
            UserDatas.getInstance().resetCutteds();
            ((MainActivity) getActivity()).stop();
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

    // 使用文件管理器打开指定文件夹
    private void openAssignFolder(String path) {
        File file = new File(path);
        if (null == file || !file.exists()) {
            return;
        }
//        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.music.ringtonemaker.ringtone.cutter.maker.provider", file);
                intent.setDataAndType(uri, "file/*");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "file/*");
            }
            startActivity(intent);
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
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
        if (mIsInit) {
            UserDatas.getInstance().resetCutteds();
            ((MainActivity) getActivity()).stop();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1005:
                if (data == null) {
                    return;
                }
                // 处理返回的data,获取选择的联系人信息
                Uri uri = data.getData();
                // 因为手机的联系人和手机号并不再同一个数据库中，所以我们需要分别做处理
                String[] contacts = getPhoneContacts(uri);
                int kk = 0;
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    public void updatePlayStatus(VoiceModel model) {
        if (model.catorytype != 3) {
            return;
        }
        mAdapter.updatePlayStatus(model);
    }

    @Override
    public void resetPlayStatus(int catorytype) {
        if (catorytype != 3) {
            return;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateCutters(List<CutterModel> list) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        // 时间倒序
        Collections.sort(list, new Comparator<CutterModel>() {
            public int compare(CutterModel o1, CutterModel o2) {
                if (o1.date < o2.date) {
                    return 1;
                }
                if (o1.date == o2.date) {
                    return 0;
                }
                return -1;
            }
        });
        mDataLists = list;

        mAdapter = new CuttersAdapter((MainActivity) getActivity(), list);
        if (mAdapter.getDatas().size() == 0) {
            mEmptyll.setVisibility(View.VISIBLE);
        } else {
            mEmptyll.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void sortByName(int sortType, boolean isNeedRevers) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (sortType != UserDatas.SORT_CUT) {
            return;
        }
        List<CutterModel> list = mAdapter.getDatas();
        if (mSortReverseByName) {
            Collections.sort(list, new Comparator<CutterModel>() {

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
                 */
                public int compare(CutterModel o1, CutterModel o2) {
                    if (o1.title.compareTo(o2.title) < 0) {
                        return 1;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<CutterModel>() {

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
                 */
                public int compare(CutterModel o1, CutterModel o2) {
                    if (o1.title.compareTo(o2.title) > 0) {
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
    public void sortByDate(int sortType, boolean isNeedRevers) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (sortType != UserDatas.SORT_CUT) {
            return;
        }
        List<CutterModel> list = mAdapter.getDatas();
        if (mSortReverseByDate) {
            Collections.sort(list, new Comparator<CutterModel>() {
                public int compare(CutterModel o1, CutterModel o2) {
                    if (o1.date < o2.date) {
                        return 1;
                    }
                    if (o1.date == o2.date) {
                        return 0;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<CutterModel>() {
                public int compare(CutterModel o1, CutterModel o2) {
                    // 按照学生的年龄进行倒序排列
                    if (o1.date > o2.date) {
                        return 1;
                    }
                    if (o1.date == o2.date) {
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

    @Override
    public void sortByTrack(int sortType, boolean isNeedRevers) {

    }

    @Override
    public void sortByArtist(int sortType, boolean isNeedRevers) {

    }

    @Override
    public void sortByAlbum(int sortType, boolean isNeedRevers) {

    }

    private String[] getPhoneContacts(Uri uri) {
        String[] contact = new String[2];
        // 得到ContentResolver对象
        ContentResolver cr = getActivity().getContentResolver();
        // 取得电话本中开始一项的光标
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            // 取得联系人姓名
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);
            // 取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            if (phone != null) {
                phone.moveToFirst();
                contact[1] = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            phone.close();
            cursor.close();
        } else {
            return null;
        }
        return contact;
    }
}
