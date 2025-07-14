package com.project.arebbus.service;

import com.project.arebbus.dto.BusCreateRequest;
import com.project.arebbus.dto.BusInstallRequest;
import com.project.arebbus.dto.BusInstallResponse;
import com.project.arebbus.dto.BusResponse;
import com.project.arebbus.dto.PagedBusResponse;
import com.project.arebbus.dto.RouteResponse;
import com.project.arebbus.dto.StopResponse;
import com.project.arebbus.exception.BusAlreadyInstalledException;
import com.project.arebbus.exception.BusNotFoundException;
import com.project.arebbus.exception.BusNotInstalledException;
import com.project.arebbus.exception.RouteNotFoundException;
import com.project.arebbus.model.Bus;
import com.project.arebbus.model.Install;
import com.project.arebbus.model.Route;
import com.project.arebbus.model.RouteStop;
import com.project.arebbus.model.User;
import com.project.arebbus.repositories.BusRepository;
import com.project.arebbus.repositories.BusUpvoteRepository;
import com.project.arebbus.repositories.InstallRepository;
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
    private final InstallRepository installRepository;

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

    public PagedBusResponse getInstalledBuses(User user, int page, int size) {
        List<Install> installs = installRepository.findByUser(user);
        
        List<Bus> installedBuses = installs.stream()
                .map(Install::getBus)
                .toList();

        int start = page * size;
        int end = Math.min(start + size, installedBuses.size());
        List<Bus> pagedBuses = installedBuses.subList(start, end);

        List<BusResponse> busResponses = pagedBuses.stream()
                .map(bus -> buildBusResponse(bus, user))
                .toList();

        int totalPages = (int) Math.ceil((double) installedBuses.size() / size);

        return PagedBusResponse.builder()
                .buses(busResponses)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .totalElements(installedBuses.size())
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
                    .installed(installRepository.existsByUserAndBus(user, bus.getBasedOn()))
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