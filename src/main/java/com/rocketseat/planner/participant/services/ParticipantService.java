package com.rocketseat.planner.participant.services;

import com.rocketseat.planner.participant.DTO.ParticipantCreateDTO;
import com.rocketseat.planner.participant.DTO.ParticipantData;
import com.rocketseat.planner.participant.entities.Participant;
import com.rocketseat.planner.participant.repository.ParticipantRepository;
import com.rocketseat.planner.trip.entities.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository repository;

    public void registerParticipantsToEvent(List<String> participantsToInvite, Trip trip){
        List<Participant> participants = participantsToInvite.stream().map(email -> new Participant(email, trip)).toList();

        this.repository.saveAll(participants);

        System.out.println(participants.get(0).getId());
    }

    public ParticipantCreateDTO registerParticipantToEvent(String email, Trip trip){
        Participant newParticipant = new Participant(email, trip);
        this.repository.save(newParticipant);
        return new ParticipantCreateDTO(newParticipant.getId());
    }

    public void triggerConfirmationEmailToParticipants(UUID tripId){};

    public void triggerConfirmationEmailToParticipant(String email){};

    public List<ParticipantData> getAllParticipantsFromEvents(UUID tripId) {
        return this.repository.findByTripId(tripId).stream().map(participant -> new ParticipantData(participant.getId(), participant.getName(), participant.getEmail(), participant.getIsConfirmed())).toList();
    }

}
