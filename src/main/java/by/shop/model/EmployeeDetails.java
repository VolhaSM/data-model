package by.shop.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@Table(name = "employee_details")
public class EmployeeDetails {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;

    @Column(name = "country")
    private String country;

    @OneToOne
    private Employee employee;
}
