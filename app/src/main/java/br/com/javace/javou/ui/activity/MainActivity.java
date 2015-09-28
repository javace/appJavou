package br.com.javace.javou.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ActionMode;

import com.crashlytics.android.Crashlytics;

import br.com.javace.javou.R;
import br.com.javace.javou.ui.base.BaseActivity;
import br.com.javace.javou.ui.fragment.ParticipantFragment;
import br.com.javace.javou.util.Constant;
import br.com.javace.javou.util.Preference;
import br.liveo.searchliveo.SearchLiveo;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity{

    private ActionMode mAcitonMode;
    private ParticipantFragment mParticipantFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        setupInit(savedInstanceState);
    }

    private void setupInit(Bundle savedInstanceState){
        Preference preference = new Preference(this);
        if (preference.getString(Constant.FIRST_RUN) == null){
            startActivityForResult(new Intent(this, SynchronizationActivity.class), 0);
            preference.setString(Constant.FIRST_RUN, "javou");
        }else{
            if (savedInstanceState == null) {
                mParticipantFragment = ParticipantFragment.newInstance();
                getFragmentManager().beginTransaction().add(R.id.container, mParticipantFragment).commit();
            }
        }
    }

    public void showActionMode(String name){
        mAcitonMode = startSupportActionMode(mParticipantFragment.actionModeCallback);
        actionModeSetTitle(name);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorActionModePrimaryDark));
        }
    }

    public void actionModeSetTitle(String name){
        mAcitonMode.setTitle(name);
    }

    public void hideActionMode(){
        if (mAcitonMode != null){
            mAcitonMode.finish();
            mAcitonMode = null;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onBackPressed() {
        if (mAcitonMode != null) {
            hideActionMode();
        }else{
            super.onBackPressed();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 0:
                mParticipantFragment = ParticipantFragment.newInstance();
                getFragmentManager().beginTransaction().add(R.id.container, mParticipantFragment).commit();
                break;

            default:
                if (data != null) {
                    if (requestCode == SearchLiveo.REQUEST_CODE_SPEECH_INPUT) {
                        if (mParticipantFragment != null) {
                            mParticipantFragment.onActivityResult(requestCode, resultCode, data);
                        }
                    }
                }
                break;
        }
    }
}