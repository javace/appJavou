package br.com.javace.javou.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.javace.javou.R;
import br.com.javace.javou.model.participant.Participant;
import br.com.javace.javou.ui.base.BaseActivity;
import br.com.javace.javou.util.Constant;

/**
 * Created by danielbaccin on 24/09/15.
 */
public class ParticipantFortunateActivity extends BaseActivity{

    private Participant mParticipant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_fortunate);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.participant_fortunate));
        mToolbar.setNavigationIcon(R.drawable.ic_stars_white_24dp);
        this.setSupportActionBar(mToolbar);

        mParticipant = getIntent().getExtras().getParcelable(Constant.PARTICIPANT);


        final TextView txtName = (TextView) findViewById(R.id.txtName);
        final TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
        final TextView txtPhone = (TextView) findViewById(R.id.txtPhone);
        final ImageView imgPhoto = (ImageView) findViewById(R.id.imgPhoto);

        if (mParticipant != null) {
            txtName.setText(mParticipant.getName());
            txtEmail.setText(mParticipant.getEmail());
            txtPhone.setText(mParticipant.getPhone());
            if(!mParticipant.getSex()){
                //show suricate girl
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, 0, BaseActivity.ActivityAnimation.SLIDE_RIGHT);
    }
}
