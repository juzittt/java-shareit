package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.IlligalArgumentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailibleExceprion;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto addBooking(Long userId, NewBookingRequest request) {
        validateBookingDates(request);

        Item item = validateItem(request.getItemId());
        User user = validateUser(userId);

        checkIfItemNotAvailable(item);

        Booking savedBooking = setFieldsOnAddingNewBooking(user, item, request);
        return bookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = validateBooking(bookingId);
        validateUserIsOwner(userId, booking);

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = validateBooking(bookingId);

        validateOwnerOrBookerOfBooking(userId, booking);
        return bookingMapper.toBookingDto(booking);
    }

    /**
     * Максим, привет!
     * В процессе разработки я столкнулся с двумя этими методами (getBookings и getOwnerBookings)
     * По сути каркас метода, за исключением вызываемых методов внутри, одинаковый, как я мог избежать этого повторения?
     * Еще меня смутило то, что в каждом кейсе у метода я вызываю
     *                     .stream()
     *                     .map(bookingMapper::toBookingDto)
     *                     .toList();
     * Могу ли я как-то это изменить, чтобы сократить код?
     */
    @Override
    public List<BookingDto> getBookings(Long userId, String state) {
        validateUser(userId);

        return switch (state.toUpperCase()) {
            case ("ALL") -> bookingRepository.findByBooker_UserIdOrderByStartDateDesc(userId)
                    .stream()
                    .map(bookingMapper::toBookingDto)
                    .toList();
            case ("CURRENT") ->
                    bookingRepository.findByBooker_UserIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc
                                    (userId, LocalDateTime.now(), LocalDateTime.now())
                            .stream()
                            .map(bookingMapper::toBookingDto)
                            .toList();
            case ("PAST") ->
                    bookingRepository.findByBooker_UserIdAndEndDateBeforeOrderByStartDateDesc(userId, LocalDateTime.now())
                            .stream()
                            .map(bookingMapper::toBookingDto)
                            .toList();
            case ("FUTURE") ->
                    bookingRepository.findByBooker_UserIdAndStartDateAfterOrderByStartDateDesc(userId, LocalDateTime.now())
                            .stream()
                            .map(bookingMapper::toBookingDto)
                            .toList();
            case ("WAITING") ->
                    bookingRepository.findByBooker_UserIdAndStatusOrderByStartDateDesc(userId, BookingStatus.WAITING)
                            .stream()
                            .map(bookingMapper::toBookingDto)
                            .toList();
            case ("REJECTED") ->
                    bookingRepository.findByBooker_UserIdAndStatusOrderByStartDateDesc(userId, BookingStatus.REJECTED)
                            .stream()
                            .map(bookingMapper::toBookingDto)
                            .toList();
            default -> List.of();
        };
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, String state) {
        validateUser(userId);

        return switch (state.toUpperCase()) {
            case ("ALL") -> bookingRepository.findByItem_Owner_UserIdOrderByStartDateDesc(userId)
                    .stream()
                    .map(bookingMapper::toBookingDto)
                    .toList();
            case ("CURRENT") ->
                    bookingRepository.findByItem_Owner_UserIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc
                                    (userId, LocalDateTime.now(), LocalDateTime.now())
                            .stream()
                            .map(bookingMapper::toBookingDto)
                            .toList();
            case ("PAST") ->
                    bookingRepository.findByItem_Owner_UserIdAndEndDateBeforeOrderByStartDateDesc(userId, LocalDateTime.now())
                            .stream()
                            .map(bookingMapper::toBookingDto)
                            .toList();
            case ("FUTURE") ->
                    bookingRepository.findByItem_Owner_UserIdAndStartDateAfterOrderByStartDateDesc(userId, LocalDateTime.now())
                            .stream()
                            .map(bookingMapper::toBookingDto)
                            .toList();
            case ("WAITING") ->
                    bookingRepository.findByItem_Owner_UserIdAndStatusOrderByStartDateDesc(userId, BookingStatus.WAITING)
                            .stream()
                            .map(bookingMapper::toBookingDto)
                            .toList();
            case ("REJECTED") ->
                    bookingRepository.findByItem_Owner_UserIdAndStatusOrderByStartDateDesc(userId, BookingStatus.REJECTED)
                            .stream()
                            .map(bookingMapper::toBookingDto)
                            .toList();
            default -> List.of();
        };
    }

    private void validateBookingDates(NewBookingRequest request) {
        LocalDateTime start = request.getStart();
        LocalDateTime end = request.getEnd();

        if (start.equals(end)) {
            throw new IlligalArgumentException("Время начала и конца бронирования совпадают");
        } else if (end.isBefore(start)) {
            throw new IlligalArgumentException("Конец не может быть раньше начала");
        }
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

    private void checkIfItemNotAvailable(Item item) {
        log.debug("[CheckIfItemNotAvailable] Validating item id: {}", item.getItemId());
        if (!item.getAvailable()) {
            throw new UnavailibleExceprion("Предмет с Id " + item.getItemId() + " не доступен");
        }
    }

    private Booking validateBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("[ValidateBooking - Error] Booking with Id: {} not Found", bookingId);
                    return new NotFoundException("Бронирование с Id " + bookingId + " не найдено");
                });
    }

    private void validateUserIsOwner(Long userId, Booking booking) {
        Long ownerId = booking.getItem().getOwner().getUserId();
        if (!userId.equals(ownerId)) {
            log.warn("[ValidateUserIsOwner - Error] User with id: {}, isn`t owner", userId);
            throw new AccessException("Пользователь с Id " + userId + ". Не является владельцем.");
        }
    }

    private Booking setFieldsOnAddingNewBooking(User user, Item item, NewBookingRequest request) {
        log.debug("[SetFieldsOnAddingNewBooking] Setting fields on adding new booking");
        Booking booking = bookingMapper.toBookingEntity(request);

        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    private void validateOwnerOrBookerOfBooking(Long userId, Booking booking) {
        Long ownerId = booking.getItem().getOwner().getUserId();
        Long bookerId = booking.getBooker().getUserId();

        if (!userId.equals(ownerId) && !userId.equals(bookerId)) {
            log.warn("[ValidateOwnerOrBookerOfBooking - Error] User with id: {}, isn`t owner or booker of booking {}",
                    userId, booking.getId());
            throw new AccessException("Пользователь с Id " + userId +
                    ". Не является владельцем предмета или заказчиком сделки c Id " + booking.getId());
        }
    }
}
