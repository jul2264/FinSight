package com.expense.tracker.service;

import com.expense.tracker.domain.Category;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AiCategorizationService {

    private final ChatClient chatClient;

    public AiCategorizationService(ObjectProvider<ChatClient.Builder> chatClientBuilderProvider) {
        ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
        this.chatClient = builder != null ? builder.build() : null;
    }

    @Cacheable(value = "categorizations", key = "#description.toLowerCase()")
    public Category categorizeExpense(String description) {
        if (chatClient == null) {
            return Category.UNCATEGORIZED;
        }

        String prompt = "Categorize the following expense description into ONE of these categories: " +
                "GROCERIES, RENT, ENTERTAINMENT, TRANSPORTATION, DINING, UTILITIES, HEALTHCARE, SHOPPING, MISC, UNCATEGORIZED. " +
                "Respond with exactly one word (the category name) and nothing else. " +
                "Description: " + description;

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        if (response == null || response.trim().isEmpty()) {
            return Category.UNCATEGORIZED;
        }

        String upperResponse = response.toUpperCase();
        for (Category cat : Category.values()) {
            if (cat != Category.UNCATEGORIZED && upperResponse.contains(cat.name())) {
                return cat;
            }
        }
        
        return Category.UNCATEGORIZED;
    }
    
    public String generateMonthlySummary(String expenseData) {
        if (chatClient == null) {
            return "AI summary unavailable because the chat client is not configured.";
        }

        String prompt = "You are an AI financial advisor. Review this monthly expense data and provide a short, plain-English summary (max 2 sentences) of the spending habits. Data: " + expenseData;
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
