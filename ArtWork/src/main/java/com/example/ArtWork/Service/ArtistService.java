package com.example.ArtWork.Service;

import org.springframework.stereotype.Service;

import com.example.ArtWork.Repository.ArtistRepository;
import com.example.ArtWork.Repository.JudgeRepository;
import com.example.ArtWork.model.Artist;
import com.example.ArtWork.model.Judge;

import java.util.List;

@Service
public class ArtistService {

    private ArtistRepository artistRepository;
    private JudgeRepository judgeRepository;  

    
    public ArtistService(ArtistRepository artistRepository, JudgeRepository judgeRepository) {
        this.artistRepository = artistRepository;
        this.judgeRepository = judgeRepository;
    }

    public Artist createArtist(Artist artist) {
    	
        Judge judge = judgeRepository.findFirstByOrderByIdAsc();
        
        if (judge == null) {
            throw new RuntimeException("No judge found in database");
         }
        artist.setJudge(judge);
        artist.setId(null);
        return artistRepository.save(artist);
    }
    
    public Artist updateArtistScore(Long id, int newScore) {
    	
    	if (newScore < 0 || newScore > 3) {
    	    throw new IllegalArgumentException("Score out of range");
    	}

	    Artist artist = artistRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Artist not found"));
	    artist.setScore(newScore);
	    return artistRepository.save(artist);
	}
    
	public List<Artist> getArtistsByJudge(Judge judge) {
		return artistRepository.findByJudge(judge);
	}
	
	public List<Artist> getArtistWithHighestScore() {
		
		return artistRepository.findByScoreGreaterThan(2);
	}
	
	public List<Artist> getAllArtists() {
	    return artistRepository.findAll();
	}

	public Artist getArtistById(Long id) {
		
	    return artistRepository.findById(id).orElse(null);

	}
	
	public void deleteArtistById(long id) {
		
		artistRepository.findById(id)
		.orElseThrow(()-> new RuntimeException("Artist Not found"));
		artistRepository.deleteById(id);
	}
	
}
	
	
	

