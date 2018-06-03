package com.example.huangjie.aidl3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private ServiceConnection connection;
    private IBookManager mBookManager;
    private IBookListener.Stub mListener;
    private MyHander mHandler = new MyHander(this);


    public static class MyHander extends Handler {
        private WeakReference<Context> reference;

        public MyHander(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (reference.get() != null) {
                Toast.makeText(reference.get(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        Intent intent = new Intent(this, BookService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }

    private void init() {
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                mBookManager = IBookManager.Stub.asInterface(service);
                try {
                    mBookManager.registerListener(mListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };


        mListener = new IBookListener.Stub() {
            @Override
            public void onBookChange(Book book) throws RemoteException {
                mHandler.obtainMessage(0x12, book).sendToTarget();
            }
        };
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBookManager != null && mBookManager.asBinder().isBinderAlive()) {
            try {
                mBookManager.unregisterListener(mListener);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
       // unbindService(connection);
    }
}
