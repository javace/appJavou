package br.com.javace.javou.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

import br.com.javace.javou.R;
import br.com.javace.javou.model.participant.Participant;
import br.com.javace.javou.util.Constant;
import br.com.javace.javou.util.Util;

public class ParticipantDao {

    private Context mContext;
    private ClasseDB classeDB;
    private SQLiteDatabase db = null;
    private static ParticipantDao instance;

    public ParticipantDao(Context context) {
        super();
        this.mContext = context;

    }

    public static ParticipantDao getInstance(Context context) {
        if (instance == null) {
            synchronized (ParticipantDao.class) {
                if (instance == null) {
                    instance = new ParticipantDao(context);
                }
            }
        }
        return instance;
    }

    private void openConnection() {

        try {
            this.classeDB = ClasseDB.getInstance(this.mContext);
            db = classeDB.getWritableDatabase();
        } catch (Exception e) {

            Log.e(Constant.TAG, "Erro ao abrir conex?es ParticipantDao");
        }
    }

    private void closeConnection() {
        try {
            if (null != db) {
                db.close();
                db = null;
                classeDB.close();
            }

        } catch (Exception e) {
            Log.e(Constant.TAG, "Erro ao fechar conex?es ParticipantDao");
        }
    }

    public void synchronizeParticipant(List<String[]> participants) {

        if (participants != null) {
            try {
                openConnection();
                for (String[] participant : participants) {
                    try {

                        if (!participant[0].equals("NOME")) {

                            ContentValues campos = new ContentValues();

                            String name = participant[0];
                            campos.put(Constant.PARTICIPANT_name, name);
                            campos.put(Constant.PARTICIPANT_email, participant[14].toLowerCase());
                            campos.put(Constant.PARTICIPANT_code, participant[5]);
                            campos.put(Constant.PARTICIPANT_phone, Util.replacePhone(participant[13]));
                            campos.put(Constant.PARTICIPANT_photo, "");
                            campos.put(Constant.PARTICIPANT_attend, 0);
                            campos.put(Constant.PARTICIPANT_nameEvent, "Javou #06 - 28/11/2015");

                            String birthDate = participant[13];
                            campos.put(Constant.PARTICIPANT_birthDate, birthDate);

                            String company = WordUtils.capitalizeFully(participant[15].toLowerCase());
                            campos.put(Constant.PARTICIPANT_company, company);

                            campos.put(Constant.PARTICIPANT_sex, !participant[17].equals("Masculino"));

                            int shirtSize = 5;
                            if (participant[3].contains("COM camiseta")) {
                                shirtSize = Util.replaceShirtSize(participant[20]);
                            }
                            campos.put(Constant.PARTICIPANT_shirtSize, shirtSize);

                            try {
                                Long row = db.insertOrThrow(Constant.TABLE_PARTICIPANT, Constant.DATABASE, campos);
                                Log.d("ParticipantDao", row.toString());
                            } catch (Exception e) {
                                e.getMessage();
                                Log.e(Constant.TAG, "insert: " + mContext.getString(R.string.app_name));
                            }
                        }

                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }

                closeConnection();
            }catch (Exception e){
                e.getStackTrace();
            }
        }
    }

    public boolean insert(Participant participant) {

        if (participant != null) {

            try {
                openConnection();

                ContentValues campos = new ContentValues();

                campos.put(Constant.PARTICIPANT_name, participant.getName());
                campos.put(Constant.PARTICIPANT_phone, participant.getPhone());
                campos.put(Constant.PARTICIPANT_email, participant.getEmail());
                campos.put(Constant.PARTICIPANT_photo, participant.getPhoto());
                campos.put(Constant.PARTICIPANT_shirtSize, participant.getShirtSize());
                campos.put(Constant.PARTICIPANT_attend, participant.isAttend() ? 1 : 0);
                campos.put(Constant.PARTICIPANT_nameEvent, participant.getNameEvent());
                campos.put(Constant.PARTICIPANT_birthDate, participant.getBirthDate());
                campos.put(Constant.PARTICIPANT_sex, participant.isSex() ? 1 : 0);
                campos.put(Constant.PARTICIPANT_company, participant.getCompany());

                try {
                    return ( db.insertOrThrow(Constant.TABLE_PARTICIPANT, Constant.DATABASE, campos) != -1);
                } catch (Exception e) {
                    e.getMessage();
                    Log.e(Constant.TAG, "insert: " + mContext.getString(R.string.app_name));
                }

            } finally {
                closeConnection();
            }
        }

        return false;
    }

    public boolean update(Participant participant) {

        try {
            openConnection();

            ContentValues campos = new ContentValues();

            campos.put(Constant.PARTICIPANT_name, participant.getName());
            campos.put(Constant.PARTICIPANT_phone, participant.getPhone());
            campos.put(Constant.PARTICIPANT_email, participant.getEmail());
            campos.put(Constant.PARTICIPANT_photo, participant.getPhoto());
            campos.put(Constant.PARTICIPANT_shirtSize, participant.getShirtSize());
            campos.put(Constant.PARTICIPANT_attend, participant.isAttend() ? 1 : 0);
            campos.put(Constant.PARTICIPANT_nameEvent, participant.getNameEvent());
            campos.put(Constant.PARTICIPANT_birthDate, participant.getBirthDate());
            campos.put(Constant.PARTICIPANT_sex, participant.isSex() ? 1 : 0);
            campos.put(Constant.PARTICIPANT_company, participant.getCompany());

            String args[] = new String[]{String.valueOf(participant.getId())};
            return (db.update(Constant.TABLE_PARTICIPANT, campos, "id = ?", args) != -1);

        } catch (Exception e) {
            e.getMessage();
            Log.e(Constant.TAG, "Erro ParticipantDao update: ", e);
            Log.e(Constant.TAG, "update: " + mContext.getString(R.string.app_name));
        } finally {
            closeConnection();
        }

        return false;
    }

    public boolean updatePresence(Participant participant) {

        try {
            openConnection();

            ContentValues contentValues = new ContentValues();
            contentValues.put(Constant.PARTICIPANT_attend, participant.isAttend() ? 0 : 1);

            String args[] = new String[]{String.valueOf(participant.getId())};
            return (db.update(Constant.TABLE_PARTICIPANT, contentValues, "id = ?", args) != -1);

        } catch (Exception e) {
            e.getMessage();
            Log.e(Constant.TAG, "Erro ParticipantDao updatePresence: ", e);
            Log.e(Constant.TAG, "updatePresence: " + mContext.getString(R.string.app_name));
        } finally {
            closeConnection();
        }

        return false;
    }

    public boolean delete(int id) {

        try {
            openConnection();

            String args[] = new String[]{String.valueOf(id)};
            return (db.delete(Constant.TABLE_PARTICIPANT, "id = ?", args) != -1);

        } catch (Exception e) {
            e.getMessage();
            Log.e(Constant.TAG, "Erro ParticipantDao delete: ", e);
            Log.e(Constant.TAG, "delete: " + mContext.getString(R.string.app_name));
        } finally {
            closeConnection();
        }

        return false;
    }

    public ArrayList<Participant> getAll() {

        Participant participant;
        int currentShirtSize = -1;
        ArrayList<Participant> lParticipant = new ArrayList<>();

        Cursor cursor;
        try {

            openConnection();
            cursor = db.query(Constant.TABLE_PARTICIPANT, Constant.PARTICIPANT_COLS, null, null, null,
                    null, Constant.PARTICIPANT_name + " ASC");

            while (cursor.moveToNext()) {
                participant = new Participant();

                participant.setId(cursor.getInt(0));
                participant.setName(cursor.getString(1));
                participant.setPhone(cursor.getString(2));
                participant.setEmail(cursor.getString(3));
                participant.setPhoto(cursor.getString(4));

                //identifying who will be the type of group
                int shirtSize = cursor.getInt(5);
                //currentShirtSize != shirtSize
                participant.setGroup(true);
                participant.setShirtSize(shirtSize);
                currentShirtSize = shirtSize;

                participant.setAttend(cursor.getInt(6) == 1);
                participant.setNameEvent(cursor.getString(7));

                participant.setBirthDate(cursor.getString(8));
                participant.setSex(cursor.getInt(9) == 1);
                participant.setRaffled(cursor.getInt(10) == 1);
                participant.setCompany(cursor.getString(11));
                participant.setCode(cursor.getInt(12));

                lParticipant.add(participant);
            }
            cursor.close();
            return lParticipant;
        } catch (Exception e) {
            Log.e(Constant.TAG, "Erro ParticipantDao getAll: ", e);
        } finally {
            closeConnection();
        }

        return null;
    }

    public boolean updateAsRaffled(Participant participantFortunate) {

        try {
            openConnection();

            ContentValues contentValues = new ContentValues();
            contentValues.put(Constant.PARTICIPANT_raffled, 1);

            String args[] = new String[]{String.valueOf(participantFortunate.getId())};
            return (db.update(Constant.TABLE_PARTICIPANT, contentValues, "id = ?", args) != -1);

        } catch (Exception e) {
            e.getMessage();
            Log.e(Constant.TAG, "Erro ParticipantDao updateAsRaffled: ", e);
            Log.e(Constant.TAG, "updateAsRaffled: " + mContext.getString(R.string.app_name));
        } finally {
            closeConnection();
        }

        return false;

    }
}
