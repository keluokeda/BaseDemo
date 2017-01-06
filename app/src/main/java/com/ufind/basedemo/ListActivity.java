package com.ufind.basedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ListActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        EntityListFragment fragment= (EntityListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_list);
        if (fragment==null){
            fragment=new EntityListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.activity_list,fragment).commit();
        }
    }
}
