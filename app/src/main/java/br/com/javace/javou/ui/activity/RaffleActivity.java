package br.com.javace.javou.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;

import java.util.ArrayList;

import br.com.javace.javou.R;
import br.com.javace.javou.dao.ParticipantDao;
import br.com.javace.javou.model.participant.Participant;
import br.com.javace.javou.model.raffle.Raffle;
import br.com.javace.javou.ui.base.BaseActivity;
import br.com.javace.javou.util.Constant;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rudsonlive on 22/09/15.
 */
public class RaffleActivity extends BaseActivity {

    private boolean isFinishOk = false;
    @Bind(R.id.animatedLoad) AnimatedCircleLoadingView mAnimatedLoad;
    private Participant mParticipantFortunate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raffle_main);
        ButterKnife.bind(this);

        mParticipantFortunate =  getIntent().getExtras().getParcelable(Constant.PARTICIPANT);
        mAnimatedLoad.setOnClickListener(onClickAnimLoad);
        startPercentMockThread();
    }

    private View.OnClickListener onClickAnimLoad = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isFinishOk) {
                isFinishOk = false;
                Toast.makeText(getApplicationContext(), "Terminou", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void startLoading() {
        mAnimatedLoad.startDeterminate();
    }

    private void startPercentMockThread() {
        startLoading();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    for (int i = 0; i <= 100; i++) {
                        Thread.sleep(65);
                        changePercent(i);
                    }
                    Thread.sleep(2000);
                    ParticipantDao participantDao = new ParticipantDao(getApplicationContext());
                    participantDao.updateAsRaffled(mParticipantFortunate);
                    Intent intent = new Intent(getBaseContext(), ParticipantFortunateActivity.class);
                    intent.putExtra(Constant.PARTICIPANT, mParticipantFortunate);
                    startActivityForResult(intent, 0, BaseActivity.ActivityAnimation.SLIDE_LEFT);
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();
    }

    private void changePercent(final int percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAnimatedLoad != null) {
                    mAnimatedLoad.setPercent(percent);

                    if (percent == 100){
                        isFinishOk = true;
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
