package com.rocketseat.planner.trip.controllers;

import com.rocketseat.planner.activity.ActivityRequestPayload;
import com.rocketseat.planner.activity.DTO.ActivityData;
import com.rocketseat.planner.activity.DTO.ActivityResponseDTO;
import com.rocketseat.planner.activity.services.ActivityService;
import com.rocketseat.planner.link.DTO.LinkData;
import com.rocketseat.planner.link.DTO.LinkResponseDTO;
import com.rocketseat.planner.link.LinkRequestPayload;
import com.rocketseat.planner.link.services.LinkService;
import com.rocketseat.planner.participant.DTO.ParticipantCreateDTO;
import com.rocketseat.planner.participant.DTO.ParticipantData;
import com.rocketseat.planner.participant.ParticipantRequestPayload;
import com.rocketseat.planner.participant.services.ParticipantService;
import com.rocketseat.planner.trip.DTO.TripCreateDTO;
import com.rocketseat.planner.trip.TripRequestPayload;
import com.rocketseat.planner.trip.entities.Trip;
import com.rocketseat.planner.trip.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@RestController
@RequestMapping("/trips")
public class TripController {

    DateTimeFormatter fmt1 = ISO_DATE_TIME;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private TripRepository repository;

    @Autowired
    private LinkService linkService;

    // TRIPS

    @PostMapping
    public ResponseEntity<TripCreateDTO> createTrip(@RequestBody TripRequestPayload payload){
        Trip newTrip = new Trip(payload);

        this.repository.save(newTrip);

        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateDTO(newTrip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id){
        Optional<Trip> trip = this.repository.findById(id);

        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip rawTrip = trip.get();
            rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), fmt1));
            rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(),fmt1));
            rawTrip.setDestination(payload.destination());

            this.repository.save(rawTrip);

            return ResponseEntity.ok(rawTrip);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip rawTrip = trip.get();
            rawTrip.setIsConfirmed(true);

            this.repository.save(rawTrip);
            this.participantService.triggerConfirmationEmailToParticipants(id);

            return ResponseEntity.ok(rawTrip);
        }

        return ResponseEntity.notFound().build();
    }

    // ACTIVITIES

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id){
        List<ActivityData> activityDataList = this.activityService.getAllActivitiesFromId(id);

        return ResponseEntity.ok(activityDataList);
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponseDTO> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip rawTrip = trip.get();

            ActivityResponseDTO activityResponse = this.activityService.registerActivity(payload, rawTrip);

            return ResponseEntity.ok(activityResponse);
        }

        return ResponseEntity.notFound().build();
    }

    // INVITES

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateDTO> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip rawTrip = trip.get();

            ParticipantCreateDTO participantDTO = this.participantService.registerParticipantToEvent(payload.email(), rawTrip);

            if(rawTrip.getIsConfirmed()){
                this.participantService.triggerConfirmationEmailToParticipant(payload.email());
            }

            return ResponseEntity.ok(participantDTO);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id){
        List<ParticipantData> participantsList = this.participantService.getAllParticipantsFromEvents(id);

        return ResponseEntity.ok(participantsList);
    }

    // LINKS

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponseDTO> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip rawTrip = trip.get();

            LinkResponseDTO linkResponse = this.linkService.registerLink(payload, rawTrip);

            return ResponseEntity.ok(linkResponse);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID id){
        List<LinkData> linkDataList = this.linkService.getAllLinksFromTrip(id);

        return ResponseEntity.ok(linkDataList);
    }
}
