package com.rocketseat.planner.link.repository;

import com.rocketseat.planner.link.entities.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<Link, UUID> {

    List<Link> findByTripId(UUID tripId);
}