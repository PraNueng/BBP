package com.example.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "grades", indexes = {
        @Index(name = "idx_order", columnList = "grade_order")
})
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grade_name", nullable = false, unique = true, length = 50)
    private String gradeName;

    @Column(name = "grade_order", nullable = false, unique = true)
    private Integer gradeOrder;

    @Column(name = "next_grade_id")
    private Long nextGradeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_grade_id", insertable = false, updatable = false)
    private Grade nextGrade;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

    public Integer getGradeOrder() { return gradeOrder; }
    public void setGradeOrder(Integer gradeOrder) { this.gradeOrder = gradeOrder; }

    public Long getNextGradeId() { return nextGradeId; }
    public void setNextGradeId(Long nextGradeId) { this.nextGradeId = nextGradeId; }

    public Grade getNextGrade() { return nextGrade; }
    public void setNextGrade(Grade nextGrade) { this.nextGrade = nextGrade; }
}