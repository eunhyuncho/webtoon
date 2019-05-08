package com.project.picktoon.domain;

import com.project.picktoon.util.PlatformType;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "platform")
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Platform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="platform_name")
    @Enumerated(EnumType.STRING)
    private PlatformType platformName;
}



