package com.example.ArtWork.Service;

import com.example.ArtWork.Repository.ArtistRepository;
import com.example.ArtWork.Repository.JudgeRepository;
import com.example.ArtWork.model.Artist;
import com.example.ArtWork.model.Judge;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import static java.util.Arrays.asList;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private JudgeRepository judgeRepository;

    @InjectMocks
    private ArtistService artistService;
    
    @Test
    public void test_createArtist_savesArtist_whenJudgeExists() {

    	Judge judge = new Judge();
        judge.setId(1L);
        judge.setName("JudgeName");

        Artist artist = new Artist();
        artist.setName("Azadeh");
        artist.setArtName("Painting");
        artist.setScore(2);

        when(judgeRepository.findFirstByOrderByIdAsc()).thenReturn(judge);

        Artist saved = new Artist();
        saved.setId(1L);
        saved.setName("Azadeh");
        saved.setArtName("Painting");
        saved.setScore(2);
        saved.setJudge(judge);

        when(artistRepository.save(any(Artist.class))).thenReturn(saved);

        Artist result = artistService.createArtist(artist);

        assertEquals("Azadeh", result.getName());
        assertEquals("Painting", result.getArtName());
        assertEquals(judge, result.getJudge());
        verify(artistRepository).save(any(Artist.class));
    }


    
    @Test
    public void test_updateArtistScoreById_and_returnsSavedArtist()   {
    	
    	Judge judge = new Judge(); 
        judge.setId(1L);
        judge.setName("name");
        
        Artist replacement = spy(new Artist("Azadeh", "Painting", 2, judge));
        
 	   Artist replaced = new Artist("Azadeh", "Painting", 2, judge);
       replaced.setId(1L);

       when(artistRepository.findById(1L)).thenReturn(Optional.of(replacement));
       when(artistRepository.save(any(Artist.class))).thenReturn(replaced);
       
 	  Artist result = artistService.updateArtistScore(1L, replacement.getScore());
 	  assertThat(result.getScore()).isEqualTo(replaced.getScore());
 	   
 	   InOrder inOrder = Mockito.inOrder(replacement, artistRepository);
 	   inOrder.verify(artistRepository).save(replacement);
 	   }
    
    @Test
    public void test_updateArtistScore_acceptsMaxBoundary_three_and_returnsSavedArtist() {
        // Arrange
        long artistId = 1L;
        int newScore = 3; // upper valid boundary

        Judge judge = new Judge();
        judge.setId(1L);
        judge.setName("name");

        Artist existingArtist = spy(new Artist("Azadeh", "Painting", 1, judge));
        existingArtist.setId(artistId);

        Artist savedArtist = new Artist("Azadeh", "Painting", newScore, judge);
        savedArtist.setId(artistId);

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(any(Artist.class))).thenReturn(savedArtist);

        // Act
        Artist result = artistService.updateArtistScore(artistId, newScore);

        // Assert
        assertEquals(newScore, result.getScore());
        verify(artistRepository).save(existingArtist);

        InOrder inOrder = Mockito.inOrder(existingArtist, artistRepository);
        inOrder.verify(artistRepository).save(existingArtist);
    }

    @Test
    public void test_updateArtistScore_acceptsMinBoundary_zero_and_returnsSavedArtist() {

    	long artistId = 1L;
        int newScore = 0; 

        Judge judge = new Judge();
        judge.setId(1L);
        judge.setName("name");

        Artist existingArtist = spy(new Artist("Azadeh", "Painting", 2, judge));
        existingArtist.setId(artistId);

        Artist savedArtist = new Artist("Azadeh", "Painting", newScore, judge);
        savedArtist.setId(artistId);

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(any(Artist.class))).thenReturn(savedArtist);

        Artist result = artistService.updateArtistScore(artistId, newScore);

        assertEquals(newScore, result.getScore());
        verify(artistRepository).save(existingArtist);

        InOrder inOrder = Mockito.inOrder(existingArtist, artistRepository);
        inOrder.verify(artistRepository).save(existingArtist);
    }

    @Test
    public void test_updateArtistScore_rejectsBelowMin_minusOne_and_throwsException() {
    	
        long artistId = 1L;
        int invalidScore = -1; 
        
        Judge judge = new Judge();
        judge.setId(1L);
        judge.setName("name");

        Artist existingArtist = spy(new Artist("Azadeh", "Painting", 2, judge));
        existingArtist.setId(artistId);

        //when(artistRepository.findById(artistId)).thenReturn(Optional.of(existingArtist));

        try {
            artistService.updateArtistScore(artistId, invalidScore);
        } catch (IllegalArgumentException e) {
            assertEquals("Score out of range", e.getMessage());
        }

        verify(artistRepository, never()).save(any(Artist.class));
    }
    
    @Test
    public void test_updateArtistScore_rejectsAboveMax_four_and_throwsException() {
    	
        long artistId = 1L;
        int invalidScore = 4; 

        Judge judge = new Judge();
        judge.setId(1L);
        judge.setName("name");

        Artist existingArtist = spy(new Artist("Azadeh", "Painting", 2, judge));
        existingArtist.setId(artistId);

        //when(artistRepository.findById(artistId)).thenReturn(Optional.of(existingArtist));

        try {
            artistService.updateArtistScore(artistId, invalidScore);
        } catch (IllegalArgumentException e) {
            assertEquals("Score out of range", e.getMessage());
        }

        verify(artistRepository, never()).save(any(Artist.class));
    }

    @Test
    public void test_getAllArtists_byJudge() {
    	
    	Judge judge = new Judge();
    	judge.setId(1L);

    	Artist artist1 = new Artist();    	    	
    	artist1.setName("Artist1");
    	artist1.setJudge(judge);
    	
    	Artist artist2 = new Artist();
    	artist2.setName("Artist2");
    	artist2.setJudge(judge);
    	
    	List<Artist> mockArtists = asList(artist1, artist2);
    	when(artistRepository.findByJudge(judge)).thenReturn(mockArtists);

        List<Artist> result = artistService.getArtistsByJudge(judge);
        assertThat(result).containsExactly(artist1, artist2);
    }
    
    @Test(expected = RuntimeException.class)
    public void test_createArtist_throwsException_whenJudgeIsNull() {
    	
        when(judgeRepository.findFirstByOrderByIdAsc()).thenReturn(null);

        Artist artist = new Artist();
        artist.setName("Azadeh");

        artistService.createArtist(artist); 
    }
    
    @Test
    public void test_getArtistsByJudge_returnsEmptyList_whenNoArtistsExist() {
    	
        Judge judge = new Judge();
        judge.setId(1L);

        when(artistRepository.findByJudge(judge)).thenReturn(asList());

        List<Artist> result = artistService.getArtistsByJudge(judge);

        assertThat(result).isEmpty();
        
    }   
   
   @Test
   public void test_getHighestScore_returnsHighestScore_whenNotNull() {
       
       Artist artist = new Artist();
       artist.setName("Azadeh");
       artist.setScore(3);

       when(artistRepository.findByScoreGreaterThan(2)).thenReturn(asList(artist));

       List<Artist> result = artistService.getArtistWithHighestScore();
       assertThat(result).containsExactly(artist);
   }
   
   @Test
   public void test_getArtistsWithHighScores_returnsEmptyList_whenNoMatch() {
	   
	   Artist artist = new Artist();
       artist.setName("Azadeh");
       artist.setScore(2);

       when(artistRepository.findByScoreGreaterThan(2))
           .thenReturn(asList());

       List<Artist> result = artistService.getArtistWithHighestScore();

       assertThat(result).isEmpty();
   }
   
   @Test
   public void test_deleteArtistById_deleteArtistsWhenExists() {
	   long artistId =1L;
	   
	   Artist artist = new Artist();
	   artist.setId(artistId);
	   artist.setName("Azadeh");
	   
	   when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
	   
	   artistService.deleteArtistById(artistId);
	   
	   verify(artistRepository).deleteById(artistId);
   }
    
   @Test
   public void test_getEmployeeById_found() {
	   
   Judge judge = new Judge(); 
   judge.setId(1L);
   judge.setName("name");
	   
   Artist artist = new Artist("Azadeh", "Painting", 3, judge);
   
   when(artistRepository.findById(1L))
   .thenReturn(Optional.of(artist));
   
   assertThat(artistService.getArtistById(1L))
   .isSameAs(artist);
   }
   
   @Test
   public void test_getEmployeeById_notFound() {
	   
	   when(artistRepository.findById(anyLong()))
	   .thenReturn(Optional.empty());
	   
	   assertThat(artistService.getArtistById(1L)).isNull();
	   
   }
   
   
   @Test
   public void test_updateArtistScore_throwsException_whenArtistNotFound() {
	   
       long missingId = 999L;

       when(artistRepository.findById(missingId)).thenReturn(Optional.empty());

       try {
           artistService.updateArtistScore(missingId, 2);
       } catch (RuntimeException e) {
           assertEquals("Artist not found", e.getMessage());
       }

       verify(artistRepository, never()).save(any(Artist.class));
   }

   @Test
   public void test_deleteArtistById_throwsExceptionWhenArtistNotFound() {
	   
       long artistId = 999L;

       when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

       try {
           artistService.deleteArtistById(artistId);
           org.junit.Assert.fail("Expected RuntimeException to be thrown");
       } catch (RuntimeException e) {
           org.junit.Assert.assertEquals("Artist Not found", e.getMessage());
       }

       verify(artistRepository).findById(artistId);
       verify(artistRepository, never()).deleteById(anyLong());
   }

 }

    
    


