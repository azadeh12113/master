package com.example.ArtWork.integration.webcontroller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.ArtWork.Repository.ArtistRepository;
import com.example.ArtWork.Repository.JudgeRepository;
import com.example.ArtWork.model.Artist;
import com.example.ArtWork.model.Judge;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ArtistWebControllerIT {

	@Autowired
	private ArtistRepository artistRepository;
	@Autowired
	private JudgeRepository judgeRepository;
	@LocalServerPort
	private int port;
	private WebDriver driver;
	private String baseUrl;

	@Before
	public void setup() {
		baseUrl = "http://localhost:" + port;
		driver = new HtmlUnitDriver();

		artistRepository.deleteAll();
		artistRepository.flush();
		judgeRepository.deleteAll();
		judgeRepository.flush();
		judgeRepository.save(new Judge("Main Judge"));
	}

	@After
	public void teardown() {
		driver.quit();
	}

	@Test
	public void testHomePage() {

		Judge judge = judgeRepository.save(new Judge("Main Judge"));
		Artist testArtist = artistRepository.save(new Artist("Azadeh", "painting", 3, judge));
		// testArtist.setId(1L);
		driver.get(baseUrl + "/artists");

		assertThat(driver.findElement(By.id("artist_table")).getText()).contains("Azadeh", "painting", "3", "Edit");

		driver.findElement(By.cssSelector("a[href*='artists/edit/" + testArtist.getId() + "']"));
		
	}

	@Test
	public void testPageNewArtist() {

		driver.get(baseUrl + "/artists/new");
		driver.findElement(By.name("name")).sendKeys("Azadeh");
		driver.findElement(By.name("artName")).sendKeys("painting");
		driver.findElement(By.name("score")).clear();
		driver.findElement(By.name("score")).sendKeys("2");

		driver.findElement(By.cssSelector("form button[type='submit']")).click();

		assertThat(artistRepository.findByName("Azadeh").getScore()).isEqualTo(2);

		Artist saved = artistRepository.findByName("Azadeh");
		assertThat(saved).isNotNull();
		assertThat(saved.getName()).isEqualTo("Azadeh");

		assertThat(saved.getArtName()).isEqualTo("painting");
		assertThat(saved.getScore()).isEqualTo(2);
	}

	@Test
	public void testEditPageUpdateArtistScore() throws Exception {

		Judge judge = judgeRepository.save(new Judge("Main Judge"));
		Artist testArtist = artistRepository.save(new Artist("test artist", "", 3, judge));
		
		driver.get(baseUrl + "/artists/edit/" + testArtist.getId());
		
		final WebElement nameField = driver.findElement(By.name("score"));
		nameField.clear();
		nameField.sendKeys("2");
		
		driver.findElement(By.name("btn_submit")).click();
		
		assertThat(artistRepository.findByName("test artist").getScore()).isEqualTo(2);
	}
}
