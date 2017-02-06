package com.av.ringtone.logic.song;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.av.ringtone.model.SongModel;

import java.util.ArrayList;

/**
 * Created by LiJiaZhi on 16/12/19.
 */

public class SongLoader {
    public static ArrayList<SongModel> getAllSongs(Context context) {
        return getSongsForCursor(makeSongCursor(context, null, null));
    }

    public static ArrayList<SongModel> getSongsForCursor(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                long id = cursor.getLong(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String album = cursor.getString(3);
                int duration = cursor.getInt(4);
                int trackNumber = cursor.getInt(5);
                long artistId = cursor.getInt(6);
                long albumId = cursor.getLong(7);
                String path=cursor.getString(8);
                long date = cursor.getLong(9);

                arrayList.add(new SongModel(id, albumId, artistId, title, artist, album, duration/1000, trackNumber,path,date));
            } while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }

    public static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString) {
        return makeSongCursor(context, selection, paramArrayOfString, MediaStore.Audio.Media.DATA);
    }

    private static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString,
        String sortOrder) {
        String selectionStatement = "is_music=1 AND title != ''";
//        String selectionStatement = "is_music=1";

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = selectionStatement + " AND " + selection;
        }
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            new String[] { "_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id","_data","date_modified"},
            selectionStatement, paramArrayOfString, sortOrder);

    }
}
