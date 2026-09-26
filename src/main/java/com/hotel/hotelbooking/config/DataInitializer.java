package com.hotel.hotelbooking.config;

import com.hotel.hotelbooking.model.*;
import com.hotel.hotelbooking.model.enums.RoomStatus;
import com.hotel.hotelbooking.model.enums.UserRole;
import com.hotel.hotelbooking.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds sample data (admin/staff/guest accounts, room types, rooms)
 * ONLY when the database is empty, so re-running the app does not
 * duplicate data.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, GuestRepository guestRepository,
                            RoomTypeRepository roomTypeRepository, RoomRepository roomRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.guestRepository = guestRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // already seeded
        }

        // ---- Users ----
        User admin = new User();
        admin.setName("Hotel Admin");
        admin.setEmail("admin@hotel.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(UserRole.ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);

        User staff = new User();
        staff.setName("Front Desk Staff");
        staff.setEmail("staff@hotel.com");
        staff.setPassword(passwordEncoder.encode("staff123"));
        staff.setRole(UserRole.STAFF);
        staff.setEnabled(true);
        userRepository.save(staff);

        User guestUser = new User();
        guestUser.setName("Demo Guest");
        guestUser.setEmail("guest@hotel.com");
        guestUser.setPassword(passwordEncoder.encode("guest123"));
        guestUser.setRole(UserRole.GUEST);
        guestUser.setEnabled(true);
        userRepository.save(guestUser);

        Guest guest = new Guest();
        guest.setUser(guestUser);
        guest.setFirstName("Demo");
        guest.setLastName("Guest");
        guest.setEmail("guest@hotel.com");
        guest.setPhone("9999999999");
        guest.setAddress("123 Sample Street");
        guest.setIdProof("DEMO-ID-0001");
        guestRepository.save(guest);

        // ---- Room Types ----
        RoomType standard = roomType("Standard Room", "Comfortable room with all basic amenities.", 2, "1500.00");
        RoomType executive = roomType("Executive Room", "Spacious room with work desk, ideal for business travellers.", 2, "2500.00");
        RoomType deluxe = roomType("Deluxe Suite", "Deluxe suite with a separate living area and premium furnishing.", 3, "4000.00");
        RoomType suite = roomType("Suite", "Our finest suite with a private balcony and lounge.", 4, "6000.00");

        roomTypeRepository.save(standard);
        roomTypeRepository.save(executive);
        roomTypeRepository.save(deluxe);
        roomTypeRepository.save(suite);

        // ---- Rooms ----
        saveRoom("101", standard, 1, RoomStatus.AVAILABLE);
        saveRoom("102", standard, 1, RoomStatus.AVAILABLE);
        saveRoom("103", executive, 1, RoomStatus.MAINTENANCE);
        saveRoom("201", executive, 2, RoomStatus.AVAILABLE);
        saveRoom("202", deluxe, 2, RoomStatus.AVAILABLE);
        saveRoom("203", suite, 2, RoomStatus.AVAILABLE);
    }

    private RoomType roomType(String name, String description, int capacity, String price) {
        RoomType rt = new RoomType();
        rt.setName(name);
        rt.setDescription(description);
        rt.setCapacity(capacity);
        rt.setBasePrice(new BigDecimal(price));
        return rt;
    }

    private void saveRoom(String number, RoomType type, int floor, RoomStatus status) {
        Room room = new Room();
        room.setRoomNumber(number);
        room.setRoomType(type);
        room.setFloor(floor);
        room.setStatus(status);
        room.setActive(true);
        roomRepository.save(room);
    }
}
