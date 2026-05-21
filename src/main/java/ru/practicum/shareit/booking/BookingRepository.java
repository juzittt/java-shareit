package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_UserIdOrderByStartDateDesc(Long userId);

    List<Booking> findByBooker_UserIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long userId, LocalDateTime startDate, LocalDateTime endDate);

    List<Booking> findByBooker_UserIdAndEndDateBeforeOrderByStartDateDesc(Long userId, LocalDateTime endDate);

    List<Booking> findByBooker_UserIdAndStartDateAfterOrderByStartDateDesc(Long userId, LocalDateTime startDate);

    List<Booking> findByBooker_UserIdAndStatusOrderByStartDateDesc(Long userId, BookingStatus status);

    List<Booking> findByItem_Owner_UserIdOrderByStartDateDesc(Long userId);

    List<Booking> findByItem_Owner_UserIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long userId, LocalDateTime startDate, LocalDateTime endDate);

    List<Booking> findByItem_Owner_UserIdAndEndDateBeforeOrderByStartDateDesc(Long userId, LocalDateTime endDate);

    List<Booking> findByItem_Owner_UserIdAndStartDateAfterOrderByStartDateDesc(Long userId, LocalDateTime startDate);

    List<Booking> findByItem_Owner_UserIdAndStatusOrderByStartDateDesc(Long userId, BookingStatus status);

    Optional<Booking> findFirstByItem_ItemIdAndEndDateBeforeAndStatusOrderByStartDateAsc(
            Long itemId, LocalDateTime endDate, BookingStatus status);

    Optional<Booking> findFirstByItem_ItemIdAndStartDateAfterAndStatusOrderByStartDateAsc(
            Long itemId, LocalDateTime startDate, BookingStatus status);

    Boolean existsByBooker_UserIdAndItem_ItemIdAndEndDateBefore(Long userId, Long itemId, LocalDateTime endDate);
}
