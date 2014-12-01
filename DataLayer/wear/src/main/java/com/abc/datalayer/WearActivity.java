package com.abc.datalayer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.view.WatchViewStub;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

public class WearActivity extends Activity
            implements GoogleApiClient.ConnectionCallbacks,
                        GoogleApiClient.OnConnectionFailedListener{

    EditText console;
    // GoogleApiClient 객체
    GoogleApiClient gClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // EditText 의 참조값 얻어오기
                console = (EditText)findViewById(R.id.console);
            }
        });
        // GoogleApiClient 객체의 참조값을 얻어와서 멤버필드에 저장하기
        gClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gClient.connect();
    }

    @Override
    protected void onStop() {
        gClient.disconnect();
        super.onStop();
    }

    // 로그 찍는 메소드
    public void printLog(String msg){
        // 출력할 문자열을 메세지 객체에 담는다.
        Message m = new Message();
        m.obj = msg;
    }

    // 핸들러 객체 정의하기
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // 전달된 문자열을 얻어온다.
            String str = (String)msg.obj;
            // EditText 에 개행기호와 함께 출력하기
            console.append(str + "\n");
        }
    };

    // GoogleApiClient 에 연결 되었을대 호출되는 메소드
    @Override
    public void onConnected(Bundle bundle) {
        printLog("onConnected()");
    }

    @Override
    public void onConnectionSuspended(int i) {
        printLog("onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        printLog("onConnectionFailed()");
    }
}
