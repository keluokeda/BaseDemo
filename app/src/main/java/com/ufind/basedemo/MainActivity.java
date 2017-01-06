package com.ufind.basedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void lazylist(View view) {
    }

    public void list(View view) {
        startActivity(new Intent(this,ListActivity.class));
    }

    public void lazydata(View view) {
        startActivity(new Intent(this,LazyDataActivity.class));
    }

    public void data(View view) {
        startActivity(new Intent(this,DataActivity.class));
    }
}
