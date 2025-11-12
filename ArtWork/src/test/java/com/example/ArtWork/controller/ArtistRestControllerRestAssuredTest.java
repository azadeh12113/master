package com.example.ArtWork.controller;

import static org.junit.Assert.*;

import org.junit.Test;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import com.example.ArtWork.model.Artist;
import com.example.ArtWork.model.Judge;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

import com.example.ArtWork.Repository.JudgeRepository;
import com.example.ArtWork.Service.ArtistService;


//import io.restassured.module.mockmvc.RestAssuredMockMvc;



	
	@RunWith(MockitoJUnitRunner.class)
	public class ArtistRestControllerRestAssuredTest {

		@Mock
		private ArtistService artistService;
		
		@Mock
		private JudgeRepository judgeRepository;

		@InjectMocks
		private ArtistRestController artistRestController;
	    
		@Before
		public void setup() {

		    RestAssuredMockMvc.standaloneSetup(artistRestController);
		}
		
		@Test
		public void testPostArtist() throws Exception {
			
		    Artist requestBodyArtist = new Artist();
		    requestBodyArtist.setName("Azadeh");
		    requestBodyArtist.setArtName("Painting");
		    requestBodyArtist.setScore(3);

		    Artist returnedArtist = new Artist();
		    returnedArtist.setId(1L);
		    returnedArtist.setName("Azadeh");
		    returnedArtist.setArtName("Painting");
		    returnedArtist.setScore(3);
		    returnedArtist.setJudge(null);

		    when(artistService.createArtist(org.mockito.ArgumentMatchers.any(Artist.class))).thenReturn(returnedArtist);

		    given()
		        .contentType(MediaType.APPLICATION_JSON_VALUE)
		        .body(requestBodyArtist)

		    .when()
		        .post("/api/artists/new")
		    .then()
		        .statusCode(200)
		        .body("name", equalTo("Azadeh"))
		        .body("artName", equalTo("Painting"))
		        .body("score", equalTo(3));
		}

		@Test
		public void testUpdateScoreArtist() throws Exception {
			
			 Artist requestBodyArtist = new Artist();
			    requestBodyArtist.setName("Azadeh");
			    requestBodyArtist.setArtName("Painting");
			    requestBodyArtist.setScore(3);
			    
			    Artist returnedArtist = new Artist();
			    returnedArtist.setId(1L);
			    returnedArtist.setName("Azadeh");
			    returnedArtist.setArtName("Painting");
			    returnedArtist.setScore(3);
			    returnedArtist.setJudge(null);

			    when(artistService.updateArtistScore(1L, requestBodyArtist.getScore())).
		thenReturn(returnedArtist);
		given().
		contentType(MediaType.APPLICATION_JSON_VALUE).
		body(requestBodyArtist).
		when().
		put("/api/artists/updateScore/1").
		then().
		statusCode(200)
		
				.body("name", equalTo("Azadeh"))
		        .body("artName", equalTo("Painting"))
		        .body("score", equalTo(3));
	
		}
		
		  @Test
		  public void testGetArtistsWithHighestScore() {
		    Artist a1 = new Artist();
		    a1.setId(1L);
		    a1.setName("Azadeh");
		    a1.setArtName("Painting");
		    a1.setScore(5);

		    Artist a2 = new Artist();
		    a2.setId(2L);
		    a2.setName("Sara");
		    a2.setArtName("Sculpture");
		    a2.setScore(5);

		    List<Artist> top = Arrays.asList(a1, a2);
		    when(artistService.getArtistWithHighestScore()).thenReturn(top);

		    given().
		    when().
		      get("/api/artists/highestScore").
		    then().
		      statusCode(200).
		      body("$", hasSize(2)).
		      body("[0].id", equalTo(1)).
		      body("[0].name", equalTo("Azadeh")).
		      body("[0].artName", equalTo("Painting")).
		      body("[0].score", equalTo(5)).
		      body("[1].id", equalTo(2)).
		      body("[1].name", equalTo("Sara")).
		      body("[1].artName", equalTo("Sculpture")).
		      body("[1].score", equalTo(5));

		    verify(artistService, times(1)).getArtistWithHighestScore();
		    
		  }
		  
		  @Test
		  public void testDeleteArtist() {
			  
		    doNothing().when(artistService).deleteArtistById(10L);

		    given().
		    when().
		      delete("/api/artists/10").
		    then().
		      statusCode(200);

		    verify(artistService, times(1)).deleteArtistById(10L);
		  }
		  
		  @Test
		  public void testGetAllArtists() {
			  
		    Artist a1 = new Artist();
		    a1.setId(1L);
		    a1.setName("Azadeh");
		    a1.setArtName("Painting");
		    a1.setScore(2);

		    Artist a2 = new Artist();
		    a2.setId(2L);
		    a2.setName("Sara");
		    a2.setArtName("Sculpture");
		    a2.setScore(4);

		    List<Artist> artists = Arrays.asList(a1, a2);
		    
		    when(artistService.getAllArtists()).thenReturn(artists);

		    given().
		      accept(MediaType.APPLICATION_JSON_VALUE).
		    when().
		      get("/api/artists").
		    then().
		      statusCode(200).
		      body("$", hasSize(2)).
		      body("[0].id", equalTo(1)).
		      body("[0].name", equalTo("Azadeh")).
		      body("[0].artName", equalTo("Painting")).
		      body("[0].score", equalTo(2)).
		      body("[1].id", equalTo(2)).
		      body("[1].name", equalTo("Sara")).
		      body("[1].artName", equalTo("Sculpture")).
		      body("[1].score", equalTo(4));

		    verify(artistService, times(1)).getAllArtists();
		    
		  }
	}
	
