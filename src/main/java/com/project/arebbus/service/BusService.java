package com.project.arebbus.service;

import com.project.arebbus.dto.*;
import com.project.arebbus.exception.BusAlreadyInstalledException;
import com.project.arebbus.exception.BusNotFoundException;
import com.project.arebbus.exception.BusNotInstalledException;
import com.project.arebbus.exception.RouteNotFoundException;
import com.project.arebbus.model.*;
import com.project.arebbus.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service class for bus business logic operations.
 * Handles bus creation, installation, retrieval, and management operations.
 */
@Service
@RequiredArgsConstructor
public class BusService {

    /** Repository for bus data access */
    /** Repository for  data access */
    private final BusRepository busRepository;
    /** Repository for route data access */
    /** Repository for  data access */
    private final RouteRepository routeRepository;
    /** Repository for route-stop relationships */
    /** Repository for  data access */
    private final RouteStopRepository routeStopRepository;
    /** Repository for bus upvote operations */
    /** Repository for  data access */
    private final BusUpvoteRepository busUpvoteRepository;
    /** Repository for bus installation tracking */
    /** Repository for bus installation tracking */
    /** Repository for  data access */
    private final InstallRepository installRepository;

    /**
     * Creates a new bus with the specified details and route.
     * 
     * @param user The user creating the bus
     * @param request The bus creation request with name, route, and capacity
     * @return BusResponse containing the created bus details
     * @throws RouteNotFoundException if the specified route doesn't exist
     */
    /**
     * Creates a new .
     * 
     * @param user The user creating the 
     * @param request The creation request
     * @return BusResponse containing the created 
     */
    public BusResponse createBus(User user, BusCreateRequest request) {
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new RouteNotFoundException(request.getRouteId()));

        Bus bus = Bus.builder()
                .name(request.getName())
                .author(user)
                .route(route)
                .capacity(request.getCapacity())
                .numInstall(0)
                .numUpvote(0L)
                .build();

        Bus savedBus = busRepository.save(bus);

        return buildBusResponse(savedBus, user);
    }

    /**
     * Retrieves a bus by its ID.
     * 
     * @param busId The ID of the bus to retrieve
     * @param user The user requesting the bus (for personalized data)
     * @return BusResponse containing bus details
     * @throws BusNotFoundException if no bus exists with the given ID
     */
    /**
     * Retrieves a  by its ID.
     * 
     * @param id The ID of the  to retrieve
     * @param user The user requesting the 
     * @return BusResponse containing  details
     */
    public BusResponse getBusById(Long busId, User user) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException(busId));

        return buildBusResponse(bus, user);
    }

    /**
     * Retrieves all buses with pagination.
     * 
     * @param user The user requesting buses (for personalized data)
     * @param page The page number (0-based)
     * @param size The number of buses per page
     * @return PagedBusResponse containing buses and pagination info
     */
    /**
     * Retrieves all  with pagination.
     * 
     * @param user The user requesting 
     * @param page The page number
     * @param size The page size
     * @return PagedBusResponse containing  and pagination info
     */
    public PagedBusResponse getAllBuses(User user, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Page<Bus> buses = busRepository.findAll(PageRequest.of(page, size, sort));

        List<BusResponse> busResponses = buses.getContent().stream()
                .map(bus -> buildBusResponse(bus, user))
                .toList();

        return PagedBusResponse.builder()
                .buses(busResponses)
                .page(buses.getNumber())
                .size(buses.getSize())
                .totalPages(buses.getTotalPages())
                .totalElements(buses.getTotalElements())
                .build();
    }

    /**
     * Installs a bus for a user, tracking the installation.
     * 
     * @param user The user installing the bus
     * @param request The installation request containing bus ID
     * @return BusInstallResponse with installation status
     * @throws BusNotFoundException if the bus doesn't exist
     * @throws BusAlreadyInstalledException if user already installed this bus
     */
    public BusInstallResponse installBus(User user, BusInstallRequest request) {
        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new BusNotFoundException(request.getBusId()));

        if (installRepository.existsByUserAndBus(user, bus)) {
            throw new BusAlreadyInstalledException(request.getBusId());
        }

        Install install = Install.builder()
                .userId(user.getId())
                .busId(bus.getId())
                .user(user)
                .bus(bus)
                .build();

        installRepository.save(install);

        bus.setNumInstall(bus.getNumInstall() + 1);
        busRepository.save(bus);

        return BusInstallResponse.builder()
                .busId(bus.getId())
                .busName(bus.getName())
                .message("Bus installed successfully")
                .installed(true)
                .build();
    }

    /**
     * Uninstalls a bus for a user, removing the installation tracking.
     * 
     * @param user The user uninstalling the bus
     * @param request The uninstallation request containing bus ID
     * @return BusInstallResponse with uninstallation status
     * @throws BusNotFoundException if the bus doesn't exist
     * @throws BusNotInstalledException if user hasn't installed this bus
     */
    public BusInstallResponse uninstallBus(User user, BusInstallRequest request) {
        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new BusNotFoundException(request.getBusId()));

        if (!installRepository.existsByUserAndBus(user, bus)) {
            throw new BusNotInstalledException(request.getBusId());
        }

        installRepository.deleteById(new com.project.arebbus.model.InstallId(user.getId(), bus.getId()));

        bus.setNumInstall(bus.getNumInstall() - 1);
        busRepository.save(bus);

        return BusInstallResponse.builder()
                .busId(bus.getId())
                .busName(bus.getName())
                .message("Bus uninstalled successfully")
                .installed(false)
                .build();
    }

    /**
     * Retrieves all buses installed by a specific user with pagination.
     * 
     * @param user The user whose installed buses to retrieve
     * @param page The page number (0-based)
     * @param size The number of buses per page
     * @return PagedBusResponse containing installed buses and pagination info
     */
    public PagedBusResponse getInstalledBuses(User user, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Page<Bus> buses = busRepository.findBusesInstalledByUser(user, PageRequest.of(page, size, sort));

        List<BusResponse> busResponses = buses.getContent().stream()
                .map(bus -> buildBusResponse(bus, user))
                .toList();

        return PagedBusResponse.builder()
                .buses(busResponses)
                .page(buses.getNumber())
                .size(buses.getSize())
                .totalPages(buses.getTotalPages())
                .totalElements(buses.getTotalElements())
                .build();
    }

    /**
     * Builds a complete BusResponse with user-specific data like upvote and install status.
     * 
     * @param bus The bus entity to convert
     * @param user The user for personalized data
     * @return BusResponse with complete bus information
     */
    /**
     * Builds a BusResponse from entity data.
     * 
     * @param entity The entity to convert
     * @return BusResponse with entity data
     */
    private BusResponse buildBusResponse(Bus bus, User user) {
        RouteResponse routeResponse = buildRouteResponse(bus.getRoute());
        
        BusResponse basedOnResponse = null;
        if (bus.getBasedOn() != null) {
            basedOnResponse = BusResponse.builder()
                    .id(bus.getBasedOn().getId())
                    .name(bus.getBasedOn().getName())
                    .authorName(bus.getBasedOn().getAuthor().getName())
                    .capacity(bus.getBasedOn().getCapacity())
                    .numInstall(bus.getBasedOn().getNumInstall())
                    .numUpvote(bus.getBasedOn().getNumUpvote())
                    .status(bus.getBasedOn().getStatus())
                    .upvoted(busUpvoteRepository.existsByUserIdAndBusId(user.getId(), bus.getBasedOn().getId()))
                    .installed(installRepository.existsByUserAndBus(user, bus.getBasedOn()))
                    .rating(bus.getBasedOn().getRating())
                    .build();
        }

        return BusResponse.builder()
                .id(bus.getId())
                .name(bus.getName())
                .authorName(bus.getAuthor().getName())
                .route(routeResponse)
                .capacity(bus.getCapacity())
                .numInstall(bus.getNumInstall())
                .numUpvote(bus.getNumUpvote())
                .status(bus.getStatus())
                .basedOn(basedOnResponse)
                .upvoted(busUpvoteRepository.existsByUserIdAndBusId(user.getId(), bus.getId()))
                .installed(installRepository.existsByUserAndBus(user, bus))
                .rating(bus.getRating())
                .build();
    }

    /**
     * Builds a RouteResponse with all associated stops.
     * 
     * @param route The route entity to convert
     * @return RouteResponse with route and stop information
     */
    /**
     * Builds a RouteResponse from entity data.
     * 
     * @param entity The entity to convert
     * @return RouteResponse with entity data
     */
    private RouteResponse buildRouteResponse(Route route) {
        List<RouteStop> routeStops = routeStopRepository.findByRouteOrderByStopIndex(route);

        List<StopResponse> stopResponses = routeStops.stream()
                .map(routeStop -> StopResponse.builder()
                        .id(routeStop.getStop().getId())
                        .name(routeStop.getStop().getName())
                        .latitude(routeStop.getStop().getLatitude())
                        .longitude(routeStop.getStop().getLongitude())
                        .authorName(routeStop.getStop().getAuthor().getName())
                        .build())
                .toList();

        return RouteResponse.builder()
                .id(route.getId())
                .name(route.getName())
                .authorName(route.getAuthor().getName())
                .stops(stopResponses)
                .build();
    }
}