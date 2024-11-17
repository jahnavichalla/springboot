import com.example.demo.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalTime;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    @Query("SELECT r FROM Room r WHERE r.id NOT IN (SELECT b.room.id FROM Booking b WHERE b.startTime < :endTime AND b.endTime > :startTime)")
    List<Room> findAvailableRooms(@Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    @Query("SELECT r FROM Room r WHERE r.capacity >= :capacity AND r.floor.floorNumber = :floorNumber AND r.id NOT IN (SELECT b.room.id FROM Booking b WHERE b.startTime < :endTime AND b.endTime > :startTime)")
    List<Room> findRecommendedRooms(@Param("capacity") int capacity, @Param("floorNumber") int floorNumber, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);
}
