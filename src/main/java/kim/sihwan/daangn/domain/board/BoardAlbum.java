package kim.sihwan.daangn.domain.board;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardAlbum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_album_id")
    private Long id;
    private String url;
    private String filename;

    @ManyToOne
    private Board board;
}
