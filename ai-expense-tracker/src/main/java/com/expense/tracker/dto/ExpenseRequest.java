package com.expense.tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(BigDecimal amount, String description, LocalDate date) {}
