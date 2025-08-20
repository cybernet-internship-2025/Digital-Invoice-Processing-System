package az.cybernet.integration.controller;

import az.cybernet.integration.model.IAMASDto;
import az.cybernet.integration.service.IAMASService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/iamas")
public class IAMASController {

    private final IAMASService iamasService;

    @GetMapping("/{pin}")
    public ResponseEntity<IAMASDto> getPinData(@PathVariable String pin){
        return ResponseEntity.ok(iamasService.getPinData(pin));
    }
}
