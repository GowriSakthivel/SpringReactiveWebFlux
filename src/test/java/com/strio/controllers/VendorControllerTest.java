package com.strio.controllers;

import com.strio.domain.Vendor;
import com.strio.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

public class VendorControllerTest {

    WebTestClient webTestClient;
    VendorController vendorController;
    VendorRepository vendorRepository;

    @Before
    public void setUp() throws Exception {
        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void list() {
        BDDMockito.given(vendorRepository.findAll())
                .willReturn(Flux.just(Vendor.builder().firstName("Ven").build(),
                        Vendor.builder().firstName("Ven1").build()));

        webTestClient.get()
                .uri("/api/vendors")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    public void getById() {
        BDDMockito.given(vendorRepository.findById("someId"))
                .willReturn(Mono.just(Vendor.builder().firstName("Ven").build()));

        webTestClient.get()
                .uri("/api/vendors/someId")
                .exchange()
                .expectBody(Vendor.class);
    }

    @Test
    public void testCreate() {
        BDDMockito.given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().firstName("ven").build()));

        Mono<Vendor> vendorMono = Mono.just(Vendor.builder().build());
        webTestClient.post()
                .uri("/api/vendors")
                .body(vendorMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void testUpdate() {
        BDDMockito.given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToUpdateMono = Mono.just(Vendor.builder().firstName("Some Name").build());

        webTestClient.put()
                .uri("/api/vendors/asdfasdf")
                .body(vendorToUpdateMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
    }
}