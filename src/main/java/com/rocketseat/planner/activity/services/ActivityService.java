package com.rocketseat.planner.activity.services;

import com.rocketseat.planner.activity.ActivityRequestPayload;
import com.rocketseat.planner.activity.DTO.ActivityData;
import com.rocketseat.planner.activity.DTO.ActivityResponseDTO;
import com.rocketseat.planner.activity.entities.Activity;
import com.rocketseat.planner.activity.repository.ActivityRepository;
import com.rocketseat.planner.trip.entities.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository repository;

    public ActivityResponseDTO registerActivity(ActivityRequestPayload payload, Trip trip) {
        Activity newActivity = new Activity(payload.title(), payload.occurs_at(), trip);

        this.repository.save(newActivity);

        return new ActivityResponseDTO(newActivity.getId());
    }

    public List<ActivityData> getAllActivitiesFromId(UUID tripId){
        return this.repository.findByTripId(tripId).stream().map(activity -> new ActivityData(activity.getId(), activity.getTitle(), activity.getOccursAt())).toList();
    }
}
