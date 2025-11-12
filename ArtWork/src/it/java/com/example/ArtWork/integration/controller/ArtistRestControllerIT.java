package com.example.ArtWork.integration.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.ArtWork.Repository.ArtistRepository;
import com.example.ArtWork.Repository.JudgeRepository;
import com.example.ArtWork.model.Artist;
import com.example.ArtWork.model.Judge;

import static io.restassured.RestAssured.given;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ArtistRestControllerIT {

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private JudgeRepository judgeRepository;

	@LocalServerPort
	private int port;

	@Before
	public void setup() {
		RestAssured.port = port;
		artistRepository.deleteAll();
		judgeRepository.deleteAll();

		artistRepository.flush();
		judgeRepository.flush();
		Judge judge = new Judge();
		judge.setName("Default Judge");
		judgeRepository.save(judge);
	}

	@Test
	public void testNewArtist() throws Exception {
		
		Judge judge = new Judge();
		judge.setName("Default Judge");
		judgeRepository.save(judge);
		Response response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(new Artist("Azadeh", "Painting", 3, judge)).when().post("/api/artists/new");

		Artist saved = response.getBody().as(Artist.class);
		assertThat(artistRepository.findById(saved.getId())).contains(saved);
	}

	@Test
	public void testUpdateArtistScore() throws Exception {

		Judge judge = new Judge();
		judge.setName("Default Judge");
		judgeRepository.save(judge);

		Artist saved = artistRepository.save(new Artist("Azadeh", "Painting", 2, judge));

		given().
		contentType(MediaType.APPLICATION_JSON_VALUE).
		body(new Artist("Azadeh", "Painting", 3, judge)).
		when().
		put("/api/artists/updateScore/" + saved.getId()).
		then().statusCode(200).
		body("score", equalTo(3));

	}

	@Test
    public void testAllArtists() throws Exception {

    	Judge judge = new Judge();
        judge.setName("Judge1");
        judgeRepository.save(judge);

        Artist artist1 = new Artist( "Azadeh", "Painter", 3, judge);
        Artist artist2 = new Artist( "Sara", "Sculptor", 2, judge);

        artistRepository.save(artist1);
        artistRepository.save(artist2);
        
        given().
            accept(MediaType.APPLICATION_JSON_VALUE).
        when().
            get("/api/artists").
        then().
            statusCode(200).
            body("[0].name", equalTo("Azadeh")).
            body("[1].name", equalTo("Sara"));
    }

    
    @Test
    public void testDeleteArtistById() throws Exception {
    	
        Judge judge = new Judge();
        judge.setName("Judge1");
        judgeRepository.save(judge);

        Artist artist = new Artist("Azadeh", "Painter", 3, judge);
        Artist saved = artistRepository.save(artist);

        given()
            .when()
            .delete("/api/artists/" + saved.getId())
            .then()
            .statusCode(200);

        assertThat(artistRepository.findById(saved.getId())).isNotPresent();
    }
   
    @Test
    public void testGetArtistsWithHighScores() throws Exception {

    	Judge judge = new Judge();
        judge.setName("Judge1");
        judgeRepository.save(judge);

        artistRepository.save(new Artist("Azadeh", "Painting", 3, judge)); 
        artistRepository.save(new Artist("Sara", "Photo", 1, judge));  

        given()
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .get("/api/artists/highestScore")
        .then()
            .statusCode(200)
            .body("[0].name", equalTo("Azadeh"));
    }
    }
