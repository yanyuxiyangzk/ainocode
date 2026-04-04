package com.ruoyi.nocode.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Dynamic Route Manager
 *
 * Supports runtime route updates via Nacos config change
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicRouteManager {

    private final RouteDefinitionWriter routeDefinitionWriter;
    private final ApplicationEventPublisher publisher;

    /**
     * Add a new route
     */
    public Mono<Void> addRoute(RouteDefinition definition) {
        log.info("Adding route: {}", definition.getId());
        return routeDefinitionWriter.save(Mono.just(definition))
                .then(Mono.defer(() -> {
                    publisher.publishEvent(new RefreshRoutesEvent(this));
                    return Mono.empty();
                }));
    }

    /**
     * Update an existing route
     */
    public Mono<Void> updateRoute(RouteDefinition definition) {
        log.info("Updating route: {}", definition.getId());
        return routeDefinitionWriter.delete(Mono.just(definition.getId()))
                .then(addRoute(definition));
    }

    /**
     * Delete a route
     */
    public Mono<Void> deleteRoute(String routeId) {
        log.info("Deleting route: {}", routeId);
        return routeDefinitionWriter.delete(Mono.just(routeId))
                .then(Mono.defer(() -> {
                    publisher.publishEvent(new RefreshRoutesEvent(this));
                    return Mono.<Void>empty();
                }))
                .onErrorResume(NotFoundException.class, e -> {
                    log.warn("Route not found: {}", routeId);
                    return Mono.<Void>empty();
                });
    }

    /**
     * Batch add routes
     */
    public Mono<Void> addRoutes(List<RouteDefinition> definitions) {
        return Mono.when(definitions.stream()
                .map(this::addRoute)
                .toList());
    }

    /**
     * Refresh all routes
     */
    public void refreshRoutes() {
        log.info("Refreshing all routes");
        publisher.publishEvent(new RefreshRoutesEvent(this));
    }
}
