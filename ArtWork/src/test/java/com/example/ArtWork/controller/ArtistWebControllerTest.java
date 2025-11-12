
package com.example.ArtWork.controller;

import com.example.ArtWork.Repository.JudgeRepository;
import com.example.ArtWork.Service.ArtistService;
import com.example.ArtWork.model.Artist;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.thymeleaf.spring5.expression.Mvc;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ArtistController.class)
public class ArtistWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArtistService artistService;

    @MockBean
    private JudgeRepository judgeRepository;

	@Test
	public void test_HomeView_ShowsArtists() throws Exception {
		
		Artist artist = new Artist();
		artist.setName("Azadeh");
		artist.setArtName("Painter");
		artist.setScore(5);

		List<Artist> artists = List.of(artist);
		when(artistService.getAllArtists()).thenReturn(artists);

		mockMvc.perform(get("/artists"))
        .andExpect(status().isOk())
        .andExpect(view().name("artists"))
        .andExpect(model().attribute("artists", artists))
        .andExpect(model().attribute("message", ""));
	}

	
	  @Test public void test_HomeView_ShowsMessageWhenThereAreNoArtists() throws Exception {
		  
		  when(artistService.getAllArtists()).thenReturn(List.of());
	  
		  mockMvc.perform(get("/artists")) 
		  .andExpect(status().isOk())
		  .andExpect(view().name("artists")) 
		  .andExpect(model().attribute("artists", List.of())) 
		  .andExpect(model().attribute("message", "No artist")); 
		  }
	 

	
	  @Test public void test_EditScore_WhenArtistIsFound() throws Exception {
		  
	  Artist artist = new Artist(); 
	  artist.setName("Azadeh");
	  artist.setArtName("Painter"); 
	  artist.setScore(3);
	  
	  when(artistService.getArtistById(1L)).thenReturn(artist);
	  
	  mockMvc.perform(get("/artists/edit/1")) 
	  .andExpect(status().isOk())
	  .andExpect(view().name("edit-score")) 
	  .andExpect(model().attribute("artist",artist));
	  
	  }
	  
    
    @Test
    public void test_EditArtist_WhenArtistIsNotFound() throws Exception {
        when(artistService.getArtistById(1L)).thenReturn(null);

        mockMvc.perform(get("/artists/edit/1"))
               .andExpect(status().isOk())
               .andExpect(view().name("edit-score"))
               .andExpect(model().attribute("artist", nullValue()))
               .andExpect(model().attribute("message", "No artist found with id: 1"));
    }
	
	
	  @Test public void test_NewArtistView_ReturnsFormWithEmptyArtist() throws Exception {
		  
		  mockMvc.perform(get("/artists/new")) 
		  .andExpect(status().isOk())
		  .andExpect(view().name("new-artist")) 
		  .andExpect(model().attribute("artist", new Artist()));
	  
		  verifyNoInteractions(artistService); 
		  }
	 
	  @Test
	  public void test_DeleteArtist_RedirectsToArtistList() throws Exception {
		  
	      mockMvc.perform(get("/artists/delete/1"))
	      .andExpect(view().name("redirect:/artists"));


	      verify(artistService).deleteArtistById(1L);
	  }

	  @Test
	  public void test_HighestScoreView_ShowsArtists() throws Exception {

	      Artist artist1 = new Artist();
	      artist1.setName("Azadeh");
	      artist1.setArtName("Painter");
	      artist1.setScore(3);

	      Artist artist2 = new Artist();
	      artist2.setName("Sara");
	      artist2.setArtName("Sculpture");
	      artist2.setScore(3);

	      List<Artist> artists = List.of(artist1, artist2);
	      when(artistService.getArtistWithHighestScore()).thenReturn(artists);

	      mockMvc.perform(get("/artists/highestScore"))
	              .andExpect(status().isOk())
	              .andExpect(view().name("high-score"))
	              .andExpect(model().attribute("artists", artists))
	              .andExpect(model().attribute("message", ""));
	  }
	  
	  @Test
	  public void showCreateForm_returnsNewArtistView_andModel() throws Exception {
		  
	    mockMvc.perform(get("/artists/new"))
	           .andExpect(status().isOk())
	           .andExpect(view().name("new-artist"))
	           .andExpect(model().attributeExists("artist"));
	  }
	  
	  @Test
	  public void createArtist_postsForm_andRedirectsToArtists() throws Exception {
	    mockMvc.perform(post("/artists")
	            .param("name", "Azadeh")
	            .param("artName", "Painting")
	            .param("score", "3"))
	        .andExpect(status().is3xxRedirection())
	        .andExpect(redirectedUrl("/artists"))
	        .andExpect(view().name("redirect:/artists"));

	    verify(artistService).createArtist(any(Artist.class));
	  }

}
