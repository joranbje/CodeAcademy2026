package no.soprasteria.kaffebar.controller;

import no.soprasteria.kaffebar.api.MenuApi;
import no.soprasteria.kaffebar.model.Coffee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class MenuController implements MenuApi {

    @Override
    public ResponseEntity<List<Coffee>> getMenu() {
        // Her ville man normalt hentet data fra en database,
        // men vi hardkoder et par kaffedrikker for workshopen.

        Coffee latte = new Coffee();
        latte.setId(UUID.randomUUID());
        latte.setName("Kaffe Latte");
        latte.setPrice(48.50);

        Coffee svart = new Coffee();
        svart.setId(UUID.randomUUID());
        svart.setName("Svart Kaffe (V60)");
        svart.setPrice(38.00);

        return ResponseEntity.ok(List.of(latte, svart));
    }
}

