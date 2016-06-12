package br.com.javace.javou.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Locale;

import br.com.javace.javou.R;
import br.com.javace.javou.dao.ParticipantDao;
import br.com.javace.javou.model.Resume.Resume;
import br.com.javace.javou.ui.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ResumeActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.txt_shape_total_raffled) TextView mTxtShapeTotalReffled;
    @BindView(R.id.txt_shape_total_attendence) TextView mTxtShapeTotalAttendence;
    @BindView(R.id.txt_shape_total_registrations) TextView mTxtShapeTotalRegistrations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);
        ButterKnife.bind(this);

        mToolbar.setTitle(getString(R.string.resume_event));
        this.setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        generateResume();
    }

    private void generateResume() {
        ParticipantDao dao = new ParticipantDao(this);
        Resume resume = dao.generateResume();
        mTxtShapeTotalRegistrations.setText(String.format(Locale.getDefault(), "%d", resume.getTotalRegistrations()));
        mTxtShapeTotalAttendence.setText(String.format(Locale.getDefault(), "%d", resume.getTotalAttendance()));
        mTxtShapeTotalReffled.setText(String.format(Locale.getDefault(), "%d", resume.getTotalRaffled()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(ActivityAnimation.SLIDE_RIGHT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish(ActivityAnimation.SLIDE_RIGHT);
        return super.onOptionsItemSelected(item);
    }
}
