package com.stoles.inventory.config;

import com.stoles.inventory.entity.AppUser;
import com.stoles.inventory.entity.WorkType;
import com.stoles.inventory.entity.Worker;
import com.stoles.inventory.repository.AppUserRepository;
import com.stoles.inventory.repository.WorkTypeRepository;
import com.stoles.inventory.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository userRepo;
    private final WorkTypeRepository workTypeRepo;
    private final WorkerRepository workerRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedWorkTypes();
        seedWorkers();
    }

    private void seedUsers() {
        if (userRepo.count() > 0) return;
        List<AppUser> users = List.of(
                AppUser.builder().username("admin").password(encoder.encode("admin123"))
                        .fullName("Administrator").role(AppUser.Role.ADMIN).active(true).build(),
                AppUser.builder().username("javed").password(encoder.encode("javed0225001"))
                        .fullName("Javed Ansari").role(AppUser.Role.ACCOUNT).active(true).build(),
                AppUser.builder().username("manager").password(encoder.encode("manager123"))
                        .fullName("Stock Manager").role(AppUser.Role.MANAGER).active(true).build()
        );
        userRepo.saveAll(users);
        log.info("✔ Default users seeded - admin/admin123 | manager/manager123");
    }

    private void seedWorkTypes() {
        if (workTypeRepo.count() > 0) return;
        workTypeRepo.saveAll(List.of(
                WorkType.builder().name("Discharge").pricePerPiece(new BigDecimal("25.00")).build(),
                WorkType.builder().name("Plain Discharge").pricePerPiece(new BigDecimal("18.00")).build(),
                WorkType.builder().name("Embroidery").pricePerPiece(new BigDecimal("45.00")).build(),
                WorkType.builder().name("Print Work").pricePerPiece(new BigDecimal("30.00")).build(),
                WorkType.builder().name("Zari Work").pricePerPiece(new BigDecimal("55.00")).build(),
                WorkType.builder().name("Fringes").pricePerPiece(new BigDecimal("12.00")).build(),
                WorkType.builder().name("Hand Work").pricePerPiece(new BigDecimal("40.00")).build()));
        log.info("✔ Work types seeded");

    }

    private void seedWorkers() {
        if (workerRepo.count() > 0) return;
        workerRepo.saveAll(List.of(
                Worker.builder().name("Ravi Kumar").phone("9876543210").build(),
                Worker.builder().name("Sunita Devi").phone("9876543211").build(),
                Worker.builder().name("Mohsin Khan").phone("9876543212").build(),
                Worker.builder().name("Priya Sharma").phone("9876543213").build(),
                Worker.builder().name("Arjun Textile").phone("9876543214").build()));
        log.info("✔ Workers seeded");

    }
}