package com.example.p4backend.controllers;

import com.example.p4backend.models.Donation;
import com.example.p4backend.models.User;
import com.example.p4backend.models.Vzw;
import com.example.p4backend.models.complete.CompleteDonation;
import com.example.p4backend.models.dto.DonationDTO;
import com.example.p4backend.repositories.DonationRepository;
import com.example.p4backend.repositories.UserRepository;
import com.example.p4backend.repositories.VzwRepository;
import lombok.Getter;
import org.bson.types.Decimal128;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@RestController
@CrossOrigin(origins = {"http://www.wip-shop.be","http://wip-shop.be"}, allowedHeaders = "*")
public class DonationController {
    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final VzwRepository vzwRepository;

    public DonationController(DonationRepository donationRepository, UserRepository userRepository, VzwRepository vzwRepository) {
        this.donationRepository = donationRepository;
        this.userRepository = userRepository;
        this.vzwRepository = vzwRepository;
    }

    @PostConstruct
    public void fillDB() {
        if (getDonationRepository().count() == 0) {
            Donation donation1 = new Donation("user1", "vzw1", new Decimal128(5), "desc");
            Donation donation2 = new Donation("user2", "vzw2", new Decimal128(15), "desc");
            Donation donation3 = new Donation("user3", "vzw3", new Decimal128(10), "desc");
            Donation donation4 = new Donation("user4", "vzw4", new Decimal128(3), "desc");
            Donation donation5 = new Donation("user1", "vzw2", new Decimal128(7), "desc");

            getDonationRepository().saveAll(List.of(donation1, donation2, donation3, donation4, donation5));
        }
    }

    @GetMapping("/donations")
    public List<CompleteDonation> getAll() {
        List<CompleteDonation> returnList = new ArrayList<>();
        List<Donation> donations = getDonationRepository().findAll();

        for (Donation donation : donations) {
            returnList.add(getCompleteDonation(donation));
        }
        return returnList;
    }

    @GetMapping("/donations/{id}")
    public CompleteDonation getDonationById(@PathVariable String id) {
        Donation donation = getDonationRepository().findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The Donation with ID " + id + " doesn't exist"));
        return getCompleteDonation(donation);
    }

    @PostMapping("/donation")
    public CompleteDonation addDonation(@RequestBody DonationDTO donationDTO) {
        Donation donation = new Donation(donationDTO);
        getDonationRepository().save(donation);
        return getCompleteDonation(donation);
    }

    // Get the filled CompleteDonation for the given donation
    private CompleteDonation getCompleteDonation(Donation donation) {
        Optional<Vzw> vzw = getVzwRepository().findById(donation.getVzwId());
        if (donation.getUserId() != null) {
            Optional<User> user = getUserRepository().findById(donation.getUserId());
            return new CompleteDonation(donation, user, vzw);
        } else {
            return new CompleteDonation(donation, vzw);
        }


    }
}
