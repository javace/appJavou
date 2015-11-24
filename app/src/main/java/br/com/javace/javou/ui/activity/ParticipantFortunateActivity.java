package br.com.javace.javou.ui.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.javace.javou.R;
import br.com.javace.javou.model.participant.Participant;
import br.com.javace.javou.ui.base.BaseActivity;
import br.com.javace.javou.util.Constant;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by danielbaccin on 24/09/15.
 */
public class ParticipantFortunateActivity extends BaseActivity{

    @Bind(R.id.txtName) TextView txtName;
    @Bind(R.id.txtEmail) TextView txtEmail;
    @Bind(R.id.txtPhone) TextView txtPhone;
    @Bind(R.id.imgPhoto) ImageView imgPhoto;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_fortunate);
        ButterKnife.bind(this);

        mToolbar.setTitle(getString(R.string.participant_fortunate));
        mToolbar.setNavigationIcon(R.drawable.ic_stars_white_24dp);
        this.setSupportActionBar(mToolbar);

        Participant participant = getIntent().getExtras().getParcelable(Constant.PARTICIPANT);

        if (participant != null) {
            txtName.setText(participant.getName());
            txtEmail.setText(participant.getEmail());
            txtPhone.setText(participant.getPhone());
            if (participant.getSex()) {
                imgPhoto.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_suricate_girl));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(ActivityAnimation.SLIDE_RIGHT);
    }
}
