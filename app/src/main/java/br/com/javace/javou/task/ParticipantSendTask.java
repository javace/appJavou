package br.com.javace.javou.task;

import android.content.Context;
import android.os.AsyncTask;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.javace.javou.model.participant.Participant;
import br.com.javace.javou.util.Constant;

/**
 * Created by Rudsonlive on 20/09/15.
 */
public class ParticipantSendTask extends AsyncTask<Void, Void, Boolean> {

    private Context mContext;
    private ArrayList<Participant> mParticipant;

    public ParticipantSendTask(Context context, ArrayList<Participant> participant){
        this.mContext = context;
        this.mParticipant = participant;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        CSVWriter writer;

        try {
            writer = new CSVWriter(new FileWriter(Constant.PATH_FILE_JAVOU));
            List<String[]> data = new ArrayList<>();

            data.add(new String[]{"CODIGO", "NOME", "EMAIL", "CELULAR", "SEXO", "EMPRESA"});

            for (Participant participant : mParticipant) {
                if (participant.isAttend()) {
                    String sex = (participant.isSex() ? "F" : "M");
                    data.add(new String[]{String.valueOf(participant.getCode()), participant.getName(), participant.getEmail(), participant.getPhone(), sex, participant.getCompany()});
                }
            }

            writer.writeAll(data);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
