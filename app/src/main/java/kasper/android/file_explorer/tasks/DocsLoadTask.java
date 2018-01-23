package kasper.android.file_explorer.tasks;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

import kasper.android.file_explorer.core.Core;
import kasper.android.file_explorer.listeners.OnDocsLoadedListener;
import kasper.android.file_explorer.models.files.Doc;
import kasper.android.file_explorer.models.files.DocTypes;

/**
 * Created by keyhan1376 on 12/15/2017.
 */

public class DocsLoadTask extends AsyncTask<Void, Void, Void> {

    private String docType;
    private OnDocsLoadedListener callback;
    private ArrayList<Doc> docs;

    public DocsLoadTask(String docType, OnDocsLoadedListener callback) {
        this.docType = docType;
        this.callback = callback;
        this.docs = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if (docType.equals("photos")) {
            try {
                String PathOfImage;
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = Core.getInstance().getContentResolver().query(uri, projection, null, null, null);
                assert cursor != null;
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                while (cursor.moveToNext()) {
                    PathOfImage = cursor.getString(column_index_data);
                    if (new File(PathOfImage).exists()) {
                        Doc doc = new Doc(PathOfImage, -1, DocTypes.Photo);
                        docs.add(doc);
                    }
                }
                cursor.close();
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        else if (docType.equals("musics")) {
            try {
                String PathOfMusic;
                long AlbumId;
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.DATA};
                Cursor cursor = Core.getInstance().getContentResolver().query(uri, projection, null, null, null);
                assert cursor != null;
                int column_index_id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                while (cursor.moveToNext()) {
                    AlbumId = cursor.getLong(column_index_id);
                    PathOfMusic = cursor.getString(column_index_data);
                    if (new File(PathOfMusic).exists()) {
                        Doc doc = new Doc(PathOfMusic, AlbumId, DocTypes.Music);
                        docs.add(doc);
                    }
                }
                cursor.close();
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        else if (docType.equals("videos")) {
            try {
                String PathOfVideo;
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {MediaStore.Video.Media.DATA};
                Cursor cursor = Core.getInstance().getContentResolver().query(uri, projection, null, null, null);
                assert cursor != null;
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                while (cursor.moveToNext()) {
                    PathOfVideo = cursor.getString(column_index_data);
                    if (new File(PathOfVideo).exists()) {
                        Doc doc = new Doc(PathOfVideo, -1, DocTypes.Movie);
                        docs.add(doc);
                    }
                }
                cursor.close();
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        callback.docsLoaded(docs);

        //pagePathAV.setAddress(pagesPathsStack.lastElement());
        //emptySignLayout.setVisibility((docs.size() == 0) ? View.VISIBLE : View.GONE);
                /*if (itemsAdapter instanceof DocAdapter) {
                    ((DocAdapter) itemsAdapter).updateAdapter(docs);
                }
                else {
                    gridSpanCount = (int)(HWareUtils.getScreenSizeX() / (docItemWidth * HWareUtils.getScreenDensity()));
                    itemsRV.setLayoutManager(new GridLayoutManager(FileExplorerActivity.this, gridSpanCount, GridLayoutManager.VERTICAL, false));
                    if (itemsDecoration != null) {
                        itemsRV.removeItemDecoration(itemsDecoration);
                    }
                    itemsDecoration = new DocDecoration(gridSpanCount);
                    itemsRV.addItemDecoration(itemsDecoration);
                    itemsAdapter = new DocAdapter(docs, new OnAdapterRequestListener() {
                        @Override
                        public void scrollToTop() {
                            itemsRV.getLayoutManager().scrollToPosition(0);
                        }
                    });
                    itemsRV.setAdapter(itemsAdapter);
                }

                pageItemsCountTV.setText((docs.size()) + " items");

                itemsRV.scrollToPosition(currentItemsRVPose);*/
    }
}
