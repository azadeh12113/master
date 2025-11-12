package com.example.ArtWork.integration;

import com.example.ArtWork.Service.ArtistService;
import com.example.ArtWork.Repository.ArtistRepository;
import com.example.ArtWork.Repository.JudgeRepository;
import com.example.ArtWork.model.Artist;
import com.example.ArtWork.model.Judge;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;


import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(ArtistService.class)
public class ArtistServiceRepositoryIT {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private JudgeRepository judgeRepository;

    @Test
    public void testServiceCanInsertIntoRepository() {
    	
        Judge judge = new Judge();
        judge.setName("Default Judge");
        judgeRepository.save(judge);

        Artist artist = new Artist();
        artist.setName("Azadeh");
        artist.setArtName("Painter");
        artist.setScore(3);
        artist.setJudge(judge);
        Artist saved = artistService.createArtist(artist);

        assertThat(artistRepository.findById(saved.getId())).isPresent();
    }
   
    @Test
    public void testServiceCanUpdateRepository() {
    	 Judge judge = new Judge();
         judge.setName("Default Judge");
         judgeRepository.save(judge);
    	Artist saved = artistRepository
    			.save(new Artist( "Azadeh", "painting", 3,judge));
    	
    	Artist modified = artistService
    			.updateArtistScore(saved.getId(), 2);
    	assertThat(artistRepository.findById(saved.getId()))
    	.contains(modified);
    	
    }
    
}




