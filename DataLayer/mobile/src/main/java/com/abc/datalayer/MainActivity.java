package com.abc.datalayer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;


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

        // 비동기 작업데 전달할 파라미터를 배열에 담는다.
        String[] params = new String[1];
        params[0] = msg;
        // 파라미터를 전달하면서 비동기 작업 실행 시키기
        new SendMessageTask().execute(params);
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

    // 비동기 작업 객체를 생성할 클래스
    class SendMessageTask extends AsyncTask<String, Void, Void>{
        // UI 스레드가 아닌 다른 스레드에서 작업되는 메소드
        @Override
        protected Void doInBackground(String... params) {
            // 전송할 문자열을 얻어온다.
            String msg = params[0];
            // 연결된 node id 를 얻어온다.
            Collection<String> nodes = getNodes();
            // 반복문 돌면서 모든 node 에 전송한다.
            for(final String node:nodes){
                // 콘솔에 node id 를 출력해보기
                printLog(node);
                // MessageApi 를 이용해서 전송한다.
                Wearable.MessageApi.sendMessage(  // (GoogleApiClient, node id, msg , byte[] )
                        gClient,
                        node,
                        msg,
                        new byte[0]
                ).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if(sendMessageResult.getStatus().isSuccess()) {
                            printLog("전송 성공!");
                        } else {
                            printLog("전송 실패!");
                        }
                    }
                });
            }
            return null;
        }
    }

    // 연결된 Node List 를 리턴하는 메소드
    public Collection<String> getNodes(){
        HashSet<String> results = new HashSet<String>();
        // 연결된 Node List 를 읽어온다.
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(gClient).await();
        // 반복문을 돌면서 연결된 node 의 id 를 HashSet 에 담는다.
        for(Node node : nodes.getNodes()){ // Node : com.google.android.gms.wearable
            results.add(node.getId());
        }
        // 연결된 node 의 아이디 값을 담고 있는 HashSet 객체를 리턴해주기
        return results;
    }
}
