package by.nyurush.portal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@Table(name = "answer", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Getter
@Setter
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text", unique = true)
    private String text;

    @ManyToMany(mappedBy = "answerList", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<Question> questionList;

    @ManyToMany(mappedBy = "correctAnswerList", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<Question> questionsToCorrectAnswers;
}
