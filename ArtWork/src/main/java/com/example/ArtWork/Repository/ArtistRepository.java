package com.example.ArtWork.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ArtWork.model.Artist;
import com.example.ArtWork.model.Judge;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    List<Artist> findByJudge(Judge judge);

    Artist findByName(String name);

    List<Artist> findByScoreGreaterThan(Integer score);
}
