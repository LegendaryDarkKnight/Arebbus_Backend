package com.project.arebbus.service;

import com.project.arebbus.dto.PagedRouteResponse;
import com.project.arebbus.dto.RouteCreateRequest;
import com.project.arebbus.dto.RouteResponse;
import com.project.arebbus.dto.StopResponse;
import com.project.arebbus.exception.RouteNotFoundException;
import com.project.arebbus.exception.StopNotFoundException;
import com.project.arebbus.model.Route;
import com.project.arebbus.model.RouteStop;
import com.project.arebbus.model.Stop;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.RouteRepository;
import com.project.arebbus.repositories.RouteStopRepository;
import com.project.arebbus.repositories.StopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final StopRepository stopRepository;

    @Transactional
    public RouteResponse createRoute(User user, RouteCreateRequest request) {
        Route route = Route.builder()
                .name(request.getName())
                .author(user)
                .build();

        Route savedRoute = routeRepository.save(route);

        List<StopResponse> stopResponses = createRouteStops(savedRoute, request.getStopIds());

        return RouteResponse.builder()
                .id(savedRoute.getId())
                .name(savedRoute.getName())
                .authorName(savedRoute.getAuthor().getName())
                .stops(stopResponses)
                .build();
    }

    private List<StopResponse> createRouteStops(Route route, List<Long> stopIds) {
        List<StopResponse> stopResponses = new java.util.ArrayList<>();
        
        for (int i = 0; i < stopIds.size(); i++) {
            Long stopId = stopIds.get(i);
            Stop stop = stopRepository.findById(stopId)
                    .orElseThrow(() -> new StopNotFoundException(stopId));

            RouteStop routeStop = RouteStop.builder()
                    .routeId(route.getId())
                    .stopId(stopId)
                    .stopIndex((long) i)
                    .route(route)
                    .stop(stop)
                    .build();

            routeStopRepository.save(routeStop);

            stopResponses.add(StopResponse.builder()
                    .id(stop.getId())
                    .name(stop.getName())
                    .latitude(stop.getLatitude())
                    .longitude(stop.getLongitude())
                    .authorName(stop.getAuthor().getName())
                    .build());
        }

        return stopResponses;
    }

    public RouteResponse getRouteById(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException(routeId));

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

    public PagedRouteResponse getAllRoutes(int page, int size) {
        Page<Route> routes = routeRepository.findAll(PageRequest.of(page, size));

        List<RouteResponse> routeResponses = routes.getContent().stream()
                .map(route -> {
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
                })
                .toList();

        return PagedRouteResponse.builder()
                .routes(routeResponses)
                .page(routes.getNumber())
                .size(routes.getSize())
                .totalPages(routes.getTotalPages())
                .totalElements(routes.getTotalElements())
                .build();
    }
}