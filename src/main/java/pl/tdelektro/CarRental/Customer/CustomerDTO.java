package pl.tdelektro.CarRental.Customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "customer_dto")
public class CustomerDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JsonIgnore
    private int id;

    @JsonIgnore
    String name;
    @JsonIgnore
    String password;
    String emailAddress;
    @JsonIgnore
    Float funds;
    @JsonIgnore
    String token;

    String getToken() {
        return token;
    }

    CustomerDTO setToken(String token) {
        this.token = token;
        return this;
    }

    CustomerDTO() {
    }

    CustomerDTO(Customer customer) {
        this.name = customer.getName();
        this.emailAddress = customer.getEmailAddress();
        this.funds = customer.getFunds();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CustomerDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public CustomerDTO setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public CustomerDTO setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public Float getFunds() {
        return funds;
    }

    public CustomerDTO setFunds(Float funds) {
        this.funds = funds;
        return this;
    }
}
