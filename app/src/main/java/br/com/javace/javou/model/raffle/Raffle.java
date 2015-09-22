package br.com.javace.javou.model.raffle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.javace.javou.model.participant.Participant;

/**
 * Created by danielbaccin on 21/07/15.
 */
public class Raffle {

    private ArrayList<Participant> participants;
    private Random randomGenerator;

    public Raffle(ArrayList<Participant> participants) {
        this.participants = participants;
        randomGenerator = new Random();
    }

    private List<Participant> getParticipantsAttend(){
        List<Participant> attendees = new ArrayList<>();
        for (Participant participant : participants) {
            if(participant.isAttend())
                attendees.add(participant);
        }
        return attendees;
    }


    public Participant getFortunate() {
        int index = randomGenerator.nextInt(getParticipantsAttend().size());
        return getParticipantsAttend().get(index);
    }
}
