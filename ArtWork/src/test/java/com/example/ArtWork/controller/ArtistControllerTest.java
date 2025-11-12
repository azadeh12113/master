package com.example.ArtWork.controller;

import com.example.ArtWork.Service.ArtistService;
import com.example.ArtWork.model.Artist;
import com.example.ArtWork.model.Judge;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


import java.util.List;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@WebMvcTest(ArtistRestController.class)
public class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;;

    @MockBean
    private ArtistService artistService;

    @Test
    public void testCreateArtist() throws Exception {
    	
        Artist artist = new Artist();    
       artist.setName("Azadeh"); 
      
        when(artistService.createArtist(any(Artist.class)))
                .thenReturn(artist);
                
        mockMvc.perform(post("/api/artists/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Azadeh\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Azadeh"));
    }
    
    @Test
    public void testAllArtistsEmpty() throws Exception{
    	when(artistService.getAllArtists()).thenReturn(List.of());


        mockMvc.perform(get("/api/artists")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]")); 
    }
      
    @Test
    public void testAllArtistsReturnsList() throws Exception{
    	
    	Judge judge = new Judge();
    	judge.setName("judge1");

    	Artist artist1 = new Artist("Azadeh","painting",3,judge);  	
    	Artist artist2 = new Artist("Sara", "sculpture", 2, judge);
    	
    	when(artistService.getAllArtists()).thenReturn(List.of(artist1,artist2));

        mockMvc.perform(get("/api/artists")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Azadeh")))
                .andExpect(jsonPath("$[0].artName", is("painting")))
                .andExpect(jsonPath("$[0].judge.name", is("judge1")))
                .andExpect(jsonPath("$[0].score", is(3)))
                .andExpect(jsonPath("$[1].name", is("Sara")))
                .andExpect(jsonPath("$[1].score", is(2)))
                .andExpect(jsonPath("$[1].judge.name", is("judge1")))
                .andExpect(jsonPath("$[1].artName", is("sculpture")));
    }
    
    @Test
    public void testHighestScoresReturnsArtists() throws Exception {
    	
    	Judge judge = new Judge();
    	judge.setName("judge1");
    	
    	Artist artist1 = new Artist("Azadeh","painting",3,judge);    	
    	Artist artist2 = new Artist("Sara", "sculpture", 2, judge);
    	
        when(artistService.getArtistWithHighestScore()).thenReturn(List.of(artist1,artist2));

        mockMvc.perform(get("/api/artists/highestScore")
        		.accept(MediaType.APPLICATION_JSON))
        		.andExpect(status().isOk())
        		.andExpect(jsonPath("$[0].name", is("Azadeh")))
        		.andExpect(jsonPath("$[0].artName", is("painting")))
        		.andExpect(jsonPath("$[0].judge.name", is("judge1")))
        		.andExpect(jsonPath("$[0].score", is(3)))
        		.andExpect(jsonPath("$[1].name", is("Sara")))
        		.andExpect(jsonPath("$[1].score", is(2)))
        		.andExpect(jsonPath("$[1].judge.name", is("judge1")))
        		.andExpect(jsonPath("$[1].artName", is("sculpture")));  
    }
//
////    @DeleteMapping("/api/artists/{id}")
////    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
////        artistService.deleteArtistById(id);
////        return ResponseEntity.ok().build();
////    }
}
