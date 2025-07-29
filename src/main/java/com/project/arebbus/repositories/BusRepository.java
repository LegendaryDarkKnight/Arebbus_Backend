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
    @Query(nativeQuery = true, value="SELECT b FROM Bus b JOIN b.installs i WHERE i.user = :user")
    List<Bus> findBusesInstalledByUser(@Param("user") User user);

    // Find buses installed by a specific user with pagination and sorting
    @Query("SELECT b FROM Bus b JOIN b.installs i WHERE i.user = :user")
    Page<Bus> findBusesInstalledByUser(@Param("user") User user, Pageable pageable);

    // Alternative approach using Install table directly
    @Query("SELECT b FROM Bus b WHERE b.id IN (SELECT i.busId FROM Install i WHERE i.userId = :userId)")
    List<Bus> findBusesInstalledByUserId(@Param("userId") Long userId);

    // Find most popular buses (by install count)
    @Query("SELECT b FROM Bus b ORDER BY b.numInstall DESC")
    List<Bus> findBusesByInstallCountDesc();

    // Find buses with most upvotes
    @Query("SELECT b FROM Bus b ORDER BY b.numUpvote DESC")
    List<Bus> findBusesByUpvoteCountDesc();

    // Find users who installed a specific bus
    @Query("SELECT u FROM User u JOIN u.installations i WHERE i.bus = :bus")
    List<User> findUsersWhoInstalledBus(@Param("bus") Bus bus);
}