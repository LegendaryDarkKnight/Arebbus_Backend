package com.project.arebbus.service;

import com.project.arebbus.dto.BusCreateRequest;
import com.project.arebbus.dto.BusResponse;
import com.project.arebbus.dto.PagedBusResponse;
import com.project.arebbus.dto.RouteResponse;
import com.project.arebbus.dto.StopResponse;
import com.project.arebbus.exception.BusNotFoundException;
import com.project.arebbus.exception.RouteNotFoundException;
import com.project.arebbus.model.Bus;
import com.project.arebbus.model.Route;
import com.project.arebbus.model.RouteStop;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.BusRepository;
import com.project.arebbus.repositories.BusUpvoteRepository;
import com.project.arebbus.repositories.RouteRepository;
import com.project.arebbus.repositories.RouteStopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final BusUpvoteRepository busUpvoteRepository;

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

    public BusResponse getBusById(Long busId, User user) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException(busId));

        return buildBusResponse(bus, user);
    }

    public PagedBusResponse getAllBuses(User user, int page, int size) {
        Page<Bus> buses = busRepository.findAll(PageRequest.of(page, size));

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
                .build();
    }

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