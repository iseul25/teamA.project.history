package com.lms.history.quizzes.service;

import com.lms.history.quizzes.entity.Attempt;
import com.lms.history.quizzes.entity.Category;
import com.lms.history.quizzes.entity.Quiz;
import com.lms.history.quizzes.entity.Score;
import com.lms.history.quizzes.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QuizService {
    private QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public List<Category> findAll() {
        return quizRepository.findAll();
    }

    public List<Category> findByQuizType(String quizType) {
        return quizRepository.findByQuizType(quizType);
    }

    public List<Map<String, Object>> findByQuizTypeWithScores(String quizType, int userId) {
        return quizRepository.findCategoriesWithScores(quizType, userId);
    }

    public void deleteByCategoryId(int quizCategoryId) {
        quizRepository.deleteByQuizCategoryId(quizCategoryId);
    }

    public List<Map<String, Object>> findScoresByQuizCategoryId(int quizCategoryId) {
        return quizRepository.findScoresByQuizCategoryId(quizCategoryId);
    }
}