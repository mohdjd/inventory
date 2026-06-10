package com.stoles.inventory.controller;

import com.stoles.inventory.dto.Dtos;
import com.stoles.inventory.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Dtos.AuthResponse login(@Valid @RequestBody Dtos.LoginRequest req) {
        return authService.login(req);
    }
}

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @GetMapping
    public List<Dtos.UserResponse> list() {
        return userService.findAll();
    }

    @PostMapping
    public ResponseEntity<Dtos.UserResponse> create(@Valid @RequestBody Dtos.UserRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(req));
    }

    @PatchMapping("/{id}/toggle-active")
    public Dtos.UserResponse toggle(@PathVariable Long id) {
        return userService.toggleActive(id);
    }

    @PatchMapping("/{id}/reset-password")
    public Dtos.UserResponse resetPwd(@PathVariable Long id, @RequestBody String pwd) {
        return userService.resetPassword(id, pwd);
    }
}

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public Dtos.DashboardResponse get() {
        return dashboardService.summary();
    }
}

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
class StockController {

    private final StockService stockService;

    @GetMapping
    public List<Dtos.StockResponse> list() {
        return stockService.findAll();
    }

    @PostMapping
    public ResponseEntity<Dtos.StockResponse> create(@Valid @RequestBody Dtos.StockRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.create(req));
    }

    @PutMapping("/{id}")
    public Dtos.StockResponse update(@PathVariable Long id, @Valid @RequestBody Dtos.StockRequest req) {
        return stockService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
class WorkerController {

    private final WorkerService workerService;
    private final DispatchService dispatchService;

    @GetMapping
    public List<Dtos.WorkerResponse> list() {
        return workerService.findAll();
    }

    @PostMapping
    public ResponseEntity<Dtos.WorkerResponse> create(@Valid @RequestBody Dtos.WorkerRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workerService.create(req));
    }

    @PutMapping("/{id}")
    public Dtos.WorkerResponse update(@PathVariable Long id, @Valid @RequestBody Dtos.WorkerRequest req) {
        return workerService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public List<Dtos.WorkerSummaryResponse> summary() {
        return dispatchService.workerSummary();
    }
}

@RestController
@RequestMapping("/api/work-types")
@RequiredArgsConstructor
class WorkTypeController {

    private final WorkTypeService workTypeService;

    @GetMapping
    public List<Dtos.WorkTypeResponse> list() {
        return workTypeService.findAll();
    }

    @PostMapping
    public ResponseEntity<Dtos.WorkTypeResponse> create(@Valid @RequestBody Dtos.WorkTypeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workTypeService.create(req));
    }

    @PutMapping("/{id}")
    public Dtos.WorkTypeResponse update(@PathVariable Long id, @Valid @RequestBody Dtos.WorkTypeRequest req) {
        return workTypeService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

@RestController
@RequestMapping("/api/dispatches")
@RequiredArgsConstructor
class DispatchController {

    private final DispatchService dispatchService;

    @GetMapping
    public List<Dtos.DispatchResponse> list(
            @RequestParam(required = false) Long workerId,
            @RequestParam(required = false) String search) {
        return dispatchService.findAll(workerId, search);
    }

    @GetMapping("/pending")
    public List<Dtos.DispatchResponse> pending() {
        return dispatchService.findPending();
    }

    @PostMapping
    public ResponseEntity<Dtos.DispatchResponse> create(@Valid @RequestBody Dtos.DispatchRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dispatchService.create(req));
    }

    @PatchMapping("/{id}/receive")
    public Dtos.DispatchResponse receive(
            @PathVariable Long id,
            @Valid @RequestBody Dtos.ReceiveRequest req) {
        return dispatchService.recordReceipt(id, req);
    }
}

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<Dtos.PaymentResponse> list(
            @RequestParam(required = false) Long workerId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        LocalDate f = from != null ? LocalDate.parse(from) : null;
        LocalDate t = to != null ? LocalDate.parse(to) : null;

        return paymentService.filter(workerId, f, t);
    }

    @PostMapping
    public ResponseEntity<Dtos.PaymentResponse> create(@Valid @RequestBody Dtos.PaymentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
