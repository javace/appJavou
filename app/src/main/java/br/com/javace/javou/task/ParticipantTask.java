package br.com.javace.javou.task;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import br.com.javace.javou.dao.ParticipantDao;
import br.com.javace.javou.model.participant.Participant;

/**
 * Created by Rudsonlive on 20/09/15.
 */
public class ParticipantTask extends AsyncTask<Void, Void, ArrayList<Participant>> {

    private Context mContext;
    public ParticipantTask(Context context){
        this.mContext = context;
    }

    @Override
    protected ArrayList<Participant> doInBackground(Void... params) {
        ParticipantDao participantDao = new ParticipantDao(mContext);
        return participantDao.getAll();
    }
}
