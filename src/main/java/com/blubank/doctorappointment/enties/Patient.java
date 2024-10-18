package com.blubank.doctorappointment.enties;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "phoneNumberIndex", columnList = "phoneNumber", unique = true)
})
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "name is mandatory")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "phoneNumber is mandatory")
    private String phoneNumber;

}