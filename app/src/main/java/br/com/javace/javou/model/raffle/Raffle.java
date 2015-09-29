package br.com.javace.javou.model.raffle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.javace.javou.model.participant.Participant;

/**
 * Created by danielbaccin on 21/07/15.
 */
public class Raffle{

    private ArrayList<Participant> participants;
    private Random randomGenerator;

    public Raffle(ArrayList<Participant> participants) {
        this.participants = participants;
        randomGenerator = new Random();
    }

    private List<Participant> getParticipantsAttend(){
        List<Participant> attendees = new ArrayList<>();
        for (Participant participant : participants) {
            if(participant.isAttend() && !participant.isRaffled()) //It can be drawn
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


    public Participant getFortunate() {
        List<Participant> participantsAttend = getParticipantsAttend();
        if(participantsAttend.isEmpty())
            return null;
        int index = randomGenerator.nextInt(participantsAttend.size());
        Participant participantFortunate = participantsAttend.get(index);
        participantFortunate.setRaffled(true);
        return participantFortunate;
    }
}
