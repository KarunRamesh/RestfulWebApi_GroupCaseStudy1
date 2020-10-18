package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name="ANSWER",schema ="quora")
@NamedQueries(
        {
                @NamedQuery(name = "getAnswerById", query = "select ans from AnswerEntity ans where ans.id=:id"),
                @NamedQuery(name = "deleteAnswer", query = "delete from AnswerEntity  ans where ans.id=:id"),
                @NamedQuery(name="getAllAnswersForQuestion", query="select ansList from AnswerEntity ansList where ansList.uuid = :questionId")
        }
)
public class AnswerEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID",length=30)
    private Integer id;
    @Column(name="UUID",length=40)
    private String uuid;
    @Column(name="ANS",length=2000)
    private String ans;

    @Column(name = "EXPIRES_AT")
    @NotNull
    private ZonedDateTime  date;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    private  QuestionEntity questionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public QuestionEntity getQuestionId() {
        return questionId;
    }

    public void setQuestionId(QuestionEntity questionId) {
        this.questionId = questionId;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
