package kim.sihwan.daangn.domain.member;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String area;
    private String role;


    @Builder
    public Member(String username, String password, String nickname, String area, String role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.area = area;
        this.role = role;
    }

}
