package com.project.arebbus.service;

import com.project.arebbus.dto.BusLocationCluster;
import com.project.arebbus.dto.BusLocationResponse;
import com.project.arebbus.dto.LocationResponse;
import com.project.arebbus.dto.LocationSetRequest;
import com.project.arebbus.dto.LocationUpdateRequest;
import com.project.arebbus.dto.WaitingUsersCountResponse;
import com.project.arebbus.exception.BusNotFoundException;
import com.project.arebbus.exception.InvalidLocationStatusTransitionException;
import com.project.arebbus.exception.LocationNotFoundException;
import com.project.arebbus.model.Bus;
import com.project.arebbus.model.Location;
import com.project.arebbus.model.LocationStatus;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.BusRepository;
import com.project.arebbus.repositories.LocationRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for location business logic operations. Handles bus location tracking, user
 * location management, and location clustering.
 */
@Service
@RequiredArgsConstructor
public class LocationService {

  /** Repository for location data access */
  /** Repository for data access */
  private final LocationRepository locationRepository;

  /** Repository for bus data access */
  /** Repository for bus data access */
  /** Repository for data access */
  private final BusRepository busRepository;

  /**
   * Sets user location with a specific status for a bus.
   *
   * @param user The user setting their location
   * @param request The location request with coordinates and bus ID
   * @param status The location status (WAITING, ON_BUS, etc.)
   * @return LocationResponse with the set location details
   * @throws BusNotFoundException if the specified bus doesn't exist
   */
  /**
   * Sets the user's location for tracking.
   *
   * @param user The user setting location
   * @param request The location request
   * @return LocationResponse containing location details
   */
  public LocationResponse setUserLocation(
      User user, LocationSetRequest request, LocationStatus status) {
    Bus bus =
        busRepository
            .findById(request.getBusId())
            .orElseThrow(() -> new BusNotFoundException(request.getBusId()));

    Location location =
        Location.builder()
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

  public LocationResponse setUserLocationWithStatus(
      User user, LocationSetRequest request, LocationStatus status) {
    Bus bus =
        busRepository
            .findById(request.getBusId())
            .orElseThrow(() -> new BusNotFoundException(request.getBusId()));

    Location location =
        Location.builder()
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
    LocationStatus currentStatus = getCurrentUserStatus(user);

    // Allow WAITING -> WAITING (same status) or NO_TRACK -> WAITING
    if (currentStatus != LocationStatus.NO_TRACK && currentStatus != LocationStatus.WAITING) {
      throw new InvalidLocationStatusTransitionException(
          "Can only set WAITING status from NO_TRACK or WAITING status. Current status: "
              + currentStatus);
    }

    return setUserLocationWithStatus(user, request, LocationStatus.WAITING);
  }

  public LocationResponse setUserOnBus(User user, LocationUpdateRequest request) {
    // Get user's current status to validate transition
    LocationStatus currentStatus = getCurrentUserStatus(user);

    // Allow ON_BUS -> ON_BUS (same status) or WAITING -> ON_BUS
    if (currentStatus != LocationStatus.WAITING && currentStatus != LocationStatus.ON_BUS) {
      throw new InvalidLocationStatusTransitionException(
          "Can only set ON_BUS status from WAITING or ON_BUS status. Current status: "
              + currentStatus);
    }

    // Get the most recent location to get the busId
    Location lastLocation = getUserLastLocation(user);

    Location location =
        Location.builder()
            .busId(lastLocation.getBusId())
            .userId(user.getId())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .time(LocalDateTime.now())
            .status(LocationStatus.ON_BUS)
            .bus(lastLocation.getBus())
            .user(user)
            .build();

    Location savedLocation = locationRepository.save(location);

    return LocationResponse.builder()
        .userId(savedLocation.getUserId())
        .busId(savedLocation.getBusId())
        .busName(lastLocation.getBus().getName())
        .latitude(savedLocation.getLatitude())
        .longitude(savedLocation.getLongitude())
        .time(savedLocation.getTime())
        .status(savedLocation.getStatus())
        .build();
  }

  public LocationResponse setUserNoTrack(User user, LocationUpdateRequest request) {
    // NO_TRACK can be set from any status, including NO_TRACK -> NO_TRACK
    LocationStatus currentStatus = getCurrentUserStatus(user);

    // For NO_TRACK, we still need busId for composite key, so use a dummy value or
    // get from last
    // location
    Long busIdForKey = null;
    try {
      Location lastLocation = getUserLastLocation(user);
      busIdForKey = lastLocation.getBusId();
    } catch (LocationNotFoundException e) {
      // If no previous location, we need some busId for the composite key - use 0 as
      // dummy
      busIdForKey = 1L;
    }

    Location location =
        Location.builder()
            .busId(busIdForKey)
            .userId(user.getId())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .time(LocalDateTime.now())
            .status(LocationStatus.NO_TRACK)
            .bus(null) // Keep bus entity as null for NO_TRACK
            .user(user)
            .build();

    Location savedLocation = locationRepository.save(location);

    return LocationResponse.builder()
        .userId(savedLocation.getUserId())
        .busId(null) // Don't expose the dummy busId in response
        .busName(null)
        .latitude(savedLocation.getLatitude())
        .longitude(savedLocation.getLongitude())
        .time(savedLocation.getTime())
        .status(savedLocation.getStatus())
        .build();
  }

  private LocationStatus getCurrentUserStatus(User user) {
    try {
      Location lastLocation = getUserLastLocation(user);
      return lastLocation.getStatus();
    } catch (LocationNotFoundException e) {
      // If user has no previous location, assume NO_TRACK
      return LocationStatus.NO_TRACK;
    }
  }

  private Location getUserLastLocation(User user) {
    Optional<Location> latestLocationOpt =
        locationRepository.findByUser(user).stream()
            .reduce((loc1, loc2) -> loc1.getTime().isAfter(loc2.getTime()) ? loc1 : loc2);

    if (latestLocationOpt.isEmpty()) {
      throw new LocationNotFoundException();
    }

    return latestLocationOpt.get();
  }

  public LocationResponse getUserCurrentLocation(User user) {
    Location location = getUserLastLocation(user);
    Bus bus = location.getBus();

    return LocationResponse.builder()
        .userId(location.getUserId())
        .busId(location.getBusId())
        .busName(bus != null ? bus.getName() : null)
        .latitude(location.getLatitude())
        .longitude(location.getLongitude())
        .time(location.getTime())
        .status(location.getStatus())
        .build();
  }

  public BusLocationResponse getBusLocations(Long busId) {
    Bus bus = busRepository.findById(busId).orElseThrow(() -> new BusNotFoundException(busId));

    // Get all users currently on this bus (latest location with ON_BUS status)
    List<Location> onBusLocations = getLatestOnBusLocationsForBus(busId);

    if (onBusLocations.isEmpty()) {
      return BusLocationResponse.builder()
          .busId(busId)
          .busName(bus.getName())
          .locations(new ArrayList<>())
          .build();
    }

    // Cluster the locations
    List<BusLocationCluster> clusters = clusterLocations(onBusLocations);

    return BusLocationResponse.builder()
        .busId(busId)
        .busName(bus.getName())
        .locations(clusters)
        .build();
  }

  private List<Location> getLatestOnBusLocationsForBus(Long busId) {
    List<Location> allLocations = locationRepository.findByBusId(busId);

    // Group by user and get the latest location for each user
    return allLocations.stream()
        .collect(java.util.stream.Collectors.groupingBy(Location::getUserId))
        .values()
        .stream()
        .map(
            userLocations ->
                userLocations.stream()
                    .reduce((loc1, loc2) -> loc1.getTime().isAfter(loc2.getTime()) ? loc1 : loc2)
                    .orElse(null))
        .filter(location -> location != null && location.getStatus() == LocationStatus.ON_BUS)
        .collect(java.util.stream.Collectors.toList());
  }

  private List<BusLocationCluster> clusterLocations(List<Location> locations) {
    List<BusLocationCluster> clusters = new ArrayList<>();
    List<Location> unprocessed = new ArrayList<>(locations);

    // Distance threshold in degrees (approximately 100 meters)
    double distanceThreshold = 0.001;

    while (!unprocessed.isEmpty()) {
      Location seed = unprocessed.remove(0);
      List<Location> cluster = new ArrayList<>();
      cluster.add(seed);

      // Find all locations within threshold distance of seed
      List<Location> toRemove = new ArrayList<>();
      for (Location loc : unprocessed) {
        if (calculateDistance(seed, loc) <= distanceThreshold) {
          cluster.add(loc);
          toRemove.add(loc);
        }
      }
      unprocessed.removeAll(toRemove);

      // Calculate cluster center (average position)
      BigDecimal avgLat =
          cluster.stream()
              .map(Location::getLatitude)
              .reduce(BigDecimal.ZERO, BigDecimal::add)
              .divide(BigDecimal.valueOf(cluster.size()), BigDecimal.ROUND_HALF_UP);

      BigDecimal avgLon =
          cluster.stream()
              .map(Location::getLongitude)
              .reduce(BigDecimal.ZERO, BigDecimal::add)
              .divide(BigDecimal.valueOf(cluster.size()), BigDecimal.ROUND_HALF_UP);

      clusters.add(
          BusLocationCluster.builder()
              .latitude(avgLat)
              .longitude(avgLon)
              .userCount(cluster.size())
              .build());
    }

    return clusters;
  }

  private double calculateDistance(Location loc1, Location loc2) {
    // Simple Euclidean distance in degrees (good enough for clustering nearby
    // locations)
    double latDiff = loc1.getLatitude().subtract(loc2.getLatitude()).doubleValue();
    double lonDiff = loc1.getLongitude().subtract(loc2.getLongitude()).doubleValue();
    return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
  }

  /**
   * Updates an existing .
   *
   * @param user The user updating the
   * @param request The update request
   * @return LocationResponse containing updated details
   */
  public LocationResponse updateUserLocation(User user, LocationUpdateRequest request) {
    // Get user's current location to preserve status and bus info
    Location lastLocation = getUserLastLocation(user);

    Location location =
        Location.builder()
            .busId(lastLocation.getBusId())
            .userId(user.getId())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .time(LocalDateTime.now())
            .status(lastLocation.getStatus()) // Keep same status
            .bus(lastLocation.getBus())
            .user(user)
            .build();

    Location savedLocation = locationRepository.save(location);

    return LocationResponse.builder()
        .userId(savedLocation.getUserId())
        .busId(savedLocation.getBusId())
        .busName(savedLocation.getBus() != null ? savedLocation.getBus().getName() : null)
        .latitude(savedLocation.getLatitude())
        .longitude(savedLocation.getLongitude())
        .time(savedLocation.getTime())
        .status(savedLocation.getStatus())
        .build();
  }

  public WaitingUsersCountResponse getWaitingUsersCount(User user) {
    // Verify user is in WAITING status
    LocationStatus currentStatus = getCurrentUserStatus(user);
    if (currentStatus != LocationStatus.WAITING) {
      throw new InvalidLocationStatusTransitionException(
          "User must be in WAITING status to get waiting count. Current status: " + currentStatus);
    }

    // Get user's current bus
    Location userLocation = getUserLastLocation(user);
    Bus bus = userLocation.getBus();

    // Count all users waiting for the same bus
    List<Location> allBusLocations = locationRepository.findByBusId(bus.getId());

    long waitingCount =
        allBusLocations.stream()
            .collect(java.util.stream.Collectors.groupingBy(Location::getUserId))
            .values()
            .stream()
            .map(
                userLocations ->
                    userLocations.stream()
                        .reduce(
                            (loc1, loc2) -> loc1.getTime().isAfter(loc2.getTime()) ? loc1 : loc2)
                        .orElse(null))
            .filter(location -> location != null && location.getStatus() == LocationStatus.WAITING)
            .count();

    return WaitingUsersCountResponse.builder()
        .busId(bus.getId())
        .busName(bus.getName())
        .waitingCount((int) waitingCount)
        .build();
  }
}
