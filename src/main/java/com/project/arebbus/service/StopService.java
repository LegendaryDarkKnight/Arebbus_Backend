package com.project.arebbus.service;

import com.project.arebbus.dto.PagedStopResponse;
import com.project.arebbus.dto.StopCreateRequest;
import com.project.arebbus.dto.StopResponse;
import com.project.arebbus.exception.StopNotFoundException;
import com.project.arebbus.model.Stop;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.StopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StopService {

    private final StopRepository stopRepository;

    public StopResponse createStop(User user, StopCreateRequest request) {
        Stop stop = Stop.builder()
                .name(request.getName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .author(user)
                .build();

        Stop savedStop = stopRepository.save(stop);

        return StopResponse.builder()
                .id(savedStop.getId())
                .name(savedStop.getName())
                .latitude(savedStop.getLatitude())
                .longitude(savedStop.getLongitude())
                .authorName(savedStop.getAuthor().getName())
                .build();
    }

    public StopResponse getStopById(Long stopId) {
        Stop stop = stopRepository.findById(stopId)
                .orElseThrow(() -> new StopNotFoundException(stopId));

        return StopResponse.builder()
                .id(stop.getId())
                .name(stop.getName())
                .latitude(stop.getLatitude())
                .longitude(stop.getLongitude())
                .authorName(stop.getAuthor().getName())
                .build();
    }

    public PagedStopResponse getAllStops(int page, int size) {
        Page<Stop> stops = stopRepository.findAll(PageRequest.of(page, size));

        List<StopResponse> stopResponses = stops.getContent().stream()
                .map(stop -> StopResponse.builder()
                        .id(stop.getId())
                        .name(stop.getName())
                        .latitude(stop.getLatitude())
                        .longitude(stop.getLongitude())
                        .authorName(stop.getAuthor().getName())
                        .build())
                .toList();

        return PagedStopResponse.builder()
                .stops(stopResponses)
                .page(stops.getNumber())
                .size(stops.getSize())
                .totalPages(stops.getTotalPages())
                .totalElements(stops.getTotalElements())
                .build();
    }
}