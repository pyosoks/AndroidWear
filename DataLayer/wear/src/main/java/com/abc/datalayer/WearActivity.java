package com.abc.datalayer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.view.WatchViewStub;
import android.widget.EditText;

public class WearActivity extends Activity {

    EditText console;

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
    }

    // 로그 찍는 메소드
    public void printLog(String msg){
        // 출력할 문자열을 메세지 객체에 담는다.
        Message m = new Message();
        m.obj = msg;
    }

    // 핸들러 객체 정의하기
    Handler handler = new Handler(Message msg){
        @Override
        public void handleMessage(Message msg) {
            // 전달된 문자열을 얻어온다.
            String str = (String)msg.obj;
            // EditText 에 개행기호와 함께 출력하기
            console.append(str + "\n");
        }
    }
}
