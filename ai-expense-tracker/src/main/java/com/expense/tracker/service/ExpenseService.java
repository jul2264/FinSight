package com.expense.tracker.service;

import com.expense.tracker.domain.Category;
import com.expense.tracker.domain.Expense;
import com.expense.tracker.domain.User;
import com.expense.tracker.dto.ExpenseRequest;
import com.expense.tracker.dto.ExpenseResponse;
import com.expense.tracker.repository.ExpenseRepository;
import com.expense.tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final AiCategorizationService aiCategorizationService;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository, AiCategorizationService aiCategorizationService) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.aiCategorizationService = aiCategorizationService;
    }

    public ExpenseResponse addExpense(ExpenseRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = aiCategorizationService.categorizeExpense(request.description());

        Expense expense = new Expense(request.amount(), request.description(), request.date(), category, user);
        Expense savedExpense = expenseRepository.save(expense);

        return new ExpenseResponse(savedExpense.getId(), savedExpense.getAmount(), savedExpense.getDescription(), savedExpense.getDate(), savedExpense.getCategory());
    }

    public List<ExpenseResponse> getUserExpenses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return expenseRepository.findByUserId(user.getId()).stream()
                .map(e -> new ExpenseResponse(e.getId(), e.getAmount(), e.getDescription(), e.getDate(), e.getCategory()))
                .collect(Collectors.toList());
    }

    public String getMonthlySummary(String username) {
        List<ExpenseResponse> expenses = getUserExpenses(username);
        
        if (expenses.isEmpty()) {
            return "You have no expenses to summarize.";
        }

        Map<Category, java.math.BigDecimal> totalsByCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                        ExpenseResponse::category,
                        Collectors.mapping(ExpenseResponse::amount, Collectors.reducing(java.math.BigDecimal.ZERO, java.math.BigDecimal::add))
                ));

        String expenseData = totalsByCategory.entrySet().stream()
                .map(entry -> entry.getKey() + ": $" + entry.getValue())
                .collect(Collectors.joining(", "));

        return aiCategorizationService.generateMonthlySummary(expenseData);
    }
}
