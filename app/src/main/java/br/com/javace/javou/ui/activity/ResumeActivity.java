package br.com.javace.javou.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import br.com.javace.javou.R;
import br.com.javace.javou.dao.ParticipantDao;
import br.com.javace.javou.model.Resume;
import br.com.javace.javou.ui.base.BaseActivity;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ResumeActivity extends BaseActivity {

    @Bind(R.id.totalRegistrations) TextView totalRegistrations;
    @Bind(R.id.totalAttendance) TextView totalAttendance;
    @Bind(R.id.totalRaffled) TextView totalRaffled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        generateResume();
    }

    private void generateResume() {
        ParticipantDao dao = new ParticipantDao(this);
        Resume resume = dao.generateResume();
        totalRegistrations.setText(getString(R.string.totalRegistrations) + resume.getTotalRegistrations());
        totalAttendance.setText(getString(R.string.totalAttendence) + resume.getTotalAttendance());
        totalRaffled.setText(getString(R.string.totalRaffled) + resume.getTotalRaffled());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(ActivityAnimation.SLIDE_RIGHT);
    }
}
