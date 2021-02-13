package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
// POJO class for user_auth table
//@Entity annotation specifies that the class is an entity and is mapped to a database table.
@Entity
// @Table annotation specifies that the entity is mapped to the specified(name = "user_auth") table in the database
@Table(name = "user_auth")

//@NamedQueries JPA annotation Specifies a static, named query
@NamedQueries({
        @NamedQuery(
                name = "userAuthByAccessToken",
                query = "select u from UserAuthEntity u where u.accessToken=:accessToken")
})
public class UserAuthEntity implements Serializable {
    //@Id annotation is used to specify the identifier property of the entity
    @Id
    // @Column annotation is used to specify the details of the column to which a field or property will be mapped.
    @Column(name = "id")
    // The @GeneratedValue annotation is used to specify the primary key generation strategy to use and strategy defined here is "IDENTITY"
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    // The @NotNull Annotation is an explicit contract declaring that the variable cannot or should not hold null value
    @NotNull
    // @Size annotation is used to to validate the size of a field
    @Size(max = 200)
    private String uuid;

    // @ManyToOne annotation is used here to specify that this entity has ManyToOne cardinality with userEntity
    @ManyToOne
    // @OnDelete annotation is used in JPA to specify the foreign key attribute in the Java class for DELETE CASCADE option.
    @OnDelete(action = OnDeleteAction.CASCADE)
    //@JoinColumn annotation marks a column for as a join column for an entity association or an element collection.
    // It contains the reference of another table
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(name = "access_token")
    @NotNull
    @Size(max = 500)
    private String accessToken;

    @Column(name = "expires_at")
    @NotNull
    private ZonedDateTime expiresAt;

    @Column(name = "login_at")
    @NotNull
    private ZonedDateTime loginAt;

    @Column(name = "logout_at")
    private ZonedDateTime logoutAt;

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

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public ZonedDateTime getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(ZonedDateTime loginAt) {
        this.loginAt = loginAt;
    }

    public ZonedDateTime getLogoutAt() {
        return logoutAt;
    }

    public void setLogoutAt(ZonedDateTime logoutAt) {
        this.logoutAt = logoutAt;
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
