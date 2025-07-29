// BusRepository.java
package com.project.arebbus.repositories;

import com.project.arebbus.model.Bus;
import com.project.arebbus.model.Route;
import com.project.arebbus.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Repository interface for Bus entity data access operations.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 */
public interface BusRepository extends JpaRepository<Bus, Long> {
    
    /**
     * Finds all buses created by a specific author.
     * 
     * @param author The user who created the buses
     * @return List of Bus entities created by the author
     */
    List<Bus> findByAuthor(User author);
    
    /**
     * Finds all buses associated with a specific route.
     * 
     * @param route The route to search for buses
     * @return List of Bus entities on the specified route
     */
    List<Bus> findByRoute(Route route);
    
    /**
     * Finds all buses with a specific status.
     * 
     * @param status The status to filter by
     * @return List of Bus entities with the specified status
     */
    List<Bus> findByStatus(String status);
    
    /**
     * Finds buses with capacity greater than the specified value.
     * 
     * @param capacity The minimum capacity threshold
     * @return List of Bus entities with higher capacity
     */
    List<Bus> findByCapacityGreaterThan(Short capacity);

    // Find buses installed by a specific user (many-to-many through Install entity)
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query
    List<Bus> findBusesInstalledByUser(@Param("user") User user);

    // Find buses installed by a specific user with pagination and sorting
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query
    Page<Bus> findBusesInstalledByUser(@Param("user") User user, Pageable pageable);

    // Alternative approach using Install table directly
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query
    ")
    List<Bus> findBusesInstalledByUserId(@Param("userId") Long userId);

    // Find most popular buses (by install count)
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query
    List<Bus> findBusesByInstallCountDesc();

    // Find buses with most upvotes
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query
    List<Bus> findBusesByUpvoteCountDesc();

    // Find users who installed a specific bus
    /**
     * Custom query method with specific business logic.
     * 
     * @return Query result based on custom implementation
     */
    @Query
    List<User> findUsersWhoInstalledBus(@Param("bus") Bus bus);
}
