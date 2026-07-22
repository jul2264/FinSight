package com.expense.tracker.dto;

import com.expense.tracker.domain.Category;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(Long id, BigDecimal amount, String description, LocalDate date, Category category) {}
