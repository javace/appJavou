package br.com.javace.javou.ui.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.javace.javou.R;
import br.com.javace.javou.model.participant.Participant;
import br.com.javace.javou.ui.base.BaseActivity;
import br.com.javace.javou.util.Constant;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by danielbaccin on 24/09/15.
 */
public class ParticipantFortunateActivity extends BaseActivity{

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.txt_name) TextView txtName;
    @BindView(R.id.txt_email) TextView txtEmail;
    @BindView(R.id.txt_phone) TextView txtPhone;
    @BindView(R.id.img_photo) ImageView imgPhoto;
    @BindView(R.id.layout_phone) LinearLayout mLayoutPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_fortunate);
        ButterKnife.bind(this);

        mToolbar.setTitle(getString(R.string.participant_fortunate));
        this.setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Participant participant = getIntent().getExtras().getParcelable(Constant.PARTICIPANT);

        if (participant != null) {
            txtName.setText(participant.getName());
            txtEmail.setText(participant.getEmail());

            mLayoutPhone.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(participant.getPhone())){
                txtPhone.setText(participant.getPhone());
                mLayoutPhone.setVisibility(View.VISIBLE);
            }

            if (participant.getSex()) {
                imgPhoto.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_suricate_girl));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish(ActivityAnimation.SLIDE_RIGHT);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(ActivityAnimation.SLIDE_RIGHT);
    }
}
