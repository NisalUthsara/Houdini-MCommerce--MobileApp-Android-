package lk.javainstitute.houdini;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiverMsg broadcastReceiverMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Houdini_Fullscreen);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Intent intent2 = new Intent(MainActivity.this, SignInActivity.class);
                 startActivity(intent2);
            }
        });

//        IntentFilter intentFilter = new IntentFilter("android.intent.action.BATTERY_LOW");
//        broadcastReceiverMsg = new BroadcastReceiverMsg();
//        registerReceiver(broadcastReceiverMsg,intentFilter);
    }
}