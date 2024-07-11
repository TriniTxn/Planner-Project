package com.rocketseat.planner.link.services;

import com.rocketseat.planner.link.DTO.LinkData;
import com.rocketseat.planner.link.DTO.LinkResponseDTO;
import com.rocketseat.planner.link.LinkRequestPayload;
import com.rocketseat.planner.link.entities.Link;
import com.rocketseat.planner.link.repository.LinkRepository;
import com.rocketseat.planner.trip.entities.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {

    @Autowired
    private LinkRepository repository;

    public LinkResponseDTO registerLink(LinkRequestPayload payload, Trip trip) {
        Link newLink = new Link(payload.title(), payload.url(), trip);

        this.repository.save(newLink);

        return new LinkResponseDTO(newLink.getId());
    }

    public List<LinkData> getAllLinksFromTrip(UUID tripId){
        return this.repository.findByTripId(tripId).stream().map(link -> new LinkData(link.getId(), link.getTitle(), link.getUrl())).toList();
    }
}
