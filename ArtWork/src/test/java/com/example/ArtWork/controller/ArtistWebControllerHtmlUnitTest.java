package com.example.ArtWork.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;

import com.example.ArtWork.Service.ArtistService;
import com.example.ArtWork.model.Artist;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import static java.util.Collections.emptyList;
import static java.util.Arrays.asList;
import com.gargoylesoftware.htmlunit.html.HtmlTable;



import com.example.ArtWork.Repository.JudgeRepository;



@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ArtistController.class)
public class ArtistWebControllerHtmlUnitTest {

	@Autowired
	private WebClient webClient;

	@MockBean
	private ArtistService artistService;

	@MockBean
	private JudgeRepository judgeRepository;

	@Test
	public void testArtistPageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/artists");
		assertThat(page.getTitleText()).isEqualTo("Artists");
	}

	@Test
	public void testHomePageWithArtists() throws Exception {
		
		when(artistService.getAllArtists()).thenReturn(asList(
				new Artist( "Azadeh", "painting",3, null),
				new Artist( "Sara", "photo",2, null)));
		HtmlPage page = this.webClient.getPage("/artists");

		HtmlTable table = page.getHtmlElementById("artist_table");
		
		assertThat(table.asNormalizedText())
	    .isEqualToNormalizingWhitespace(
	        "Artists Name Art Score Judge Actions " +
	        "Azadeh painting 3 No judge Edit | Delete " +
	        "Sara photo 2 No judge Edit | Delete"
	    );

	}

	@Test
	public void testEditArtistScore() throws Exception {
	    Artist a = new Artist("original name", "Painting", null, null);
	    a.setId(1L);                                      
	    when(artistService.getArtistById(1L)).thenReturn(a);

	    HtmlPage page = this.webClient.getPage("/artists/edit/1"); 
	    HtmlForm form = page.getFormByName("artist_form");

	    form.getInputByName("score").setValueAttribute("3");
	    form.getButtonByName("btn_submit").click();

	    verify(artistService).updateArtistScore(1L, 3);
	}
	
	@Test
	public void testHighestScorePageWithArtists() throws Exception {

		when(artistService.getArtistWithHighestScore()).thenReturn(asList(
	        new Artist("Azadeh", "painting", 3, null),
	        new Artist("Sara",   "photo",    2, null)
	    ));


		HtmlPage page = this.webClient.getPage("/artists/highestScore");


		assertThat(page.getTitleText()).isEqualTo("High Score Artists");
	    assertThat(page.asNormalizedText()).contains("Artists With High Scores");
	    
	    HtmlTable table = page.getHtmlElementById("highest_Score_table");

	    assertThat(table.asNormalizedText())
	        .isEqualToNormalizingWhitespace(
	            "Name Art Name Score " +       
	            "Azadeh painting 3 " +         
	            "Sara photo 2"                 
	        );

	    verify(artistService).getArtistWithHighestScore();
	}

	@Test
	public void testHomePageProvideLinkForCreatingNewArtist() throws Exception {
	
	HtmlPage page = webClient.getPage("/artists");
	assertThat(
	page
	.getAnchorByText("Add New Artist")
	.getHrefAttribute()
	).isEqualTo("/artists/new");
	}
	
	@Test
	public void testHomePageWithArtistsShowArtistsInTable() throws Exception {
	
		Artist a1 = new Artist("Azadeh", "painting", 3, null);
		a1.setId(1L);
		Artist a2 = new Artist("Sara", "photo", 2, null);
		a2.setId(2L);
		when(artistService.getAllArtists()).thenReturn(asList(
				a1, a2));
		HtmlPage page = this.webClient.getPage("/artists");

		HtmlTable table = page.getHtmlElementById("artist_table");

		assertThat(table.asNormalizedText())
	    .isEqualToNormalizingWhitespace(
	        "Artists Name Art Score Judge Actions " +
	        "Azadeh painting 3 No judge Edit | Delete " +
	        "Sara photo 2 No judge Edit | Delete"
	    );

		page.getAnchorByHref("artists/edit/1");
		page.getAnchorByHref("artists/edit/2");
		
		page.getAnchorByHref("artists/delete/1");
		page.getAnchorByHref("artists/delete/2");
	}
}