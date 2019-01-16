package com.example.alexandru.masterdetail2.api;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient {
    private static final String SERVER_ADDR = "ws://192.168.43.76:1337";
    private static final String TAG = "WebSocket";

    public static WebSocket webSocket = null;

    public static synchronized void handleMessages(Consumer<String> receivedMessageConsumer) {
        if(webSocket == null) {
            Request request = new Request.Builder().url(SERVER_ADDR).build();

            WebSocketListener listener = new WebSocketListener() {
                private static final int NORMAL_CLOSURE_STATUS = 1000;

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    Log.i(TAG, "Receiving data : " + text);
                    receivedMessageConsumer.accept(text);
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    webSocket.close(NORMAL_CLOSURE_STATUS, null);
                    Log.i(TAG, "Closing : " + code + " / " + reason);
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                    Log.e(TAG, "Error : " + t.getMessage());
                }
            };
            webSocket = new OkHttpClient().newWebSocket(request, listener);
        }
        else {
            Log.e(TAG, "Already connected");
        }
    }

    public static synchronized void closeConnection() {
        if (webSocket != null) {
            webSocket.close(1000, "Bye!");
            webSocket = null;
        }
    }

}
