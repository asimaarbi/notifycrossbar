package com.deskconn.notifycrossbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.Subscription;

public class MainActivity extends AppCompatActivity {

    private Session mSession;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) this.findViewById(R.id.mywidget);
        tv.setSelected(true);  // Set focus to the textview


        connect();
    }

    private void jabSubscribeHoJae(List<Object> params) {
        TextView textView = findViewById(R.id.tlabel);
        TextView textView1 = findViewById(R.id.tlabel1);
        textView.setText("pid: " + params.get(0));
        textView1.setText("date: " + params.get(1));
        tv.setText("pid: " + params.get(0)+"   date: "+ params.get(1));

    }

    private void connect() {
        Session ses = new Session();
        ses.addOnJoinListener((session, details) -> {
            mSession = session;
            CompletableFuture<Subscription> future = mSession.subscribe("com.flask_app.page_served", this::jabSubscribeHoJae);
            future.whenComplete((callResult, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                }
            });

            System.out.println("Joined session");
        });
        ses.addOnLeaveListener((session, details) -> {
            mSession = null;
        });

        Client client = new Client(ses, "ws://192.168.100.149:8080/ws", "realm1");
        client.connect().whenComplete((exitInfo, throwable) -> {
            System.out.println("Disconnected completely!");
        });
    }
}
