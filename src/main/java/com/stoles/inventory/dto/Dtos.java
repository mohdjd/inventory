package com.stoles.inventory.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stoles.inventory.entity.AppUser;
import com.stoles.inventory.entity.DispatchStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Dtos {

    // - Auth
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LoginRequest {
        @NotBlank String username;
        @NotBlank String password;
    }
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AuthResponse {
        private String token;
        private String username;
        private String fullName;
        private String role;
    }

    // - User
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserRequest {
        @NotBlank String username;
        @NotBlank @Size(min = 6) String password;
        @NotBlank String fullName;
        @NotNull AppUser.Role role;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserResponse {
        private Long id;
        private String username;
        private String fullName;
        private String role;
        private boolean active;
        private LocalDateTime createdAt;
    }

    //-Stock
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StockRequest {
        @NotBlank String label;
        @NotBlank String fabric;
        @NotBlank String size;
        @NotBlank String weight;
        @NotNull @Min(1) Integer quantity;
        @NotNull LocalDate purchaseDate;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StockResponse {
        private Long id;
        private String label;
        private String fabric;
        private String size;
        private String weight;
        private Integer quantity;
        private LocalDate purchaseDate;
        private String createdBy;           // "Full Name (@username)"
        private LocalDateTime createdAt;
    }

    // - Worker
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class WorkerRequest {
        @NotBlank String name;
        private String phone;
        private String address;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WorkerResponse {
        private Long id;
        private String name;
        private String phone;
        private String address;
        private String createdBy;
        private LocalDateTime createdAt;

    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class WorkerSummaryResponse {
        private Long workerId;
        private String workerName;
        private String phone;
        private Integer totalSent;
        private Integer totalReceived;
        private Integer pending;
        private Long totalJobs;
        private BigDecimal totalEarned;
        private BigDecimal totalPaid;
        private BigDecimal outstanding;
    }

    // - WorkType
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class WorkTypeRequest {
        @NotBlank String name;
        @NotNull @DecimalMin("0.0") BigDecimal pricePerPiece;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class WorkTypeResponse {
        private Long id;
        private String name;
        private BigDecimal pricePerPiece;
        private String createdBy;
        private LocalDateTime createdAt;
    }

    // - Dispatch
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DispatchRequest {
        @NotNull Long stockItemId;
        @NotNull Long workerId;
        @NotNull Long workTypeId;
        private BigDecimal pricePerPiece;
        @NotNull @Min(1) Integer sentQty;
        @NotNull LocalDate sentDate;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class DispatchResponse {
        private Long id;
        private Long stockItemId;
        private String label;
        private String fabric;
        private String size;
        private String weight;
        private Long workerId;
        private String workerName;
        private Long workTypeId;
        private String workTypeName;
        private BigDecimal pricePerPiece;
        private Integer sentQty;
        private Integer receivedQty;
        private Integer pendingQty;
        private LocalDate sentDate;
        private LocalDate receivedDate;
        private DispatchStatus status;
        private BigDecimal totalPayable;
        private String createdBy;       // who dispatched
        private String receivedBy;      // who recorded receipt
        private LocalDateTime createdAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ReceiveRequest {
        @NotNull @Min(1) Integer quantity;
        @NotNull LocalDate receivedDate;
    }

    //-Payment
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PaymentRequest {
        @NotNull Long workerId;
        @NotNull @DecimalMin("0.01") BigDecimal amount;
        @NotNull LocalDate paymentDate;
        private String paymentMode;
        private String referenceNo;
        private String remarks;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PaymentResponse {
        private Long id;
        private Long workerId;
        private String workerName;
        private BigDecimal amount;
        private LocalDate paymentDate;
        private String paymentMode;
        private String referenceNo;
        private String remarks;
        private String createdBy;       // who sent the payment
        private LocalDateTime createdAt;
    }

    //- Dashboard
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DashboardResponse {
        private Integer totalStockPieces;
        private Integer totalSent;
        private Integer totalReceived;
        private Integer totalPendingAtWorker;
        private Integer availableStock;
        private BigDecimal totalEarned;
        private BigDecimal totalPaid;
        private BigDecimal totalOutstanding;
        private List<WorkerSummaryResponse> workerSummary;
        private List<DispatchResponse> recentDispatches;
    }

    // - Error
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ErrorResponse {
        private int status;
        private String error;
        private Map<String, String> fieldErrors;
        private LocalDateTime timestamp;
    }
}