package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.booker.userId = :userId ORDER BY b.startDate DESC")
    List<Booking> findAllByBookerId(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.userId = :userId " +
            "AND b.startDate < :now AND b.endDate > :now ORDER BY b.startDate DESC")
    List<Booking> findCurrentByBookerId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.userId = :userId " +
            "AND b.endDate < :now ORDER BY b.startDate DESC")
    List<Booking> findPastByBookerId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.userId = :userId " +
            "AND b.startDate > :now ORDER BY b.startDate DESC")
    List<Booking> findFutureByBookerId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.userId = :userId " +
            "AND b.status = :status ORDER BY b.startDate DESC")
    List<Booking> findByBookerIdAndStatus(@Param("userId") Long userId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.itemId = :itemId " +
            "AND b.endDate < :now AND b.status = :status ORDER BY b.startDate ASC")
    Optional<Booking> findLastApprovedByItemId(@Param("itemId") Long itemId,
                                               @Param("now") LocalDateTime now,
                                               @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.itemId = :itemId " +
            "AND b.startDate > :now AND b.status = :status ORDER BY b.startDate ASC")
    Optional<Booking> findNextApprovedByItemId(@Param("itemId") Long itemId,
                                               @Param("now") LocalDateTime now,
                                               @Param("status") BookingStatus status);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.booker.userId = :userId AND b.item.itemId = :itemId AND b.endDate < :now")
    boolean hasUserBookedItem(@Param("userId") Long userId,
                              @Param("itemId") Long itemId,
                              @Param("now") LocalDateTime now);
}