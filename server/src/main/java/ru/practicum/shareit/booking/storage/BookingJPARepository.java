package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingJPARepository extends JpaRepository<Booking, Long> {
    @Query(value = "select * from bookings as bk where booker_id = ?1 order by bk.start_date desc",
            nativeQuery = true)
    List<Booking> findByBookerId(Long bookerId);

    @Query(value = "select * " +
            "from bookings " +
            "where booker_id = :bookerId " +
            "and start_date < :now " +
            "and end_date > :now order by start_date desc", nativeQuery = true)
    List<Booking> findByBookerIdAndCurrent(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query(value = "select * " +
            "from bookings " +
            "where booker_id = :bookerId " +
            "and end_date < :now order by start_date desc", nativeQuery = true)
    List<Booking> findByBookerIdAndPast(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query(value = "select * " +
            "from bookings " +
            "where booker_id = :bookerId " +
            "and start_date > :now order by start_date desc", nativeQuery = true)
    List<Booking> findByBookerIdAndFuture(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query(value = "select * " +
            "from bookings " +
            "where booker_id = :bookerId " +
            "and status = :status order by start_date desc", nativeQuery = true)
    List<Booking> findByBookerIdAndStatus(@Param("bookerId") Long bookerId, @Param("status") Status status);

    @Query(value = "select * " +
            "from bookings " +
            "where item_id in (select id from items where owner_id = :ownerId) " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByItemOwnerId(@Param("ownerId") Long ownerId);

    @Query(value = "select * " +
            "from bookings " +
            "where item_id in (select id from items where owner_id = :ownerId) " +
            "and start_date < :now " +
            "and end_date > :now " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByItemOwnerIdAndCurrent(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query(value = "select * " +
            "from bookings " +
            "where item_id in (select id from items where owner_id = :ownerId) " +
            "and end_date < :now " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByItemOwnerIdAndPast(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query(value = "select * " +
            "from bookings " +
            "where item_id in (select id from items where owner_id = :ownerId) " +
            "and start_date > :now " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByItemOwnerIdAndFuture(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query(value = "select * " +
            "from bookings " +
            "where item_id in (select id from items where owner_id = :ownerId) " +
            "and status = :status " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByItemOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") Status status);


    @Query(value = "select * " +
            "from bookings " +
            "where booker_id = :userId " +
            "and item_id = :itemId", nativeQuery = true)
    Optional<Booking> findByBookerIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);

    @Query(value = "select * " +
            "from bookings " +
            "where item_id = :itemId " +
            "order by id desc " +
            "limit 1", nativeQuery = true)
    Booking findLastBookingByItemId(@Param("itemId") Long itemId);

    @Query(value = "select * " +
            "from bookings " +
            "where id = (select id " +
            "from bookings " +
            "where item_id = :itemId " +
            "order by id desc " +
            "limit 1) - 1", nativeQuery = true)
    Booking findSecondLastBookingByItemId(@Param("itemId") Long itemId);
}