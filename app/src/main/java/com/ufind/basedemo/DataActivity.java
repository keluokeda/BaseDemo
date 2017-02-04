package com.ufind.basedemo;

import android.os.Bundle;

public class DataActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        TestDataFragment dataFragment= (TestDataFragment) getSupportFragmentManager().findFragmentById(R.id.activity_data);
        if (dataFragment==null){
            dataFragment=new TestDataFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.activity_data,dataFragment).commit();
        }
    }


}
