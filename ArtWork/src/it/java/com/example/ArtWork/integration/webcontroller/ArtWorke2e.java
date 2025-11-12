package com.example.ArtWork.integration.webcontroller;

import org.junit.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

public class ArtWorke2e { // NOSONAR

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static int port = Integer.parseInt(System.getProperty("server.port", "8080"));
  private static String baseUrl = "http://localhost:" + port;

  private WebDriver driver;

  @BeforeClass
  public static void setupClass() {
    WebDriverManager.chromedriver().setup(); // manage ChromeDriver automatically
  }

  @Before
  public void setup() {
    port = Integer.parseInt(System.getProperty("server.port", "8080"));
    baseUrl = "http://localhost:" + port;
    driver = new ChromeDriver();
  }

  @After
  public void teardown() {
    if (driver != null) driver.quit();
  }

  // --- Test 1: Home navigation wiring
  @Test
  public void testHome_NavigationButtonsWork() {
    driver.get(baseUrl + "/");

    // Click "I'm an Artist" -> should go to /artists/new
    driver.findElement(By.xpath("//button[contains(.,\"I'm an Artist\")]")).click();
    assertThat(driver.getCurrentUrl()).contains("/artists/new");

    // Go back and click "I'm a Judge" -> should go to /artists (list page)
    driver.navigate().back();
    driver.findElement(By.xpath("//button[contains(.,\"I'm a Judge\")]")).click();
    assertThat(driver.getCurrentUrl()).contains("/artists");
  }

  // --- Test 2: Create flow — fill the New Artist form and see it on the list
  @Test
  public void testCreateNewArtist_DisplayedInList() {
    driver.get(baseUrl + "/artists/new");

    // Form fields are bound via th:field -> names are 'name', 'artName', 'score'
    driver.findElement(By.name("name")).sendKeys("Azadeh");
    driver.findElement(By.name("artName")).sendKeys("Painting");
    driver.findElement(By.name("score")).sendKeys(Keys.chord(Keys.CONTROL, "a") + "2");

    // Submit by clicking the Save button (type=submit)
    driver.findElement(By.xpath("//button[normalize-space()='Save']")).click();

    // After POST /artists, the page shows the table of artists (artists.html)
    WebElement table = driver.findElement(By.tagName("table"));
    String tableText = table.getText();
    assertThat(tableText).contains("Azadeh", "Painting", "2");
  }

  // --- Test 3: Edit score — seed via REST, then edit through the UI
  @Test
  public void testEditArtistScore_UpdatesRow() throws Exception {
    // Seed artist through REST (end-to-end setup; no repo injection)
    String id = postArtist("Sara", "Photo", 1);

    // Open list and click the Edit link for that id -> /artists/edit/{id}
    driver.get(baseUrl + "/artists");
    driver.findElement(By.cssSelector("a[href*='/artists/edit/" + id + "']")).click();

    // edit-score.html: input 'score' and 'Update' button
    WebElement scoreField = driver.findElement(By.name("score"));
    scoreField.clear();
    scoreField.sendKeys("3");
    driver.findElement(By.xpath("//button[normalize-space()='Update']")).click();

    // Back on list, row should reflect the new score
    WebElement table = driver.findElement(By.tagName("table"));
    String tableText = table.getText();
    assertThat(tableText).contains("Sara", "Photo", "3");
  }

  // Helper: create artist via REST API (/api/artists) and return its id
  // NOTE: Requires at least one Judge present in DB; service assigns the first judge.
  private String postArtist(String name, String artName, int score) throws Exception {
    RestTemplate rest = new RestTemplate();

    Map<String, Object> body = new HashMap<>();
    body.put("name", name);
    body.put("artName", artName);
    body.put("score", score);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

    ResponseEntity<String> resp =
        rest.postForEntity(baseUrl + "/api/artists", entity, String.class);

    JsonNode json = MAPPER.readTree(resp.getBody());
    return json.get("id").asText();
  }
}
