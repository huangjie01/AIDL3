package com.example.huangjie.aidl3;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangjie on 2018/6/3.
 */

public class BookService extends Service {
    private RemoteCallbackList<IBookListener> mListenerList;
    private static final String TAG = "BookService";
    private ArrayList<Book> mBookList;
    private boolean destory;


    @Override
    public void onCreate() {
        super.onCreate();
        mListenerList = new RemoteCallbackList<>();
        mBookList = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (!destory) {
                    try {
                        Thread.sleep(6000);
                        String bookId = "id:" + mBookList.size() + 1;
                        String bookName = "书名" + bookId;
                        Book book = new Book(bookId, bookName);
                        mBookList.add(book);
                        onNewBookArrived(book);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 当新书到达
     *
     * @param book
     */
    public void onNewBookArrived(Book book) {

        final int count = mListenerList.beginBroadcast();
        for (int i = 0; i < count; i++) {
            IBookListener broadcastItem = mListenerList.getBroadcastItem(i);
            if (broadcastItem != null) {
                try {
                    broadcastItem.onBookChange(book);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mListenerList.finishBroadcast();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBookBinder;
    }

    private IBinder mBookBinder = new IBookManager.Stub() {
        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
            onNewBookArrived(book);
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void registerListener(IBookListener listener) throws RemoteException {
            mListenerList.register(listener);
            Log.e(TAG, "监听器注册成功");
        }

        @Override
        public void unregisterListener(IBookListener listener) throws RemoteException {

            mListenerList.unregister(listener);

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        destory = true;
    }
}
