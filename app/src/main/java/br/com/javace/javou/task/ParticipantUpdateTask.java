package br.com.javace.javou.task;

import android.content.Context;
import android.os.AsyncTask;

import br.com.javace.javou.dao.ParticipantDao;
import br.com.javace.javou.model.participant.Participant;

/**
 * Created by Rudsonlive on 20/09/15.
 */
public class ParticipantUpdateTask extends AsyncTask<Void, Void, Boolean> {

    private Context mContext;
    private Participant mParticipant;

    public ParticipantUpdateTask(Context context, Participant participant){
        this.mContext = context;
        this.mParticipant = participant;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        ParticipantDao participantDao = ParticipantDao.getInstance(mContext);
        return participantDao.update(mParticipant);
    }
}
