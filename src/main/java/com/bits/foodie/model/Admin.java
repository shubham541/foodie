package com.bits.foodie.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
@NoArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int adminId;
    private String adminName;

    @Column(unique = true)
    private String adminEmail;
    private String role;
    private String password;

    public Admin(String adminName, String adminEmail, String role , String password) {
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.role = role;
        this.password=password;
    }

    public void deactivateUser() {

    }

    public void cancelOrder() {

    }

    public void rescheduleOrder() {

    }

    public void generateReports() {

    }

    public void monitorActivity() {

    }
}
