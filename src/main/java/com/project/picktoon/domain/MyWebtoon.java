package com.project.picktoon.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "my_webtoon")
@Setter
@Getter
@ToString
public class MyWebtoon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private boolean alarm;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn (name="webtoon_id")
    private Webtoon webtoon;

    public MyWebtoon(){
        alarm = true;
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 1536989a83e316143dc1b17b62f943ad481ee5c5