package com.abc.datalayer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends Activity
                implements GoogleApiClient.ConnectionCallbacks,
                            GoogleApiClient.OnConnectionFailedListener {

    // 필요한 멤버필드 정의하기
    EditText console, inputText;
    // GoogleApiClient 객체를 멤버필드에 담기위해
    GoogleApiClient gClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // EditText 의 참조값을 얻어와서 멤버필드에 저장하기
        console = (EditText)findViewById(R.id.console);
        inputText = (EditText)findViewById(R.id.inputText);
        // GoogleApiClient 객체을 생성해서 멤버필드에 담기
        gClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // GoogleApiClient 에 연결 요청을 한다.
        gClient.connect();
    }

    @Override
    protected void onStop() {
        //GoogleApiClient 에 연결 해제 한다.
        gClient.disconnect();
        super.onStop();
    }

    // 전송 버튼을 눌렀을 때 호출되는 메소드
    public void send(View v){
        // 입력한 문자열을 읽어온다.
        String msg = inputText.getText().toString();

        if(msg.equals(""))
            return;
    }

    // 로그 찍는 메소드
    public void printLog(String msg){
        // 출력할 문자열을 메세지 객체에 담는다.
        Message m = new Message();
        m.obj = msg;
        // 핸들러 메세지를 보낸다.
        handler.sendMessage(m);
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

    // GoogleApiClient 에 연결이 되었을때 호출되는 메소드
    @Override
    public void onConnected(Bundle bundle) {
        printLog("onConnected()");
    }

    // GoogleApiClient 에 연결이 연기 혹은 지연되었을때 호출되는 메소드
    @Override
    public void onConnectionSuspended(int i) {
        printLog("onConnectionSuspended()");
    }

    // GoogleApiClient 에 연결이 실패되었을때 호출되는 메소드
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        printLog("onConnectionFailed()");
    }
}
