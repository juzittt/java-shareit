package ru.practicum.shareit.item;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Data
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final ItemMapper itemMapper;

    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto addItem(Long userId, NewItemRequest request) {
        User user = validateUser(userId);
        Item item = itemMapper.newItemRequest(request);
        item.setOwner(user);

        if (request.getRequestId() != null) {
            ItemRequest itemRequest = checkItemRequestExists(request.getRequestId());
            item.setRequest(itemRequest);
        }

        Item savedItem = itemRepository.save(item);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest request) {
        validateUser(userId);
        Item item = validateItem(itemId);
        validateOwner(userId, item);

        itemMapper.updateItemFromRequest(item, request);
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId, Long userId) {
        Item item = validateItem(itemId);
        validateOwner(userId, item);
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public ItemBooking getItem(Long userId, Long itemId) {
        validateUser(userId);

        Item item = validateItem(itemId);

        return collectBookingAndComment(item, userId);
    }

    @Override
    public Page<ItemBooking> getItems(Long userId, Integer from, Integer size) {
        validateUser(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("itemId").descending());

        Page<ItemBooking> items = itemRepository.findByOwnerId(userId, pageable)
                .map(item -> collectBookingAndComment(item, userId));
        return items;
    }

    @Override
    public Page<ItemDto> searchItems(Long userId, String text, Integer from, Integer size) {
        log.info("[searchItems] Searching items. userId={}, text='{}'", userId, text);
        validateUser(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").descending());
        Boolean available = true;

        if (text.isBlank()) {
            return Page.empty();
        }

        Page<ItemDto> result = itemRepository.searchAvailableItems(text, available, pageable)
                .map(itemMapper::toItemDto);

        log.info("[searchItems] Researched items: {} by request '{}'", result.getTotalElements(), text);
        return result;
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, NewCommentRequest request) {
        User user = validateUser(userId);
        Item item = validateItem(itemId);

        validateBookerOfItem(item, userId);

        Comment comment = commentMapper.toCommentEntity(request);
        comment.setItem(item);
        comment.setCommentator(user);
        comment.setCreated(LocalDateTime.now());

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
        log.debug("[validateBookerOfItem] Validate user is booker of item. userId={}, itemId={}", userId, item.getItemId());
        Boolean isBooker = bookingRepository.hasUserBookedItem(userId, item.getItemId(), LocalDateTime.now());

        if (!isBooker) {
            log.warn("[validateBookerOfItem - Error] User not a booker of item. userId={}, itemId={}", userId, item.getItemId());
            throw new AccessException("Пользователь с Id " + userId + " не бронировал объект с Id " + item.getItemId());
        }
    }

    private ItemBooking collectBookingAndComment(Item item, Long userId) {
        ItemBooking itemBooking = itemMapper.toItemBooking(item);
        Long itemOwnerId = item.getOwner().getUserId();

        if (userId.equals(itemOwnerId)) {
            itemBooking.setLastBooking(bookingRepository
                    .findLastApprovedByItemId(item.getItemId(), LocalDateTime.now(), BookingStatus.APPROVED)
                    .map(bookingMapper::toItemBookingsTimes)
                    .orElse(null));

            itemBooking.setNextBooking(bookingRepository
                    .findNextApprovedByItemId(item.getItemId(), LocalDateTime.now(), BookingStatus.APPROVED)
                    .map(bookingMapper::toItemBookingsTimes)
                    .orElse(null));
        }

        itemBooking.setComments(commentRepository.findByItem_ItemIdOrderByCreatedDesc(item.getItemId())
                .stream()
                .map(commentMapper::toCommentDto)
                .toList());

        return itemBooking;
    }

    private ItemRequest checkItemRequestExists(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с Id: " + requestId + " не найден"));
    }
}
