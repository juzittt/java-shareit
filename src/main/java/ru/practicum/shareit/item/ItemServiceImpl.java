package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final ItemMapper itemMapper;

    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Override
    public ItemDto addItem(Long userId, NewItemRequest request) {
        User user = validateUser(userId);
        Item item = itemMapper.newItemRequest(request);
        item.setOwner(user);

        Item savedItem = itemRepository.save(item);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest request) {
        validateUser(userId);
        Item item = validateItem(itemId);
        validateOwner(userId, item);

        itemMapper.updateItemFromRequest(item, request);
        return itemMapper.toItemDto(item);
    }

    @Override
    public void deleteItem(Long itemId, Long userId) {
        Item item = validateItem(itemId);
        validateOwner(userId, item);
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.debug("[GetItemById] Getting item with id: {}", itemId);
        return itemMapper.toItemDto(getItemEntity(itemId));
    }

    @Override
    public ItemBooking getItem(Long userId, Long itemId) {
        validateUser(userId);

        Item item = validateItem(itemId);

        return collectBookingAndComment(item, userId);
    }

    @Override
    public List<ItemBooking> getItems(Long userId) {
        validateUser(userId);

        List<ItemBooking> items = itemRepository.findByOwner_UserId(userId)
                .stream()
                .map(item -> collectBookingAndComment(item, userId))
                .toList();
        return items;
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        validateUser(userId);

        if (text.isBlank()) {
            return List.of();
        }

        return itemRepository.findByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCase(text, text)
                .stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, NewCommentRequest request) {
        User user = validateUser(userId);
        Item item = validateItem(itemId);

        validateBookerOfItem(item, userId);

        Comment comment = commentMapper.toCommentEntity(request);
        comment.setItem(item);
        comment.setCommentator(user);

        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toCommentDto(savedComment);
    }

    public Item getItemEntity(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[getItemEntity - Error] Item with id:{} not found", id);
                    return new NotFoundException("Предмет с id = " + id + " не найден.");
                });
    }

    private User validateUser(Long userId) {
        log.debug("[ValidateUser] Validating user id: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[ValidateUser] User with id: {} not found", userId);
                    return new NotFoundException("Пользователь с Id " + userId + ", не найден");
                });
    }

    private Item validateItem(Long itemId) {
        log.debug("[ValidateItem] Validating item id: {}", itemId);
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("[ValidateItem] Item with id: {} not found", itemId);
                    return new NotFoundException("Предмет с Id " + itemId + ", не найден");
                });
    }

    private void validateOwner(Long userId, Item item) {
        log.debug("[ValidateOwner] Validating owner id: {} for item with id: {}", userId, item.getItemId());
        Long ownerId = item.getOwner().getUserId();
        if (!userId.equals(ownerId)) {
            log.warn("[ValidateOwner - Error] User with id: {}, isn`t owner of item {}", userId, item.getItemId());
            throw new AccessException("Пользователь с Id " + userId +
                    ". Не является владельцем предмета c Id " + item.getItemId());
        }
    }

    private void validateBookerOfItem(Item item, Long userId) {
        Boolean isBooker = bookingRepository
                .existsByBooker_UserIdAndItem_ItemIdAndEndDateBefore(userId, item.getItemId(), LocalDateTime.now());

        if (!isBooker) {
            throw new AccessException("Пользователь с Id " + userId + " не бронировал объект с Id " + item.getItemId());
        }
    }

    private ItemBooking collectBookingAndComment(Item item, Long userId) {
        ItemBooking itemBooking = itemMapper.toItemBooking(item);
        Long itemOwnerId = item.getOwner().getUserId();

        if (userId.equals(itemOwnerId)) {
            itemBooking.setLastBooking(bookingRepository
                    .findFirstByItem_ItemIdAndEndDateBeforeAndStatusOrderByStartDateAsc(item.getItemId(),
                            LocalDateTime.now(), BookingStatus.APPROVED)
                    .map(bookingMapper::toItemBookingsTimes)
                    .orElse(null));

            itemBooking.setNextBooking(bookingRepository
                    .findFirstByItem_ItemIdAndStartDateAfterAndStatusOrderByStartDateAsc(item.getItemId(),
                            LocalDateTime.now(), BookingStatus.APPROVED)
                    .map(bookingMapper::toItemBookingsTimes)
                    .orElse(null));
        }

        itemBooking.setComments(commentRepository.findByItem_ItemIdOrderByCreatedDesc(item.getItemId())
                .stream()
                .map(commentMapper::toCommentDto)
                .toList());

        return itemBooking;
    }
}
