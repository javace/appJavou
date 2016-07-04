package br.com.javace.javou.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import br.com.javace.javou.R;
import br.com.javace.javou.dao.ParticipantDao;
import br.com.javace.javou.ui.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rudsonlive on 22/09/15.
 */
public class SynchronizationActivity extends BaseActivity {

    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronization);
        ButterKnife.bind(this);

        new SynchronizeParticipant().execute();
    }

    private class SynchronizeParticipant extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), R.string.warning_synchronizing_participants, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Void... params) {
            CSVReader reader;
            try {
                reader = new CSVReader(new InputStreamReader(getResources().getAssets().open("javou.csv"), "iso-8859-1"));
                List<String[]> participant = reader.readAll();

                ParticipantDao participantDao = new ParticipantDao(SynchronizationActivity.this);
                participantDao.synchronizeParticipant(participant);

                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressBar.setVisibility(View.GONE);
            finish(ActivityAnimation.SLIDE_RIGHT);
        }

    }
}
