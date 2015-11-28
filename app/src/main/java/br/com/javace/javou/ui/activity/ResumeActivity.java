package br.com.javace.javou.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.javace.javou.R;
import br.com.javace.javou.dao.ParticipantDao;
import br.com.javace.javou.model.Resume;
import br.com.javace.javou.ui.base.BaseActivity;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ResumeActivity extends BaseActivity {

    @Bind(R.id.shapeTotalRegistrations) TextView shapeTotalRegistrations;
    @Bind(R.id.shapeTotalAttendence) TextView shapeTotalAttendence;
    @Bind(R.id.shapeTotalRaffled) TextView shapeTotalReffled;
    @Bind(R.id.toolbar) Toolbar mToolbar;


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
        shapeTotalRegistrations.setText("" + resume.getTotalRegistrations());
        shapeTotalAttendence.setText("" + resume.getTotalAttendance());
        shapeTotalReffled.setText("" + resume.getTotalRaffled());
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
