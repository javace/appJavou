package br.com.javace.javou;

import android.test.AndroidTestCase;

import java.util.ArrayList;

import br.com.javace.javou.dao.ParticipantDao;
import br.com.javace.javou.model.Resume;
import br.com.javace.javou.model.participant.Participant;
import br.com.javace.javou.model.raffle.Raffle;
import br.com.javace.javou.util.Constant;

/**
 * Created by daniel on 27/11/15.
 */
public class ParticipantDaoTest extends AndroidTestCase {


    public void testTotalRegistrations(){
        mContext.deleteDatabase(Constant.DATABASE);
        ParticipantDao dao = new ParticipantDao(mContext);
        dao.insert(generateBasicParticipant("nome 1", "88 8888888", true));
        dao.insert(generateBasicParticipant("nome 2", "88 8888888", true));
        dao.insert(generateBasicParticipant("nome 3", "88 8888888", true));
        int totalRegistrations = dao.getTotalRegistrations();
        assertEquals("total registrations", 3, totalRegistrations);
    }

    public void testTotalAttendance(){
        mContext.deleteDatabase(Constant.DATABASE);
        ParticipantDao dao = new ParticipantDao(mContext);
        dao.insert(generateBasicParticipant("nome 1", "88 8888888", false));
        dao.insert(generateBasicParticipant("nome 2", "88 8888888", true));
        dao.insert(generateBasicParticipant("nome 3", "88 8888888", true));
        int totalAttendence = dao.getTotalAttendance();
        assertEquals("total Attendance", 2, totalAttendence);
    }

    public void testTotalRaffled(){
        mContext.deleteDatabase(Constant.DATABASE);
        ParticipantDao dao = new ParticipantDao(mContext);
        dao.insert(generateBasicParticipant("nome 1", "88 8888888", true));
        dao.insert(generateBasicParticipant("nome 2", "88 8888888", true));
        dao.insert(generateBasicParticipant("nome 3", "88 8888888", true));

        ArrayList<Participant> allPArticipants = dao.getAll();
        Raffle raffle = new Raffle(allPArticipants);
        dao.updateAsRaffled(raffle.getFortunate());

        int totalRaffled = dao.getTotalRaffled();
        assertEquals("total Raffled", 1, totalRaffled);
    }

    public void testResume(){

        mContext.deleteDatabase(Constant.DATABASE);
        ParticipantDao dao = new ParticipantDao(mContext);
        dao.insert(generateBasicParticipant("nome 1", "88 8888888", true));
        dao.insert(generateBasicParticipant("nome 2", "88 8888888", true));
        dao.insert(generateBasicParticipant("nome 3", "88 8888888", true));

        ArrayList<Participant> allPArticipants = dao.getAll();
        Raffle raffle = new Raffle(allPArticipants);
        dao.updateAsRaffled(raffle.getFortunate());

        Resume resume = dao.generateResume();
        assertEquals("total registrations", 3, resume.getTotalRegistrations());
        assertEquals("total Attendance", 3, resume.getTotalAttendance());
        assertEquals("total Raffled", 1, resume.getTotalRaffled());

    }

    private Participant generateBasicParticipant(String name, String phone, boolean attended) {
        Participant participant = new Participant();
        participant.setName(name);
        participant.setPhone(phone);
        participant.setAttend(attended);
        return participant;
    }

}
