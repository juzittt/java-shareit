package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.owner.userId = :ownerId")
    Page<Item> findByOwnerId(@Param("ownerId")Long userId, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.available = :available " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    Page<Item> searchAvailableItems(@Param("text") String text,
                                    @Param("available") Boolean available,
                                    Pageable pageable);}
