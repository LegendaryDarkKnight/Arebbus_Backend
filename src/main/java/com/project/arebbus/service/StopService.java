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

    /** Repository for  data access */
    private final StopRepository stopRepository;

    /**
     * Creates a new .
     * 
     * @param user The user creating the 
     * @param request The creation request
     * @return StopResponse containing the created 
     */
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

    /**
     * Retrieves a  by its ID.
     * 
     * @param id The ID of the  to retrieve
     * @param user The user requesting the 
     * @return StopResponse containing  details
     */
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

    /**
     * Retrieves all  with pagination.
     * 
     * @param user The user requesting 
     * @param page The page number
     * @param size The page size
     * @return PagedStopResponse containing  and pagination info
     */
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

    public List<StopResponse> getNearbyStops(double latitude, double longitude, double radiusKm) {
        List<Stop> allStops = stopRepository.findAll();
        
        return allStops.stream()
                .filter(stop -> calculateDistance(latitude, longitude, stop.getLatitude().doubleValue(), stop.getLongitude().doubleValue()) <= radiusKm)
                .map(stop -> StopResponse.builder()
                        .id(stop.getId())
                        .name(stop.getName())
                        .latitude(stop.getLatitude())
                        .longitude(stop.getLongitude())
                        .authorName(stop.getAuthor().getName())
                        .build())
                .toList();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in kilometers
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in kilometers
    }
}