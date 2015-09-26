package br.com.javace.javou.task;

import android.content.Context;
import android.os.AsyncTask;

import br.com.javace.javou.dao.ParticipantDao;

/**
 * Created by Rudsonlive on 20/09/15.
 */
public class ParticipantDeleteTask extends AsyncTask<Void, Void, Boolean> {

    private int mId;
    private Context mContext;
    public ParticipantDeleteTask(Context context, int id){
        this.mId = id;
        this.mContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        ParticipantDao participantDao = ParticipantDao.getInstance(mContext);
        return participantDao.delete(mId);
    }
}
