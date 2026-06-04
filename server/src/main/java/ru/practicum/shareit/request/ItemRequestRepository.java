package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT r FROM ItemRequest r WHERE r.requestor.userId = :userId ORDER BY r.created DESC")
    List<ItemRequest> findAllByRequestorId(@Param("userId") Long userId);
}
