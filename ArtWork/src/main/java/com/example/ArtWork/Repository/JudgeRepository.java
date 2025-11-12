package com.example.ArtWork.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ArtWork.model.Artist;
import com.example.ArtWork.model.Judge;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ArtWork.model.Judge;

@Repository
public interface JudgeRepository extends JpaRepository<Judge, Long> {
	
	Judge findFirstByOrderByIdAsc();

}


