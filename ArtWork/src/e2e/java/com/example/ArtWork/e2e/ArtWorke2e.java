package com.example.ArtWork.e2e;

import org.junit.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ArtWorke2e { // match prof naming style: *WebControllerE2E

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ArtWorke2e.class);

  private static int port =
      Integer.parseInt(System.getProperty("server.port", "8080"));

  private static String baseUrl = "http://localhost:" + port;

  private WebDriver driver;

  @BeforeClass
  public static void setupClass() {
    WebDriverManager.chromedriver().setup();
  }

  @Before
  public void setup() {
    // recompute in case Maven profile reserves a random port
    port = Integer.parseInt(System.getProperty("server.port", "8080"));
    baseUrl = "http://localhost:" + port;
    driver = new ChromeDriver();
    LOGGER.info("Starting test with baseUrl={}", baseUrl);
  }

  @After
  public void teardown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test
  public void testHome_NavigationButtonsWork() {
    WebDriverWait wait = new WebDriverWait(driver, 10);

    // "I'm an Artist" -> /artists/new
    driver.get(baseUrl + "/");
    driver.findElement(By.cssSelector("form[action='/artists/new'] button[type='submit']")).click();
    wait.until(ExpectedConditions.urlContains("/artists/new"));
    assertThat(driver.getCurrentUrl()).contains("/artists/new");
    LOGGER.info("Navigated to /artists/new successfully");

    // "I'm a Judge" -> /artists
    driver.get(baseUrl + "/");
    driver.findElement(By.cssSelector("form[action='/artists'] button[type='submit']")).click();
    wait.until(ExpectedConditions.urlContains("/artists"));
    assertThat(driver.getCurrentUrl()).contains("/artists");
    LOGGER.info("Navigated to /artists successfully");
  }

  @Test
  public void testCreateNewArtist_DisplayedInList() {
    WebDriverWait wait = new WebDriverWait(driver, 10);

    driver.get(baseUrl + "/artists/new");
    driver.findElement(By.name("name")).sendKeys("Azadeh");
    driver.findElement(By.name("artName")).sendKeys("Painting");
    driver.findElement(By.name("score"))
          .sendKeys(Keys.chord(Keys.CONTROL, "a") + "2");

    driver.findElement(By.xpath("//button[normalize-space()='Save']")).click();

    wait.until(ExpectedConditions.urlContains("/artists"));
    WebElement table = wait.until(
        ExpectedConditions.presenceOfElementLocated(By.cssSelector("table"))
    );
    assertThat(table.getText()).contains("Azadeh", "Painting", "2");
    LOGGER.info("Artist created via UI and visible in the list");
  }

  @Test
  public void testEditArtistScore_UpdatesRow() throws Exception {
    WebDriverWait wait = new WebDriverWait(driver, 10);

    // Seed via REST (PDF/prof pattern): POST /api/artists/new
    String id = postArtistJson("Sara", "Photo", 1);
    LOGGER.info("Seeded artist via REST with id={}", id);

    // Open list and click the Edit link for that id
    driver.get(baseUrl + "/artists");
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table")));

    // Your Thymeleaf renders a RELATIVE href like "artists/edit/{id}" (no leading slash)
    // Use a contains(@href, ...) like the prof uses a[href='...'] but adapted to your markup.
    driver.findElement(By.xpath("//a[contains(@href,'artists/edit/" + id + "')]")).click();

    WebElement scoreField = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("score")));
    scoreField.clear();
    scoreField.sendKeys("3");
    driver.findElement(By.xpath("//button[normalize-space()='Update']")).click();

    wait.until(ExpectedConditions.urlContains("/artists"));
    WebElement table = wait.until(
        ExpectedConditions.presenceOfElementLocated(By.cssSelector("table"))
    );
    assertThat(table.getText()).contains("Sara", "Photo", "3");
    LOGGER.info("Edited artist score via UI and verified on the list page");
  }

  // -------- helpers (match the PDF/prof seeding style) --------
  private String postArtistJson(String name, String artName, int score) throws Exception {
    RestTemplate rest = new RestTemplate();
    ObjectMapper mapper = new ObjectMapper(); // local (like prof style)

    Map<String, Object> body = new HashMap<>();
    body.put("name", name);
    body.put("artName", artName);
    body.put("score", score);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

    String json =
        rest.postForEntity(baseUrl + "/api/artists/new", entity, String.class)
            .getBody();

    return mapper.readTree(json).get("id").asText();
  }
}
