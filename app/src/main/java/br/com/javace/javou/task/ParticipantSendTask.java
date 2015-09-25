package br.com.javace.javou.task;

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

    private ArrayList<Participant> mParticipant;

    public ParticipantSendTask(ArrayList<Participant> participant){
        this.mParticipant = participant;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        CSVWriter writer;
        boolean isAttend = false;

        try {
            writer = new CSVWriter(new FileWriter(Constant.PATH_FILE_JAVOU));
            List<String[]> data = new ArrayList<>();

            data.add(Constant.FILE_COLS);

            for (Participant participant : mParticipant) {
                if (participant.isAttend()) {
                    isAttend = true;
                    String sex = (participant.isSex() ? "F" : "M");
                    data.add(new String[]{String.valueOf(participant.getCode()), participant.getName(), participant.getEmail(), participant.getPhone(), sex, participant.getCompany()});
                }
            }

            writer.writeAll(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isAttend;
    }
}
