package com.example.ArtWork.controller;

//import com.example.ArtWork.Repository.JudgeRepository;

import com.example.ArtWork.Service.ArtistService;
import com.example.ArtWork.model.Artist;
//import com.example.ArtWork.model.Judge;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/artists")
public class ArtistController {

    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    
    }
   // @Autowired
  //  private JudgeRepository JudgeRepository;

    @GetMapping
    public String getAllArtists(Model model) {
        List<Artist> artists = artistService.getAllArtists();
        model.addAttribute("artists", artists);
        model.addAttribute("message", artists.isEmpty() ? "No artist" : "");
        return "artists";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("artist", new Artist());
        return "new-artist"; 
    }

    @PostMapping
    public String createArtist(@ModelAttribute Artist artist) {
    	 	
        artistService.createArtist(artist);

        return "redirect:/artists";
    }

    @GetMapping("/delete/{id}")
    public String deleteArtist(@PathVariable Long id) {
        artistService.deleteArtistById(id);
        return "redirect:/artists";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditScoreForm(@PathVariable Long id, Model model) {
        Artist artist = artistService.getArtistById(id);
        model.addAttribute("artist", artist);
        model.addAttribute("message", artist == null ? "No artist found with id: " + id : "");
        return "edit-score"; 
    }

    @PostMapping("/edit/{id}")
    public String updateScore(@PathVariable Long id, @RequestParam int score) {
        artistService.updateArtistScore(id, score);
        return "redirect:/artists";
    }
    
    @GetMapping("/highestScore")
    public String showHighestScoreArtists(Model model) {
        List<Artist> topArtists = artistService.getArtistWithHighestScore();
        model.addAttribute("artists", topArtists);
        model.addAttribute("message", topArtists.isEmpty() ? "No artist found" : "");
        return "high-score";
    }

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }
}
