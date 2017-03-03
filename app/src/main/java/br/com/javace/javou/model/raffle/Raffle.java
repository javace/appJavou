package br.com.javace.javou.model.raffle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.javace.javou.model.participant.Participant;

/**
 * Created by danielbaccin on 21/07/15.
 */
public class Raffle{

    private Random randomGenerator;
    private ArrayList<Participant> participants;

    public Raffle(ArrayList<Participant> participants) {
        this.randomGenerator = new Random();
        this.participants = participants;
    }

    private List<Participant> getParticipantsAttend(){
        List<Participant> attendees = new ArrayList<>();
        for (Participant participant : participants) {
            if(participant.isAttend() && !participant.isRaffled()) //It can be drawn
                attendees.add(participant);
        }
        return attendees;
    }

    private List<Participant> getParticipantsAttendSex(){
        List<Participant> attendees = new ArrayList<>();
        for (Participant participant : participants) {
            if(participant.isAttend() && !participant.isRaffled() && participant.getSex()) //It can be drawn
                attendees.add(participant);
        }
        return attendees;
    }

    public boolean isValid(){
        for (Participant participant : participants) {
            if(participant.isAttend() && !participant.isRaffled())
                return true;
        }
        return false;
    }

    public boolean isValidSex(){
        for (Participant participant : participants) {
            if(participant.isAttend() && !participant.isRaffled() && participant.getSex())
                return true;
        }
        return false;
    }

    public Participant getFortunate() {
        List<Participant> participantsAttend = getParticipantsAttend();

        if(participantsAttend.isEmpty()) {
            return null;
        }

        int index = randomGenerator.nextInt(participantsAttend.size());
        Participant participantFortunate = participantsAttend.get(index);
        participantFortunate.setRaffled(true);
        return participantFortunate;
    }

    public Participant getFortunateSex() {
        List<Participant> participantsAttend = getParticipantsAttendSex();

        if(participantsAttend.isEmpty()) {
            return null;
        }

        int index = randomGenerator.nextInt(participantsAttend.size());
        Participant participantFortunate = participantsAttend.get(index);
        participantFortunate.setRaffled(true);
        return participantFortunate;
    }
}
