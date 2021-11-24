package by.nyurush.portal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

@Table(name = "user_answer", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="exam_id")
    private Exam exam;

    @ManyToOne
    @JoinColumn(name="question_id")
    private Question question;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_chosen_answer",
            joinColumns = {@JoinColumn(name = "user_answer_id")},
            inverseJoinColumns = {@JoinColumn(name = "answer_id")}
    )
    private List<Answer> answerList;
}
