package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name="QUESTION",schema ="quora")
@NamedQueries(
        {
                @NamedQuery(name="getAllQuestionsByAllUsers", query="select q from QuestionEntity q"),
                @NamedQuery(name = "questionUserByUuid", query = "select u from QuestionEntity u where u.uuid = :uuid"),

        }
)



public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID",length=2)
    private Integer id;
    @Column(name="UUID",length=100)
    private String uuid;
    @Column(name="CONTENT",length=2000)
    private String content;
    @Column(name = "DATE")
    @NotNull
    private ZonedDateTime date;
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
