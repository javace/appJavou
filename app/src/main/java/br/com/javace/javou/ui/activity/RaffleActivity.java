package br.com.javace.javou.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;

import java.util.ArrayList;
import java.util.List;

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

    @Bind(R.id.animatedLoad) AnimatedCircleLoadingView mAnimatedLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raffle_main);
        ButterKnife.bind(this);
        startPercentMockThread();
    }

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

                    ParticipantDao participantDao = new ParticipantDao(getApplicationContext());
                    ArrayList<Participant> participants = participantDao.getAll();
                    Raffle raffle = new Raffle(participants);

                    Participant participantFortunate =  raffle.getFortunate();
                    Intent intent = new Intent(getBaseContext(), ParticipantFortunateActivity.class);
                    intent.putExtra(Constant.PARTICIPANT, participantFortunate);

                    startActivityForResult(intent, 0, BaseActivity.ActivityAnimation.SLIDE_LEFT);

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
                }
            }
        });
    }
}
