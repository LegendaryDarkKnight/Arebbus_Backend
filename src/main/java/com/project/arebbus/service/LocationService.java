package com.project.arebbus.service;

import com.project.arebbus.dto.LocationResponse;
import com.project.arebbus.dto.LocationSetRequest;
import com.project.arebbus.exception.BusNotFoundException;
import com.project.arebbus.exception.LocationNotFoundException;
import com.project.arebbus.model.Bus;
import com.project.arebbus.model.Location;
import com.project.arebbus.model.LocationId;
import com.project.arebbus.model.LocationStatus;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.BusRepository;
import com.project.arebbus.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final BusRepository busRepository;

    public LocationResponse setUserLocationWithStatus(User user, LocationSetRequest request, LocationStatus status) {
        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new BusNotFoundException(request.getBusId()));

        Location location = Location.builder()
                .busId(bus.getId())
                .userId(user.getId())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .time(LocalDateTime.now())
                .status(status)
                .bus(bus)
                .user(user)
                .build();

        Location savedLocation = locationRepository.save(location);

        return LocationResponse.builder()
                .userId(savedLocation.getUserId())
                .busId(savedLocation.getBusId())
                .busName(bus.getName())
                .latitude(savedLocation.getLatitude())
                .longitude(savedLocation.getLongitude())
                .time(savedLocation.getTime())
                .status(savedLocation.getStatus())
                .build();
    }

    public LocationResponse setUserWaiting(User user, LocationSetRequest request) {
        return setUserLocationWithStatus(user, request, LocationStatus.WAITING);
    }

    public LocationResponse setUserOnBus(User user, LocationSetRequest request) {
        return setUserLocationWithStatus(user, request, LocationStatus.ON_BUS);
    }

    public LocationResponse setUserLeftBus(User user, LocationSetRequest request) {
        return setUserLocationWithStatus(user, request, LocationStatus.LEFT_BUS);
    }

    public LocationResponse getUserCurrentLocation(User user) {
        Optional<Location> latestLocationOpt = locationRepository.findByUser(user)
                .stream()
                .reduce((loc1, loc2) -> loc1.getTime().isAfter(loc2.getTime()) ? loc1 : loc2);

        if (latestLocationOpt.isEmpty()) {
            throw new LocationNotFoundException();
        }

        Location location = latestLocationOpt.get();
        Bus bus = location.getBus();

        return LocationResponse.builder()
                .userId(location.getUserId())
                .busId(location.getBusId())
                .busName(bus.getName())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .time(location.getTime())
                .status(location.getStatus())
                .build();
    }
}