package com.expense.tracker.controller;

import com.expense.tracker.dto.ExpenseRequest;
import com.expense.tracker.dto.ExpenseResponse;
import com.expense.tracker.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense(@RequestBody ExpenseRequest request, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(expenseService.addExpense(request, username));
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getExpenses(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(expenseService.getUserExpenses(username));
    }

    @GetMapping("/summary")
    public ResponseEntity<String> getSummary(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(expenseService.getMonthlySummary(username));
    }
}
