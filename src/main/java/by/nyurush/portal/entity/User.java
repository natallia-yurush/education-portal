package by.nyurush.portal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Table(name = "user", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "complete_name")
    private String completeName;

    @Column(name = "email")
    private String email;

    @Column(name = "photo", columnDefinition="mediumblob")
    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] photo;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private UserRole role;

    @Column(name = "active")
    private boolean active;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_course",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "course_id")}
    )
    private List<Course> courseList;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_exam",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "exam_id")}
    )
    private List<Exam> examList;
}
